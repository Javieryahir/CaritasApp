package com.example.caritasapp.reservations

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.caritasapp.R

private val Teal = Color(0xFF5D97A3)
private val CardStroke = Color(0x33000000)

/* -------------------- Composable de orquestación (con NavController) -------------------- */

@Composable
fun ConfirmReservation(
    navController: NavController
) {
    // Intenta leer desde rutas conocidas del back stack
    val shelterHandle = remember(navController) {
        runCatching { navController.getBackStackEntry("shelter").savedStateHandle }.getOrNull()
    }
    val searchHandle = remember(navController) {
        runCatching { navController.getBackStackEntry("search").savedStateHandle }.getOrNull()
    }
    val healthHandle = remember(navController) {
        runCatching { navController.getBackStackEntry("health").savedStateHandle }.getOrNull()
    }

    // Priorizamos "shelter" y luego "search"/"health"
    val shelterName = shelterHandle?.get<String>("shelter_name")
        ?: searchHandle?.get<String>("shelter_name")
        ?: healthHandle?.get<String>("shelter_name")
        ?: "Albergue seleccionado"

    val dateRange = shelterHandle?.get<String>("date_range")
        ?: searchHandle?.get<String>("date_range")
        ?: healthHandle?.get<String>("date_range")
        ?: "Fechas por confirmar"

    ConfirmReservationContent(
        shelterName = shelterName,
        dateRange = dateRange,
        onBack = { navController.popBackStack() },
        onConfirm = { navController.navigate("waiting") }
    )
}

/* -------------------- UI pura (apta para Preview) -------------------- */

@Composable
private fun ConfirmReservationContent(
    shelterName: String,
    dateRange: String,
    // puedes dejarlo, ya no se usa
    onBack: () -> Unit,
    onConfirm: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .padding(20.dp)
    ) {
        // ✅ SIN botón de cerrar

        // Contenido centrado
        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .fillMaxWidth(0.94f),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Revisar y Confirmar",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(16.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, CardStroke, RoundedCornerShape(24.dp))
                    .padding(18.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(id = R.drawable.shelter),
                    contentDescription = "Locación",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color.LightGray, RoundedCornerShape(16.dp))
                )

                Spacer(Modifier.height(14.dp))

                Text(
                    text = shelterName,
                    fontSize = 30.sp,
                    fontWeight = FontWeight.SemiBold,
                    lineHeight = 34.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(18.dp))
                HorizontalDivider(thickness = 2.dp, color = Color(0xFFE5E5E5))
                Spacer(Modifier.height(12.dp))

                Text(
                    "Fechas",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(6.dp))
                Text(
                    dateRange,
                    fontSize = 20.sp,
                    color = Color(0xFF5F5F5F),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(16.dp))
                HorizontalDivider(thickness = 2.dp, color = Color(0xFFE5E5E5))
                Spacer(Modifier.height(12.dp))

                Text(
                    "Total",
                    fontSize = 30.sp,
                    fontWeight = FontWeight.ExtraBold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    "~$100 Pesos",
                    fontSize = 38.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(Modifier.height(18.dp))

            // ✅ Botones: Atrás (teal con flecha blanca) + Confirmar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                FilledIconButton(
                    onClick = onBack,
                    shape = CircleShape,
                    modifier = Modifier.size(84.dp),
                    colors = IconButtonDefaults.filledIconButtonColors(
                        containerColor = Teal,   // mismo color que confirmar
                        contentColor = Color.White
                    )
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Atrás",
                        tint = Color.White,      // flecha blanca
                        modifier = Modifier.size(40.dp)
                    )
                }

                Button(
                    onClick = onConfirm,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Teal,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(36.dp),
                    modifier = Modifier
                        .height(84.dp)
                        .weight(1f)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Check,
                        contentDescription = "Confirmar",
                        modifier = Modifier
                            .size(45.dp)
                            .padding(end = 10.dp)
                    )
                    Text("Confirmar", fontSize = 24.sp, fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}

/* -------------------- Preview con datos mock -------------------- */

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PreviewConfirmReservation() {
    ConfirmReservationContent(
        shelterName = "Divina Providencia",
        dateRange = "30/1/2025 - 24/2/2025",
        onBack = {},
        onConfirm = {}
    )
}

