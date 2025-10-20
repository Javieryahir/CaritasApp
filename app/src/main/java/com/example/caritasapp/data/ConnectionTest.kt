package com.example.caritasapp.data

import android.content.Context
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

object ConnectionTest {
    private const val TAG = "ConnectionTest"
    
    suspend fun testConnection(context: Context): ConnectionResult = withContext(Dispatchers.IO) {
        try {
            Log.d(TAG, "Testing connection to local server...")
            
            val client = OkHttpClient.Builder()
                .connectTimeout(10, java.util.concurrent.TimeUnit.SECONDS)
                .readTimeout(10, java.util.concurrent.TimeUnit.SECONDS)
                .build()
            
            val request = Request.Builder()
                .url("localhost:8080/")
                .build()
            
            Log.d(TAG, "Making request to: ${request.url}")
            
            val response = client.newCall(request).execute()
            
            Log.d(TAG, "Response received: ${response.code}")
            Log.d(TAG, "Response body: ${response.body?.string()}")
            
            ConnectionResult.Success("Connected successfully! Response code: ${response.code}")
            
        } catch (e: ConnectException) {
            Log.e(TAG, "Connection refused: ${e.message}")
            ConnectionResult.Error("Connection refused. Is your local server running on port 8080?")
        } catch (e: SocketTimeoutException) {
            Log.e(TAG, "Connection timeout: ${e.message}")
            ConnectionResult.Error("Connection timeout. Check if your server is accessible.")
        } catch (e: UnknownHostException) {
            Log.e(TAG, "Unknown host: ${e.message}")
            ConnectionResult.Error("Unknown host. Check your network configuration.")
        } catch (e: Exception) {
            Log.e(TAG, "Unexpected error: ${e.message}", e)
            ConnectionResult.Error("Unexpected error: ${e.message}")
        }
    }
}

sealed class ConnectionResult {
    data class Success(val message: String) : ConnectionResult()
    data class Error(val message: String) : ConnectionResult()
}
