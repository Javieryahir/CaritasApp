package com.example.caritasapp.reserve

import android.content.Intent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material3.*
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import androidx.navigation.NavController
import com.example.caritasapp.R
import com.example.caritasapp.data.HostelData
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*

private val Accent = Color(0xFF009CA6)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShelterDetailsScreen(navController: NavController) {
    // Datos recibidos desde ReservationPage
    val sh = navController.previousBackStackEntry?.savedStateHandle
    val name  = sh?.get<String>("shelter_name") ?: "Nombre de Locación"
    val addr  = sh?.get<String>("shelter_address") ?: "Dirección del lugar"
    val lat   = sh?.get<Double>("shelter_lat") ?: 25.666
    val lng   = sh?.get<Double>("shelter_lng") ?: -100.316
    val latLng = LatLng(lat, lng)
    
    // Get hostel data from API (individual fields)
    val hostelId = sh?.get<String>("hostel_id")
    val hostelDescription = sh?.get<String>("hostel_description")
    val hostelMaxCapacity = sh?.get<Int>("hostel_max_capacity")
    val hostelAvailableSpaces = sh?.get<Int>("hostel_available_spaces")
    val hostelImageUrl = sh?.get<String>("hostel_image_url")
    val hostelLocationUrl = sh?.get<String>("hostel_location_url")

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(latLng, 15f)
    }

    val context = LocalContext.current
    val mapsUrl = "https://maps.google.com/?q=$lat,$lng"

    // Sheet de servicios (solo lectura)
    var showServices by remember { mutableStateOf(false) }
    val services = remember {
        listOf(
            Service("Desayuno", "$15"),
            Service("Comida", "$15"),
            Service("Cena", "$10"),
            Service("Lavadoras", "$10"),
            Service("Duchas", "$10"),
            Service("Traslados", "$20"),
            Service("Hospedaje Diario", "$30"),
            Service("Psicólogo", "Gratis"),
            Service("Dentista", "Gratis"),
            Service("Expedición de oficios", "$5"),
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize(), // <- sin statusBarsPadding: la imagen vuelve a quedar detrás de la barra de estado
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Imagen superior (se ve detrás de wifi/batería)
        if (hostelImageUrl != null && hostelImageUrl.isNotEmpty()) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(hostelImageUrl)
                    .crossfade(true)
                    .build(),
                contentDescription = "Imagen del albergue",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(240.dp),
                error = painterResource(id = R.drawable.shelter),
                placeholder = painterResource(id = R.drawable.shelter)
            )
        } else {
            Image(
                painter = painterResource(id = R.drawable.shelter),
                contentDescription = "Imagen del albergue",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(240.dp)
            )
        }

        // Contenedor principal
        Surface(
            color = Color.White,
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
            tonalElevation = 6.dp,
            modifier = Modifier.fillMaxSize()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp, vertical = 18.dp)
                    .padding(bottom = 28.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Título + dirección (sin icono)
                Text(
                    text = name, // Use the name from location data
                    fontSize = 32.sp,
                    fontWeight = FontWeight.ExtraBold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(10.dp))
                
                // Description from API if available, otherwise show address
                if (hostelDescription != null && hostelDescription.isNotEmpty()) {
                    Text(
                        text = hostelDescription,
                        color = Color.Gray,
                        fontSize = 18.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(Modifier.height(8.dp))
                } else {
                    // Only show address if we don't have API description
                    Text(
                        text = addr,
                        color = Color.Gray,
                        fontSize = 20.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(Modifier.height(8.dp))
                }
                
                // Show capacity information if available
                if (hostelAvailableSpaces != null && hostelMaxCapacity != null) {
                    Spacer(Modifier.height(16.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        // Available spaces
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = hostelAvailableSpaces.toString(),
                                fontSize = 28.sp,
                                fontWeight = FontWeight.Bold,
                                color = Accent
                            )
                            Text(
                                text = "Cupos Disponibles",
                                fontSize = 14.sp,
                                color = Color.Gray
                            )
                        }
                        
                        // Max capacity
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = hostelMaxCapacity.toString(),
                                fontSize = 28.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Gray
                            )
                            Text(
                                text = "Capacidad Total",
                                fontSize = 14.sp,
                                color = Color.Gray
                            )
                        }
                    }
                }

                Spacer(Modifier.height(20.dp))

                // Mapa
                GoogleMap(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(250.dp)
                        .clip(RoundedCornerShape(16.dp)),
                    cameraPositionState = cameraPositionState,
                    uiSettings = MapUiSettings(
                        zoomControlsEnabled = false,
                        myLocationButtonEnabled = false,
                        compassEnabled = false
                    ),
                    properties = MapProperties(isMyLocationEnabled = false)
                ) {
                    Marker(state = MarkerState(latLng), title = name)
                }

                Spacer(Modifier.height(14.dp))

                // Link a Google Maps
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp)
                        .clickable {
                            context.startActivity(
                                Intent(Intent.ACTION_VIEW, mapsUrl.toUri())
                            )
                        }
                ) {
                    Icon(Icons.Filled.Link, contentDescription = null, tint = Accent)
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = "Abrir en Google Maps",
                        color = Accent,
                        fontSize = 20.sp,
                        textDecoration = TextDecoration.Underline
                    )
                }

                Spacer(Modifier.height(26.dp))

                // Botón "Servicios disponibles" (secundario / tonal)
                FilledTonalButton(
                    onClick = { showServices = true },
                    shape = RoundedCornerShape(24.dp),
                    colors = ButtonDefaults.filledTonalButtonColors(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                ) {
                    Text(
                        "Servicios disponibles",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                Spacer(Modifier.height(22.dp))

                // Indicador de siguiente paso
                Text(
                    text = "Siguiente paso para reservar: Política de Salud",
                    color = Accent,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 6.dp)
                )

                // Fixed bottom buttons with modern design
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
                            onClick = { navController.popBackStack() },
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

                        // Continue button
                        Button(
                            onClick = { // dentro del onClick del botón "Política de Salud"
                                println("🔍 DetailsScreen Navigation Debug:")
                                
                                val count = navController
                                    .getBackStackEntry("search")
                                    .savedStateHandle
                                    .get<String>("peopleCount")
                                    ?.toIntOrNull() ?: 1
                                
                                println("  peopleCount: $count")

                                // Pass dates through to health forms
                                val currentHandle = navController.currentBackStackEntry?.savedStateHandle
                                val startDate = currentHandle?.get<String>("startDate")
                                val endDate = currentHandle?.get<String>("endDate")
                                
                                println("  startDate: $startDate")
                                println("  endDate: $endDate")
                                
                                if (startDate != null && endDate != null) {
                                    navController.currentBackStackEntry?.savedStateHandle?.apply {
                                        set("startDate", startDate)
                                        set("endDate", endDate)
                                    }
                                    println("  ✅ Dates passed to health forms")
                                } else {
                                    println("  ❌ Missing dates for health forms")
                                }

                                println("  🚀 Navigating to: health/$count")
                                navController.navigate("health/$count")
                            },
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Accent,
                                contentColor = Color.White,
                                disabledContainerColor = Accent.copy(alpha = 0.6f),
                                disabledContentColor = Color.White.copy(alpha = 0.7f)
                            ),
                            modifier = Modifier
                                .height(56.dp)
                                .weight(1f)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.MedicalServices,
                                contentDescription = "Política de Salud",
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(Modifier.width(8.dp))
                            Text("Siguiente: Política de Salud", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }
        }
    }

    // ===== Sheet con la lista de servicios =====
    if (showServices) {
        val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        ModalBottomSheet(
            onDismissRequest = { showServices = false },
            sheetState = sheetState
        ) {
            ServicesSheetContent(
                items = services,
                onClose = { showServices = false }
            )
        }
    }
}

/* ===== Auxiliares ===== */

private data class Service(val name: String, val price: String)

@Composable
private fun ServicesSheetContent(
    items: List<Service>,
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
                "Servicios disponibles",
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold
            )
            TextButton(onClick = onClose) {
                Text("Cerrar", fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
            }
        }

        Spacer(Modifier.height(10.dp))

        items.forEachIndexed { index, s ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(s.name, fontSize = 20.sp, fontWeight = FontWeight.Medium)
                Text(
                    s.price,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = if (s.price.equals("Gratis", true)) Accent
                    else MaterialTheme.colorScheme.onSurface
                )
            }
            if (index != items.lastIndex) {
                HorizontalDivider()
            }
        }
        Spacer(Modifier.height(8.dp))
    }
}
