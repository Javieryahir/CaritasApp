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
                401 -> ApiException(statusCode, "Unauthorized. Please try again.")
                404 -> ApiException(statusCode, "Service not found. Please try again later.")
                500 -> ApiException(statusCode, "Server error. Please try again later.")
                else -> ApiException(statusCode, "Network error. Please try again.")
            }
        }
    }
}




