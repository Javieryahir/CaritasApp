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
                val response = chain.proceed(request)
                
                println("Request: ${request.method} ${request.url}")
                println("Response: ${response.code}")
                
                if (response.code != 200) {
                    println("ERROR: HTTP ${response.code}")
                    // Try to read the response body for error details
                    try {
                        val responseBody = response.peekBody(Long.MAX_VALUE)
                        val responseString = responseBody.string()
                        println("Error Response Body: $responseString")
                    } catch (e: Exception) {
                        println("Could not read error response body: ${e.message}")
                    }
                }
                
                response
            }
            .addInterceptor { chain ->
                val originalRequest = chain.request()
                val requestBuilder = originalRequest.newBuilder()
                
                // Add Bearer token if available
                sessionManager?.getIdToken()?.let { token ->
                    requestBuilder.addHeader("Authorization", "Bearer $token")
                }
                
                chain.proceed(requestBuilder.build())
            }
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    private fun createRetrofit(sessionManager: SessionManager? = null): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .client(createOkHttpClient(sessionManager))
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
    }

    val apiService: ApiService = createRetrofit().create(ApiService::class.java)
    
    fun createAuthenticatedApiService(sessionManager: SessionManager): ApiService {
        return createRetrofit(sessionManager).create(ApiService::class.java)
    }

    fun createSessionManager(context: Context): SessionManager = SessionManager(context)

    fun createAuthRepository(context: Context): AuthRepository {
        val sessionManager = createSessionManager(context)
        val authenticatedApiService = createAuthenticatedApiService(sessionManager)
        return AuthRepository.getInstance(authenticatedApiService, sessionManager)
    }
}
