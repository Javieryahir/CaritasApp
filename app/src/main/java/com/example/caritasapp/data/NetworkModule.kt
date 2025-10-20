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
            .addInterceptor { chain ->
                val request = chain.request()
                println("🌐 Making request to: ${request.url}")
                println("🌐 Request method: ${request.method}")
                println("🌐 Request headers: ${request.headers}")
                
                try {
                    val response = chain.proceed(request)
                    println("✅ Response received: ${response.code}")
                    println("✅ Response headers: ${response.headers}")
                    
                    if (response.code != 200) {
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
                    throw e
                }
            }
            .addInterceptor { chain ->
                val originalRequest = chain.request()
                val requestBuilder = originalRequest.newBuilder()
                
                // Add Bearer token if available
                sessionManager?.getIdToken()?.let { token ->
                    requestBuilder.addHeader("Authorization", "Bearer $token")
                    println("🔍 Adding Authorization header: Bearer ${token.take(20)}...")
                } ?: run {
                    println("🔍 No Authorization token available")
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

    val apiService: ApiService = createRetrofit(BuildConfig.BASE_URL).create(ApiService::class.java)
    val onlineApiService: ApiService = createRetrofit(BuildConfig.ONLINE_URL).create(ApiService::class.java)
    
    fun createAuthenticatedApiService(sessionManager: SessionManager): ApiService {
        return createRetrofit(BuildConfig.ONLINE_URL, sessionManager).create(ApiService::class.java)
    }

    fun createSessionManager(context: Context): SessionManager = SessionManager(context)

    fun createAuthRepository(context: Context): AuthRepository {
        val sessionManager = createSessionManager(context)
        val authenticatedApiService = createAuthenticatedApiService(sessionManager)
        return AuthRepository.getInstance(authenticatedApiService, sessionManager)
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
