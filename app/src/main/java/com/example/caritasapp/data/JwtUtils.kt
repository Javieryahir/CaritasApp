package com.example.caritasapp.data

import android.util.Base64
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonPrimitive

object JwtUtils {
    private val json = Json { ignoreUnknownKeys = true }
    
    fun extractUserIdFromToken(token: String): String? {
        return try {
            // JWT tokens have 3 parts separated by dots: header.payload.signature
            val parts = token.split(".")
            if (parts.size != 3) return null
            
            // Decode the payload (second part)
            val payload = parts[1]
            val decodedBytes = Base64.decode(payload, Base64.URL_SAFE or Base64.NO_PADDING)
            val payloadString = String(decodedBytes)
            
            // Parse the JSON payload
            val jsonObject = json.parseToJsonElement(payloadString) as JsonObject
            
            // Extract user ID from common JWT claims
            val userId = jsonObject["sub"]?.jsonPrimitive?.content
                ?: jsonObject["user_id"]?.jsonPrimitive?.content
                ?: jsonObject["id"]?.jsonPrimitive?.content
                ?: jsonObject["userId"]?.jsonPrimitive?.content
            
            userId
        } catch (e: Exception) {
            println("Error extracting user ID from token: ${e.message}")
            null
        }
    }
    
    fun extractUserInfoFromToken(token: String): UserInfoFromToken? {
        return try {
            val parts = token.split(".")
            if (parts.size != 3) return null
            
            val payload = parts[1]
            val decodedBytes = Base64.decode(payload, Base64.URL_SAFE or Base64.NO_PADDING)
            val payloadString = String(decodedBytes)
            
            val jsonObject = json.parseToJsonElement(payloadString) as JsonObject
            
            val userId = jsonObject["sub"]?.jsonPrimitive?.content
                ?: jsonObject["user_id"]?.jsonPrimitive?.content
                ?: jsonObject["id"]?.jsonPrimitive?.content
                ?: jsonObject["userId"]?.jsonPrimitive?.content
            
            val firstName = jsonObject["given_name"]?.jsonPrimitive?.content
                ?: jsonObject["firstName"]?.jsonPrimitive?.content
                ?: jsonObject["first_name"]?.jsonPrimitive?.content
            
            val lastName = jsonObject["family_name"]?.jsonPrimitive?.content
                ?: jsonObject["lastName"]?.jsonPrimitive?.content
                ?: jsonObject["last_name"]?.jsonPrimitive?.content
            
            val email = jsonObject["email"]?.jsonPrimitive?.content
            
            if (userId != null) {
                UserInfoFromToken(
                    id = userId,
                    firstName = firstName,
                    lastName = lastName,
                    email = email
                )
            } else {
                null
            }
        } catch (e: Exception) {
            println("Error extracting user info from token: ${e.message}")
            null
        }
    }
}

data class UserInfoFromToken(
    val id: String,
    val firstName: String?,
    val lastName: String?,
    val email: String?
)
