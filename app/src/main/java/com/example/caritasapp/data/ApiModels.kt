package com.example.caritasapp.data

import kotlinx.serialization.Serializable

// Request models
@Serializable
data class SignupRequest(
    val phoneNumber: String,
    val firstName: String,
    val lastName: String
)

@Serializable
data class LoginRequest(
    val phoneNumber: String
)

// Response models
@Serializable
data class UserData(
    val id: String,
    val phoneNumber: String,
    val firstName: String,
    val lastName: String,
    val email: String? = null,
    val token: String? = null,
    val createdAt: String? = null,
    val updatedAt: String? = null
)

@Serializable
data class LoginResponse(
    val userId: String
)

@Serializable
data class SignupResponse(
    val message: String,
    val success: Boolean? = null
)

@Serializable
data class SignupConfirmRequest(
    val phoneNumber: String,
    val code: String
)

@Serializable
data class SignupConfirmResponse(
    val userId: String
)

// Shelter models
@Serializable
data class ShelterData(
    val id: String,
    val name: String,
    val address: String,
    val latitude: Double,
    val longitude: Double,
    val capacity: Int,
    val availableSpots: Int,
    val services: List<String>,
    val description: String? = null,
    val distance: Double? = null
)

// Reservation models
@Serializable
data class ReservationRequest(
    val shelterId: String,
    val startDate: String,
    val endDate: String,
    val peopleCount: Int,
    val guestInfo: List<GuestInfo>,
    val selectedServices: List<String> = emptyList()
)

@Serializable
data class GuestInfo(
    val name: String,
    val age: Int,
    val allergies: String? = null,
    val disabilities: String? = null,
    val medications: String? = null
)

@Serializable
data class ReservationResponse(
    val id: String,
    val message: String,
    val status: String,
    val reservation: ReservationData
)

@Serializable
data class ReservationData(
    val id: String,
    val shelterId: String,
    val shelterName: String,
    val startDate: String,
    val endDate: String,
    val peopleCount: Int,
    val status: String,
    val createdAt: String,
    val guestInfo: List<GuestInfo>,
    val selectedServices: List<String>
)

// Service models
@Serializable
data class ServiceData(
    val id: String,
    val name: String,
    val description: String,
    val price: Double,
    val category: String
)

// Generic API response
@Serializable
data class ApiResponse(
    val message: String,
    val success: Boolean
)

// Hostels models

@Serializable
data class HostelData(
    val id: String,
    val name: String,
    val description: String,
    val price: Double,
    val maxCapacity: Int,
    val locationUrl: String,
    val imageUrls: List<String>,
    val hostelServices: List<HostelService>,
    val reservations: String? = null
)

@Serializable
data class HostelService(
    val id: String,
    val hostel: String? = null,
    val service: ServiceInfo
)

@Serializable
data class ServiceInfo(
    val id: String,
    val price: Double,
    val type: String
)

// User reservations response with pagination
@Serializable
data class UserReservationsResponse(
    val data: List<ReservationData>,
    val total: Int,
    val page: Int,
    val limit: Int,
    val hasMore: Boolean
)

// User reservations with active and previous
@Serializable
data class UserReservationsWithActiveResponse(
    val activeReservation: ReservationData?,
    val previousReservations: List<ReservationData>
)

// API response model that matches the actual API structure
@Serializable
data class ApiReservationResponse(
    val activeReservation: ApiReservationData?,
    val previousReservations: List<ApiReservationData>
)

@Serializable
data class ApiReservationData(
    val id: String,
    val hostelName: String,
    val startDate: String,
    val endDate: String,
    val state: String
)

// Detailed reservation response models
@Serializable
data class DetailedReservationResponse(
    val id: String,
    val user: DetailedUserData,
    val hostel: DetailedHostelData,
    val startDate: String,
    val endDate: String,
    val state: String,
    val personReservations: List<PersonReservationData>,
    val serviceReservations: List<NewServiceReservationResponse>
)

@Serializable
data class DetailedUserData(
    val id: String,
    val firstName: String,
    val lastName: String,
    val phoneNumber: String
)

