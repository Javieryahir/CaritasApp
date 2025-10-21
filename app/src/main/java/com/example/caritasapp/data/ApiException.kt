package com.example.caritasapp.data

class ApiException(
    val statusCode: Int,
    val errorMessage: String,
    val originalException: Throwable? = null
) : Exception(errorMessage) {
    
    companion object {
        fun fromHttpError(statusCode: Int, errorBody: String?): ApiException {
            return when (statusCode) {
                400 -> {
                    val message = when {
                        errorBody?.contains("User already exists") == true -> "User already exists. Please try logging in instead."
                        errorBody?.contains("Invalid request data") == true -> "Invalid request data. Please check your information."
                        else -> "Bad request. Please check your information."
                    }
                    ApiException(statusCode, message)
                }
                401 -> {
                    val message = when {
                        errorBody?.contains("token has expired") == true -> "Your session has expired. Please log in again."
                        errorBody?.contains("Unauthorized") == true -> "Unauthorized. Please try again."
                        else -> "Unauthorized. Please try again."
                    }
                    ApiException(statusCode, message)
                }
                404 -> ApiException(statusCode, "Service not found. Please try again later.")
                500 -> {
                    val message = when {
                        errorBody?.contains("Internal Server Error") == true -> "Server error. Please try again later."
                        errorBody?.contains("Database") == true -> "Database error. Please try again later."
                        errorBody?.contains("Validation") == true -> "Validation error. Please check your information."
                        errorBody?.isNotEmpty() == true -> "Server error: $errorBody"
                        else -> "Server error. Please try again later."
                    }
                    ApiException(statusCode, message)
                }
                else -> ApiException(statusCode, "Network error. Please try again.")
            }
        }
    }
}







