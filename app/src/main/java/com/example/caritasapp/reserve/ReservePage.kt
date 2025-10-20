package com.example.caritasapp.reserve

import android.app.DatePickerDialog
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.widget.DatePicker
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.createBitmap
import androidx.navigation.NavController
import com.example.caritasapp.R
import com.example.caritasapp.navegationbar.AppBottomBar
import com.example.caritasapp.data.HostelData
import com.example.caritasapp.data.NetworkModule
import kotlinx.coroutines.launch
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import java.util.Calendar

private val Accent = Color(0xFF009CA6)
private const val ACCENT_INT = 0xFF009CA6.toInt()

// ---------- Modelo ----------
data class LocationData(
    val name: String,
    val latLng: LatLng,
    val details: String,
    val imageUrl: String? = null,
    val capacity: Int = 4
)

// ---------- Util: marcador con texto estilo "label" ----------
fun createTextMarker(text: String, selected: Boolean): BitmapDescriptor {
    return try {
        val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = if (selected) android.graphics.Color.WHITE else android.graphics.Color.BLACK
            textSize = 38f
            textAlign = Paint.Align.LEFT
        }
        val bgPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = if (selected) ACCENT_INT else android.graphics.Color.WHITE
        }

        val bounds = Rect()
        textPaint.getTextBounds(text, 0, text.length, bounds)

        val horizontal = 28
        val vertical = 20
        val width = bounds.width() + horizontal * 2
        val height = bounds.height() + vertical * 2
        val radius = 36f

        val bmp = createBitmap(width, height)
        val canvas = Canvas(bmp)
        canvas.drawRoundRect(0f, 0f, width.toFloat(), height.toFloat(), radius, radius, bgPaint)
        canvas.drawText(text, horizontal.toFloat(), bounds.height() + vertical.toFloat(), textPaint)
        BitmapDescriptorFactory.fromBitmap(bmp)
    } catch (e: Exception) {
        // Fallback to default marker if custom marker creation fails
        BitmapDescriptorFactory.defaultMarker()
    }
}

