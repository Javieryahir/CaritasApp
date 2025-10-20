package com.example.caritasapp.loading

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.caritasapp.data.NetworkModule
import com.example.caritasapp.data.UserReservationData
import kotlinx.coroutines.delay

private val Accent = Color(0xFF009CA6)

@Composable
fun LoadingScreen(navController: NavController) {
    val context = LocalContext.current
    val sessionManager = remember { NetworkModule.createSessionManager(context) }
    val reservationRepository = remember { NetworkModule.createReservationRepository(context) }
    
    var loadingMessage by remember { mutableStateOf("Verificando usuario...") }
    var hasActiveReservation by remember { mutableStateOf(false) }
    var activeReservation by remember { mutableStateOf<UserReservationData?>(null) }
    
    LaunchedEffect(Unit) {
        try {
            // Get current user
            val currentUser = sessionManager.getUser()
            if (currentUser == null) {
                loadingMessage = "Usuario no encontrado"
                delay(1000)
                navController.navigate("login") {
                    popUpTo("loading") { inclusive = true }
                }
                return@LaunchedEffect
            }
            
            loadingMessage = "Verificando reservaciones..."
            delay(500) // Small delay for better UX
            
            // Check for active reservations using the history endpoint
            val apiService = NetworkModule.createAuthenticatedApiService(sessionManager)
            val historyResponse = apiService.getUserReservationsHistory(currentUser.id, 5, 1)
            println("🔍 LoadingScreen - History response: $historyResponse")
            
            if (historyResponse.activeReservation != null) {
                val currentActiveReservation = historyResponse.activeReservation
                println("🔍 LoadingScreen - Active reservation state: ${currentActiveReservation.state}")
                
                if (currentActiveReservation.state == "ACTIVE" || currentActiveReservation.state == "PENDING") {
                    hasActiveReservation = true
                    loadingMessage = if (currentActiveReservation.state == "PENDING") "Reservación pendiente encontrada" else "Reservación activa encontrada"
                    println("🔍 LoadingScreen - ${currentActiveReservation.state} reservation found: ${currentActiveReservation.id}")
                    
                    delay(1000) // Show the message briefly
                    
                    // Navigate to service reservations
                    println("🔍 LoadingScreen - Navigating to service_reservations")
                    navController.navigate("service_reservations") {
                        popUpTo("loading") { inclusive = true }
                    }
                } else {
                    hasActiveReservation = false
                    activeReservation = null
                    loadingMessage = "Cargando aplicación..."
                    println("🔍 LoadingScreen - No active/pending reservation found")
                    
                    delay(1000) // Show the message briefly
                    
                    // Navigate to main search screen
                    println("🔍 LoadingScreen - Navigating to search")
                    navController.navigate("search") {
                        popUpTo("loading") { inclusive = true }
                    }
                }
            } else {
                hasActiveReservation = false
                activeReservation = null
                loadingMessage = "Cargando aplicación..."
                println("🔍 LoadingScreen - No active reservation in history")
                
                delay(1000) // Show the message briefly
                
                // Navigate to main search screen
                println("🔍 LoadingScreen - Navigating to search")
                navController.navigate("search") {
                    popUpTo("loading") { inclusive = true }
                }
            }
        } catch (e: Exception) {
            println("🔍 LoadingScreen - Error: ${e.message}")
            loadingMessage = "Error de conexión"
            delay(2000)
            
            // Navigate to search as fallback
            println("🔍 LoadingScreen - Error occurred, navigating to search as fallback")
            navController.navigate("search") {
                popUpTo("loading") { inclusive = true }
            }
        }
    }
    
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color.White
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // App logo or icon could go here
            CircularProgressIndicator(
                color = Accent,
                modifier = Modifier.size(64.dp)
            )
            
            Spacer(Modifier.height(32.dp))
            
            Text(
                text = "Caritas App",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Accent
            )
            
            Spacer(Modifier.height(16.dp))
            
            Text(
                text = loadingMessage,
                fontSize = 16.sp,
                color = Color.Gray,
                textAlign = TextAlign.Center
            )
        }
    }
}
