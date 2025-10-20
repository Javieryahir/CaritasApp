package com.example.caritasapp.reservations

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AssistChip
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.caritasapp.data.ApiService
import com.example.caritasapp.data.NetworkModule
import com.example.caritasapp.data.ReservationData
import com.example.caritasapp.data.UserReservationsWithActiveResponse
import com.example.caritasapp.data.ApiReservationResponse
import com.example.caritasapp.data.ApiReservationData
import com.example.caritasapp.data.DetailedReservationResponse
import com.example.caritasapp.navegationbar.AppBottomBar
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private val Teal = Color(0xFF5D97A3)
private val Accent = Color(0xFF009CA6)
private val LightAccent = Color(0xFFE0F7F8)
private val Success = Color(0xFF4CAF50)
private val Warning = Color(0xFFFF9800)
private val Error = Color(0xFFF44336)
private val Surface = Color.White

// Conversion functions
private fun convertApiResponseToUiModel(apiResponse: ApiReservationResponse): UserReservationsWithActiveResponse {
    return UserReservationsWithActiveResponse(
        activeReservation = apiResponse.activeReservation?.let { convertApiReservationToReservationData(it) },
        previousReservations = apiResponse.previousReservations
            .map { convertApiReservationToReservationData(it) }
            .sortedByDescending { it.startDate }
    )
}

private fun convertApiReservationToReservationData(apiReservation: ApiReservationData): ReservationData {
    return ReservationData(
        id = apiReservation.id,
        shelterId = "", // Not provided by API
        shelterName = apiReservation.hostelName,
        startDate = apiReservation.startDate,
        endDate = apiReservation.endDate,
        peopleCount = 1, // Default value since not provided by API
        status = apiReservation.state.lowercase(),
        createdAt = "", // Not provided by API
        guestInfo = emptyList(), // Not provided by API
        selectedServices = emptyList() // Not provided by API
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReservationPage(navController: NavController) {
    val context = LocalContext.current
    val sessionManager = remember { NetworkModule.createSessionManager(context) }
    val apiService = remember { NetworkModule.createAuthenticatedApiService(sessionManager) }
    val scope = rememberCoroutineScope()
    
    // Bottom sheet state
    var showBottomSheet by remember { mutableStateOf(false) }
    var selectedReservation by remember { mutableStateOf<DetailedReservationResponse?>(null) }
    
    var reservations by remember { mutableStateOf<UserReservationsWithActiveResponse?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }
    
    // Refresh function
    val refreshReservations: () -> Unit = {
        scope.launch {
            try {
                isLoading = true
                error = null
                val user = sessionManager.getUser()
                if (user != null) {
                    val apiResponse = apiService.getUserReservationsHistory(user.id, 5, 1)
                    reservations = convertApiResponseToUiModel(apiResponse)
                } else {
                    error = "Usuario no encontrado"
                }
            } catch (e: Exception) {
                error = e.message ?: "Error loading reservations"
            } finally {
                isLoading = false
            }
        }
    }
    
    // Function to handle reservation card click
    val onReservationClick: (String) -> Unit = { reservationId ->
        scope.launch {
            try {
                val detailedReservation = apiService.getDetailedReservation(reservationId)
                selectedReservation = detailedReservation
                showBottomSheet = true
            } catch (e: Exception) {
                error = "Error loading reservation details: ${e.message}"
            }
        }
    }
    
    // Load reservations
    LaunchedEffect(Unit) {
        try {
            isLoading = true
            error = null
            val user = sessionManager.getUser()
            if (user != null) {
                val apiResponse = apiService.getUserReservationsHistory(user.id, 5, 1)
                reservations = convertApiResponseToUiModel(apiResponse)
            } else {
                error = "Usuario no encontrado"
            }
        } catch (e: Exception) {
            error = e.message ?: "Error loading reservations"
        } finally {
            isLoading = false
        }
    }
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Surface)
    ) {
            Column(
                    modifier = Modifier
                        .fillMaxSize()
                    .statusBarsPadding()
            ) {
                // Header
                HeaderSection(
                    onRefresh = refreshReservations
                )
                
                // Content
                when {
                    isLoading -> {
                        LoadingState()
                    }
                    error != null -> {
                        ErrorState(
                            error = error!!,
                            onRetry = {
                                // Retry logic
                            }
                        )
                    }
                    reservations?.activeReservation == null && reservations?.previousReservations?.isEmpty() == true -> {
                        EmptyState(navController)
                    }
                    else -> {
                        ReservationsContent(
                            reservations = reservations!!,
                            navController = navController,
                            onReservationClick = onReservationClick
                        )
                    }
                }
            }
            
        
        // Bottom Navigation
        AppBottomBar(
            navController = navController,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(start = 12.dp, end = 12.dp, bottom = 20.dp)
        )
    }
    
    // Show bottom sheet when needed
    if (showBottomSheet && selectedReservation != null) {
        androidx.compose.material3.ModalBottomSheet(
            onDismissRequest = {
                showBottomSheet = false
                selectedReservation = null
            }
        ) {
            ReservationDetailsBottomSheet(
                reservation = selectedReservation!!,
                onDismiss = {
                    showBottomSheet = false
                    selectedReservation = null
                },
                onContactEmail = {
                    // Handle email contact
                },
                onContactPhone = {
                    // Handle phone contact
                }
            )
        }
    }
}

