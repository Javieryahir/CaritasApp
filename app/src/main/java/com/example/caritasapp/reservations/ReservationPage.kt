package com.example.caritasapp.reservations

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
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ListAlt
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.DirectionsCar
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.createBitmap
import androidx.navigation.NavController
import com.example.caritasapp.R
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
import kotlinx.coroutines.launch
import java.util.Calendar
import androidx.compose.ui.platform.testTag


private val Accent = Color(0xFF009AA7)
private const val ACCENT_INT = 0xFF009AA7.toInt()

// ---------- Modelo ----------
data class LocationData(
    val name: String,
    val latLng: LatLng,
    val details: String,
    val imageUrl: String? = null,
    val capacity: Int = 4,
    val distanceLabel: String = "1.2 miles"
)

// ---------- Util: marcador con texto estilo "label" ----------
fun createTextMarker(text: String, selected: Boolean): BitmapDescriptor {
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
    return BitmapDescriptorFactory.fromBitmap(bmp)
}

// ---------- Pantalla principal ----------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReservationPage(navController: NavController) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    // Rango de fechas: inicio y fin
    var startDate by remember { mutableStateOf<Calendar?>(null) }
    var endDate by remember { mutableStateOf<Calendar?>(null) }

    // utilidad para formato dd/MM/yyyy
    fun format(c: Calendar?): String =
        if (c == null) "" else "${c.get(Calendar.DAY_OF_MONTH)}/${c.get(Calendar.MONTH) + 1}/${c.get(Calendar.YEAR)}"

    // Abre 2 diálogos: primero INICIO, luego FIN (con min = inicio)
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

                // segundo diálogo: FIN
                val endInit = (endDate ?: start)
                DatePickerDialog(
                    context,
                    { _: DatePicker, y2: Int, m2: Int, d2: Int ->
                        val end = Calendar.getInstance().apply {
                            set(y2, m2, d2, 0, 0, 0); set(Calendar.MILLISECOND, 0)
                        }
                        // si el usuario elige antes del inicio, corrige al inicio
                        if (end.before(start)) end.timeInMillis = start.timeInMillis
                        endDate = end
                    },
                    endInit.get(Calendar.YEAR),
                    endInit.get(Calendar.MONTH),
                    endInit.get(Calendar.DAY_OF_MONTH)
                ).apply {
                    datePicker.minDate = start.timeInMillis
                }.show()
            },
            startInit.get(Calendar.YEAR),
            startInit.get(Calendar.MONTH),
            startInit.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

