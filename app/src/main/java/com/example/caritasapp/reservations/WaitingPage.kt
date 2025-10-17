package com.example.caritasapp.reservations

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Celebration
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController

@Composable
fun WaitingPage(navController: NavController) {   // 👈 ahora recibe NavController
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF4A98A6))
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Su reserva está siendo\nrevisada",
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Text(
            text = "Manténgase al pendiente de su confirmación en\nla pestaña de Reservaciones",
            fontSize = 20.sp,
            color = Color.White,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        Icon(
            imageVector = Icons.Filled.Celebration,
            contentDescription = "Manos",
            tint = Color.White,
            modifier = Modifier
                .size(180.dp)
                .padding(bottom = 32.dp)
        )

        Text(
            text = "En caso de haber seleccionado un servicio, los detalles se le darán en el albergue para la coordinación y pago de ellos.",
            fontSize = 20.sp,
            color = Color.White,
            textAlign = TextAlign.Center,
            fontStyle = FontStyle.Italic,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        Button(
            onClick = {
                // 👇 vuelve a la pantalla de búsqueda/reservas
                navController.navigate("search") {
                    popUpTo("search") { inclusive = true } // limpia el back stack
                    launchSingleTop = true
                }
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.White,
                contentColor = Color(0xFF4A98A6)
            ),
            shape = CircleShape,
            modifier = Modifier.size(80.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.Check,
                contentDescription = "Confirmar",
                modifier = Modifier.size(36.dp)
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PreviewWaitingPage() {
    WaitingPage(rememberNavController())
}
