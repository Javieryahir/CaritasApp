package com.example.caritasapp.reservations

import android.app.DatePickerDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
private val LightAccent = Color(0xFFE0F7F8)
private val Success = Color(0xFF4CAF50)
private val Error = Color(0xFFF44336)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RescheduleReservationScreen(
    reservationId: String,
    navController: NavController
) {
    val context = LocalContext.current
    val sessionManager = remember { NetworkModule.createSessionManager(context) }
    val apiService = remember { NetworkModule.createAuthenticatedApiService(sessionManager) }
    val scope = rememberCoroutineScope()
    
    var reservation by remember { mutableStateOf<DetailedReservationResponse?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }
    var isSubmitting by remember { mutableStateOf(false) }
    
    // Date picker states
    var showStartDatePicker by remember { mutableStateOf(false) }
    var showEndDatePicker by remember { mutableStateOf(false) }
    var selectedStartDate by remember { mutableStateOf<Date?>(null) }
    var selectedEndDate by remember { mutableStateOf<Date?>(null) }
    
    // Load reservation details
    LaunchedEffect(reservationId) {
        try {
            isLoading = true
            error = null
            val detailedReservation = apiService.getDetailedReservation(reservationId)
            reservation = detailedReservation
            
            // Pre-populate with original dates
            val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            selectedStartDate = inputFormat.parse(detailedReservation.startDate)
            selectedEndDate = inputFormat.parse(detailedReservation.endDate)
        } catch (e: Exception) {
            error = e.message ?: "Error loading reservation details"
        } finally {
            isLoading = false
        }
    }
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Top App Bar
            TopAppBar(
                title = { 
                    Text(
                        "Reagendar Reservación",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.Black
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White,
                    titleContentColor = Color.Black
                )
            )
            
            // Content
            when {
                isLoading -> {
                    LoadingView()
                }
                error != null -> {
                    ErrorView(error!!, onRetry = { /* Retry logic */ })
                }
                reservation != null -> {
                    RescheduleContent(
                        reservation = reservation!!,
                        selectedStartDate = selectedStartDate,
                        selectedEndDate = selectedEndDate,
                        onStartDateClick = { showStartDatePicker = true },
                        onEndDateClick = { showEndDatePicker = true },
                        onSubmit = { startDate, endDate ->
                            scope.launch {
                                try {
                                    isSubmitting = true
                                    val request = RepeatReservationRequest(
                                        reservationId = reservationId,
                                        startDate = startDate,
                                        endDate = endDate
                                    )
                                    apiService.repeatReservation(reservationId, request)
                                    navController.navigate("waiting") {
                                        popUpTo("reservations") { inclusive = false }
                                    }
                                } catch (e: Exception) {
                                    error = e.message ?: "Error rescheduling reservation"
                                } finally {
                                    isSubmitting = false
                                }
                            }
                        },
                        isSubmitting = isSubmitting
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
    
    // Date Pickers
    if (showStartDatePicker) {
        showDatePicker(
            onDateSelected = { date ->
                selectedStartDate = date
                showStartDatePicker = false
            },
            minDate = System.currentTimeMillis() // Start date can be today or later
        )
    }
    
    if (showEndDatePicker) {
        showDatePicker(
            onDateSelected = { date ->
                selectedEndDate = date
                showEndDatePicker = false
            },
            minDate = selectedStartDate?.time?.let { startTime ->
                // End date must be at least the day after start date
                val calendar = Calendar.getInstance()
                calendar.timeInMillis = startTime
                calendar.add(Calendar.DAY_OF_MONTH, 1)
                calendar.timeInMillis
            } ?: System.currentTimeMillis()
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
            "Cargando detalles...",
            fontSize = 18.sp,
            color = Color.Gray,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun ErrorView(error: String, onRetry: () -> Unit) {
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
            modifier = Modifier.size(64.dp),
            tint = Error
        )
        Spacer(Modifier.height(16.dp))
        Text(
            "Error al cargar",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )
        Spacer(Modifier.height(8.dp))
        Text(
            error,
            fontSize = 14.sp,
            color = Color.Gray,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(24.dp))
        Button(
            onClick = onRetry,
            colors = ButtonDefaults.buttonColors(containerColor = Accent)
        ) {
            Text("Intentar de nuevo")
        }
    }
}

@Composable
private fun RescheduleContent(
    reservation: DetailedReservationResponse,
    selectedStartDate: Date?,
    selectedEndDate: Date?,
    onStartDateClick: () -> Unit,
    onEndDateClick: () -> Unit,
    onSubmit: (String, String) -> Unit,
    isSubmitting: Boolean
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // Reservation Details Card
        ReservationDetailsCard(reservation = reservation)
        
        // Date Selection Section
        DateSelectionSection(
            selectedStartDate = selectedStartDate,
            selectedEndDate = selectedEndDate,
            onStartDateClick = onStartDateClick,
            onEndDateClick = onEndDateClick
        )
        
        // Date validation message
        if (selectedStartDate != null && selectedEndDate != null) {
            if (selectedEndDate.before(selectedStartDate) || selectedEndDate.equals(selectedStartDate)) {
                Text(
                    text = "La fecha de fin debe ser posterior a la fecha de inicio",
                    color = Error,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(horizontal = 4.dp)
                )
            }
        }
        
        // Submit Button
        Button(
            onClick = {
                if (selectedStartDate != null && selectedEndDate != null) {
                    // Validate that end date is after start date
                    if (selectedEndDate!!.after(selectedStartDate)) {
                        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                        onSubmit(dateFormat.format(selectedStartDate), dateFormat.format(selectedEndDate))
                    } else {
                        // Show error or handle invalid date range
                        println("🔍 RescheduleReservationScreen - End date must be after start date")
                    }
                }
            },
            enabled = selectedStartDate != null && selectedEndDate != null && !isSubmitting && 
                     (selectedEndDate == null || selectedStartDate == null || selectedEndDate!!.after(selectedStartDate)),
            colors = ButtonDefaults.buttonColors(containerColor = Accent),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
        ) {
            if (isSubmitting) {
                CircularProgressIndicator(
                    color = Color.White,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(Modifier.width(8.dp))
            }
            Text(
                text = if (isSubmitting) "Reagendando..." else "Confirmar Reagendación",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun ReservationDetailsCard(reservation: DetailedReservationResponse) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = LightAccent),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                text = "Detalles de la Reservación",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            
            Spacer(Modifier.height(16.dp))
            
            // Hostel name
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Filled.LocationOn,
                    contentDescription = null,
                    tint = Accent,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = reservation.hostel.name,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.Black
                )
            }
            
            Spacer(Modifier.height(12.dp))
            
            // Original dates
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Filled.CalendarMonth,
                    contentDescription = null,
                    tint = Accent,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = "Fechas originales: ${formatReservationDateRange(reservation.startDate, reservation.endDate)}",
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }
            
            Spacer(Modifier.height(12.dp))
            
            // People count
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Filled.Person,
                    contentDescription = null,
                    tint = Accent,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = "Personas: ${reservation.personReservations.size}",
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }
        }
    }
}

@Composable
private fun DateSelectionSection(
    selectedStartDate: Date?,
    selectedEndDate: Date?,
    onStartDateClick: () -> Unit,
    onEndDateClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                text = "Seleccionar Nuevas Fechas",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            
            Spacer(Modifier.height(16.dp))
            
            // Start Date
            DatePickerButton(
                label = "Fecha de Inicio",
                selectedDate = selectedStartDate,
                onClick = onStartDateClick
            )
            
            Spacer(Modifier.height(16.dp))
            
            // End Date
            DatePickerButton(
                label = "Fecha de Fin",
                selectedDate = selectedEndDate,
                onClick = onEndDateClick
            )
        }
    }
}

