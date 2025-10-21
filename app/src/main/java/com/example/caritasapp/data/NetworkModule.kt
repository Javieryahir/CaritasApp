package com.example.caritasapp.data

import android.content.Context
import com.example.caritasapp.BuildConfig
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import java.util.concurrent.TimeUnit
import javax.net.ssl.HostnameVerifier
import javax.net.ssl.SSLSession

object NetworkModule {

    private val json = Json {
        ignoreUnknownKeys = true
        encodeDefaults = true
        prettyPrint = true
        isLenient = true
    }

    private fun createOkHttpClient(sessionManager: SessionManager? = null): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            })
            .hostnameVerifier { hostname, session ->
                // For IP addresses connecting to AWS API Gateway, allow the connection
                // since we're adding the correct Host header
                if (hostname == "13.223.151.115") {
                    println("🔒 Allowing SSL connection to IP: $hostname (AWS API Gateway)")
                    true
                } else {
                    // Use default hostname verification for domain names
                    javax.net.ssl.HttpsURLConnection.getDefaultHostnameVerifier().verify(hostname, session)
                }
            }
            .addInterceptor { chain ->
                val request = chain.request()
                println("🌐 Making request to: ${request.url}")
                println("🌐 Request method: ${request.method}")
                println("🌐 Request headers: ${request.headers}")
                println("🌐 Full URL: ${request.url}")
                println("🌐 Host: ${request.url.host}")
                println("🌐 Port: ${request.url.port}")
                println("🌐 Scheme: ${request.url.scheme}")
                
                try {
                    val response = chain.proceed(request)
                    println("✅ Response received: ${response.code}")
                    println("✅ Response headers: ${response.headers}")
                    
                    if (response.code >= 400) {
                        println("❌ ERROR: HTTP ${response.code}")
                        // Try to read the response body for error details
                        try {
                            val responseBody = response.peekBody(Long.MAX_VALUE)
                            val responseString = responseBody.string()
                            println("❌ Error Response Body: $responseString")
                        } catch (e: Exception) {
                            println("❌ Could not read error response body: ${e.message}")
                        }
                    }
                    
                    response
                } catch (e: Exception) {
                    println("❌ Network error: ${e.message}")
                    println("❌ Error type: ${e.javaClass.simpleName}")
                    println("❌ Stack trace: ${e.stackTrace.joinToString("\n")}")
                    throw e
                }
            }
            .addInterceptor { chain ->
                val originalRequest = chain.request()
                val requestBuilder = originalRequest.newBuilder()
                
                // Check if this is an authentication endpoint that shouldn't have Authorization header
                val url = originalRequest.url.toString()
                val isAuthEndpoint = url.contains("/api/signup") || 
                                   url.contains("/api/signup/confirm") || 
                                   url.contains("/api/login") || 
                                   url.contains("/api/login/refresh")
                
                // Add Host header for IP-based requests
                if (url.contains("13.223.151.115")) {
                    requestBuilder.addHeader("Host", "api.caritas.automvid.store")
                    println("🔍 Adding Host header for IP request: api.caritas.automvid.store")
                }
                
                if (!isAuthEndpoint) {
                    // Add Bearer token if available (only for non-auth endpoints)
                    sessionManager?.getIdToken()?.let { token ->
                        requestBuilder.addHeader("Authorization", "Bearer $token")
                        println("🔍 Adding Authorization header: Bearer ${token.take(20)}...")
                    } ?: run {
                        println("🔍 No Authorization token available")
                    }
                } else {
                    println("🔍 Skipping Authorization header for auth endpoint: $url")
                }
                
                chain.proceed(requestBuilder.build())
            }
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    private fun createRetrofit(baseUrl: String, sessionManager: SessionManager? = null): Retrofit {
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(createOkHttpClient(sessionManager))
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
    }
    
    // Custom error handler for Retrofit
    suspend fun <T> handleApiCall(apiCall: suspend () -> T): T {
        return try {
            apiCall()
        } catch (e: retrofit2.HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            throw ApiException.fromHttpError(e.code(), errorBody)
        } catch (e: Exception) {
            throw e
        }
    }

    val apiService: ApiService = createRetrofit(BuildConfig.BASE_URL).create(ApiService::class.java)
    val onlineApiService: ApiService = createRetrofit(BuildConfig.ONLINE_URL).create(ApiService::class.java)
    
    fun createAuthenticatedApiService(sessionManager: SessionManager): ApiService {
        return createRetrofit(BuildConfig.ONLINE_URL, sessionManager).create(ApiService::class.java)
    }

    fun createSessionManager(context: Context): SessionManager = SessionManager(context)
    
    // Simple connectivity test
    suspend fun testConnectivity(): Boolean {
        return try {
            val testUrl = "https://13.223.151.115/"
            println("🧪 Testing connectivity to: $testUrl")
            
            val client = OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .build()
            
            val request = okhttp3.Request.Builder()
                .url(testUrl)
                .addHeader("Host", "api.caritas.automvid.store")
                .build()
            
            val response = client.newCall(request).execute()
            println("🧪 Test response: ${response.code}")
            response.close()
            true
        } catch (e: Exception) {
            println("🧪 Connectivity test failed: ${e.message}")
            println("🧪 Error type: ${e.javaClass.simpleName}")
            false
        }
    }

    fun createAuthRepository(context: Context): AuthRepository {
        val sessionManager = createSessionManager(context)
        val authApiService = createAuthApiService(sessionManager)
        val onlineApiService = createOnlineApiService(sessionManager)
        return AuthRepository.getInstance(authApiService, onlineApiService, sessionManager)
    }
    
    fun createAuthApiService(sessionManager: SessionManager): ApiService {
        return createRetrofit(BuildConfig.BASE_URL, sessionManager).create(ApiService::class.java)
    }
    
    fun createOnlineApiService(sessionManager: SessionManager): ApiService {
        return createRetrofit(BuildConfig.ONLINE_URL, sessionManager).create(ApiService::class.java)
    }
    
    fun createReservationRepository(context: Context): ReservationRepository {
        val sessionManager = createSessionManager(context)
        val localApiService = createRetrofit(BuildConfig.BASE_URL, sessionManager).create(ApiService::class.java)
        return ReservationRepository.getInstance(localApiService)
    }
    
    fun createUserRepository(context: Context): UserRepository {
        val sessionManager = createSessionManager(context)
        val authenticatedApiService = createAuthenticatedApiService(sessionManager)
        return UserRepository.getInstance(authenticatedApiService)
    }
    
    fun createPersonRepository(context: Context): PersonRepository {
        val sessionManager = createSessionManager(context)
        val localApiService = createRetrofit(BuildConfig.BASE_URL, sessionManager).create(ApiService::class.java)
        return PersonRepository.getInstance(localApiService)
    }
}
