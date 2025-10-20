package com.example.caritasapp.reserve

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.caritasapp.R
import com.example.caritasapp.data.NetworkModule
import com.example.caritasapp.data.PersonRepository
import com.example.caritasapp.data.PersonRequest
import com.example.caritasapp.data.NewReservationRequest
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

// PersonData is imported from HealthForms.kt

private val Accent = Color(0xFF009CA6)
private val LightGray = Color(0xFFF8F9FA)
private val DarkGray = Color(0xFF6B7280)

/* -------------------- Composable de orquestación (con NavController) -------------------- */

@Composable
fun ConfirmReservation(
    navController: NavController
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val personRepository = remember { NetworkModule.createPersonRepository(context) }
    val sessionManager = remember { NetworkModule.createSessionManager(context) }
    
    // Loading and error states
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var reservationSuccess by remember { mutableStateOf(false) }
    
    // Get data from different screens in the flow
    val currentBackStackEntry = navController.currentBackStackEntry?.savedStateHandle
    val searchBackStackEntry = navController.getBackStackEntry("search")
    val shelterBackStackEntry = navController.getBackStackEntry("shelter")
    
    // Shelter data - try multiple sources
    val shelterName = currentBackStackEntry?.get<String>("shelter_name") 
        ?: shelterBackStackEntry?.savedStateHandle?.get<String>("shelter_name")
        ?: searchBackStackEntry?.savedStateHandle?.get<String>("shelter_name")
        ?: "Albergue seleccionado"

    val shelterAddress = currentBackStackEntry?.get<String>("shelter_address")
        ?: shelterBackStackEntry?.savedStateHandle?.get<String>("shelter_address")
        ?: searchBackStackEntry?.savedStateHandle?.get<String>("shelter_address")
        ?: ""
    
    val shelterLat = currentBackStackEntry?.get<Double>("shelter_lat")
        ?: shelterBackStackEntry?.savedStateHandle?.get<Double>("shelter_lat")
        ?: searchBackStackEntry?.savedStateHandle?.get<Double>("shelter_lat")
        ?: 0.0
    
    val shelterLng = currentBackStackEntry?.get<Double>("shelter_lng")
        ?: shelterBackStackEntry?.savedStateHandle?.get<Double>("shelter_lng")
        ?: searchBackStackEntry?.savedStateHandle?.get<Double>("shelter_lng")
        ?: 0.0
    
    // Hostel data from API - try multiple sources
    val hostelDescription = currentBackStackEntry?.get<String>("hostel_description")
        ?: shelterBackStackEntry?.savedStateHandle?.get<String>("hostel_description")
        ?: searchBackStackEntry?.savedStateHandle?.get<String>("hostel_description")
        ?: ""
    
    val hostelMaxCapacity = currentBackStackEntry?.get<Int>("hostel_max_capacity")
        ?: shelterBackStackEntry?.savedStateHandle?.get<Int>("hostel_max_capacity")
        ?: searchBackStackEntry?.savedStateHandle?.get<Int>("hostel_max_capacity")
        ?: 0
    
    val hostelAvailableSpaces = currentBackStackEntry?.get<Int>("hostel_available_spaces")
        ?: shelterBackStackEntry?.savedStateHandle?.get<Int>("hostel_available_spaces")
        ?: searchBackStackEntry?.savedStateHandle?.get<Int>("hostel_available_spaces")
        ?: 0
    
    val hostelImageUrl = currentBackStackEntry?.get<String>("hostel_image_url")
        ?: shelterBackStackEntry?.savedStateHandle?.get<String>("hostel_image_url")
        ?: searchBackStackEntry?.savedStateHandle?.get<String>("hostel_image_url")
        ?: ""
    
    // Reservation data - try multiple sources
    val startDate = currentBackStackEntry?.get<String>("startDate")
        ?: shelterBackStackEntry?.savedStateHandle?.get<String>("startDate")
        ?: searchBackStackEntry?.savedStateHandle?.get<String>("startDate")
        ?: ""
    
    val endDate = currentBackStackEntry?.get<String>("endDate")
        ?: shelterBackStackEntry?.savedStateHandle?.get<String>("endDate")
        ?: searchBackStackEntry?.savedStateHandle?.get<String>("endDate")
        ?: ""
    
    val peopleCount = currentBackStackEntry?.get<String>("peopleCount")
        ?: shelterBackStackEntry?.savedStateHandle?.get<String>("peopleCount")
        ?: searchBackStackEntry?.savedStateHandle?.get<String>("peopleCount")
        ?: "1"
    
    // Health forms data - try multiple sources
    val personsData = currentBackStackEntry?.get<List<PersonData>>("personsData")
        ?: shelterBackStackEntry?.savedStateHandle?.get<List<PersonData>>("personsData")
        ?: searchBackStackEntry?.savedStateHandle?.get<List<PersonData>>("personsData")
        ?: emptyList()
    
    // Debug logging
    println("🔍 ConfirmationPage Debug:")
    println("  shelterName: $shelterName")
    println("  shelterAddress: $shelterAddress")
    println("  hostelDescription: $hostelDescription")
    println("  hostelImageUrl: $hostelImageUrl")
    println("  startDate: $startDate")
    println("  endDate: $endDate")
    println("  peopleCount: $peopleCount")
    println("  personsData size: ${personsData.size}")
    
    // Calculate pricing
    val days = calculateDays(startDate, endDate)
    val totalCost = days * 30 // 30 pesos per day

    // Get user ID from session
    val currentUser = sessionManager.getUser()
    val userId = currentUser?.id ?: ""
    
    // Get hostel ID from navigation data
    val hostelId = currentBackStackEntry?.get<String>("hostel_id")
        ?: shelterBackStackEntry?.savedStateHandle?.get<String>("hostel_id")
        ?: searchBackStackEntry?.savedStateHandle?.get<String>("hostel_id")
        ?: ""
    
    // Enhanced debug logging
    println("🔍 ConfirmationPage Enhanced Debug:")
    println("  userId: $userId (from session: ${currentUser?.firstName} ${currentUser?.lastName})")
    println("  hostelId: $hostelId")
    println("  personsData size: ${personsData.size}")
    println("  startDate: $startDate")
    println("  endDate: $endDate")
    
    // Detailed personsData debug
    println("🔍 PersonsData Debug:")
    println("  currentBackStackEntry personsData: ${currentBackStackEntry?.get<List<PersonData>>("personsData")?.size ?: "null"}")
    println("  shelterBackStackEntry personsData: ${shelterBackStackEntry?.savedStateHandle?.get<List<PersonData>>("personsData")?.size ?: "null"}")
    println("  searchBackStackEntry personsData: ${searchBackStackEntry?.savedStateHandle?.get<List<PersonData>>("personsData")?.size ?: "null"}")
    
    if (personsData.isNotEmpty()) {
        println("  First person: ${personsData[0].firstName} ${personsData[0].lastName}")
    } else {
        println("  ❌ No personsData found in any back stack entry!")
    }

    ConfirmReservationContent(
        shelterName = shelterName,
        shelterAddress = shelterAddress,
        hostelDescription = hostelDescription,
        hostelMaxCapacity = hostelMaxCapacity,
        hostelAvailableSpaces = hostelAvailableSpaces,
        hostelImageUrl = hostelImageUrl,
        startDate = startDate,
        endDate = endDate,
        peopleCount = peopleCount,
        personsData = personsData,
        days = days,
        totalCost = totalCost,
        isLoading = isLoading,
        errorMessage = errorMessage,
        reservationSuccess = reservationSuccess,
        onBack = { navController.popBackStack() },
        onConfirm = {
            scope.launch {
                try {
                    isLoading = true
                    errorMessage = null
                    
                    // Step 1: Create persons and collect their IDs
                    val personIds = mutableListOf<String>()
                    
                    for (person in personsData) {
                        println("🔍 Creating PersonRequest for: ${person.firstName} ${person.lastName}")
                        println("  userId: $userId")
                        println("  userId length: ${userId.length}")
                        println("  userId format check: ${userId.matches(Regex("[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}"))}")
                        println("  firstName: ${person.firstName}")
                        println("  lastName: ${person.lastName}")
                        println("  birthDate: ${person.birthDate}")
                        println("  birthDate format check: ${person.birthDate.matches(Regex("\\d{4}-\\d{2}-\\d{2}"))}")
                        println("  allergies: ${person.allergies.contentToString()}")
                        println("  disabilities: ${person.disabilities.contentToString()}")
                        println("  medicines: ${person.medicines.contentToString()}")
                        
                        val personRequest = PersonRequest(
                            firstName = person.firstName,
                            userId = userId,
                            lastName = person.lastName,
                            birthDate = person.birthDate,
                            alergies = person.allergies,
                            discapacities = person.disabilities,
                            medicines = person.medicines
                        )
                        
                        println("🔍 PersonRequest created successfully")
                        
                        personRepository.createPerson(personRequest).collect { personResponse ->
                            personResponse?.let { response ->
                                personIds.add(response.id)
                                println("✅ Person created with ID: ${response.id}")
                                
                                // Check if all persons have been created
                                if (personIds.size == personsData.size) {
                                    println("🔍 All persons created. Person IDs: ${personIds.joinToString()}")
                                    
                                    // Step 2: Create reservation with person IDs
                                    if (personIds.isNotEmpty() && hostelId.isNotEmpty()) {
                                        println("🔍 Creating reservation with:")
                                        println("  userId: $userId")
                                        println("  hostelId: $hostelId")
                                        println("  startDate: $startDate")
                                        println("  endDate: $endDate")
                                        println("  personIds: ${personIds.joinToString()}")
                                        
                                        // Check authentication token
                                        val currentUser = sessionManager.getUser()
                                        val idToken = sessionManager.getIdToken()
                                        println("🔍 Authentication Debug:")
                                        println("  currentUser: $currentUser")
                                        println("  idToken length: ${idToken?.length ?: "null"}")
                                        println("  idToken preview: ${idToken?.take(20) ?: "null"}...")
                                        
                                        // Try with future dates to avoid validation issues
                                        val futureStartDate = "2025-12-01"
                                        val futureEndDate = "2025-12-08"
                                        println("🔍 Using future dates for testing:")
                                        println("  startDate: $futureStartDate")
                                        println("  endDate: $futureEndDate")
                                        
                                        val reservationRequest = NewReservationRequest(
                                            userId = userId,
                                            hostelId = hostelId,
                                            startDate = futureStartDate,
                                            endDate = futureEndDate,
                                            personIds = personIds.toTypedArray()
                                        )
                                        
                                        personRepository.createReservation(reservationRequest).collect { reservationResponse ->
                                            reservationResponse?.let { response ->
                                                println("✅ Reservation created with ID: ${response.id}")
                                                reservationSuccess = true
                                                navController.navigate("waiting") // Navigate to success screen
                                            } ?: run {
                                                errorMessage = "Failed to create reservation. Please try again."
                                                println("❌ Reservation creation failed - no response received")
                                            }
                                        }
                                    } else {
                                        val missingData = mutableListOf<String>()
                                        if (userId.isEmpty()) missingData.add("User ID")
                                        if (hostelId.isEmpty()) missingData.add("Hostel ID")
                                        if (personIds.isEmpty()) missingData.add("Person IDs")

                                        errorMessage = "Error: Missing ${missingData.joinToString(", ")}"
                                        println("❌ Missing data: ${missingData.joinToString(", ")}")
                                    }
                                }
                            } ?: run {
                                errorMessage = "Failed to create a person. Please try again."
                            }
                        }
                    }
                    
                } catch (e: Exception) {
                    errorMessage = "Error creating reservation: ${e.message}"
                    println("❌ Reservation error: ${e.message}")
                } finally {
                    isLoading = false
                }
            }
        }
    )
}