// ---------- Pantalla principal ----------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReservationPage(navController: NavController) {
    var selectedServices by rememberSaveable { mutableStateOf(setOf<String>()) }
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    
    // Repository for API calls
    val reservationRepository = remember { NetworkModule.createReservationRepository(context) }
    val handle = navController.currentBackStackEntry?.savedStateHandle
    val savedShelterName by (handle?.getStateFlow("shelter_name", "") ?: MutableStateFlow("")).collectAsState("")


    // Rango de fechas - Initialize with default dates
    var startDate by remember { mutableStateOf<Calendar?>(Calendar.getInstance().apply { 
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }) }
    var endDate by remember { mutableStateOf<Calendar?>(Calendar.getInstance().apply { 
        add(Calendar.DAY_OF_MONTH, 7) // 7 days from now
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }) }
    
    // Store initial dates in navigation state
    LaunchedEffect(startDate, endDate) {
        if (startDate != null && endDate != null) {
            val startDateStr = "${startDate!!.get(Calendar.YEAR)}-${String.format("%02d", startDate!!.get(Calendar.MONTH) + 1)}-${String.format("%02d", startDate!!.get(Calendar.DAY_OF_MONTH))}"
            val endDateStr = "${endDate!!.get(Calendar.YEAR)}-${String.format("%02d", endDate!!.get(Calendar.MONTH) + 1)}-${String.format("%02d", endDate!!.get(Calendar.DAY_OF_MONTH))}"
            
            println("🔍 ReservePage storing dates:")
            println("  startDate: $startDateStr")
            println("  endDate: $endDateStr")
            
            navController.currentBackStackEntry
                ?.savedStateHandle
                ?.set("startDate", startDateStr)
            navController.currentBackStackEntry
                ?.savedStateHandle
                ?.set("endDate", endDateStr)
        }
    }

    fun format(c: Calendar?): String =
        if (c == null) "" else "${c.get(Calendar.DAY_OF_MONTH)}/${c.get(Calendar.MONTH) + 1}/${c.get(Calendar.YEAR)}"

    fun pickDateRange() {
        val now = Calendar.getInstance()
        val startInit = (startDate ?: now)

        DatePickerDialog(
            context,
            { _: DatePicker, y: Int, m: Int, d: Int ->
                val start = Calendar.getInstance().apply {
                    set(y, m, d, 0, 0, 0); set(Calendar.MILLISECOND, 0)
                }
                startDate = start

                val endInit = (endDate ?: start)
                DatePickerDialog(
                    context,
                    { _: DatePicker, y2: Int, m2: Int, d2: Int ->
                        val end = Calendar.getInstance().apply {
                            set(y2, m2, d2, 0, 0, 0); set(Calendar.MILLISECOND, 0)
                        }
                        if (end.before(start)) end.timeInMillis = start.timeInMillis
                        endDate = end

                        // 👇 GUARDA el rango en el back stack de esta pantalla ("search")
                        val label = "${format(start)} - ${format(end)}"
                        navController.currentBackStackEntry
                            ?.savedStateHandle
                            ?.set("date_range", label)
                        
                        // Also store individual dates for API calls and confirmation
                        val startDateStr = "${start.get(Calendar.YEAR)}-${String.format("%02d", start.get(Calendar.MONTH) + 1)}-${String.format("%02d", start.get(Calendar.DAY_OF_MONTH))}"
                        val endDateStr = "${end.get(Calendar.YEAR)}-${String.format("%02d", end.get(Calendar.MONTH) + 1)}-${String.format("%02d", end.get(Calendar.DAY_OF_MONTH))}"
                        
                        navController.currentBackStackEntry
                            ?.savedStateHandle
                            ?.set("startDate", startDateStr)
                        navController.currentBackStackEntry
                            ?.savedStateHandle
                            ?.set("endDate", endDateStr)
                    },
                    endInit.get(Calendar.YEAR),
                    endInit.get(Calendar.MONTH),
                    endInit.get(Calendar.DAY_OF_MONTH)
                ).apply { datePicker.minDate = start.timeInMillis }.show()
            },
            startInit.get(Calendar.YEAR),
            startInit.get(Calendar.MONTH),
            startInit.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    val dateLabel = if (startDate != null && endDate != null)
        "${format(startDate)} - ${format(endDate)}"
    else "Fechas"

    // Datos demo
    var locations by remember { mutableStateOf(listOf(
        LocationData(
            "Divina Providencia",
            LatLng(25.668394809524564, -100.30311761472923),
            "Albergue con espacios comunes y atención matutina.",
            capacity = 4
        ),
        LocationData(
            "Posada del Peregrino",
            LatLng(25.68334790328978, -100.34546687358926),
            "Opciones de estancia temporal y apoyo alimentario.",
            capacity = 6
        ),
        LocationData(
            "Alberge Contigo",
            LatLng(25.791571000975924, -100.1387095558184),
            "Centro con cupos limitados y registro diario.",
            capacity = 3
        )
    )) }

    // Estado: ubicación seleccionada
    var selectedLocation by remember { mutableStateOf<LocationData?>(null) }
    var selectedHostelData by remember { mutableStateOf<HostelData?>(null) }
    var hostelsResponse by remember { mutableStateOf<List<HostelData>?>(null) }
    LaunchedEffect(savedShelterName) {
        selectedLocation = locations.find { it.name == savedShelterName }
    }
    
    // Fetch hostels when dates are selected
    LaunchedEffect(startDate, endDate) {
        try {
            if (startDate != null && endDate != null) {
                val startDateStr = "${startDate!!.get(Calendar.YEAR)}-${String.format("%02d", startDate!!.get(Calendar.MONTH) + 1)}-${String.format("%02d", startDate!!.get(Calendar.DAY_OF_MONTH))}"
                val endDateStr = "${endDate!!.get(Calendar.YEAR)}-${String.format("%02d", endDate!!.get(Calendar.MONTH) + 1)}-${String.format("%02d", endDate!!.get(Calendar.DAY_OF_MONTH))}"
                
                scope.launch {
                    try {
                        reservationRepository.getHostels().collect { hostels ->
                            hostels?.let { hostelsList ->
                                hostelsResponse = hostelsList
                                // Update locations with API data
                                locations = hostelsList.mapIndexed { index, hostel ->
                                    // Assign different coordinates to each hostel to avoid overlap
                                    val coordinates = when (hostel.name) {
                                        "Posada del Peregrino" -> LatLng(25.668394809524564, -100.30311761472923)
                                        "Divina Providencia" -> LatLng(25.68334790328978, -100.34546687358926)
                                        "Apodaca" -> LatLng(25.791571000975924, -100.1387095558184)
                                        else -> {
                                            // Generate coordinates in a grid pattern for unknown hostels
                                            val baseLat = 25.668394809524564
                                            val baseLng = -100.30311761472923
                                            val offset = index * 0.01 // 0.01 degree offset per hostel
                                            LatLng(baseLat + offset, baseLng + offset)
                                        }
                                    }
                                    
                                    LocationData(
                                        name = hostel.name,
                                        latLng = coordinates,
                                        details = hostel.description,
                                        capacity = hostel.maxCapacity
                                    )
                                }
                            }
                        }
                    } catch (e: Exception) {
                        // API call failed, keep using demo data
                        // Don't crash the app
                    }
                }
            }
        } catch (e: Exception) {
            // Date parsing failed, keep using demo data
            // Don't crash the app
        }
    }
    
    // Update selected hostel data when location changes
    LaunchedEffect(selectedLocation, hostelsResponse) {
        selectedLocation?.let { location ->
            hostelsResponse?.find { it.name == location.name }?.let { hostel ->
                selectedHostelData = hostel
            }
        }
    }

    // Cámara - Default to Monterrey coordinates if no locations
    val defaultLocation = LatLng(25.668394809524564, -100.30311761472923)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(
            locations.firstOrNull()?.latLng ?: defaultLocation, 
            13f
        )
    }

    val scaffoldState = rememberBottomSheetScaffoldState()
    var showFilters by remember { mutableStateOf(false) }
    var showShelterPicker by remember { mutableStateOf(false) }

    // >>> Estado del diálogo de personas (movido acá porque el botón ahora está abajo)
    var showPersonDialog by remember { mutableStateOf(false) }
    var peopleCount by remember { mutableStateOf("1") }
    
    // Store people count in navigation state
    LaunchedEffect(peopleCount) {
        println("🔍 ReservePage storing peopleCount: $peopleCount")
        navController.currentBackStackEntry
            ?.savedStateHandle
            ?.set("peopleCount", peopleCount)
    }

    // --- UI ---
    BottomSheetScaffold(
        scaffoldState = scaffoldState,
        sheetPeekHeight = 0.dp,
        sheetShape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        sheetDragHandle = { BottomSheetDefaults.DragHandle() },
        sheetContent = {
            SelectionSheet(
                location = selectedLocation,
                hostelData = selectedHostelData,
                onDetailsClick = {
                    // Guarda los datos del shelter seleccionado y navega
                    selectedLocation?.let { loc ->
                        println("🔍 ReservePage navigating to shelter:")
                        println("  shelter_name: ${loc.name}")
                        println("  shelter_address: ${loc.details}")
                        
                        navController.currentBackStackEntry?.savedStateHandle?.apply {
                            set("shelter_name", loc.name)
                            set("shelter_address", loc.details)
                            set("shelter_lat", loc.latLng.latitude)
                            set("shelter_lng", loc.latLng.longitude)
                            // Pass individual hostel data fields instead of the whole object
                            selectedHostelData?.let { hostel ->
                                println("  hostel_description: ${hostel.description}")
                                set("hostel_id", hostel.id)
                                set("hostel_description", hostel.description)
                                set("hostel_max_capacity", hostel.maxCapacity)
                                set("hostel_price", hostel.price)
                                set("hostel_image_url", hostel.imageUrls.firstOrNull() ?: "")
                                set("hostel_location_url", hostel.locationUrl)
                            }
                            // Pass dates through navigation
                            if (startDate != null && endDate != null) {
                                val startDateStr = "${startDate!!.get(Calendar.YEAR)}-${String.format("%02d", startDate!!.get(Calendar.MONTH) + 1)}-${String.format("%02d", startDate!!.get(Calendar.DAY_OF_MONTH))}"
                                val endDateStr = "${endDate!!.get(Calendar.YEAR)}-${String.format("%02d", endDate!!.get(Calendar.MONTH) + 1)}-${String.format("%02d", endDate!!.get(Calendar.DAY_OF_MONTH))}"
                                println("  startDate: $startDateStr")
                                println("  endDate: $endDateStr")
                                set("startDate", startDateStr)
                                set("endDate", endDateStr)
                            }
                        }
                        navController.navigate("shelter")
                    }
                }
            )
        },
        topBar = {
            Surface(
                tonalElevation = 2.dp,
                shadowElevation = 0.dp,
                color = MaterialTheme.colorScheme.background
            ) {
                TopControls(
                    selectedDate = dateLabel,
                    onPickDate = { pickDateRange() },
                    onFilterClick = { showFilters = true }
                )
            }
        }
    ) { paddingValues ->
        Box(Modifier.fillMaxSize()) {
            // Mapa
            ShelterMap(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = paddingValues.calculateTopPadding()),
                locations = locations,
                cameraPositionState = cameraPositionState,
                selectedLocation = selectedLocation,
                onMarkerClick = { loc ->
                    selectedLocation = loc
                    scope.launch { scaffoldState.bottomSheetState.expand() }
                }
            )

            // ===== Grupo de botones debajo del chip: Personas + Albergues =====
            Row(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = paddingValues.calculateTopPadding() + 8.dp)
                    .wrapContentWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Botón circular: Número de personas (izquierda)
                Surface(
                    onClick = { showPersonDialog = true },
                    shape = CircleShape,
                    color = Accent,
                    tonalElevation = 3.dp,
                    modifier = Modifier.size(60.dp)
                ) {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        val peopleCountInt = peopleCount.toIntOrNull() ?: 1
                        if (peopleCountInt > 1) {
                            Text(
                                text = peopleCountInt.toString(),
                                color = Color.White,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold
                            )
                        } else {
                            Icon(
                                imageVector = ImageVector.vectorResource(id = R.drawable.person_add_24px),
                                contentDescription = "Número de personas",
                                tint = Color.White,
                                modifier = Modifier.size(30.dp)
                            )
                        }
                    }
                }

                // Botón "Albergues Disponibles" (se adapta al espacio del Row)
                ShelterPickerButton(
                    modifier = Modifier.wrapContentWidth()
                ) {
                    showShelterPicker = true
                }
            }

            // Barra de navegación inferior
            AppBottomBar(
                navController = navController,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(start = 12.dp, end = 12.dp, bottom = 28.dp) // 👈 sin colchón extra
            )
        }

        if (showFilters) {
            val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
            ModalBottomSheet(
                onDismissRequest = { showFilters = false },
                sheetState = sheetState
            ) {
                ServiceFilterSheet(
                    selected = selectedServices,
                    onToggle = { label ->
                        selectedServices =
                            if (selectedServices.contains(label)) selectedServices - label
                            else selectedServices + label
                    },
                    onClose = { showFilters = false }
                )
            }
        }


        // ...
        if (showShelterPicker) {
            val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
            ModalBottomSheet(
                onDismissRequest = { showShelterPicker = false },
                sheetState = sheetState
            ) {
                ShelterPickerSheet(
                    locations = locations,
                    selected = selectedLocation,
                    onSelect = { loc ->
                        selectedLocation = loc
                        showShelterPicker = false

                        handle?.apply {
                            set("shelter_name", loc.name)
                            set("shelter_lat",  loc.latLng.latitude)
                            set("shelter_lng",  loc.latLng.longitude)
                        }

                        scope.launch {
                            try {
                                cameraPositionState.animate(
                                    com.google.android.gms.maps.CameraUpdateFactory.newLatLngZoom(loc.latLng, 15f)
                                )
                            } catch (e: Exception) {
                                // Camera animation failed, but don't crash the app
                            }
                        }
                    },
                    onClose = { showShelterPicker = false }
                )
            }
        }

        // Diálogo para editar cantidad de personas
        if (showPersonDialog) {
            AlertDialog(
                onDismissRequest = { showPersonDialog = false },
                dismissButton = {
                    TextButton(onClick = { showPersonDialog = false }) {
                        Text(
                            "Cancelar",
                            color = Accent,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                },
                confirmButton = {
                    FilledTonalButton(
                        onClick = { showPersonDialog = false },
                        shape = RoundedCornerShape(26.dp),
                        colors = androidx.compose.material3.ButtonDefaults.filledTonalButtonColors(
                            containerColor = Accent,
                            contentColor = Color.White
                        ),
                        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 10.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Check,
                                contentDescription = "Aceptar",
                                tint = Color.White
                            )
                            Text(
                                "Aceptar",
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                },
                title = {
                    Text(
                        "Número de personas",
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                },
                text = {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            "Indica cuántas personas se registran contigo:",
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center,
                            fontSize = 18.sp
                        )
                        Spacer(Modifier.height(16.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            // Botón restar
                            Surface(
                                onClick = {
                                    val current = peopleCount.toIntOrNull() ?: 1
                                    val updated = if (current > 1) current - 1 else 1
                                    peopleCount = updated.toString()
                                    navController.currentBackStackEntry
                                        ?.savedStateHandle
                                        ?.set("peopleCount", peopleCount)
                                },
                                shape = CircleShape,
                                color = Accent,
                                tonalElevation = 2.dp,
                                modifier = Modifier.size(44.dp)
                            ) {
                                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Filled.Remove,
                                        contentDescription = "Disminuir",
                                        tint = Color.White
                                    )
                                }
                            }

                                OutlinedTextField(
                                    value = peopleCount,
                                    onValueChange = { newValue ->
                                        if (newValue.all { it.isDigit() }) {
                                            val sanitized = if (newValue.isBlank()) "" else newValue.trimStart('0').ifBlank { "1" }
                                            peopleCount = sanitized
                                            println("🔍 ReservePage peopleCount updated: $peopleCount")
                                            navController.currentBackStackEntry
                                                ?.savedStateHandle
                                                ?.set("peopleCount", peopleCount)
                                        }
                                    },
                                singleLine = true,
                                placeholder = {
                                    Text(
                                        "1",
                                        modifier = Modifier.fillMaxWidth(),
                                        textAlign = TextAlign.Center,
                                        fontSize = 26.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                },
                                textStyle = LocalTextStyle.current.copy(
                                    textAlign = TextAlign.Center,
                                    fontSize = 26.sp,
                                    fontWeight = FontWeight.SemiBold
                                ),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.width(120.dp)
                            )

                            // Botón sumar
                            Surface(
                                onClick = {
                                    val current = peopleCount.toIntOrNull() ?: 1
                                    val updated = current + 1
                                    peopleCount = updated.toString()
                                    navController.currentBackStackEntry
                                        ?.savedStateHandle
                                        ?.set("peopleCount", peopleCount)
                                },
                                shape = CircleShape,
                                color = Accent,
                                tonalElevation = 2.dp,
                                modifier = Modifier.size(44.dp)
                            ) {
                                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Filled.Add,
                                        contentDescription = "Aumentar",
                                        tint = Color.White
                                    )
                                }
                            }
                        }
                    }
                }
            )
        }

    }
}