// Header Section
@Composable
private fun HeaderSection(onRefresh: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Mis Reservaciones",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            
            IconButton(
                onClick = onRefresh,
                modifier = Modifier
                    .background(Accent, CircleShape)
                    .size(40.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.Refresh,
                    contentDescription = "Actualizar",
                    modifier = Modifier.size(20.dp),
                    tint = Color.White
                )
            }
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = "Gestiona tus alojamientos y reservaciones",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Gray
        )
    }
}

// Loading State
@Composable
private fun LoadingState() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CircularProgressIndicator(
                color = Accent,
                modifier = Modifier.size(48.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Cargando reservaciones...",
                style = MaterialTheme.typography.bodyLarge,
                color = Color.Gray
            )
        }
    }
}

// Error State
@Composable
private fun ErrorState(error: String, onRetry: () -> Unit) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(32.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.History,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = Error
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Error al cargar",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            Spacer(modifier = Modifier.height(8.dp))
                Text(
                text = error,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(24.dp))
            TextButton(
                onClick = onRetry,
                colors = androidx.compose.material3.ButtonDefaults.textButtonColors(
                    contentColor = Accent
                )
            ) {
                Text("Intentar de nuevo")
            }
        }
    }
}

// Empty State
@Composable
private fun EmptyState(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .padding(horizontal = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            Icons.Filled.CalendarMonth,
            contentDescription = null,
            modifier = Modifier.size(120.dp),
            tint = Color.Gray
        )
        
        Spacer(Modifier.height(24.dp))
        
        Text(
            "No tienes reservaciones",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            color = Color.Gray
        )
        
        Spacer(Modifier.height(16.dp))
        
        Text(
            "Reserva tu alojamiento y comienza tu viaje",
            fontSize = 16.sp,
            textAlign = TextAlign.Center,
            color = Color.Gray,
            lineHeight = 24.sp
        )
        
        Spacer(Modifier.height(32.dp))
        
        androidx.compose.material3.Button(
            onClick = { navController.navigate("search") },
            shape = RoundedCornerShape(16.dp),
            colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                containerColor = Accent
            ),
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
        ) {
            Icon(Icons.Filled.Search, contentDescription = null)
            Spacer(Modifier.width(8.dp))
            Text("Buscar Albergues", fontSize = 18.sp)
        }
    }
}

