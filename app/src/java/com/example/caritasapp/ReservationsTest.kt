package com.example.caritasapp

import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ReservationsTest {

    @get:Rule val compose = createAndroidComposeRule<MainActivity>()

    private fun cupoNode() = compose.onNodeWithTag("cupo-valor")

    @Test
    fun crear_y_cancelar_reserva_actualiza_cupo() {
        // Si tu flujo requiere abrir el detalle del albergue, descomenta esta línea:
        // compose.onAllNodesWithTag("card-albergue").onFirst().performClick()

        // Lee el cupo inicial (el Text con testTag "cupo-valor" debe contener SOLO el número)
        val cupoInicial = cupoNode().getText().toInt()

        // Crear reserva
        compose.onNodeWithTag("btn-crear-reserva").performClick()
        val cupoPost = cupoNode().getText().toInt()
        assert(cupoPost == cupoInicial - 1)

        // Cancelar
        compose.onNodeWithTag("btn-cancelar-reserva").performClick()
        val cupoFinal = cupoNode().getText().toInt()
        assert(cupoFinal == cupoInicial)
    }

    // Helper para leer el texto de un nodo
    private fun SemanticsNodeInteraction.getText(): String =
        fetchSemanticsNode().config[SemanticsProperties.Text].joinToString("")
}