/* ========================= COMPONENTES ========================= */

@Composable
private fun TopControls(
    selectedDate: String,
    onPickDate: () -> Unit,
    onFilterClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        val dateText = selectedDate.ifBlank { "Seleccionar Fechas" }
        val fontSizeSp = when {
            dateText.length <= 20 -> 22.sp
            dateText.length <= 26 -> 20.sp
            dateText.length <= 32 -> 18.sp
            else -> 16.sp
        }

        // CHIP de Fechas
        Surface(
            onClick = onPickDate,
            shape = RoundedCornerShape(32.dp),
            color = Accent,
            tonalElevation = 2.dp,
            modifier = Modifier.weight(1f)
        ) {
            Row(
                modifier = Modifier
                    .height(64.dp)
                    .fillMaxWidth()
                    .padding(horizontal = 18.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Filled.CalendarMonth,
                    contentDescription = "Fechas",
                    tint = Color.White,
                    modifier = Modifier.size(26.dp)
                )
                Spacer(Modifier.width(10.dp))
                Text(
                    text = dateText,
                    color = Color.White,
                    style = MaterialTheme.typography.titleLarge.copy(fontSize = fontSizeSp),
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    textAlign = TextAlign.Center
                )
            }
        }

        // Botón de Filtros (se mantiene en top bar)
        Surface(
            onClick = onFilterClick,
            shape = CircleShape,
            color = Accent,
            tonalElevation = 2.dp,
            modifier = Modifier.size(64.dp)
        ) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = ImageVector.vectorResource(id = R.drawable.filter_list_24px),
                    contentDescription = "Filtros",
                    tint = Color.White,
                    modifier = Modifier.size(40.dp)
                )
            }
        }
    }
}