// Reservations Content
@Composable
private fun ReservationsContent(
    reservations: UserReservationsWithActiveResponse,
    navController: NavController,
    onReservationClick: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Active Reservation
        reservations.activeReservation?.let { active ->
            item {
                Text(
                    text = "Reservación Activa",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }
            item {
                ReservationCard(
                    reservation = active,
                    isActive = true,
                    onAction = { onReservationClick(active.id) },
                    navController = navController
                )
            }
        }
        
        // Previous Reservations
        if (reservations.previousReservations.isNotEmpty()) {
            item {
                Text(
                    text = "Historial",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
                )
            }
            items(reservations.previousReservations) { reservation ->
                ReservationCard(
                    reservation = reservation,
                    isActive = false,
                    onAction = { onReservationClick(reservation.id) },
                    navController = navController
                )
            }
        }
    }
}

// Reservation Card
@Composable
private fun ReservationCard(
    reservation: ReservationData,
    isActive: Boolean,
    onAction: () -> Unit,
    navController: NavController
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onAction() },
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            // Header with status
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Filled.LocationOn,
                        contentDescription = null,
                        tint = Accent,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = reservation.shelterName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                
                // Debug: Print the status to see what values we're getting
                println("🔍 ReservationPage - Reservation status: '${reservation.status}' for reservation: ${reservation.id}")
                
                if (reservation.status == "completed" || reservation.status == "COMPLETED" || reservation.status == "finished" || reservation.status == "FINISHED" || reservation.status == "inactive" || reservation.status == "INACTIVE") {
                    // Show "Reagendar" button for completed reservations
                    androidx.compose.material3.Button(
                        onClick = { 
                            // Navigate to reschedule screen
                            navController.navigate("reschedule/${reservation.id}")
                        },
                        colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                            containerColor = Accent
                        ),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = "Reagendar",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White,
                            fontWeight = FontWeight.Medium
                        )
                    }
                } else {
                    StatusChip(
                        status = reservation.status,
                        isActive = isActive
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Date and people info
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                InfoItem(
                    icon = Icons.Filled.CalendarMonth,
                    label = "Fechas",
                    value = formatDateRange(reservation.startDate, reservation.endDate)
                )
                InfoItem(
                    icon = Icons.Filled.Person,
                    label = "Personas",
                    value = "${reservation.peopleCount}"
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Services
            if (reservation.selectedServices.isNotEmpty()) {
                Text(
                    text = "Servicios incluidos:",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
            Text(
                    text = reservation.selectedServices.joinToString(", "),
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Black
                )
                Spacer(modifier = Modifier.height(16.dp))
            }
            
            // Action button
            if (isActive) {
                AssistChip(
                    onClick = onAction,
                    label = {
                        Text("Ver Detalles")
                    },
                    colors = AssistChipDefaults.assistChipColors(
                        containerColor = Accent,
                        labelColor = Color.White
                    ),
                    shape = RoundedCornerShape(8.dp)
                )
            }
        }
    }
}

// Status Chip
@Composable
private fun StatusChip(status: String, isActive: Boolean) {
    val (backgroundColor, textColor) = when {
        isActive -> Success to Color.White
        status == "completed" -> Color.Gray to Color.White
        status == "cancelled" -> Error to Color.White
        else -> Warning to Color.White
    }
    
    Surface(
        color = backgroundColor,
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = if (isActive) Icons.Filled.CheckCircle else Icons.Filled.History,
                contentDescription = null,
                modifier = Modifier.size(16.dp),
                tint = textColor
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = if (isActive) "Activa" else status.replaceFirstChar { it.uppercase() },
                style = MaterialTheme.typography.labelSmall,
                color = textColor,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

// Info Item
@Composable
private fun InfoItem(
    icon: ImageVector,
    label: String,
    value: String
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(16.dp),
            tint = Color.Gray
        )
        Spacer(modifier = Modifier.width(6.dp))
                        Column {
                            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = Color.Gray
            )
                            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                color = Color.Black
            )
        }
    }
}

