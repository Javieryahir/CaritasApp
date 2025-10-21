package com.example.caritasapp.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class ReservationRepository(
    private val apiService: ApiService
) {
    companion object {
        @Volatile
        private var INSTANCE: ReservationRepository? = null
        
        fun getInstance(apiService: ApiService): ReservationRepository {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: ReservationRepository(apiService).also { INSTANCE = it }
            }
        }
    }
    
    suspend fun getShelters(): Flow<List<ShelterData>> = flow {
        try {
            val shelters = apiService.getShelters()
            emit(shelters)
        } catch (e: Exception) {
            // Fallback to mock data if API fails
            emit(getMockShelters())
        }
    }
    
    suspend fun searchShelters(latitude: Double, longitude: Double, radius: Double = 10.0): Flow<List<ShelterData>> = flow {
        try {
            val shelters = apiService.searchShelters(latitude, longitude, radius)
            emit(shelters)
        } catch (e: Exception) {
            // Fallback to mock data if API fails
            emit(getMockShelters())
        }
    }
    
    suspend fun getShelter(id: String): Flow<ShelterData?> = flow {
        try {
            val shelter = apiService.getShelter(id)
            emit(shelter)
        } catch (e: Exception) {
            // Fallback to mock data if API fails
            emit(getMockShelters().find { it.id == id })
        }
    }
    
    suspend fun createReservation(request: ReservationRequest): Flow<ReservationResponse?> = flow {
        try {
            val response = apiService.createReservation(request)
            emit(response)
        } catch (e: Exception) {
            // Handle error - could emit error state
            emit(null)
        }
    }
    
    suspend fun getUserReservations(): Flow<List<ReservationData>> = flow {
        try {
            val reservations = apiService.getUserReservations()
            emit(reservations)
        } catch (e: Exception) {
            // Fallback to mock data if API fails
            emit(getMockReservations())
        }
    }
    
    suspend fun getUserReservationsWithPagination(limit: Int = 5, page: Int = 1): Flow<UserReservationsResponse?> = flow {
        try {
            val response = apiService.getUserReservationsWithPagination(limit, page)
            emit(response)
        } catch (e: Exception) {
            // Fallback to mock data if API fails
            emit(getMockUserReservationsResponse())
        }
    }
    
    suspend fun hasActiveReservations(): Flow<Boolean> = flow {
        try {
            val response = apiService.getUserReservationsWithPagination(5, 1)
            val hasActive = response.data.any { it.status == "confirmed" || it.status == "active" }
            emit(hasActive)
        } catch (e: Exception) {
            // Fallback to mock data if API fails
            emit(true) // Assume has reservations for demo purposes
        }
    }
    
    suspend fun createTransportation(request: TransportationRequest): Flow<TransportationResponse?> = flow {
        try {
            val response = apiService.createTransportation(request)
            emit(response)
        } catch (e: Exception) {
            // Handle error - could emit error state
            emit(null)
        }
    }
    
    suspend fun getUserReservation(userId: String): Flow<UserReservationResponse?> = flow {
        try {
            println("🔍 ReservationRepository - Getting user reservation for userId: $userId")
            val response = apiService.getUserReservation(userId)
            println("🔍 ReservationRepository - API response: $response")
            emit(response)
        } catch (e: Exception) {
            println("🔍 ReservationRepository - Error getting user reservation: ${e.message}")
            // Handle error - could emit error state
            emit(null)
        }
    }
    
    suspend fun getServices(): Flow<List<ServiceData>> = flow {
        try {
            val services = apiService.getServices()
            emit(services)
        } catch (e: Exception) {
            // Fallback to mock data if API fails
            emit(getMockServices())
        }
    }
    
    suspend fun getHostels(
        startDate: String,
        endDate: String,
        limit: Int = 5,
        page: Int = 1,
        filters: String? = null
    ): Flow<List<HostelData>> = flow {
        try {
            println("🔍 ReservationRepository - Calling API with parameters:")
            println("  startDate: $startDate")
            println("  endDate: $endDate")
            println("  limit: $limit")
            println("  page: $page")
            println("  filters: $filters")
            
            val hostels = apiService.getHostels(startDate, endDate, limit, page, filters)
            println("🔍 ReservationRepository - API returned ${hostels.size} hostels")
            hostels.forEach { hostel ->
                println("  - ${hostel.name} (ID: ${hostel.id})")
            }
            emit(hostels)
        } catch (e: Exception) {
            println("🔍 ReservationRepository - API call failed: ${e.message}")
            println("🔍 ReservationRepository - Using mock data")
            // Fallback to mock data if API fails
            emit(getMockHostels())
        }
    }
    
    suspend fun getAvailableServices(): Flow<List<ServiceListItem>> = flow {
        try {
            val services = apiService.getAvailableServices()
            emit(services)
        } catch (e: Exception) {
            // Fallback to empty list if API fails
            emit(emptyList())
        }
    }
    
    suspend fun getHostelServices(hostelId: String): Flow<HostelData?> = flow {
        try {
            println("🔍 ReservationRepository - Fetching hostel services for ID: $hostelId")
            val hostelData = apiService.getHostelServices(hostelId)
            println("🔍 ReservationRepository - Hostel services response:")
            println("  Hostel: ${hostelData.name}")
            println("  Services count: ${hostelData.hostelServices?.size ?: 0}")
            hostelData.hostelServices?.forEach { service ->
                println("  - ${service.service.type} ($${service.service.price})")
            }
            emit(hostelData)
        } catch (e: Exception) {
            println("🔍 ReservationRepository - Error fetching hostel services: ${e.message}")
            emit(null)
        }
    }
    
    suspend fun createServiceReservation(request: NewServiceReservationRequest): Flow<NewServiceReservationResponse?> = flow {
        try {
            val response = apiService.createServiceReservation(request)
            emit(response)
        } catch (e: Exception) {
            // Handle error - could emit error state
            emit(null)
        }
    }
    
    // Mock data for fallback
    private fun getMockShelters(): List<ShelterData> {
        return listOf(
            ShelterData(
                id = "1",
                name = "Divina Providencia",
                address = "Calle Principal 123, Monterrey",
                latitude = 25.668394809524564,
                longitude = -100.30311761472923,
                capacity = 4,
                availableSpots = 2,
                services = listOf("Desayuno", "Comida", "Cena", "Duchas", "Lavadoras"),
                description = "Albergue con espacios comunes y atención matutina."
            ),
            ShelterData(
                id = "2",
                name = "Posada del Peregrino",
                address = "Av. Central 456, Monterrey",
                latitude = 25.68334790328978,
                longitude = -100.34546687358926,
                capacity = 6,
                availableSpots = 4,
                services = listOf("Desayuno", "Comida", "Cena", "Duchas", "Traslados"),
                description = "Opciones de estancia temporal y apoyo alimentario."
            ),
            ShelterData(
                id = "3",
                name = "Alberge Contigo",
                address = "Plaza Mayor 789, Monterrey",
                latitude = 25.791571000975924,
                longitude = -100.1387095558184,
                capacity = 3,
                availableSpots = 1,
                services = listOf("Desayuno", "Comida", "Duchas", "Psicólogo", "Dentista"),
                description = "Centro con cupos limitados y registro diario."
            )
        )
    }
    
    private fun getMockReservations(): List<ReservationData> {
        return listOf(
            ReservationData(
                id = "1",
                shelterId = "1",
                shelterName = "Divina Providencia",
                startDate = "2024-01-15",
                endDate = "2024-01-17",
                peopleCount = 2,
                status = "confirmed",
                createdAt = "2024-01-10T10:00:00Z",
                guestInfo = listOf(
                    GuestInfo("Juan Pérez", 35, "Ninguna", "Ninguna", "Ninguna"),
                    GuestInfo("María García", 32, "Ninguna", "Ninguna", "Ninguna")
                ),
                selectedServices = listOf("Desayuno", "Comida")
            )
        )
    }
    
    private fun getMockServices(): List<ServiceData> {
        return listOf(
            ServiceData("1", "Desayuno", "Desayuno completo", 15.0, "Alimentación"),
            ServiceData("2", "Comida", "Comida del día", 15.0, "Alimentación"),
            ServiceData("3", "Cena", "Cena ligera", 10.0, "Alimentación"),
            ServiceData("4", "Duchas", "Acceso a duchas", 10.0, "Higiene"),
            ServiceData("5", "Lavadoras", "Servicio de lavandería", 10.0, "Higiene"),
            ServiceData("6", "Traslados", "Transporte a ubicaciones", 20.0, "Transporte"),
            ServiceData("7", "Psicólogo", "Consulta psicológica", 0.0, "Salud"),
            ServiceData("8", "Dentista", "Consulta dental", 0.0, "Salud"),
            ServiceData("9", "Expedición de oficios", "Documentos oficiales", 5.0, "Documentación")
        )
    }
    
    private fun getMockHostels(): List<HostelData> {
        return listOf(
            HostelData(
                id = "31d0517d-e83a-43ec-861f-37a0d2091670",
                name = "Posada del Peregrino",
                description = "Este es el albergue Posada del Peregrino",
                price = 30.0,
                maxCapacity = 30,
                locationUrl = "https://maps.app.goo.gl/cCnyrwYFLcMK6RHw6",
                imageUrls = listOf(
                    "https://lh3.googleusercontent.com/gps-cs-s/AC9h4nr7cjm7Qelam-mKgI5Q4-6KmZ2SdSw7a9Qs6CKRhp7uHzmCXF1efAEnL4aXyOcQ3dj63OhZcYE9Mj7zIBELJR0z0kRvAIyk5hlre-32KpaJ9x-cR2H29SOn-iXdbIiFupMtV6x0NQ=w203-h152-k-no"
                ),
                hostelServices = listOf(
                    HostelService(
                        id = "service1",
                        service = ServiceInfo("breakfast", 15.0, "breakfasts")
                    ),
                    HostelService(
                        id = "service2", 
                        service = ServiceInfo("meal", 15.0, "meals")
                    )
                )
            ),
            HostelData(
                id = "5c886a27-8f54-42d2-93b4-49aba9b0b540",
                name = "Divina Providencia",
                description = "Este es el albergue Divina Providencia",
                price = 30.0,
                maxCapacity = 30,
                locationUrl = "https://maps.app.goo.gl/skRvdYhFNKpqXcdq9",
                imageUrls = listOf(
                    "https://streetviewpixels-pa.googleapis.com/v1/thumbnail?panoid=_oeMYgkm-Z0ANaoEc4hg3A&cb_client=search.gws-prod.gps&w=408&h=240&yaw=311.8523&pitch=0&thumbfov=100"
                ),
                hostelServices = listOf(
                    HostelService(
                        id = "service3",
                        service = ServiceInfo("breakfast", 15.0, "breakfasts")
                    ),
                    HostelService(
                        id = "service4",
                        service = ServiceInfo("meal", 15.0, "meals")
                    )
                )
            ),
            HostelData(
                id = "4f707e3a-3a7d-4086-890e-532d846f28c4",
                name = "Apodaca",
                description = "Este es el albergue Apodaca",
                price = 30.0,
                maxCapacity = 30,
                locationUrl = "https://maps.app.goo.gl/Y4mjL2uSjFEo2QAV7",
                imageUrls = listOf(
                    "https://lh3.googleusercontent.com/gps-cs-s/AC9h4nrCkuBshWtNW5N7KeQsnoQGxuzjlv8U8HqZyt83P0miQxo-e2-HyuH5L_HuzdA0T8VJ-3XLM0Xk0-pezy0Ol-vBYjfDY-zVefZuhDWrpg24B9-NHfYj2jahbQ7rNUA2wwKb1IzbKXos9INc=w203-h152-k-no"
                ),
                hostelServices = listOf(
                    HostelService(
                        id = "service5",
                        service = ServiceInfo("breakfast", 15.0, "breakfasts")
                    ),
                    HostelService(
                        id = "service6",
                        service = ServiceInfo("meal", 15.0, "meals")
                    )
                )
            )
        )
    }
    
    private fun getMockUserReservationsResponse(): UserReservationsResponse {
        return UserReservationsResponse(
            data = getMockReservations(),
            total = 1,
            page = 1,
            limit = 5,
            hasMore = false
        )
    }
}