// etiqueta que se muestra en el pill
    val dateLabel = if (startDate != null && endDate != null)
        "${format(startDate)} - ${format(endDate)}"
    else
        "Seleccionar Fechas"


    // Estado: ubicación seleccionada (para el sheet)
    var selectedLocation by remember { mutableStateOf<LocationData?>(null) }

    // Datos de ejemplo
    val locations = remember {
        listOf(
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
                capacity = 6,
                distanceLabel = "2.4 miles"
            ),
            LocationData(
                "Alberge Contigo",
                LatLng(25.791571000975924, -100.1387095558184),
                "Centro con cupos limitados y registro diario.",
                capacity = 3,
                distanceLabel = "0.8 miles"
            )
        )
    }

    // Cámara
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(locations.first().latLng, 13f)
    }

    val scaffoldState = rememberBottomSheetScaffoldState()
    var showFilters by remember { mutableStateOf(false) }
    var showShelterPicker by remember { mutableStateOf(false) }
    var selectedServices by rememberSaveable { mutableStateOf(setOf<String>()) }

    // --- UI ---
    BottomSheetScaffold(
        scaffoldState = scaffoldState,
        sheetPeekHeight = 0.dp,
        sheetShape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        sheetDragHandle = { BottomSheetDefaults.DragHandle() },
        sheetContent = {
            SelectionSheet(
                location = selectedLocation,
                onDetailsClick = { navController.navigate("shelter") }
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

            // Lista rápida de albergues debajo del chip
            // Botón "Albergues" debajo del chip (abre el selector)
            ShelterPickerButton(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(
                        top = paddingValues.calculateTopPadding() + 8.dp
                    )
                    .widthIn(min = 280.dp)
                    .wrapContentWidth()
            ) {
                showShelterPicker = true
            }



            // Barra de navegación inferior (solo visual)
            BottomNavBar(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 8.dp)
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
                        scope.launch {
                            cameraPositionState.animate(
                                com.google.android.gms.maps.CameraUpdateFactory.newLatLngZoom(loc.latLng, 15f)
                            )
                        }
                    },
                    onClose = { showShelterPicker = false }
                )
            }
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
        // Texto mostrado en el chip y tamaño adaptativo
        val dateText = selectedDate.ifBlank { "Seleccionar Fechas" }
        val fontSizeSp = when {
            dateText.length <= 20 -> 22.sp
            dateText.length <= 26 -> 20.sp
            dateText.length <= 32 -> 18.sp
            else -> 16.sp
        }

        // CHIP de fechas: contenido centrado
        Surface(
            onClick = onPickDate,
            shape = RoundedCornerShape(32.dp),
            color = MaterialTheme.colorScheme.surface,
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
                    contentDescription = "Seleccionar fechas",
                    modifier = Modifier.size(26.dp)
                )
                Spacer(Modifier.width(10.dp))
                Text(
                    text = dateText,
                    style = MaterialTheme.typography.titleLarge.copy(fontSize = fontSizeSp),
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    textAlign = TextAlign.Center,
                    overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                )
            }
        }

        // Botón de filtros: tamaño ESTÁTICO
        Surface(
            onClick = onFilterClick,
            shape = CircleShape,
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 2.dp,
            modifier = Modifier
                .size(64.dp)
                .testTag("btn-filtros")
        ) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = ImageVector.vectorResource(id = R.drawable.filter_list_24px),
                    contentDescription = "Filtros",
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
    onDetailsClick: () -> Unit
) {
    if (location == null) return

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp)
    ) {
        // Imagen (placeholder)
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surfaceVariant
        ) { /* TODO: cargar imagen real con Coil */ }

        // Contenido centrado
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Nombre centrado
            Text(
                text = location.name,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(16.dp))

            // Cupos
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = location.capacity.toString(),
                    style = MaterialTheme.typography.titleLarge.copy(fontSize = 40.sp),
                    fontWeight = FontWeight.ExtraBold
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    text = "Cupos",
                    style = MaterialTheme.typography.titleMedium.copy(fontSize = 20.sp),
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(Modifier.height(18.dp))

            // Botón "Detalles" debajo de Cupos
            FilledTonalButton(
                onClick = onDetailsClick,
                shape = RoundedCornerShape(28.dp),
                modifier = Modifier
                    .height(56.dp)
                    .widthIn(min = 200.dp),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(
                    horizontal = 24.dp, vertical = 10.dp
                )
            ) {
                Text(
                    "Detalles",
                    style = MaterialTheme.typography.titleLarge, // ~22sp por defecto
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
private fun BottomNavBar(modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp),
        shape = RoundedCornerShape(18.dp),
        tonalElevation = 6.dp
    ) {
        NavigationBar(tonalElevation = 0.dp) {
            NavItem(Icons.Outlined.Home, "Reservar", selected = true)
            NavItem(Icons.AutoMirrored.Outlined.ListAlt, "Reservaciones")
            NavItem(Icons.Outlined.DirectionsCar, "Transporte")
            NavItem(Icons.Outlined.AccountCircle, "Cuenta")
        }
    }
}

@Composable
private fun RowScope.NavItem(
    icon: ImageVector,
    label: String,
    selected: Boolean = false
) {
    NavigationBarItem(
        selected = selected,
        onClick = { /* TODO navegar */ },
        icon = { Icon(icon, contentDescription = label) },
        label = { Text(label) }
    )
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
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "Filtros",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
            TextButton(onClick = onClose) {
                Text(
                    "Cerrar",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
            }
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
    val containerColor = MaterialTheme.colorScheme.surfaceVariant

    Surface(
        onClick = onClick,
        shape = shape,
        tonalElevation = if (selected) 2.dp else 0.dp,
        border = BorderStroke(2.dp, borderColor)
    ) {
        Column(
            modifier = Modifier
                .width(140.dp)          // ancho de la tarjeta
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Surface(
                shape = RoundedCornerShape(18.dp),
                color = containerColor
            ) {
                Box(
                    modifier = Modifier.size(96.dp),
                    contentAlignment = Alignment.Center
                ) {
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
                style = MaterialTheme.typography.titleMedium.copy(
                    fontSize = 18.sp,
                    lineHeight = 22.sp
                ),
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
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 3.dp,
        border = BorderStroke(1.5.dp, MaterialTheme.colorScheme.outlineVariant),
        modifier = modifier
    ) {
        Row(
            modifier = Modifier
                .height(60.dp)
                .padding(horizontal = 18.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Filled.LocationOn,
                contentDescription = null,
                modifier = Modifier.size(26.dp)
            )
            Spacer(Modifier.width(10.dp))
            Text(
                "Albergues Disponibles",
                style = MaterialTheme.typography.titleLarge,
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
            Text(
                "Albergues",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
            TextButton(onClick = onClose) {
                Text(
                    "Cerrar",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
            }
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
                            Text(
                                text = loc.distanceLabel,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
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