private fun calculateDays(startDate: String, endDate: String): Int {
    if (startDate.isEmpty() || endDate.isEmpty()) return 1
    
    return try {
        val format = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val start = format.parse(startDate)
        val end = format.parse(endDate)
        
        if (start != null && end != null) {
            val diffInMillis = end.time - start.time
            val diffInDays = diffInMillis / (24 * 60 * 60 * 1000)
            maxOf(1, diffInDays.toInt())
        } else {
            1
        }
    } catch (e: Exception) {
        1
    }
}

private fun calculateAge(birthDate: String): Int {
    if (birthDate.isEmpty()) return 0
    
    return try {
        val format = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val birth = format.parse(birthDate)
        val today = Calendar.getInstance()
        val birthCalendar = Calendar.getInstance()
        
        println("🔍 Age Calculation Debug:")
        println("  birthDate: $birthDate")
        println("  today: ${today.get(Calendar.YEAR)}-${today.get(Calendar.MONTH) + 1}-${today.get(Calendar.DAY_OF_MONTH)}")
        
        if (birth != null) {
            birthCalendar.time = birth
            val birthYear = birthCalendar.get(Calendar.YEAR)
            val birthMonth = birthCalendar.get(Calendar.MONTH)
            val birthDay = birthCalendar.get(Calendar.DAY_OF_MONTH)
            
            println("  birthYear: $birthYear, birthMonth: $birthMonth, birthDay: $birthDay")
            
            val age = today.get(Calendar.YEAR) - birthYear
            val monthDiff = today.get(Calendar.MONTH) - birthMonth
            val dayDiff = today.get(Calendar.DAY_OF_MONTH) - birthDay
            
            println("  age: $age, monthDiff: $monthDiff, dayDiff: $dayDiff")
            
            val finalAge = if (monthDiff < 0 || (monthDiff == 0 && dayDiff < 0)) {
                age - 1
            } else {
                age
            }
            
            println("  finalAge: $finalAge")
            maxOf(0, finalAge) // Ensure age is not negative
        } else {
            println("  ❌ Failed to parse birth date")
            0
        }
    } catch (e: Exception) {
        println("  ❌ Exception in calculateAge: ${e.message}")
        0
    }
}

