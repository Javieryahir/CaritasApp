package com.example.caritasapp.data.remote

import com.example.caritasapp.data.remote.dto.HostelDto
import retrofit2.http.GET
import retrofit2.http.Query

interface HostelApi {
    @GET("api/hostels")
    suspend fun getHostels(
        @Query("startDate") startDate: String,      // YYYY-MM-DD
        @Query("endDate") endDate: String,          // YYYY-MM-DD
        @Query("filters") filters: String?,         // "laundry,meal,breakfast"
        @Query("limit") limit: Int = 3,
        @Query("page") page: Int = 1
    ): List<HostelDto>
}
