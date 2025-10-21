package com.example.caritasapp.data

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
    // Authentication endpoints
    @POST("api/signup")
    suspend fun signup(@Body request: SignupRequest): SignupResponse
    
    @POST("api/signup/confirm")
    suspend fun confirmSignup(@Body request: SignupConfirmRequest): SignupConfirmResponse
    
    @POST("api/login")
    suspend fun login(@Body request: LoginRequest): LoginResponse
    
    @POST("api/login/refresh")
    suspend fun refreshToken(@Body request: RefreshTokenRequest): RefreshTokenResponse
    
    // Shelter endpoints
    @GET("api/shelters")
    suspend fun getShelters(): List<ShelterData>
    
    @GET("api/shelters/{id}")
    suspend fun getShelter(@Path("id") id: String): ShelterData
    
    @GET("api/shelters/search")
    suspend fun searchShelters(
        @Query("lat") latitude: Double,
        @Query("lng") longitude: Double,
        @Query("radius") radius: Double = 10.0
    ): List<ShelterData>
    
    // Hostels endpoints
    @GET("api/hostels")
    suspend fun getHostels(
        @Query("startDate") startDate: String,
        @Query("endDate") endDate: String,
        @Query("limit") limit: Int = 5,
        @Query("page") page: Int = 1,
        @Query("filters") filters: String? = null
    ): List<HostelData>
    
    // Reservation endpoints
    @POST("api/reservations")
    suspend fun createReservation(@Body request: ReservationRequest): ReservationResponse
    
    @GET("api/reservations")
    suspend fun getUserReservations(): List<ReservationData>
    
    @GET("api/reservations/user")
    suspend fun getUserReservationsWithPagination(
        @Query("limit") limit: Int = 5,
        @Query("page") page: Int = 1
    ): UserReservationsResponse
    
    @GET("api/reservations/user")
    suspend fun getUserReservationsWithActive(
        @Query("limit") limit: Int = 5,
        @Query("page") page: Int = 1
    ): UserReservationsWithActiveResponse
    
    @GET("api/reservations/user/history/{userId}")
    suspend fun getUserReservationsHistory(
        @Path("userId") userId: String,
        @Query("limit") limit: Int = 5,
        @Query("page") page: Int = 1
    ): ApiReservationResponse
    
    @GET("api/reservations/{id}")
    suspend fun getReservation(@Path("id") id: String): ReservationData
    
    @GET("api/reservations/{registrationId}")
    suspend fun getDetailedReservation(@Path("registrationId") registrationId: String): DetailedReservationResponse
    
    @POST("api/reservations/{id}/cancel")
    suspend fun cancelReservation(@Path("id") id: String): ApiResponse
    
    // Services endpoints
    @GET("api/services")
    suspend fun getServices(): List<ServiceData>
    
    @GET("api/services/{id}")
    suspend fun getService(@Path("id") id: String): ServiceData
    
    // User endpoints
    @GET("api/users/{id}")
    suspend fun getUser(@Path("id") id: String): UserData
    
    // Transportation endpoints
    @POST("api/internal/transportations")
    suspend fun createTransportation(@Body request: TransportationRequest): TransportationResponse
    
    // Service reservation endpoints
    @GET("api/admin/services")
    suspend fun getAvailableServices(): List<ServiceListItem>
    
    @GET("api/hostels/{hostelId}")
    suspend fun getHostelServices(@Path("hostelId") hostelId: String): HostelData
    
    @POST("api/service-reservations")
    suspend fun createServiceReservation(@Body request: NewServiceReservationRequest): NewServiceReservationResponse
    
    // Person endpoints
    @POST("api/persons")
    suspend fun createPerson(@Body request: PersonRequest): PersonResponse
    
    // New Reservation endpoints
    @POST("api/reservations")
    suspend fun createNewReservation(@Body request: NewReservationRequest): NewReservationResponse
    
    // User reservations endpoint
    @GET("api/reservations/user/{userId}")
    suspend fun getUserReservation(@Path("userId") userId: String): UserReservationResponse
    
    // Get user reservations for quick reservation
    @GET("api/reservations/user/{userId}")
    suspend fun getUserReservations(@Path("userId") userId: String): List<ReservationData>
    
    // Repeat reservation endpoint
    @POST("api/reservations/repeat/{prevReservationId}")
    suspend fun repeatReservation(
        @Path("prevReservationId") prevReservationId: String,
        @Body request: RepeatReservationRequest
    ): NewReservationResponse
}
