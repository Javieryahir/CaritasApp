package com.example.caritasapp.reserve

import android.content.Intent
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
        Image(
            painter = painterResource(id = R.drawable.shelter),
            contentDescription = "Imagen del albergue",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .height(240.dp)
        )

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
                    text = name,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.ExtraBold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(10.dp))
                Text(
                    text = addr,
                    color = Color.Gray,
                    fontSize = 20.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )

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

                // Botón "Servicios disponibles"
                Button(
                    onClick = { showServices = true },
                    shape = RoundedCornerShape(28.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Accent,
                        contentColor = Color.White
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(64.dp)
                ) {
                    Text(
                        "Servicios disponibles",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Spacer(Modifier.height(22.dp))

                // Fila: "Política de Salud" + botón circular de atrás a la derecha
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // 1) Botón circular de atrás (izquierda)
                    FilledIconButton(
                        onClick = { navController.popBackStack() },
                        shape = CircleShape,
                        colors = IconButtonDefaults.filledIconButtonColors(
                            containerColor = Accent,
                            contentColor = Color.White
                        ),
                        modifier = Modifier.size(64.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Atrás"
                        )
                    }

                    // 2) Botón "Política de Salud" (ocupa el resto)
                    Button(
                        onClick = { // dentro del onClick del botón "Política de Salud"
                            val count = navController
                                .getBackStackEntry("search")
                                .savedStateHandle
                                .get<String>("peopleCount")
                                ?.toIntOrNull() ?: 1

                            navController.navigate("health/$count")
                        },
                        shape = RoundedCornerShape(28.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Accent,
                            contentColor = Color.White
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .height(64.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.MedicalServices,
                            contentDescription = "Política de Salud",
                            modifier = Modifier.padding(end = 12.dp)
                        )
                        Text("Política de Salud", fontSize = 22.sp, fontWeight = FontWeight.SemiBold)
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
