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
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState

private val Accent = Color(0xFF009CA6)

private data class BottomNavItem(
    val route: String,
    val label: String,
    val icon: ImageVector
)

private val bottomItems = listOf(
    BottomNavItem("search",       "Reservar",      Icons.Outlined.Home),
    BottomNavItem("reservations", "Reservaciones", Icons.AutoMirrored.Outlined.ListAlt),
    BottomNavItem("transport",    "Transporte",    Icons.Outlined.DirectionsCar),
    BottomNavItem("account",      "Cuenta",        Icons.Outlined.AccountCircle)
)

@Composable
fun AppBottomBar(navController: NavController, modifier: Modifier = Modifier) {
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
                    // AppBottomBar.kt  (dentro de NavigationBarItem(onClick = { ... }))
                    onClick = {
                        val target = item.route
                        if (current != target) {
                            navController.navigate(target) {
                                launchSingleTop = true
                                restoreState = true
                                // 👇 Mantén "search" en el back stack para conservar su savedStateHandle
                                popUpTo("search") { saveState = true }
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
