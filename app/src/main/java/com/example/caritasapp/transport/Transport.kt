package com.example.caritasapp.transport

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.caritasapp.navegationbar.AppBottomBar
import com.example.caritasapp.data.ReservationRepository
import com.example.caritasapp.data.NetworkModule
import com.example.caritasapp.data.TransportationRequest
import com.example.caritasapp.data.TransportationResponse
import com.example.caritasapp.data.SessionManager
import com.example.caritasapp.data.UserReservationData
import java.util.Calendar
import java.util.Locale
import android.net.Uri

private val Teal = Color(0xFF5D97A3)
private val Accent = Color(0xFF009CA6)
private val LightAccent = Color(0xFFE0F7F8)
private val Success = Color(0xFF4CAF50)
private val Warning = Color(0xFFFF9800)
private val Error = Color(0xFFF44336)
private val Surface = Color.White
private val CardBg = Color(0xFFD1E0D7)

private enum class Mode { GoTo, PickUp } // Ir a / Recoger

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransportScreen(navController: NavController) {
    val context = LocalContext.current
    val reservationRepository = remember { NetworkModule.createReservationRepository(context) }
    
    // State for checking active reservations
    var isLoading by remember { mutableStateOf(true) }
    var hasActiveReservation by remember { mutableStateOf(false) }
    var activeReservation by remember { mutableStateOf<UserReservationData?>(null) }
    
    // Get current user ID from session
    val sessionManager = remember { SessionManager(context) }
    val currentUser = sessionManager.getUser()
    
    // Check for active reservations when screen loads
    LaunchedEffect(Unit) {
        if (currentUser != null) {
            try {
                reservationRepository.getUserReservation(currentUser.id).collect { response ->
                    isLoading = false
                    if (response != null && response.reservation.state == "ACTIVE") {
                        hasActiveReservation = true
                        activeReservation = response.reservation
                    } else {
                        hasActiveReservation = false
                        activeReservation = null
                    }
                }
            } catch (e: Exception) {
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
        when {
            isLoading -> LoadingView()
            hasActiveReservation && activeReservation != null -> {
                activeReservation?.let { reservation ->
                    TransportFormScreen(navController, reservation)
                }
            }
            else -> NoActiveReservationsView(navController)
        }

        AppBottomBar(
            navController = navController,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(start = 12.dp, end = 12.dp, bottom = 20.dp)
        )
    }
}

@Composable
private fun LoadingView() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .padding(horizontal = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        CircularProgressIndicator(
            color = Accent,
            modifier = Modifier.size(48.dp)
        )
        Spacer(Modifier.height(16.dp))
        Text(
            "Verificando reservaciones...",
            fontSize = 20.sp,
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
            .statusBarsPadding()
            .padding(horizontal = 20.dp),
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
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            color = Color.Gray
        )
        
        Spacer(Modifier.height(16.dp))
        
        Text(
            "Para reservar transporte, primero necesitas tener una reservación activa en un albergue.",
            fontSize = 18.sp,
            textAlign = TextAlign.Center,
            color = Color.Gray,
            lineHeight = 26.sp
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
            Text("Buscar Albergues", fontSize = 20.sp)
        }
    }
}

@Composable
private fun TransportFormScreen(navController: NavController, activeReservation: UserReservationData) {
    val context = LocalContext.current
    val cal = remember { Calendar.getInstance() }
    val reservationRepository = remember { NetworkModule.createReservationRepository(context) }

    // ------------- Get shelter name from active reservation -------------
    val shelterName = activeReservation.hostel.name

    // ------------- Estado del formulario -------------
    var mode by remember { mutableStateOf(Mode.GoTo) } // Ir a por defecto

    var pickup by remember { mutableStateOf("") }
    var dropoff by remember { mutableStateOf("") }
    var date by remember { mutableStateOf("") }
    var time by remember { mutableStateOf("") }
    var peopleCount by remember { mutableIntStateOf(1) }

    fun pickDate() {
        DatePickerDialog(
            context,
            { _, y, m, d -> date = "$y-${String.format(Locale.US, "%02d", m + 1)}-${String.format(Locale.US, "%02d", d)}" },
            cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)
        ).show()
    }
    fun pickTime() {
        TimePickerDialog(
            context,
            { _, h, min -> time = "%02d:%02d:00".format(h, min) },
            cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), true
        ).show()
    }

    // ------------- Lógica para inyectar el albergue según modo -------------
    val pickupValue: String
    val pickupReadOnly: Boolean
    val dropoffValue: String
    val dropoffReadOnly: Boolean

    if (mode == Mode.GoTo) {
        // "Ir a" -> el albergue va en LUGAR DE RECOGIDA (no editable)
        pickupValue = shelterName.ifBlank { "No hay albergue seleccionado" }
        pickupReadOnly = shelterName.isNotBlank() // Solo lectura si hay albergue
        dropoffValue = dropoff
        dropoffReadOnly = false
    } else {
        // "Recoger" -> el albergue va en LOCACIÓN FINAL (no editable)
        pickupValue = pickup
        pickupReadOnly = false
        dropoffValue = shelterName.ifBlank { "No hay albergue seleccionado" }
        dropoffReadOnly = shelterName.isNotBlank() // Solo lectura si hay albergue
    }

    // UI with proper spacing
    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .padding(horizontal = 16.dp)
            .padding(bottom = 100.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(16.dp))
        
        // Header with better proportions
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Accent),
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    Icons.Filled.LocalTaxi,
                    contentDescription = null,
                    modifier = Modifier.size(48.dp),
                    tint = Color.White
                )
                Spacer(Modifier.height(12.dp))
                Text(
                    "Reservar Transporte",
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold,
                    fontSize = 26.sp,
                    color = Color.White
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    "Servicio de transporte seguro y confiable",
                    textAlign = TextAlign.Center,
                    fontSize = 14.sp,
                    color = Color.White.copy(alpha = 0.9f)
                )
            }
        }

        Spacer(Modifier.height(20.dp))

        // Toggle buttons with better spacing and centering
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            TogglePill(
                selected = mode == Mode.GoTo,
                onClick = { mode = Mode.GoTo },
                label = "Ir a",
                leading = { Icon(Icons.Filled.NearMe, null, modifier = Modifier.size(18.dp)) },
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp)
            )
            TogglePill(
                selected = mode == Mode.PickUp,
                onClick = { mode = Mode.PickUp },
                label = "Recoger",
                leading = { Icon(Icons.Filled.LocalTaxi, null, modifier = Modifier.size(18.dp)) },
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp)
            )
        }

        Spacer(Modifier.height(20.dp))

        // Form Card with better internal spacing
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = CardBg),
            elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
        ) {
            Column(
                modifier = Modifier.padding(20.dp)
            ) {
                // Pickup Location Section
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(bottom = 8.dp)
                ) {
                    Text(
                        if (mode == Mode.GoTo) "Albergue (Lugar de Recogida)" else "Lugar de Recogida",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 18.sp,
                        color = Color.Black
                    )
                    if (pickupReadOnly && shelterName.isNotBlank()) {
                        Spacer(Modifier.width(8.dp))
                        Icon(
                            Icons.Filled.CheckCircle,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = Accent
                        )
                    }
                }
                CenteredOutlinedField(
                    value = pickupValue,
                    onValueChange = { pickup = it },
                    placeholder = if (mode == Mode.GoTo) "Albergue seleccionado" else "Ingresa la dirección de recogida",
                    placeholderIcon = { Icon(Icons.Filled.LocationOn, null, modifier = Modifier.size(20.dp)) },
                    readOnly = pickupReadOnly,
                    textSize = 14.sp
                )

                Spacer(Modifier.height(16.dp))

                // Dropoff Location Section
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(bottom = 8.dp)
                ) {
                    Text(
                        if (mode == Mode.PickUp) "Albergue (Destino)" else "Destino",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 18.sp,
                        color = Color.Black
                    )
                    if (dropoffReadOnly && shelterName.isNotBlank()) {
                        Spacer(Modifier.width(8.dp))
                        Icon(
                            Icons.Filled.CheckCircle,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = Accent
                        )
                    }
                }
                CenteredOutlinedField(
                    value = dropoffValue,
                    onValueChange = { dropoff = it },
                    placeholder = if (mode == Mode.PickUp) "Albergue seleccionado" else "Ingresa la dirección de destino",
                    placeholderIcon = { Icon(Icons.Filled.Map, null, modifier = Modifier.size(20.dp)) },
                    readOnly = dropoffReadOnly,
                    textSize = 14.sp
                )

                Spacer(Modifier.height(16.dp))

                // People Counter Section
                Text(
                    "Número de Personas",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 18.sp,
                    color = Color.Black,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                PeopleCounter(
                    count = peopleCount,
                    onCountChange = { peopleCount = it },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(16.dp))

                // Date and Time Section
                Text(
                    "Fecha y Hora",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 18.sp,
                    color = Color.Black,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    ClickableField(
                        labelOrValue = date.ifBlank { "Seleccionar fecha" },
                        icon = { Icon(Icons.Filled.CalendarMonth, null, modifier = Modifier.size(18.dp)) },
                        onClick = { pickDate() },
                        modifier = Modifier.weight(1f),
                        textSize = 14.sp,
                        height = 48.dp
                    )
                    ClickableField(
                        labelOrValue = time.ifBlank { "Seleccionar hora" },
                        icon = { Icon(Icons.Filled.AccessTime, null, modifier = Modifier.size(18.dp)) },
                        onClick = { pickTime() },
                        modifier = Modifier.weight(1f),
                        textSize = 14.sp,
                        height = 48.dp
                    )
                }
            }
        }

        Spacer(Modifier.height(20.dp))

        // Action Button with better styling
        var isSubmitting by remember { mutableStateOf(false) }
        var showErrorDialog by remember { mutableStateOf(false) }
        
        // Handle API call
        LaunchedEffect(isSubmitting) {
            if (isSubmitting) {
                try {
                    val request = TransportationRequest(
                        orderDate = date,
                        count = peopleCount,
                        hostelName = shelterName.ifBlank { "Albergue seleccionado" },
                        place = if (mode == Mode.GoTo) dropoffValue else pickupValue, // Lugar donde van/recogen
                        pickupTime = time,
                        fromHostel = mode == Mode.GoTo // true si es "Ir a", false si es "Recoger"
                    )
                    
                    reservationRepository.createTransportation(request).collect { response: TransportationResponse? ->
                        isSubmitting = false
                        println("Transportation response: $response")
                        if (response != null && response.id.isNotBlank()) {
                            println("Success: Transportation created with ID: ${response.id}")
                            // Navigate to waiting page with transportation details
                            val pickupArg = Uri.encode(pickupValue)
                            val dropoffArg = Uri.encode(dropoffValue)
                            val dateArg = Uri.encode(date)
                            val timeArg = Uri.encode(time)
                            
                            navController.navigate(
                                "waiting_transport?pickup=$pickupArg&dropoff=$dropoffArg&date=$dateArg&time=$timeArg"
                            )
                        } else {
                            println("Error: Invalid response or empty ID")
                            showErrorDialog = true
                        }
                    }
                } catch (_: Exception) {
                    isSubmitting = false
                    showErrorDialog = true
                }
            }
        }
        
        Button(
            onClick = {
                if (pickupValue.isNotBlank() && dropoffValue.isNotBlank() && date.isNotBlank() && time.isNotBlank()) {
                    isSubmitting = true
                }
            },
            enabled = !isSubmitting && pickupValue.isNotBlank() && dropoffValue.isNotBlank() && date.isNotBlank() && time.isNotBlank(),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (isSubmitting) Accent.copy(alpha = 0.6f) else Accent,
                contentColor = Color.White
            ),
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
        ) {
            if (isSubmitting) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    color = Color.White,
                    strokeWidth = 2.dp
                )
                Spacer(Modifier.width(8.dp))
                Text("Enviando...", fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
            } else {
                Icon(Icons.Filled.LocalTaxi, contentDescription = null, modifier = Modifier.size(20.dp))
                Spacer(Modifier.width(8.dp))
                Text("Reservar Transporte", fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
            }
        }
        
        // Error Dialog
        if (showErrorDialog) {
            AlertDialog(
                onDismissRequest = { showErrorDialog = false },
                title = { Text("Error") },
                text = { Text("No se pudo enviar la solicitud de transporte. Por favor, intenta de nuevo.") },
                confirmButton = {
                    TextButton(onClick = { showErrorDialog = false }) {
                        Text("Intentar de nuevo")
                    }
                }
            )
        }
    }
}