@Composable
private fun ShelterMap(
    modifier: Modifier = Modifier,
    locations: List<LocationData>,
    cameraPositionState: CameraPositionState,
    selectedLocation: LocationData?,
    onMarkerClick: (LocationData) -> Unit
) {
    GoogleMap(
        modifier = modifier,
        cameraPositionState = cameraPositionState,
        uiSettings = MapUiSettings(
            zoomControlsEnabled = false,
            myLocationButtonEnabled = false,
            compassEnabled = false
        ),
        properties = MapProperties(isMyLocationEnabled = false)
    ) {
        locations.forEach { location ->
            val isSelected = selectedLocation?.name == location.name
            Marker(
                state = MarkerState(location.latLng),
                title = location.name,
                icon = createTextMarker(location.name, isSelected),
                onClick = { onMarkerClick(location); true }
            )
        }
    }
}

@Composable
private fun SelectionSheet(
    location: LocationData?,
    hostelData: HostelData?,
    onDetailsClick: () -> Unit
) {
    if (location == null) return

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp)
    ) {
        // Enhanced header with image and gradient
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Surface(
                modifier = Modifier.fillMaxSize(),
                shape = RoundedCornerShape(20.dp),
                color = Accent.copy(alpha = 0.1f)
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    // Hostel image if available
                    if (hostelData?.imageUrls != null && hostelData.imageUrls.isNotEmpty()) {
                        AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(hostelData.imageUrls.first())
                                .crossfade(true)
                                .build(),
                            contentDescription = "Hostel Image",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop,
                            error = painterResource(id = R.drawable.ic_launcher_foreground),
                            placeholder = painterResource(id = R.drawable.ic_launcher_foreground)
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Filled.LocationOn,
                            contentDescription = "Hostel",
                            modifier = Modifier.size(80.dp),
                            tint = Accent.copy(alpha = 0.6f)
                        )
                    }
                }
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Hostel name with better styling
            Text(
                text = location.name,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(Modifier.height(8.dp))

            // Description if available
            if (hostelData?.description != null) {
                Text(
                    text = hostelData.description,
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth(),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(Modifier.height(16.dp))
            }

            // Enhanced capacity display
            Row(
                horizontalArrangement = Arrangement.spacedBy(24.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Available spots
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = location.capacity.toString(),
                        style = MaterialTheme.typography.titleLarge.copy(fontSize = 32.sp),
                        fontWeight = FontWeight.ExtraBold,
                        color = Accent
                    )
                    Text(
                        text = "Cupos Disponibles",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Divider
                Box(
                    modifier = Modifier
                        .width(1.dp)
                        .height(40.dp)
                        .background(MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                )

                // Max capacity if available
                if (hostelData?.maxCapacity != null) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = hostelData.maxCapacity.toString(),
                            style = MaterialTheme.typography.titleLarge.copy(fontSize = 32.sp),
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "Capacidad Total",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(Modifier.height(24.dp))

            // Enhanced button with icon
            FilledTonalButton(
                onClick = onDetailsClick,
                shape = RoundedCornerShape(28.dp),
                modifier = Modifier
                    .height(56.dp)
                    .widthIn(min = 200.dp),
                contentPadding = PaddingValues(horizontal = 24.dp, vertical = 10.dp),
                colors = androidx.compose.material3.ButtonDefaults.filledTonalButtonColors(
                    containerColor = Accent,
                    contentColor = Color.White
                )
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.CalendarMonth,
                        contentDescription = "Hacer Reservación",
                        tint = Color.White
                    )
                    Text(
                        "Hacer Reservación",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}

private data class ServiceItem(val label: String, val iconRes: Int)

@Composable
private fun ServiceFilterSheet(
    selected: Set<String>,
    onToggle: (String) -> Unit,
    onClose: () -> Unit
) {
    val accent = Color(0xFF009AA7)
    val services = listOf(
        ServiceItem("Desayuno",   R.drawable.breakfast_dining_24px),
        ServiceItem("Comida",     R.drawable.meal_lunch_24px),
        ServiceItem("Cena",       R.drawable.meal_dinner_24px),
        ServiceItem("Lavadoras",  R.drawable.local_laundry_service_24px),
        ServiceItem("Duchas",     R.drawable.shower_24px),
        ServiceItem("Psicólogo",  R.drawable.neurology_24px),
        ServiceItem("Chequeo Dental", R.drawable.dentistry_24px),
        ServiceItem("Expedición de Oficios", R.drawable.article_24px),

    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Filtros", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
            TextButton(onClick = onClose) { Text("Cerrar", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold) }
        }

        Spacer(Modifier.height(8.dp))

        services.chunked(2).forEach { row ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                row.forEach { s ->
                    ServiceCard(
                        item = s,
                        selected = s.label in selected,
                        accent = accent,
                        onClick = { onToggle(s.label) }
                    )
                }
            }
        }

        Spacer(Modifier.height(12.dp))
    }
}

@Composable
private fun ServiceCard(
    item: ServiceItem,
    selected: Boolean,
    accent: Color,
    onClick: () -> Unit
) {
    val shape = RoundedCornerShape(16.dp)
    val borderColor = if (selected) accent else MaterialTheme.colorScheme.outlineVariant
    val iconTint = if (selected) accent else MaterialTheme.colorScheme.onSurface
    val containerColor = if (selected) accent.copy(alpha = 0.50f) else MaterialTheme.colorScheme.surfaceVariant

    Surface(
        onClick = onClick,
        shape = shape,
        tonalElevation = if (selected) 2.dp else 0.dp,
        border = BorderStroke(2.dp, borderColor)
    ) {
        Column(
            modifier = Modifier
                .width(140.dp)
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Surface(shape = RoundedCornerShape(18.dp), color = containerColor) {
                Box(modifier = Modifier.size(96.dp), contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = ImageVector.vectorResource(id = item.iconRes),
                        contentDescription = item.label,
                        tint = iconTint,
                        modifier = Modifier.size(56.dp)
                    )
                }
            }
            Spacer(Modifier.height(8.dp))
            Text(
                text = item.label,
                style = MaterialTheme.typography.titleMedium.copy(fontSize = 18.sp, lineHeight = 22.sp),
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center,
                maxLines = 2
            )
        }
    }
}

@Composable
private fun ShelterPickerButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(28.dp),
        color = Accent,
        tonalElevation = 3.dp,
        border = BorderStroke(1.5.dp, Accent),
        modifier = modifier
            .height(60.dp)
            .wrapContentWidth()
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 18.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Filled.LocationOn,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(26.dp)
            )
            Spacer(Modifier.width(10.dp))
            Text(
                "Albergues Disponibles",
                style = MaterialTheme.typography.titleLarge,
                color = Color.White,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
private fun ShelterPickerSheet(
    locations: List<LocationData>,
    selected: LocationData?,
    onSelect: (LocationData) -> Unit,
    onClose: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Albergues", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
            TextButton(onClick = onClose) { Text("Cerrar", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold) }
        }

        Spacer(Modifier.height(8.dp))

        locations.forEach { loc ->
            val isSelected = selected?.name == loc.name
            Surface(
                onClick = { onSelect(loc) },
                shape = RoundedCornerShape(16.dp),
                tonalElevation = if (isSelected) 2.dp else 0.dp,
                border = BorderStroke(2.dp, if (isSelected) Accent else MaterialTheme.colorScheme.outlineVariant),
                color = if (isSelected) Accent.copy(alpha = 0.08f) else MaterialTheme.colorScheme.surface,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp)
            ) {
                Row(
                    modifier = Modifier
                        .heightIn(min = 56.dp)
                        .padding(horizontal = 14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            Modifier
                                .size(10.dp)
                                .background(if (isSelected) Accent else MaterialTheme.colorScheme.outlineVariant, CircleShape)
                        )
                        Spacer(Modifier.width(10.dp))
                        Column {
                            Text(
                                text = loc.name,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium,
                                color = if (isSelected) Accent else MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(Modifier.height(2.dp))
                        }
                    }
                    Icon(
                        imageVector = Icons.Filled.LocationOn,
                        contentDescription = null,
                        tint = if (isSelected) Accent else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }
        }

        Spacer(Modifier.height(8.dp))
    }
}