@Composable
private fun DatePickerButton(
    label: String,
    selectedDate: Date?,
    onClick: () -> Unit
) {
    Column {
        Text(
            text = label,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = Color.Black
        )
        Spacer(Modifier.height(8.dp))
        OutlinedButton(
            onClick = onClick,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = Accent
            ),
            border = androidx.compose.foundation.BorderStroke(1.dp, Accent),
            shape = RoundedCornerShape(8.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Filled.CalendarMonth,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = if (selectedDate != null) {
                        SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(selectedDate)
                    } else {
                        "Seleccionar fecha"
                    },
                    fontSize = 14.sp
                )
            }
        }
    }
}

@Composable
private fun showDatePicker(
    onDateSelected: (Date) -> Unit,
    minDate: Long = System.currentTimeMillis()
) {
    val context = LocalContext.current
    val calendar = Calendar.getInstance()
    
    LaunchedEffect(Unit) {
        val datePickerDialog = android.app.DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                val selectedDate = Calendar.getInstance().apply {
                    set(year, month, dayOfMonth, 0, 0, 0)
                    set(Calendar.MILLISECOND, 0)
                }
                onDateSelected(selectedDate.time)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        // Set minimum date
        datePickerDialog.datePicker.minDate = minDate
        datePickerDialog.show()
    }
}

// Helper functions
private fun formatReservationDateRange(startDate: String, endDate: String): String {
    return try {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val outputFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
        
        val start = inputFormat.parse(startDate)
        val end = inputFormat.parse(endDate)
        
        "${outputFormat.format(start!!)} - ${outputFormat.format(end!!)}"
    } catch (e: Exception) {
        "$startDate - $endDate"
    }
}