@Serializable
data class DetailedHostelData(
    val id: String,
    val name: String,
    val description: String,
    val price: Double,
    val maxCapacity: Int,
    val locationUrl: String,
    val imageUrls: List<String>,
    val hostelServices: String?,
    val reservations: String?
)

@Serializable
data class PersonReservationData(
    val id: String,
    val person: PersonData,
    val reservation: String?
)

@Serializable
data class PersonData(
    val id: String,
    val user: String?,
    val firstName: String,
    val lastName: String,
    val birthDate: String,
    val alergies: List<String>,
    val discapacities: List<String>,
    val medicines: List<String>
)


// Legacy service reservation models (deprecated - use NewServiceReservationRequest/Response instead)
@Serializable
data class LegacyServiceReservationRequest(
    val serviceId: String,
    val quantity: Int,
    val date: String,
    val timeSlot: String? = null
)

@Serializable
data class LegacyServiceReservationResponse(
    val id: String,
    val message: String,
    val status: String,
    val reservation: LegacyServiceReservationData
)

@Serializable
data class LegacyServiceReservationData(
    val id: String,
    val reservation: String?,
    val service: LegacyServiceReservationService,
    val orderDate: String,
    val costCount: Int,
    val state: String,
    val externalReservationId: String
)

@Serializable
data class LegacyServiceReservationService(
    val id: String,
    val price: Double,
    val type: String
)

@Serializable
data class ServiceAvailabilityResponse(
    val service: ServiceData,
    val availableSlots: List<TimeSlot>,
    val maxQuantity: Int
)

@Serializable
data class TimeSlot(
    val id: String,
    val time: String,
    val available: Boolean,
    val maxQuantity: Int
)

// Transportation models
@Serializable
data class TransportationRequest(
    val orderDate: String,
    val count: Int,
    val hostelName: String,
    val place: String,
    val pickupTime: String,
    val fromHostel: Boolean
)

@Serializable
data class TransportationResponse(
    val id: String,
    val orderDate: String,
    val count: Int,
    val hostelName: String,
    val place: String,
    val fromHostel: Boolean,
    val pickupTime: String
)

// Person models for API integration
@Serializable
data class PersonRequest(
    val firstName: String,
    val userId: String,
    val lastName: String,
    val birthDate: String,
    val alergies: Array<String>,
    val discapacities: Array<String>,
    val medicines: Array<String>
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as PersonRequest

        if (userId != other.userId) return false
        if (firstName != other.firstName) return false
        if (lastName != other.lastName) return false
        if (birthDate != other.birthDate) return false
        if (!alergies.contentEquals(other.alergies)) return false
        if (!discapacities.contentEquals(other.discapacities)) return false
        if (!medicines.contentEquals(other.medicines)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = userId.hashCode()
        result = 31 * result + firstName.hashCode()
        result = 31 * result + lastName.hashCode()
        result = 31 * result + birthDate.hashCode()
        result = 31 * result + alergies.contentHashCode()
        result = 31 * result + discapacities.contentHashCode()
        result = 31 * result + medicines.contentHashCode()
        return result
    }
}

@Serializable
data class PersonResponse(
    val id: String,
    val user: PersonUser,
    val firstName: String,
    val lastName: String,
    val birthDate: String,
    val alergies: Array<String>,
    val discapacities: Array<String>,
    val medicines: Array<String>
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as PersonResponse

        if (id != other.id) return false
        if (user != other.user) return false
        if (firstName != other.firstName) return false
        if (lastName != other.lastName) return false
        if (birthDate != other.birthDate) return false
        if (!alergies.contentEquals(other.alergies)) return false
        if (!discapacities.contentEquals(other.discapacities)) return false
        if (!medicines.contentEquals(other.medicines)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + user.hashCode()
        result = 31 * result + firstName.hashCode()
        result = 31 * result + lastName.hashCode()
        result = 31 * result + birthDate.hashCode()
        result = 31 * result + alergies.contentHashCode()
        result = 31 * result + discapacities.contentHashCode()
        result = 31 * result + medicines.contentHashCode()
        return result
    }
}

@Serializable
data class PersonUser(
    val id: String,
    val firstName: String,
    val lastName: String,
    val phoneNumber: String
)

