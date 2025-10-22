package com.example.caritasapp.reservations

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Article
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.caritasapp.data.*
import com.example.caritasapp.navegationbar.AppBottomBar
import java.text.SimpleDateFormat
import java.util.*

private val Accent = Color(0xFF009CA6)
private val LightAccent = Color(0xFFE0F7F8)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ServiceReservationScreen(navController: NavController) {
    val context = LocalContext.current
    val reservationRepository = remember { NetworkModule.createReservationRepository(context) }
    val sessionManager = remember { NetworkModule.createSessionManager(context) }
    
    // State for checking active reservations
    var isLoading by remember { mutableStateOf(true) }
    var hasActiveReservation by remember { mutableStateOf(false) }
    var activeReservation by remember { mutableStateOf<UserReservationData?>(null) }
    var availableServices by remember { mutableStateOf<List<ServiceListItem>>(emptyList()) }
    var serviceReservations by remember { mutableStateOf<List<UserServiceReservation>>(emptyList()) }
    var hostelServices by remember { mutableStateOf<List<HostelService>>(emptyList()) }
    
    // Get current user ID from session
    val currentUser = sessionManager.getUser()
    
    // Check for active reservations when screen loads
    LaunchedEffect(Unit) {
        if (currentUser != null) {
            try {
                // Check for active reservation
                reservationRepository.getUserReservation(currentUser.id).collect { response ->
                    isLoading = false
                    if (response != null && (response.reservation.state == "ACTIVE" || response.reservation.state == "PENDING")) {
                        hasActiveReservation = true
                        activeReservation = response.reservation
                        
                        // Extract service reservations from the active reservation
                        serviceReservations = response.reservation.serviceReservations
                            .filter { it.state == "PENDING" || it.state == "ACTIVE" }
                        
                        // Load hostel services based on the active reservation's hostel ID
                        val hostelId = response.reservation.hostel.id
                        println("🔍 ServiceReservationScreen - Fetching services for hostel ID: $hostelId")
                        
                        reservationRepository.getHostelServices(hostelId).collect { hostelData ->
                            hostelData?.let { hostel ->
                                hostelServices = hostel.hostelServices ?: emptyList()
                                println("🔍 ServiceReservationScreen - Loaded ${hostelServices.size} hostel services")
                                hostelServices.forEach { hostelService ->
                                    println("  - Service ID: ${hostelService.service.id}, Type: ${hostelService.service.type}, Price: $${hostelService.service.price}")
                                }
                                
                                // Convert hostel services to ServiceListItem format for compatibility
                                availableServices = hostelServices.map { hostelService ->
                                    ServiceListItem(
                                        id = hostelService.service.id,
                                        type = hostelService.service.type,
                                        price = hostelService.service.price
                                    )
                                }
                                println("🔍 ServiceReservationScreen - Converted to ${availableServices.size} available services")
                            } ?: run {
                                println("🔍 ServiceReservationScreen - No hostel data received for hostel ID: $hostelId")
                            }
                        }
                    } else {
                        hasActiveReservation = false
                        activeReservation = null
                        serviceReservations = emptyList()
                    }
                }
        } catch (_: Exception) {
                isLoading = false
                hasActiveReservation = false
            }
        } else {
            isLoading = false
            hasActiveReservation = false
        }
    }
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
        ) {
            // Header Section
            HeaderSection()
            
            // Content
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = Color.White
            ) {
                when {
                    isLoading -> LoadingView()
                    hasActiveReservation && activeReservation != null -> {
                        activeReservation?.let { reservation ->
                            ServiceListView(
                                services = availableServices,
                                serviceReservations = serviceReservations,
                                navController = navController
                            )
                        }
                    }
                    else -> NoActiveReservationsView(navController)
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
}

// Header Section
@Composable
private fun HeaderSection() {
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
                text = "Servicios",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = "Reserva servicios adicionales para tu estadía",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Gray
        )
    }
}

