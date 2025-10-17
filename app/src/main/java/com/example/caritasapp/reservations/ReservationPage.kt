// ReservationPage.kt
package com.example.caritasapp.reservations

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
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
import com.example.caritasapp.navegationbar.AppBottomBar

private val Teal   = Color(0xFF5D97A3)
private val ChipBg = Color(0xFF69A7B2)

// ==== Tipografías (ajustables) ====
private val TitleSize         = 36.sp   // "Reservaciones"
private val SectionTitleSize  = 22.sp   // "Última reservación", "Pasadas reservaciones"
private val ItemTitleSize     = 20.sp   // Título de cada item
private val ItemMetaSize      = 18.sp   // Fechas y precio
private val ChipTextSize      = 18.sp   // Texto de "Reagendar"

@Composable
fun ReservationPage(navController: NavController) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        val bottomBarPadding = 110.dp

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(horizontal = 20.dp),
            contentPadding = PaddingValues(
                top = 28.dp,
                bottom = bottomBarPadding
            )
        ) {
            // Título principal centrado
            item {
                Text(
                    "Reservaciones",
                    fontSize = TitleSize,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1E1E1E),
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp, bottom = 20.dp)
                )
            }

            // Última reservación
            item {
                Text(
                    "Última reservación",
                    fontSize = SectionTitleSize,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF4C4C4C),
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 14.dp)
                )
            }

            // Última
            item {
                ReservationItem(
                    title = "Albergue",
                    dates = "12 oct - 14 oct 2025",
                    price = "$0",
                    onRebook = { navController.navigate("reservations_details") }
                )
                Spacer(Modifier.height(20.dp))
            }

            // Separador
            item {
                HorizontalDivider()
                Spacer(Modifier.height(20.dp))
            }

            // Pasadas reservaciones
            item {
                Text(
                    "Pasadas reservaciones",
                    fontSize = SectionTitleSize,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF4C4C4C),
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 14.dp)
                )
            }

            // Lista de pasadas (mock)
            val past = listOf(
                Triple("Albergue",  "12 oct - 14 oct 2025", "$0"),
                Triple("Albergue",  "12 oct",               "$50"),
                Triple("Albergue",  "12 oct",               "$10"),
                Triple("Albergue",  "12 oct",               "$10"),
            )

            items(past) { (title, dates, price) ->
                ReservationItem(
                    title = title,
                    dates = dates,
                    price = price,
                    onRebook = { navController.navigate("reservations_details") }
                )
                Spacer(Modifier.height(12.dp))
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

@Composable
private fun ReservationItem(
    title: String,
    dates: String,
    price: String,
    onRebook: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        tonalElevation = 0.dp,
        border = ButtonDefaults.outlinedButtonBorder(enabled = true),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // icono calendario
            Surface(
                shape = CircleShape,
                color = Color.White,
                tonalElevation = 1.dp,
                border = ButtonDefaults.outlinedButtonBorder(enabled = true)
            ) {
                Box(Modifier.size(48.dp), contentAlignment = Alignment.Center) {
                    Icon(
                        Icons.Filled.CalendarMonth,
                        contentDescription = null,
                        tint = Teal
                    )
                }
            }

            Spacer(Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(title, fontWeight = FontWeight.SemiBold, fontSize = ItemTitleSize)
                Text(dates, color = Color.Gray, fontSize = ItemMetaSize)
                Text(price, color = Color.Gray, fontSize = ItemMetaSize)
            }

            AssistChip(
                onClick = onRebook,
                label = { Text("Reagendar", color = Color.White, fontSize = ChipTextSize) },
                colors = AssistChipDefaults.assistChipColors(containerColor = ChipBg),
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true, name = "ReservationPage")
@Composable
fun PreviewReservationPage() {
    ReservationPage(navController = androidx.navigation.compose.rememberNavController())
}