// New service reservation models
@Serializable
data class ServiceListItem(
    val id: String,
    val price: Double,
    val type: String
)

@Serializable
data class NewServiceReservationRequest(
    val reservationId: String,
    val serviceName: String,
    val orderDate: String,
    val count: Int
)

@Serializable
data class NewServiceReservationResponse(
    val id: String,
    val reservation: NewServiceReservationData?,
    val service: ServiceListItem,
    val orderDate: String,
    val costCount: Int,
    val state: String,
    val externalReservationId: String
)

@Serializable
data class NewServiceReservationData(
    val id: String,
    val user: String?,
    val hostel: String?,
    val startDate: String,
    val endDate: String,
    val state: String,
    val personReservations: String?,
    val serviceReservations: String?
)

// New Reservation API models
@Serializable
data class NewReservationRequest(
    val userId: String,
    val hostelId: String,
    val startDate: String,
    val endDate: String,
    val personIds: Array<String>
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as NewReservationRequest

        if (userId != other.userId) return false
        if (hostelId != other.hostelId) return false
        if (startDate != other.startDate) return false
        if (endDate != other.endDate) return false
        if (!personIds.contentEquals(other.personIds)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = userId.hashCode()
        result = 31 * result + hostelId.hashCode()
        result = 31 * result + startDate.hashCode()
        result = 31 * result + endDate.hashCode()
        result = 31 * result + personIds.contentHashCode()
        return result
    }
}

@Serializable
data class NewReservationResponse(
    val id: String,
    val user: ReservationUser,
    val hostel: ReservationHostel,
    val startDate: String,
    val endDate: String,
    val state: String,
    val personReservations: List<PersonReservation>? = null,
    val serviceReservations: String? = null
)

@Serializable
data class ReservationUser(
    val id: String,
    val firstName: String,
    val lastName: String,
    val phoneNumber: String
)

@Serializable
data class ReservationHostel(
    val id: String,
    val name: String,
    val description: String,
    val price: Double,
    val maxCapacity: Int,
    val locationUrl: String,
    val imageUrls: List<String>,
    val hostelServices: String? = null,
    val reservations: String? = null
)

@Serializable
data class PersonReservation(
    val id: String,
    val person: ReservationPerson,
    val reservation: String? = null
)

@Serializable
data class ReservationPerson(
    val id: String,
    val user: String? = null,
    val firstName: String,
    val lastName: String,
    val birthDate: String,
    val alergies: List<String>,
    val discapacities: List<String>,
    val medicines: List<String>
)

// New models for the user reservations endpoint
@Serializable
data class UserReservationResponse(
    val reservation: UserReservationData
)

@Serializable
data class UserReservationData(
    val id: String,
    val user: UserReservationUser,
    val hostel: UserReservationHostel,
    val startDate: String,
    val endDate: String,
    val state: String,
    val personReservations: List<UserPersonReservation>,
    val serviceReservations: List<UserServiceReservation>
)

@Serializable
data class UserServiceReservation(
    val id: String,
    val reservation: String?,
    val service: UserServiceData,
    val orderDate: String,
    val costCount: Int,
    val state: String,
    val externalReservationId: String
)

@Serializable
data class UserServiceData(
    val id: String,
    val price: Double,
    val type: String
)

@Serializable
data class UserReservationUser(
    val id: String,
    val firstName: String,
    val lastName: String,
    val phoneNumber: String
)

@Serializable
data class UserReservationHostel(
    val id: String,
    val name: String,
    val description: String,
    val price: Double,
    val maxCapacity: Int,
    val locationUrl: String,
    val imageUrls: List<String>,
    val hostelServices: String?,
    val reservations: String?
)

@Serializable
data class UserPersonReservation(
    val id: String,
    val person: UserPersonData,
    val reservation: String?
)

@Serializable
data class UserPersonData(
    val id: String,
    val user: String?,
    val firstName: String,
    val lastName: String,
    val birthDate: String,
    val alergies: List<String>,
    val discapacities: List<String>,
    val medicines: List<String>
)

// Repeat reservation request model
@Serializable
data class RepeatReservationRequest(
    val reservationId: String,
    val startDate: String,
    val endDate: String
)