@Composable
private fun LoadingView() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        CircularProgressIndicator(
            color = Accent,
            modifier = Modifier.size(48.dp)
        )
        Spacer(Modifier.height(16.dp))
        Text(
            "Cargando servicios...",
            fontSize = 18.sp,
            color = Color.Gray,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun NoActiveReservationsView(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            Icons.Filled.Hotel,
            contentDescription = null,
            modifier = Modifier.size(120.dp),
            tint = Color.Gray
        )
        
        Spacer(Modifier.height(24.dp))
        
        Text(
            "No tienes reservaciones activas",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            color = Color.Gray
        )
        
        Spacer(Modifier.height(16.dp))
        
        Text(
            "Para reservar servicios, primero necesitas tener una reservación activa en un albergue.",
            fontSize = 16.sp,
            textAlign = TextAlign.Center,
            color = Color.Gray,
            lineHeight = 24.sp
        )
        
        Spacer(Modifier.height(32.dp))
        
        Button(
            onClick = { navController.navigate("search") },
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Accent),
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

@Composable
private fun ServiceListView(
    services: List<ServiceListItem>,
    serviceReservations: List<UserServiceReservation>,
    navController: NavController
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp)
            .padding(bottom = 20.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        item {
            Spacer(Modifier.height(16.dp))
        }
        
        // Active Services Section
        if (serviceReservations.isNotEmpty()) {
            item {
                Text(
                    text = "Servicios Reservados",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    modifier = Modifier.padding(bottom = 12.dp)
                )
            }
            
            items(serviceReservations) { serviceReservation ->
                ActiveServiceCard(
                    serviceReservation = serviceReservation,
                    onClick = { 
                        // Store service reservation data in navigation state
                        println("🔍 ServiceReservationScreen - Setting navigation data for existing service:")
                        println("  service_id: ${serviceReservation.id}")
                        println("  service_name: ${getServiceDisplayName(serviceReservation.service.type)}")
                        println("  service_type: ${serviceReservation.service.type}")
                        println("  people_count: ${serviceReservation.costCount}")
                        println("  order_date: ${serviceReservation.orderDate}")
                        println("  service_state: ${serviceReservation.state}")
                        println("  qr_code: ${serviceReservation.id}")
                        
                        // Navigate to confirmation screen
                        navController.navigate("service_confirmation")
                        
                        // Set the data in the destination's savedStateHandle after navigation
                        navController.currentBackStackEntry?.savedStateHandle?.apply {
                            set("service_id", serviceReservation.id)
                            set("service_name", getServiceDisplayName(serviceReservation.service.type))
                            set("service_type", serviceReservation.service.type)
                            set("people_count", serviceReservation.costCount)
                            set("order_date", serviceReservation.orderDate)
                            set("service_state", serviceReservation.state)
                            set("qr_code", serviceReservation.id) // Use service reservation ID as QR code data
                        }
                    }
                )
            }
        }
        
        // Available Services Section
        item {
            Spacer(Modifier.height(16.dp))
            Text(
                text = "Servicios Disponibles",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                modifier = Modifier.padding(bottom = 12.dp)
            )
        }
        
        val availableServices = services.filter { service ->
            // Filter out services that have already been reserved
            serviceReservations.none { reservation -> 
                reservation.service.id == service.id 
            }
        }
        
        println("🔍 ServiceListView - Available services count: ${availableServices.size}")
        println("🔍 ServiceListView - Service reservations count: ${serviceReservations.size}")
        
        if (availableServices.isNotEmpty()) {
            items(availableServices) { service ->
                ServiceCard(
                    service = service,
                    onClick = { 
                        navController.navigate("service_details/${service.id}")
                    }
                )
            }
        } else {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = LightAccent),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Filled.CheckCircle,
                            contentDescription = null,
                            modifier = Modifier.size(48.dp),
                            tint = Accent
                        )
                        Spacer(Modifier.height(16.dp))
                        Text(
                            text = "¡Todos los servicios reservados!",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black,
                            textAlign = TextAlign.Center
                        )
                        Spacer(Modifier.height(8.dp))
                        Text(
                            text = "Has reservado todos los servicios disponibles.",
                            fontSize = 14.sp,
                            color = Color.Gray,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ActiveServiceCard(
    serviceReservation: UserServiceReservation,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp),
        colors = CardDefaults.cardColors(containerColor = LightAccent),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Icon(
                    imageVector = getServiceIcon(serviceReservation.service.type),
                    contentDescription = null,
                    modifier = Modifier.size(32.dp),
                    tint = Accent
                )
                
                Column {
                    Text(
                        text = getServiceDisplayName(serviceReservation.service.type),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                    Text(
                        text = formatServiceDate(serviceReservation.orderDate),
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                    Text(
                        text = "Para ${serviceReservation.costCount} persona${if (serviceReservation.costCount > 1) "s" else ""}",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }
            }
            
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Surface(
                    color = getServiceStateColor(serviceReservation.state),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = getServiceStateBadge(serviceReservation.state),
                        color = Color.White,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
                
                Icon(
                    imageVector = Icons.Filled.QrCode,
                    contentDescription = "Ver QR",
                    modifier = Modifier.size(20.dp),
                    tint = Accent
                )
            }
        }
    }
}

@Composable
private fun ServiceCard(
    service: ServiceListItem,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Icon(
                    imageVector = getServiceIcon(service.type),
                    contentDescription = null,
                    modifier = Modifier.size(32.dp),
                    tint = Accent
                )
                
                Column {
                    Text(
                        text = getServiceDisplayName(service.type),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                    Text(
                        text = getServiceDescription(service.type),
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }
            }
            
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (service.price > 0) {
                    Text(
                        text = "$${service.price.toInt()}",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Accent
                    )
                }
                
                Button(
                    onClick = onClick,
                    colors = ButtonDefaults.buttonColors(containerColor = Accent),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.CalendarMonth,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(Modifier.width(4.dp))
                    Text("Reservar", fontSize = 12.sp)
                }
            }
        }
    }
}

