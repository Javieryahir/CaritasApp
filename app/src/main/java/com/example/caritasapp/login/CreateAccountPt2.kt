package com.example.caritasapp.login

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

private val Teal = Color(0xFF5D97A3)

@Composable
fun CreateAccountPt2(navController: NavController) {
    val prev = navController.previousBackStackEntry?.savedStateHandle
    val fullName = prev?.get<String>("fullName").orEmpty()
    val phone    = prev?.get<String>("phone").orEmpty()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Teal)
            .padding(horizontal = 28.dp, vertical = 20.dp)
    ) {
        // ===== Contenido centrado con flechas pegadas (igual que Pt1) =====
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "CONFIRMAR DATOS",
                color = Color.White,
                fontSize = 40.sp,
                fontWeight = FontWeight.Black,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(10.dp))

            Section("Nombre Completo")
            Text(
                fullName.ifBlank { "—" },
                color = Color.White,
                fontSize = 24.sp,
                modifier = Modifier.padding(bottom = 18.dp),
            )

            HorizontalDivider(
                modifier = Modifier.fillMaxWidth(),
                thickness = 1.dp,
                color = Color(0x66FFFFFF)
            )

            Spacer(Modifier.height(18.dp))

            Section("Celular")
            Text(
                phone.ifBlank { "—" },
                color = Color.White,
                fontSize = 24.sp
            )

            HorizontalDivider(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 18.dp),
                thickness = 1.dp,
                color = Color(0x66FFFFFF)
            )

            // --- Flechas grandes y juntas, pegadas al contenido ---
            Spacer(Modifier.height(16.dp))
            Row(
                modifier = Modifier.wrapContentWidth(),
                horizontalArrangement = Arrangement.spacedBy(22.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                FilledIconButton(
                    onClick = { navController.popBackStack() },
                    shape = CircleShape,
                    modifier = Modifier.size(84.dp),
                    colors = IconButtonDefaults.filledIconButtonColors(containerColor = Color.White)
                ) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Atrás",
                        tint = Teal,
                        modifier = Modifier.size(40.dp)
                    )
                }

                FilledIconButton(
                    onClick = {
                        navController.currentBackStackEntry
                            ?.savedStateHandle?.set("phone", phone)
                        navController.navigate("create3")
                    },
                    shape = CircleShape,
                    modifier = Modifier.size(84.dp),
                    colors = IconButtonDefaults.filledIconButtonColors(containerColor = Color.White)
                ) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = "Siguiente",
                        tint = Teal,
                        modifier = Modifier.size(40.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun Section(title: String) {
    Text(
        title,
        color = Color.White,
        fontWeight = FontWeight.ExtraBold,
        fontSize = 28.sp,
        textAlign = TextAlign.Center,
        modifier = Modifier.padding(bottom = 6.dp)
    )
}
