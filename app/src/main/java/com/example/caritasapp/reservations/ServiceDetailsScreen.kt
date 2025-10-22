package com.example.caritasapp.reservations

import android.app.DatePickerDialog
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Article
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.caritasapp.data.*
import com.example.caritasapp.navegationbar.AppBottomBar
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

private val Accent = Color(0xFF009CA6)
private val Teal = Color(0xFF5D97A3)
private val LightAccent = Color(0xFFE0F7F8)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ServiceDetailsScreen(serviceId: String, navController: NavController) {
    val context = LocalContext.current
    val reservationRepository = remember { NetworkModule.createReservationRepository(context) }
    val sessionManager = remember { NetworkModule.createSessionManager(context) }
    val scope = rememberCoroutineScope()
    
    // State
    var service by remember { mutableStateOf<ServiceListItem?>(null) }
    var activeReservation by remember { mutableStateOf<UserReservationData?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var peopleCount by remember { mutableIntStateOf(1) }
    var selectedDate by remember { mutableStateOf("") }
    var isSubmitting by remember { mutableStateOf(false) }
    var showErrorDialog by remember { mutableStateOf(false) }
    var hostelServices by remember { mutableStateOf<List<HostelService>>(emptyList()) }
    
    // Load service and active reservation
    LaunchedEffect(serviceId) {
        try {
            isLoading = true
            val currentUser = sessionManager.getUser()
            
            if (currentUser != null) {
                // Get active reservation
                reservationRepository.getUserReservation(currentUser.id).collect { response ->
                    if (response != null && response.reservation.state == "ACTIVE") {
                        activeReservation = response.reservation
                        
                        // Find the service in the existing service reservations
                        val foundServiceReservation = response.reservation.serviceReservations.find { serviceReservation ->
                            serviceReservation.service.id == serviceId
                        }
                        
                        if (foundServiceReservation != null) {
                            // Use the service data from the existing reservation
                            service = ServiceListItem(
                                id = foundServiceReservation.service.id,
                                type = foundServiceReservation.service.type,
                                price = foundServiceReservation.service.price
                            )
                            println("🔍 ServiceDetailsScreen - Found existing service: ${service?.type} ($${service?.price})")
                            println("🔍 ServiceDetailsScreen - Service display name: ${getServiceDisplayName(service!!.type)}")
                        } else {
                            // If not found in existing reservations, try to fetch from hostel services
                            val hostelId = response.reservation.hostel.id
                            println("🔍 ServiceDetailsScreen - Service not found in existing reservations, fetching from hostel ID: $hostelId")
                            
                            reservationRepository.getHostelServices(hostelId).collect { hostelData ->
                                hostelData?.let { hostel ->
                                    hostelServices = hostel.hostelServices ?: emptyList()
                                    println("🔍 ServiceDetailsScreen - Loaded ${hostelServices.size} hostel services")
                                    
                                    // Find the service with matching ID
                                    val foundService = hostelServices.find { hostelService ->
                                        println("🔍 Comparing: ${hostelService.service.id} == $serviceId")
                                        hostelService.service.id == serviceId
                                    }
                                    
                                    if (foundService != null) {
                                        // Convert HostelService to ServiceListItem
                                        service = ServiceListItem(
                                            id = foundService.service.id,
                                            type = foundService.service.type,
                                            price = foundService.service.price
                                        )
                                        println("🔍 ServiceDetailsScreen - Found service from hostel: ${service?.type} ($${service?.price})")
                                        println("🔍 ServiceDetailsScreen - Service display name: ${getServiceDisplayName(service!!.type)}")
                                    } else {
                                        println("🔍 ServiceDetailsScreen - Service not found with ID: $serviceId")
                                        println("🔍 ServiceDetailsScreen - Available service IDs: ${hostelServices.map { it.service.id }}")
                                    }
                                } ?: run {
                                    println("🔍 ServiceDetailsScreen - No hostel data received")
                                }
                                isLoading = false
                            }
                        }
                        isLoading = false
                    } else {
                        isLoading = false
                    }
                }
            } else {
                isLoading = false
            }
        } catch (e: Exception) {
            println("🔍 ServiceDetailsScreen - Error loading service: ${e.message}")
            isLoading = false
        }
    }
    
    // Date picker function
    fun pickDate() {
        if (activeReservation != null) {
            val calendar = Calendar.getInstance()
            val datePickerDialog = DatePickerDialog(
                context,
                { _, year, month, dayOfMonth ->
                    val selectedCalendar = Calendar.getInstance().apply {
                        set(year, month, dayOfMonth)
                    }
                    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                    selectedDate = dateFormat.format(selectedCalendar.time)
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            )
            
            // Set date constraints based on active reservation
            try {
                val startDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                val endDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                
                val startDate = startDateFormat.parse(activeReservation!!.startDate)
                val endDate = endDateFormat.parse(activeReservation!!.endDate)
                
                // Set minimum date to reservation start date
                datePickerDialog.datePicker.minDate = startDate!!.time
                // Set maximum date to reservation end date
                datePickerDialog.datePicker.maxDate = endDate!!.time
            } catch (e: Exception) {
                println("🔍 ServiceDetailsScreen - Error parsing dates: ${e.message}")
            }
            
            datePickerDialog.show()
        }
    }
    
    // Handle service reservation
    LaunchedEffect(isSubmitting) {
        if (isSubmitting && service != null && activeReservation != null && selectedDate.isNotEmpty()) {
            try {
                val request = NewServiceReservationRequest(
                    reservationId = activeReservation!!.id,
                    serviceName = service!!.type,
                    orderDate = selectedDate,
                    count = peopleCount
                )
                
                reservationRepository.createServiceReservation(request).collect { response ->
                    isSubmitting = false
                    if (response != null) {
                        // Store service reservation data in navigation state
                        println("🔍 ServiceDetailsScreen - Setting navigation data for new service:")
                        println("  service_id: ${response.id}")
                        println("  service_name: ${getServiceDisplayName(service!!.type)}")
                        println("  service_type: ${service!!.type}")
                        println("  people_count: $peopleCount")
                        println("  order_date: $selectedDate")
                        println("  service_state: ${response.state}")
                        println("  qr_code: ${response.id}")
                        
                        // Navigate to confirmation screen
                        navController.navigate("service_confirmation")
                        
                        // Set the data in the destination's savedStateHandle after navigation
                        navController.currentBackStackEntry?.savedStateHandle?.apply {
                            set("service_id", response.id)
                            set("service_name", getServiceDisplayName(service!!.type))
                            set("service_type", service!!.type)
                            set("people_count", peopleCount)
                            set("order_date", selectedDate)
                            set("service_state", response.state)
                            set("qr_code", response.id) // Use service reservation ID as QR code data
                        }
                    } else {
                        showErrorDialog = true
                    }
                }
            } catch (e: Exception) {
                isSubmitting = false
                showErrorDialog = true
            }
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Detalles del Servicio",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver",
                            tint = Color.Black
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White,
                    titleContentColor = Color.Black
                )
            )
        },
        bottomBar = {
            AppBottomBar(
                navController = navController,
                modifier = Modifier.padding(start = 12.dp, end = 12.dp, bottom = 12.dp)
            )
        }
    ) { paddingValues ->
        when {
            isLoading -> LoadingView()
            service == null -> ErrorView(navController)
            else -> {
                ServiceDetailsContent(
                    service = service!!,
                    peopleCount = peopleCount,
                    onPeopleCountChange = { peopleCount = it },
                    selectedDate = selectedDate,
                    onDateClick = { pickDate() },
                    onSubmit = { isSubmitting = true },
                    isSubmitting = isSubmitting,
                    navController = navController,
                    paddingValues = paddingValues
                )
            }
        }
        
        // Error Dialog
        if (showErrorDialog) {
            AlertDialog(
                onDismissRequest = { showErrorDialog = false },
                title = { Text("Error") },
                text = { Text("No se pudo crear la reservación del servicio. Por favor, intenta de nuevo.") },
                confirmButton = {
                    TextButton(onClick = { showErrorDialog = false }) {
                        Text("Intentar de nuevo")
                    }
                }
            )
        }
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
            "Cargando servicio...",
            fontSize = 18.sp,
            color = Color.Gray,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun ErrorView(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
                        ) {
                            Icon(
            Icons.Filled.Error,
                                contentDescription = null,
            modifier = Modifier.size(120.dp),
            tint = Color.Gray
        )
        
        Spacer(Modifier.height(24.dp))
        
        Text(
            "Servicio no encontrado",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            color = Color.Gray
        )
        
        Spacer(Modifier.height(16.dp))
        
        Text(
            "El servicio solicitado no está disponible.",
            fontSize = 16.sp,
            textAlign = TextAlign.Center,
            color = Color.Gray,
            lineHeight = 24.sp
        )
        
        Spacer(Modifier.height(32.dp))
        
        Button(
            onClick = { navController.popBackStack() },
            colors = ButtonDefaults.buttonColors(containerColor = Accent),
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
        ) {
            Text("Volver", fontSize = 18.sp)
        }
    }
}

@Composable
private fun ServiceDetailsContent(
    service: ServiceListItem,
    peopleCount: Int,
    onPeopleCountChange: (Int) -> Unit,
    selectedDate: String,
    onDateClick: () -> Unit,
    onSubmit: () -> Unit,
    isSubmitting: Boolean,
    navController: NavController,
    paddingValues: PaddingValues
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .padding(horizontal = 16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        // Header
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = LightAccent),
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Icon with background circle
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .background(
                            Accent.copy(alpha = 0.2f),
                            CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = getServiceIcon(service.type),
                        contentDescription = null,
                        modifier = Modifier.size(40.dp),
                        tint = Accent
                    )
                }
                
                Spacer(Modifier.height(16.dp))
                
                Text(
                    text = getServiceDisplayName(service.type),
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = Accent,
                    textAlign = TextAlign.Center
                )
                
                Spacer(Modifier.height(8.dp))
                
                Text(
                    text = "Información del servicio",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.Gray,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Medium
                )
            }
        }
        
        Spacer(Modifier.height(24.dp))
        
        // Service Information
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier.padding(20.dp)
            ) {
                Text(
                    text = "Información",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                
                Text(
                    text = getServiceDescription(service.type),
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray,
                    lineHeight = 20.sp
                )
                
                if (service.price > 0) {
                    Spacer(Modifier.height(12.dp))
                    Text(
                        text = "Precio: $${service.price.toInt()}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Accent
                    )
                }
            }
        }
        
        Spacer(Modifier.height(20.dp))
        
        // Number of People Section
        Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                modifier = Modifier.padding(20.dp)
                    ) {
                        Text(
                    text = "Número de personas",
                            style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    modifier = Modifier.padding(bottom = 12.dp)
                )
                
                PeopleCounter(
                    count = peopleCount,
                    onCountChange = onPeopleCountChange,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
        
        Spacer(Modifier.height(20.dp))
        
        // Date Selection
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier.padding(20.dp)
            ) {
                Text(
                    text = "Fecha de uso",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                
                
                OutlinedButton(
                    onClick = onDateClick,
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = LightAccent,
                        contentColor = Accent
                    ),
                    border = BorderStroke(1.dp, Accent),
                    modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            imageVector = Icons.Filled.CalendarMonth,
                            contentDescription = null,
                        modifier = Modifier.size(20.dp)
                        )
                    Spacer(Modifier.width(8.dp))
                        Text(
                        text = if (selectedDate.isEmpty()) "Seleccionar fecha" else selectedDate,
                        fontSize = 16.sp
                    )
                }
            }
        }
        
        Spacer(Modifier.height(32.dp))
        
        
        // Reserve Button
        Button(
            onClick = {
                println("🔍 ServiceDetailsScreen - Reserve button clicked")
                onSubmit()
            },
            enabled = !isSubmitting && selectedDate.isNotEmpty(),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (isSubmitting) Accent.copy(alpha = 0.6f) else Accent,
                contentColor = Color.White
            ),
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
        ) {
            if (isSubmitting) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    color = Color.White,
                    strokeWidth = 2.dp
                )
                Spacer(Modifier.width(8.dp))
                Text("Reservando...", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
            } else {
                Icon(Icons.Filled.CalendarMonth, contentDescription = null, modifier = Modifier.size(20.dp))
                Spacer(Modifier.width(8.dp))
                Text("Reservación", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
            }
        }
        
        // Extra spacing to ensure button is visible
        Spacer(Modifier.height(32.dp))
    }
}

