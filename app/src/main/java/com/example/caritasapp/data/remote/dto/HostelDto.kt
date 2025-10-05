package com.example.caritasapp.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class HostelDto(
    val id: String,
    val name: String,
    val description: String,
    val maxCapacity: Int,
    val locationUrl: String,
    val imageUrls: List<String> = emptyList(),
    val availableSpaces: Int
)