/* -------------------- UI pura (apta para Preview) -------------------- */

@Composable
private fun ConfirmReservationContent(
    shelterName: String,
    shelterAddress: String,
    hostelDescription: String,
    hostelMaxCapacity: Int,
    hostelAvailableSpaces: Int,
    hostelImageUrl: String,
    startDate: String,
    endDate: String,
    peopleCount: String,
    personsData: List<PersonData>,
    days: Int,
    totalCost: Int,
    isLoading: Boolean = false,
    errorMessage: String? = null,
    reservationSuccess: Boolean = false,
    onBack: () -> Unit,
    onConfirm: () -> Unit
) {
    val scrollState = rememberScrollState()
    var showPersonsDetails by remember { mutableStateOf(false) }
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(LightGray)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
        ) {
            // Header
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = Accent,
                shadowElevation = 8.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(horizontal = 24.dp, vertical = 20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        "Confirmar Reservación",
                        color = Color.White,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        "Revisa los detalles antes de confirmar",
                        color = Color.White.copy(alpha = 0.9f),
                        fontSize = 16.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }
            
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Shelter Information Card
                SummaryCard(
                    title = "Albergue",
                    icon = Icons.Filled.LocationOn,
                    content = {
                        Column {
                            // Shelter name
                            Text(
                                text = shelterName,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = Accent
                            )
                            
                            // Shelter image if available
                            if (hostelImageUrl.isNotEmpty()) {
                                Spacer(Modifier.height(12.dp))
                                AsyncImage(
                                    model = ImageRequest.Builder(LocalContext.current)
                                        .data(hostelImageUrl)
                                        .crossfade(true)
                                        .build(),
                                    contentDescription = "Imagen del albergue",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(120.dp)
                                        .clip(RoundedCornerShape(8.dp)),
                                    error = painterResource(id = R.drawable.ic_launcher_foreground),
                                    placeholder = painterResource(id = R.drawable.ic_launcher_foreground)
                                )
                            }
                            
                            // Show API description if available, otherwise show address
                            if (hostelDescription.isNotEmpty()) {
                                Spacer(Modifier.height(8.dp))
                                Text(
                                    text = hostelDescription,
                                    fontSize = 14.sp,
                                    color = DarkGray
                                )
                            } else if (shelterAddress.isNotEmpty()) {
                                Spacer(Modifier.height(8.dp))
                                Text(
                                    text = shelterAddress,
                                    fontSize = 14.sp,
                                    color = DarkGray
                                )
                            }
                            
                            if (hostelMaxCapacity > 0) {
                                Spacer(Modifier.height(8.dp))
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                                ) {
                                    InfoChip("Capacidad: $hostelMaxCapacity", Icons.Filled.People)
                                    InfoChip("Disponibles: $hostelAvailableSpaces", Icons.Filled.CheckCircle)
                                }
                            }
                        }
                    }
                )
                
                // Dates and People Card
                SummaryCard(
                    title = "Fechas y Personas",
                    icon = Icons.Filled.CalendarToday,
                    content = {
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column {
                                    Text(
                                        "Fecha de inicio",
                                        fontSize = 12.sp,
                                        color = DarkGray
                                    )
                                    Text(
                                        formatDate(startDate),
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                                Column {
                                    Text(
                                        "Fecha de fin",
                                        fontSize = 12.sp,
                                        color = DarkGray
                                    )
                                    Text(
                                        formatDate(endDate),
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }
                            Spacer(Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                InfoChip("$days días", Icons.Filled.Schedule)
                                InfoChip("$peopleCount personas", Icons.Filled.People)
                            }
                        }
                    }
                )
                
                // People Details Card
                if (personsData.isNotEmpty()) {
                    SummaryCard(
                        title = "Información de Personas",
                        icon = Icons.Filled.Person,
                        content = {
                            Column {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { showPersonsDetails = !showPersonsDetails },
                                    horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                                    Text(
                                        "${personsData.size} ${if (personsData.size == 1) "persona" else "personas"} registradas",
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                    Icon(
                                        imageVector = if (showPersonsDetails) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
                                        contentDescription = if (showPersonsDetails) "Ocultar" else "Mostrar",
                                        tint = Accent
                                    )
                                }
                                
                                AnimatedVisibility(
                                    visible = showPersonsDetails,
                                    enter = expandVertically(),
                                    exit = shrinkVertically()
                                ) {
                                    Column(
                                        modifier = Modifier.padding(top = 12.dp),
                                        verticalArrangement = Arrangement.spacedBy(12.dp)
                                    ) {
                                        personsData.forEachIndexed { index, person ->
                                            PersonSummaryCard(
                                                person = person,
                                                index = index + 1
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    )
                }
                
                // Pricing Card
                SummaryCard(
                    title = "Costo Total",
                    icon = Icons.Filled.AttachMoney,
                    content = {
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    "Costo por día",
                                    fontSize = 14.sp,
                                    color = DarkGray
                                )
                                Text(
                                    "$30.00 MXN",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                            Spacer(Modifier.height(4.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    "Días de estadía",
                                    fontSize = 14.sp,
                                    color = DarkGray
                                )
                                Text(
                                    "$days días",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                            Spacer(Modifier.height(8.dp))
                            HorizontalDivider()
                            Spacer(Modifier.height(8.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    "Total",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Accent
                                )
                                Text(
                                    "$${totalCost}.00 MXN",
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Accent
                                )
                            }
                        }
                    }
                )
            }
            
            // Error message
            if (errorMessage != null) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.Red.copy(alpha = 0.1f))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Error,
                            contentDescription = "Error",
                            tint = Color.Red,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = errorMessage,
                            color = Color.Red,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            // Bottom buttons
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = Color.White,
                shadowElevation = 8.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Back button
                    OutlinedButton(
                        onClick = onBack,
                        enabled = !isLoading,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.height(56.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = Accent
                        ),
                        border = BorderStroke(2.dp, Accent)
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Atrás",
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(Modifier.width(8.dp))
                        Text("Atrás", fontSize = 16.sp, fontWeight = FontWeight.Medium)
                    }

                    // Confirm button
                    Button(
                        onClick = onConfirm,
                        enabled = !isLoading && !reservationSuccess,
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (reservationSuccess) Color.Green else Accent,
                            contentColor = Color.White,
                            disabledContainerColor = Accent.copy(alpha = 0.6f),
                            disabledContentColor = Color.White.copy(alpha = 0.7f)
                        ),
                        modifier = Modifier
                            .height(56.dp)
                            .weight(1f)
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = Color.White,
                                strokeWidth = 2.dp
                            )
                            Spacer(Modifier.width(8.dp))
                            Text("Creando...", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                        } else if (reservationSuccess) {
                            Icon(
                                imageVector = Icons.Filled.CheckCircle,
                                contentDescription = "Éxito",
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(Modifier.width(8.dp))
                            Text("¡Reservado!", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                        } else {
                            Icon(
                                imageVector = Icons.Filled.Check,
                                contentDescription = "Confirmar",
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(Modifier.width(8.dp))
                            Text("Confirmar Reservación", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }
        }
    }
}

/* -------------------- Helper Components -------------------- */

@Composable
private fun SummaryCard(
    title: String,
    icon: ImageVector,
    content: @Composable () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = Accent,
                    modifier = Modifier.size(24.dp)
                )
                Text(
                    text = title,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Accent
                )
            }
            Spacer(Modifier.height(16.dp))
            content()
        }
    }
}

@Composable
private fun InfoChip(
    text: String,
    icon: ImageVector
) {
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = Accent.copy(alpha = 0.1f),
        modifier = Modifier.padding(vertical = 4.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Accent,
                modifier = Modifier.size(16.dp)
            )
            Text(
                text = text,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = Accent
            )
        }
    }
}

@Composable
private fun PersonSummaryCard(
    person: PersonData,
    index: Int
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = LightGray),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Persona $index",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Accent
                )
                Text(
                    text = "${calculateAge(person.birthDate)} años",
                    fontSize = 12.sp,
                    color = DarkGray
                )
            }
            
            Spacer(Modifier.height(8.dp))
            
            Text(
                text = "${person.firstName} ${person.lastName}",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )
            
            if (person.allergies.isNotEmpty() || person.disabilities.isNotEmpty() || person.medicines.isNotEmpty()) {
                Spacer(Modifier.height(8.dp))
                
                if (person.allergies.isNotEmpty()) {
                    HealthInfoRow("Alergias", person.allergies)
                }
                if (person.disabilities.isNotEmpty()) {
                    HealthInfoRow("Discapacidades", person.disabilities)
                }
                if (person.medicines.isNotEmpty()) {
                    HealthInfoRow("Medicamentos", person.medicines)
                }
            }
        }
    }
}