/* =================== Componentes =================== */

@Composable
private fun TogglePill(
    selected: Boolean,
    onClick: () -> Unit,
    label: String,
    leading: @Composable () -> Unit,
    modifier: Modifier = Modifier
) {
    val container = if (selected) Accent else Color.White
    val content = if (selected) Color.White else Accent
    val border = if (selected) Accent else Accent.copy(alpha = 0.3f)

    Card(
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = container),
        border = androidx.compose.foundation.BorderStroke(1.5.dp, border),
        elevation = CardDefaults.cardElevation(defaultElevation = if (selected) 3.dp else 1.dp),
        modifier = modifier
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            contentAlignment = Alignment.Center
        ) {
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                leading()
                Spacer(Modifier.width(6.dp))
                Text(
                    label,
                    color = content,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CenteredOutlinedField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    placeholderIcon: @Composable (() -> Unit)? = null,
    readOnly: Boolean = false,
    textSize: androidx.compose.ui.unit.TextUnit = 16.sp
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        singleLine = true,
        readOnly = readOnly,
        placeholder = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                placeholderIcon?.invoke()
                if (placeholderIcon != null) Spacer(Modifier.width(8.dp))
                Text(placeholder, textAlign = TextAlign.Center, fontSize = textSize)
            }
        },
        textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.Center, fontSize = textSize),
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            unfocusedContainerColor = Color.White,
            focusedContainerColor = Color.White,
            focusedBorderColor = Accent,
            unfocusedBorderColor = Accent.copy(alpha = 0.3f)
        ),
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
private fun ClickableField(
    labelOrValue: String,
    icon: @Composable () -> Unit,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    textSize: androidx.compose.ui.unit.TextUnit = 16.sp,
    height: Dp = 56.dp
) {
    Card(
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = androidx.compose.foundation.BorderStroke(1.dp, Accent.copy(alpha = 0.3f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = modifier.height(height)
    ) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                icon()
                Spacer(Modifier.width(8.dp))
                Text(
                    text = labelOrValue,
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodyLarge,
                    fontSize = textSize,
                    color = if (labelOrValue == "Fecha" || labelOrValue == "Hora") Color.Gray else Color.Black
                )
            }
        }
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
        border = androidx.compose.foundation.BorderStroke(1.dp, Accent.copy(alpha = 0.3f)),
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
                        RoundedCornerShape(8.dp)
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
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold,
                    color = Accent
                )
                Text(
                    text = if (count == 1) "persona" else "personas",
                    fontSize = 14.sp,
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
                        RoundedCornerShape(8.dp)
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

/* ---------- Preview ---------- */
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PreviewTransportScreen() {
    TransportScreen(navController = rememberNavController())
}
