package com.example.caritasapp

import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.caritasapp.reservations.TopControls // Importa tu composable
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.util.concurrent.atomic.AtomicBoolean

@RunWith(AndroidJUnit4::class)
class TopControlsTest {

    // Usamos createComposeRule() para tests de UI aislados.
    // Esto NO inicia tu aplicación completa, solo nos da un lienzo para dibujar.
    @get:Rule
    val composeTestRule = createComposeRule()

    /**
     * Este test verifica dos cosas muy simples del componente TopControls:
     * 1. Que el botón de filtros con el testTag "btn-filtros" existe y se muestra.
     * 2. Que al hacer clic en él, la función que le pasamos se ejecuta.
     */
    @Test
    fun topControls_filterButton_existsAndIsClickable() {
        // --- PREPARACIÓN ---
        // Usamos esta variable para confirmar que el clic realmente funcionó.
        val filterWasClicked = AtomicBoolean(false)

        // --- PASO 1: Renderizar SOLAMENTE el componente a probar ---
        // Con setContent, le decimos a la regla de Compose que dibuje nuestro TopControls.
        // Lo envolvemos en un MaterialTheme para que no haya errores de estilos (colores, fuentes, etc.).
        composeTestRule.setContent {
            MaterialTheme {
                TopControls(
                    selectedDate = "Fechas de Prueba",
                    onPickDate = {}, // No nos interesa probar este clic, así que lo dejamos vacío.
                    onFilterClick = {
                        // Esta es la función que se llamará al hacer clic.
                        // Cambiamos nuestra variable a true.
                        filterWasClicked.set(true)
                    }
                )
            }
        }

        // --- PASO 2: Buscar el botón ---
        // Buscamos el nodo que tiene el testTag que SÍ está en tu código ReservationPage.kt.
        val filterButtonNode = composeTestRule.onNodeWithTag("btn-filtros")

        // --- PASO 3: Realizar acciones y verificaciones (Aserciones) ---

        // 1. Verificamos que el botón existe y está visible en la pantalla.
        filterButtonNode.assertIsDisplayed()

        // 2. Simulamos un clic de usuario sobre ese botón.
        filterButtonNode.performClick()

        // 3. Verificamos que nuestra variable cambió a 'true'.
        // Esto confirma que el lambda 'onFilterClick' fue ejecutado.
        assert(filterWasClicked.get()) {
            "El callback onFilterClick no fue llamado después de hacer clic en el botón de filtros."
        }
    }
}
