package com.example.caritasapp.account

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.caritasapp.navegationbar.AppBottomBar

private val Teal = Color(0xFF5D97A3)
private val Pill = Color(0xFFE98653)

@Composable
fun AccountScreen(navController: NavController) {
    val fullName = remember { mutableStateOf("Nombre Completo") }
    val phone    = remember { mutableStateOf("55122323232") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // ========== Header teal bajado hasta el centro ==========
        BoxWithConstraints(Modifier.fillMaxSize()) {
            val headerHeight = this.maxHeight / 2 - 160.dp // borde inferior en el centro

            Surface(
                color = Teal,
                shape = RoundedCornerShape(bottomStart = 36.dp, bottomEnd = 36.dp),
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .fillMaxWidth()
                    .height(headerHeight)
            ) {}
        }

        // ========== Contenido centrado (avatar en el centro) ==========
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
                .padding(bottom = 110.dp), // espacio para la bottom bar
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Avatar centrado; quedará mitad sobre teal / mitad sobre blanco
            Surface(
                shape = CircleShape,
                color = Color.White,
                modifier = Modifier
                    .size(124.dp)
                    .shadow(8.dp, CircleShape)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Filled.AccountCircle,
                        contentDescription = "Avatar",
                        tint = Teal,
                        modifier = Modifier.size(115.dp)
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            // Nombre (en teal para que se lea sobre blanco)
            Text(
                text = fullName.value,
                color = Teal,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(18.dp))

            // Tarjeta con datos
            Surface(
                color = Color.White,
                shape = RoundedCornerShape(20.dp),
                tonalElevation = 2.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 22.dp, horizontal = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        "Teléfono",
                        style = MaterialTheme.typography.titleMedium,
                        color = Teal.copy(alpha = 0.9f),
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        phone.value,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF202020),
                        textAlign = TextAlign.Center
                    )
                }
            }

            Spacer(Modifier.height(24.dp))

            // Botón cerrar sesión
            Button(
                onClick = {
                    navController.navigate("login") {
                        popUpTo(navController.graph.startDestinationId) { inclusive = true }
                        launchSingleTop = true
                    }
                },
                shape = RoundedCornerShape(28.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Pill,
                    contentColor = Color.Black
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                Text("Cerrar Sesión", fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
            }
        }

        // Bottom bar
        AppBottomBar(
            navController = navController,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(start = 12.dp, end = 12.dp, bottom = 20.dp)
        )
    }
}

// --- Preview ---
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PreviewAccountScreen() {
    AccountScreen(navController = rememberNavController())
}
