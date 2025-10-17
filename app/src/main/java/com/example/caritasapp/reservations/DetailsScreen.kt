package com.example.caritasapp.reservations

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
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.Phone
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.caritasapp.R
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*

private val Accent = Color(0xFF009CA6)
private val Teal   = Color(0xFF5D97A3)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailsScreen(navController: NavController) {
    val name = "Nombre de Locación"
    val addr = "Dirección del lugar"
    val latLng = LatLng(25.666, -100.316)

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(latLng, 15f)
    }
    val context = LocalContext.current
    val mapsUrl = "https://maps.google.com/?q=${latLng.latitude},${latLng.longitude}"

    var showServices by remember { mutableStateOf(false) }
    val services = remember {
        listOf(
            "Desayuno" to "$15", "Comida" to "$15", "Cena" to "$10",
            "Lavadoras" to "$10", "Duchas" to "$10", "Traslados" to "$20"
        )
    }

    Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
        Image(
            painter = painterResource(id = R.drawable.shelter),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .height(240.dp)
        )

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
                Text(
                    name,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.ExtraBold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(10.dp))
                Text(
                    addr,
                    color = Color.Gray,
                    fontSize = 20.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(20.dp))

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

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp)
                        .clickable { context.startActivity(Intent(Intent.ACTION_VIEW, mapsUrl.toUri())) }
                ) {
                    Icon(Icons.Filled.Link, null, tint = Accent)
                    Spacer(Modifier.width(8.dp))
                    Text(
                        "Abrir en Google Maps",
                        color = Accent,
                        fontSize = 20.sp,
                        textDecoration = TextDecoration.Underline,
                        textAlign = TextAlign.Center
                    )
                }

                Spacer(Modifier.height(26.dp))

                Button(
                    onClick = { showServices = true },
                    shape = RoundedCornerShape(28.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Accent, contentColor = Color.White),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(64.dp)
                ) {
                    Text(
                        "Servicios disponibles",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.SemiBold,
                        textAlign = TextAlign.Center
                    )
                }

                Spacer(Modifier.height(28.dp))

                // ---------- Sección de contacto ----------
                Text(
                    "¿Quejas o Sugerencias?",
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
                Text(
                    "Contáctenos aquí",
                    color = Color.Gray,
                    fontSize = 18.sp,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )

                Spacer(Modifier.height(14.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Atrás
                    FilledIconButton(
                        onClick = { navController.popBackStack() },
                        shape = CircleShape,
                        colors = IconButtonDefaults.filledIconButtonColors(
                            containerColor = Teal, contentColor = Color.White
                        ),
                        modifier = Modifier.size(64.dp)
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Atrás")
                    }

                    // Email (tamaño normal)
                    ContactPill(
                        label = "Email",
                        icon = { Icon(Icons.Filled.Email, null) },
                        modifier = Modifier.weight(1f),
                        textSize = 20.sp
                    ) {
                        val intent = Intent(
                            Intent.ACTION_SENDTO,
                            "mailto:soporte@caritas.org".toUri()
                        )
                        context.startActivity(intent)
                    }

                    // Celular (texto más pequeño)
                    ContactPill(
                        label = "Celular",
                        icon = { Icon(Icons.Filled.Phone, null) },
                        modifier = Modifier.weight(1f),
                        textSize = 18.sp
                    ) {
                        val intent = Intent(Intent.ACTION_DIAL, "tel:5555555555".toUri())
                        context.startActivity(intent)
                    }
                }
            }
        }
    }

    if (showServices) {
        val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        ModalBottomSheet(onDismissRequest = { showServices = false }, sheetState = sheetState) {
            Column(
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    "Servicios disponibles",
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(10.dp))
                services.forEachIndexed { i, (n, p) ->
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(n, fontSize = 20.sp, fontWeight = FontWeight.Medium)
                        Text(p, fontSize = 20.sp, fontWeight = FontWeight.SemiBold)
                    }
                    if (i != services.lastIndex) {
                        HorizontalDivider(
                            Modifier,
                            DividerDefaults.Thickness,
                            DividerDefaults.color
                        )
                    }
                }
                Spacer(Modifier.height(8.dp))
            }
        }
    }
}

@Composable
private fun ContactPill(
    label: String,
    icon: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    textSize: androidx.compose.ui.unit.TextUnit = 20.sp,   // ← textSize ANTES del onClick
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        shape = RoundedCornerShape(36.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Teal,
            contentColor = Color.White
        ),
        modifier = modifier.height(64.dp)
    ) {
        icon()
        Spacer(Modifier.width(10.dp))
        Text(
            text = label,
            fontSize = textSize,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun PreviewReservationsDetails() {
    DetailsScreen(navController = rememberNavController())
}
