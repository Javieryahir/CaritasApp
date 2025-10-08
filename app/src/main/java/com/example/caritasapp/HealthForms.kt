package com.example.caritasapp

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.caritasapp.composables.HyperlinkText


@Composable
fun HealthFormsScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Título
        Text(
            text = "Formulario de Salud",
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 24.dp),
            textAlign = TextAlign.Center
        )

        // Sección: Alergias
        Text("Listado de Alergias", fontSize = 20.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))
        ToggleableField(label = "Alergia")

        Spacer(modifier = Modifier.height(24.dp))

        // Sección: Discapacidades
        Text("Discapacidades", fontSize = 20.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))
        ToggleableField(label = "Discapacidad")

        Spacer(modifier = Modifier.height(24.dp))

        // Sección: Medicamentos
        Text("Medicamentos Actuales", fontSize = 20.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))
        ToggleableField(label = "Medicamento")

        Spacer(modifier = Modifier.height(40.dp))

        // Botón Reservación
        Button(
            onClick = { /* TODO */ },
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF4A98A6),
                contentColor = Color.White
            ),
            shape = RoundedCornerShape(50),
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.CalendarToday,
                contentDescription = "Reservación",
                modifier = Modifier.padding(end = 8.dp)
            )
            Text("Reservación", fontSize = 22.sp, fontWeight = FontWeight.Medium)
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Política
        HyperlinkText(
            fullText = "Política de privacidad",
            hyperlinks = mapOf(
                "Política de privacidad" to "https://tecmx-my.sharepoint.com/:b:/r/personal/a01782862_tec_mx/Documents/AppMovil/PoliticasdePrivacidad/AVISO%20DE%20PRIVACIDAD%202025.pdf?csf=1&web=1&e=Tum1V9"
            ),
            fontSize = 18.sp,
            textColor = Color.Gray,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun ToggleableField(label: String) {
    var text by remember { mutableStateOf("") }
    var isEnabled by remember { mutableStateOf(false) }

    Row(verticalAlignment = Alignment.CenterVertically) {
        OutlinedTextField(
            value = text,
            onValueChange = { text = it },
            enabled = isEnabled, // 🔹 se activa o desactiva
            placeholder = {
                Text(
                    label,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            modifier = Modifier
                .weight(1f)
                .background(Color.LightGray, RoundedCornerShape(8.dp))
        )

        Spacer(modifier = Modifier.width(8.dp))

        // Botón toggle
        IconButton(
            onClick = { isEnabled = !isEnabled }, // alterna
            modifier = Modifier
                .size(40.dp)
                .background(Color.LightGray, CircleShape)
        ) {
            if (isEnabled) {
                Icon(Icons.Filled.Remove, contentDescription = "Desactivar")
            } else {
                Icon(Icons.Filled.Add, contentDescription = "Activar")
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PreviewHealthForms() {
    HealthFormsScreen()
}
