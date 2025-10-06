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
class FiltersTest {

    // Usamos createComposeRule() para sno lanzar la Activity completa.
    @get:Rule
    val compose = createComposeRule()

    /**
     * Este test verifica que el botón de filtros en el componente TopControls
     * existe y que responde a los clics.
     */
    @Test
    fun topControls_filterButton_existsAndIsClickable() {
        // --- PREPARACIÓN ---
        // Usamos una variable atómica para verificar si el clic funcionó.
        val filterButtonClicked = AtomicBoolean(false)

        // --- PASO 1: Renderizar el componente a probar ---
        // Usamos setContent para mostrar SÓLO el composable TopControls.
        // Lo envolvemos en un MaterialTheme para que tenga los estilos correctos.
        compose.setContent {
            MaterialTheme {
                TopControls(
                    selectedDate = "Seleccionar Fechas",
                    onPickDate = { },
                    onFilterClick = {
                        // Cuando se haga clic, cambiamos el valor a true.
                        filterButtonClicked.set(true)
                    }
                )
            }
        }

        // --- PASO 2: Buscar el botón de filtros ---
        // Buscamos el nodo que SÍ sabemos que existe y tiene el testTag "btn-filtros".
        val filterButton = compose.onNodeWithTag("btn-filtros")

        // --- PASO 3: Realizar acciones y verificaciones ---
        // 1. Verificamos que el botón realmente está en la pantalla.
        filterButton.assertIsDisplayed()

        // 2. Simulamos un clic en el botón.
        filterButton.performClick()

        // 3. Verificamos que la función lambda (onFilterClick) fue llamada.
        assert(filterButtonClicked.get()) {
            "El clic en el botón de filtros no llamó a la función onFilterClick."
        }

        // --- ¡Test exitoso! ---
        // Si todas las aserciones pasan, este test tendrá éxito.
    }
}
