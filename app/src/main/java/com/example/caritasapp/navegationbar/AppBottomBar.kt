package com.example.caritasapp.navegationbar

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ListAlt
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.DirectionsCar
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.caritasapp.data.NetworkModule
import com.example.caritasapp.data.UserReservationData
import kotlinx.coroutines.launch

private val Accent = Color(0xFF009CA6)

private data class BottomNavItem(
    val route: String,
    val label: String,
    val icon: ImageVector
)

@Composable
fun AppBottomBar(navController: NavController, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val sessionManager = remember { NetworkModule.createSessionManager(context) }
    val reservationRepository = remember { NetworkModule.createReservationRepository(context) }
    
    // State for checking active reservations
    var hasActiveReservation by remember { mutableStateOf(false) }
    var activeReservation by remember { mutableStateOf<UserReservationData?>(null) }
    
    // Check for active reservations
    LaunchedEffect(Unit) {
        val currentUser = sessionManager.getUser()
        if (currentUser != null) {
            try {
                reservationRepository.getUserReservation(currentUser.id).collect { response ->
                    println("🔍 AppBottomBar - Checking reservation: $response")
                    val previousState = hasActiveReservation
                    
                    if (response != null && response.reservation.state == "ACTIVE") {
                        hasActiveReservation = true
                        activeReservation = response.reservation
                        println("🔍 AppBottomBar - Active reservation found: ${response.reservation.id}")
                        
                        // Note: Auto-navigation is now handled by LoadingScreen
                    } else {
                        hasActiveReservation = false
                        activeReservation = null
                        println("🔍 AppBottomBar - No active reservation")
                    }
                    // Print the updated state after each change
                    println("🔍 AppBottomBar - hasActiveReservation: $hasActiveReservation, reservarRoute: ${if (hasActiveReservation) "service_reservations" else "search"}")
                }
            } catch (e: Exception) {
                hasActiveReservation = false
                activeReservation = null
                println("🔍 AppBottomBar - Error checking reservation: ${e.message}")
            }
        } else {
            println("🔍 AppBottomBar - No current user")
        }
    }
    
    // Dynamic bottom items based on reservation status
    val reservarRoute = if (hasActiveReservation) "service_reservations" else "search"
    
    val bottomItems = listOf(
        BottomNavItem(
            reservarRoute, 
            "Reservar", 
            Icons.Outlined.Home
        ),
        BottomNavItem("reservations", "Historial", Icons.AutoMirrored.Outlined.ListAlt),
        BottomNavItem("transport", "Transporte", Icons.Outlined.DirectionsCar),
        BottomNavItem("account", "Cuenta", Icons.Outlined.AccountCircle)
    )
    
    val back by navController.currentBackStackEntryAsState()
    val current = back?.destination?.route

    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(18.dp),
        tonalElevation = 6.dp,
        color = Accent                                  // fondo del contenedor (teal)
    ) {
        NavigationBar(
            containerColor = Accent,                    // fondo barra
            contentColor = Color.White,                 // color por defecto del contenido
            tonalElevation = 0.dp,
            windowInsets = WindowInsets(0, 0, 0, 0)
        ) {
            bottomItems.forEach { item ->
                val selected = current == item.route

                NavigationBarItem(
                    selected = selected,
                    onClick = {
                        println("🔍 AppBottomBar - Clicking on ${item.label}, hasActiveReservation: $hasActiveReservation")
                        
                        // Handle dynamic navigation for reservar tab
                        if (item.label == "Reservar") {
                            val targetRoute = if (hasActiveReservation) "service_reservations" else "search"
                            println("🔍 AppBottomBar - Navigating to: $targetRoute")
                            
                            navController.navigate(targetRoute) {
                                launchSingleTop = true
                                restoreState = true
                                popUpTo("search") { saveState = true }
                            }
                        } else {
                            // For other tabs, use the item route
                            val target = item.route
                            println("🔍 AppBottomBar - Navigating to: $target")
                            
                            // Special handling for account screen to avoid navigation conflicts
                            if (target == "account") {
                                navController.navigate(target) {
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            } else {
                                navController.navigate(target) {
                                    launchSingleTop = true
                                    restoreState = true
                                    popUpTo("search") { saveState = true }
                                }
                            }
                        }
                    },
                    icon = {
                        Icon(
                            item.icon,
                            contentDescription = item.label,
                            modifier = Modifier.size(40.dp) // ícono un poco más grande
                        )
                    },
                    label = {
                        Text(
                            item.label,
                            fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium // texto más marcado
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color.White,
                        unselectedIconColor = Color.White.copy(alpha = 0.80f),
                        selectedTextColor = Color.White,
                        unselectedTextColor = Color.White.copy(alpha = 0.80f),
                        // 👉 sube la opacidad del “pill” para que resalte más
                        indicatorColor = Color.White.copy(alpha = 0.45f)   // prueba 0.45–0.60
                    ),
                    alwaysShowLabel = true
                )
            }

        }
    }
}