@Composable
private fun PeopleCounter(
    count: Int,
    onCountChange: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Decrease button
            IconButton(
                onClick = { if (count > 1) onCountChange(count - 1) },
                modifier = Modifier
                    .size(40.dp)
                    .background(
                        if (count > 1) Accent.copy(alpha = 0.1f) else Color.Gray.copy(alpha = 0.1f),
                        CircleShape
                    )
            ) {
                Icon(
                    Icons.Filled.Remove,
                    contentDescription = "Decrease",
                    tint = if (count > 1) Accent else Color.Gray,
                    modifier = Modifier.size(20.dp)
                )
            }

            // Count display
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = count.toString(),
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Accent
                )
                Text(
                    text = if (count == 1) "persona" else "personas",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }
            
            // Increase button
            IconButton(
                onClick = { if (count < 8) onCountChange(count + 1) },
                modifier = Modifier
                    .size(40.dp)
                    .background(
                        if (count < 8) Accent.copy(alpha = 0.1f) else Color.Gray.copy(alpha = 0.1f),
                        CircleShape
                    )
            ) {
                Icon(
                    Icons.Filled.Add,
                    contentDescription = "Increase",
                    tint = if (count < 8) Accent else Color.Gray,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

// Helper functions (same as in ServiceReservationScreen)
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
        "breakfasts" -> "Servicio de desayuno completo con opciones nutritivas y balanceadas."
        "meals" -> "Comida del día con menú variado y opciones saludables."
        "dinners" -> "Cena ligera y nutritiva para finalizar el día."
        "laundries" -> "Servicio de lavandería con detergente y secado incluido."
        "baths" -> "Acceso a regaderas con agua caliente y productos de higiene."
        "transportations" -> "Servicio de transporte seguro a ubicaciones específicas."
        "mentals" -> "Consulta psicológica profesional y apoyo emocional."
        "dentals" -> "Chequeo dental y atención odontológica básica."
        "documents" -> "Ayuda con expedición de documentos oficiales y trámites."
        else -> "Información del servicio disponible."
    }
}