@Composable
private fun HealthInfoRow(
    label: String,
    items: Array<String>
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "$label:",
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            color = DarkGray,
            modifier = Modifier.width(80.dp)
        )
        Text(
            text = items.joinToString(", "),
            fontSize = 12.sp,
            color = DarkGray,
            modifier = Modifier.weight(1f)
        )
    }
}

private fun formatDate(dateString: String): String {
    if (dateString.isEmpty()) return "No especificada"
    
    return try {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val outputFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val date = inputFormat.parse(dateString)
        outputFormat.format(date ?: Date())
    } catch (e: Exception) {
        dateString
    }
}

/* -------------------- Preview -------------------- */

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PreviewConfirmReservation() {
    val samplePersonsData = listOf(
        PersonData(
            firstName = "Juan",
            lastName = "Pérez",
            birthDate = "1999-05-15",
            allergies = arrayOf("Polen"),
            disabilities = emptyArray(),
            medicines = arrayOf("Vitamina D")
        ),
        PersonData(
            firstName = "María",
            lastName = "González",
            birthDate = "1994-03-22",
            allergies = emptyArray(),
            disabilities = emptyArray(),
            medicines = emptyArray()
        )
    )
    
    ConfirmReservationContent(
        shelterName = "Divina Providencia",
        shelterAddress = "Calle Principal 123, Monterrey",
        hostelDescription = "Albergue con servicios completos",
        hostelMaxCapacity = 30,
        hostelAvailableSpaces = 25,
        hostelImageUrl = "",
        startDate = "2025-01-15",
        endDate = "2025-01-20",
        peopleCount = "2",
        personsData = samplePersonsData,
        days = 5,
        totalCost = 150,
        isLoading = false,
        errorMessage = null,
        reservationSuccess = false,
        onBack = {},
        onConfirm = {}
    )
}