// Helper functions
private fun getServiceIcon(type: String): ImageVector {
    return when (type) {
        "breakfasts" -> Icons.Filled.Restaurant
        "meals" -> Icons.Filled.Restaurant
        "dinners" -> Icons.Filled.Restaurant
        "laundries" -> Icons.Filled.LocalLaundryService
        "baths" -> Icons.Filled.Shower
        "transportations" -> Icons.Filled.LocalTaxi
        "mentals" -> Icons.Filled.Psychology
        "dentals" -> Icons.Filled.LocalHospital
        "documents" -> Icons.AutoMirrored.Filled.Article
        else -> Icons.Filled.Info
    }
}

private fun getServiceDisplayName(type: String): String {
    return when (type) {
        "breakfasts" -> "Desayuno"
        "meals" -> "Comida"
        "dinners" -> "Cena"
        "laundries" -> "Lavadoras"
        "baths" -> "Duchas"
        "transportations" -> "Transporte"
        "mentals" -> "Psicólogo"
        "dentals" -> "Dental"
        "documents" -> "Expedición de Oficios"
        else -> type
    }
}

private fun getServiceDescription(type: String): String {
    return when (type) {
        "breakfasts" -> "Desayuno"
        "meals" -> "Comida"
        "dinners" -> "Cena"
        "laundries" -> "Lavadoras"
        "baths" -> "Regaderas"
        "transportations" -> "Transporte"
        "mentals" -> "Psicólogo"
        "dentals" -> "Chequeo Dental"
        "documents" -> "Expedición de Oficios"
        else -> ""
    }
}

private fun getServiceStateBadge(state: String): String {
    return when (state) {
        "PENDING" -> "PENDIENTE"
        "ACTIVE" -> "ACTIVA"
        "COMPLETED" -> "COMPLETADA"
        "CANCELLED" -> "CANCELADA"
        else -> "DESCONOCIDO"
    }
}

private fun getServiceStateColor(state: String): Color {
    return when (state) {
        "PENDING" -> Color(0xFFFF9800) // Orange
        "ACTIVE" -> Color(0xFF4CAF50) // Green
        "COMPLETED" -> Color(0xFF2196F3) // Blue
        "CANCELLED" -> Color(0xFFF44336) // Red
        else -> Color.Gray
    }
}

private fun formatServiceDate(dateString: String): String {
    return try {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val outputFormat = SimpleDateFormat("MMM dd yyyy", Locale.getDefault())
        val date = inputFormat.parse(dateString)
        outputFormat.format(date ?: Date())
    } catch (_: Exception) {
        dateString
    }
}