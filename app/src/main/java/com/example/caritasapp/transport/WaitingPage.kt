package com.example.caritasapp.transport

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Celebration
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController

private val Teal = Color(0xFF4A98A6)
private val CardBg = Color(0xFFD1E0D7)

@Composable
fun WaitingPage(
    navController: NavController,
    pickup: String,
    dropoff: String,
    date: String,
    time: String
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Teal)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center   // 👈 todo centrado verticalmente
    ) {

        Text(
            text = "Su reserva de transporte\nestá siendo revisada",
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            textAlign = TextAlign.Center
        )

        Spacer(Modifier.height(10.dp))

        Text(
            text = "Manténgase al pendiente de su confirmación en\nla pestaña de Reservaciones",
            fontSize = 20.sp,
            color = Color.White,
            textAlign = TextAlign.Center
        )

        Spacer(Modifier.height(22.dp))

        Icon(
            imageVector = Icons.Filled.Celebration,
            contentDescription = "Celebración",
            tint = Color.White,
            modifier = Modifier.size(160.dp)
        )

        Spacer(Modifier.height(22.dp))

        // --- Tarjeta resumen centrada ---
        Surface(
            color = CardBg,
            shape = RoundedCornerShape(22.dp),
            tonalElevation = 2.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(22.dp),
                horizontalAlignment = Alignment.CenterHorizontally,   // 👈 centrado horizontal
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                SummaryRow("Lugar de Recogida", pickup)
                SummaryRow("Locación Final",   dropoff)
                SummaryRow("Fecha",            date.ifBlank { "—" })
                SummaryRow("Hora",             time.ifBlank { "—" })
            }
        }

        Spacer(Modifier.height(28.dp))

        // Botón redondo -> vuelve a Transporte
        Button(
            onClick = {
                navController.navigate("transport") {
                    launchSingleTop = true
                }
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.White,
                contentColor = Teal
            ),
            shape = CircleShape,
            modifier = Modifier.size(80.dp)
        ) {
            Icon(Icons.Filled.Check, contentDescription = "Volver a Transporte", modifier = Modifier.size(36.dp))
        }
    }
}

@Composable
private fun SummaryRow(title: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            title,
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF2F2F2F),
            textAlign = TextAlign.Center
        )
        Text(
            value.ifBlank { "No especificado" },
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1E1E1E),
            textAlign = TextAlign.Center
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun PreviewWaitingPage() {
    WaitingPage(
        navController = rememberNavController(),
        pickup = "Albergue Contigo",
        dropoff = "Centro Comunitario",
        date = "15/10/2025",
        time = "10:30"
    )
}
