package com.example.caritasapp.data

import com.example.caritasapp.data.remote.HostelApi
import com.example.caritasapp.data.remote.dto.HostelDto

class HostelRepository(private val api: HostelApi) {

    suspend fun getHostels(
        startDate: String,
        endDate: String,
        filtersCsv: String?
    ): Result<List<HostelDto>> = runCatching {
        api.getHostels(
            startDate = startDate,
            endDate = endDate,
            filters = filtersCsv,
            limit = 3,
            page = 1
        )
    }
}
