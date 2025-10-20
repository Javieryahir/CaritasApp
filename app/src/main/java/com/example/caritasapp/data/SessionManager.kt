package com.example.caritasapp.data

import android.content.Context
import android.content.SharedPreferences
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import androidx.core.content.edit

class SessionManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("caritas_session", Context.MODE_PRIVATE)
    private val json = Json { ignoreUnknownKeys = true }
    
    fun saveUser(user: UserData) {
        val userJson = json.encodeToString(user)
        prefs.edit { putString("user", userJson) }
    }
    
    fun getUser(): UserData? {
        val userJson = prefs.getString("user", null) ?: return null
        return try {
            json.decodeFromString<UserData>(userJson)
        } catch (e: Exception) {
            null
        }
    }
    
    fun saveToken(token: String) {
        prefs.edit { putString("token", token) }
    }
    
    fun getToken(): String? {
        return prefs.getString("token", null)
    }
    
    fun saveTokens(idToken: String, accessToken: String, refreshToken: String) {
        prefs.edit {
            putString("idToken", idToken)
                .putString("accessToken", accessToken)
                .putString("refreshToken", refreshToken)
        }
    }
    
    fun getIdToken(): String? {
        return prefs.getString("idToken", null)
    }
    
    fun getAccessToken(): String? {
        return prefs.getString("accessToken", null)
    }
    
    fun getRefreshToken(): String? {
        return prefs.getString("refreshToken", null)
    }
    
    fun isLoggedIn(): Boolean {
        return getIdToken() != null && getUser() != null
    }
    
    fun logout() {
        prefs.edit { clear() }
    }
}

