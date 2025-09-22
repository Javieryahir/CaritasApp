package com.example.caritasapp

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ConfirmReservation() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Título
        Text(
            text = "Revisar y Confirmar",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp),
            textAlign = TextAlign.Center
        )

        // Card con info
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, Color.Black, RoundedCornerShape(16.dp))
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Locación
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 16.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.shelter), // tu imagen
                    contentDescription = "Locación",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(64.dp)
                        .background(Color.LightGray, RoundedCornerShape(8.dp))
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "Locacion de Reservación",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            Divider(color = Color.LightGray, thickness = 1.dp)

            // Fechas
            InfoRow(
                title = "Fechas",
                subtitle = "Jan 30 - Feb 24, 2025",
                buttonText = "Cambiar",
                icon = Icons.Filled.Edit
            )

            Divider(color = Color.LightGray, thickness = 1.dp)

            // Lavandería
            InfoRow(
                title = "Precio Lavanderia",
                subtitle = "~$50 pesos",
                buttonText = "Cambiar",
                icon = Icons.Filled.Edit
            )

            Divider(color = Color.LightGray, thickness = 1.dp)

            // Regadera
            InfoRow(
                title = "Precio Regadera",
                subtitle = "~$50 pesos",
                buttonText = "Cambiar",
                icon = Icons.Filled.Edit
            )

            Divider(color = Color.LightGray, thickness = 1.dp)

            // Transporte
            InfoRow(
                title = "Agregar Transporte",
                subtitle = "Adicional",
                buttonText = "Agregar",
                icon = Icons.Filled.Add
            )

            Divider(color = Color.LightGray, thickness = 1.dp)

            // Total
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                horizontalAlignment = Alignment.Start
            ) {
                Text("Total", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Text(
                    text = "~$100 Pesos",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Nota
        Text(
            text = "El precio es un estimado. El precio total se le dira en el albergue.",
            fontSize = 12.sp,
            color = Color.Gray,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Botón confirmar
        Button(
            onClick = { /* TODO: Confirmar */ },
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF2D9CDB),
                contentColor = Color.White
            ),
            shape = CircleShape,
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.Check,
                contentDescription = "Confirmar",
                modifier = Modifier.padding(end = 8.dp)
            )
            Text("Confirmar", fontSize = 18.sp, fontWeight = FontWeight.Medium)
        }
    }
}

@Composable
fun InfoRow(
    title: String,
    subtitle: String,
    buttonText: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Text(title, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            Text(subtitle, fontSize = 14.sp, color = Color.Gray)
        }
        Button(
            onClick = { /* TODO */ },
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF2D9CDB),
                contentColor = Color.White
            ),
            shape = RoundedCornerShape(50),
            modifier = Modifier.height(36.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = buttonText,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(buttonText, fontSize = 14.sp)
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PreviewConfirmReservation() {
    ConfirmReservation()
}
