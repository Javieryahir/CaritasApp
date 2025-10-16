package com.example.caritasapp.reservations

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Celebration // puedes usar cheer_24 si lo añades como drawable
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

@Composable
fun WaitingPage() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF4A98A6)) // Fondo azul
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Título
        Text(
            text = "Su reserva está siendo\nrevisada",
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Subtítulo
        Text(
            text = "Manténgase al pendiente de su confirmación en\nla pestaña de Reservaciones",
            fontSize = 20.sp,
            color = Color.White,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        // Ícono de manos (placeholder usando otro ícono por defecto)
        Icon(
            imageVector = Icons.Filled.Celebration, // ⚠️ cámbialo a cheer_24 si lo importas como drawable
            contentDescription = "Manos",
            tint = Color.White,
            modifier = Modifier
                .size(180.dp)
                .padding(bottom = 32.dp)
        )

        // Nota
        Text(
            text = "En caso de haber seleccionado un servicio, los detalles se le darán en el albergue para la coordinación y pago de ellos.",
            fontSize = 20.sp,
            color = Color.White,
            textAlign = TextAlign.Center,
            fontStyle = FontStyle.Italic,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        // Botón redondo con check
        Button(
            onClick = { /* TODO: Acción continuar */ },
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
    WaitingPage()
}
