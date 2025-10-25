package com.example.caritasapp

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import androidx.navigation.compose.rememberNavController
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.caritasapp.reservations.ReservationPage
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Prueba de la pantalla de historial de reservaciones (reservations.ReservationPage).
 *
 * Caso positivo:
 *  - El encabezado "Mis Reservaciones" se muestra al iniciar.
 *
 * Caso negativo:
 *  - El bottom sheet de detalles ("Detalles de la Reservación") NO se muestra
 *    al inicio porque todavía no se ha seleccionado ninguna reservación.
 *
 * Esto valida estado inicial estable y que no hay UI "abierta" por accidente.
 */
@RunWith(AndroidJUnit4::class)
class ReservationsScreenInitialStateTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun header_is_visible_and_bottomSheet_is_not_visible_initially() {

        // Montamos SOLO la pantalla de historial/reservaciones
        composeRule.setContent {
            val navController = rememberNavController()
            ReservationPage(navController = navController)
        }

        // ---- Caso positivo: el header principal aparece ----
        composeRule.onNodeWithText("Mis Reservaciones")
            .assertIsDisplayed()

        // ---- Caso negativo: el bottom sheet de detalles NO está visible aún ----
        // Ese sheet, cuando está visible, dibuja texto "Detalles de la Reservación".
        // Al inicio showBottomSheet = false, entonces ese texto NO debe existir.
        composeRule.onAllNodesWithText("Detalles de la Reservación")
            .assertCountEquals(0)
    }
}
