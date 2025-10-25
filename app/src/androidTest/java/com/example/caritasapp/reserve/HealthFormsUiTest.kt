package com.example.caritasapp.reserve

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.navigation.compose.rememberNavController
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import androidx.test.ext.junit.runners.AndroidJUnit4

/**
 * Prueba de UI para HealthForms:
 *  - Positivo: agregar y eliminar entradas en "Alergias".
 *  - Negativo: al activar "Ninguna", se ocultan inputs y el botón "Agregar Alergias".
 * Comentario: valida funcionalidad UX de listas dinámicas y el toggle de exclusión.
 */
@RunWith(AndroidJUnit4::class)
class HealthFormsUiTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun dynamicAllergies_addRemove_and_noneHidesList() {
        composeRule.setContent {
            val nav = rememberNavController()
            HealthFormsScreen(navController = nav, count = 1)
        }

        // Estado inicial:
        composeRule.onAllNodesWithTag("input-Alergias-0").assertCountEquals(1)
        composeRule.onAllNodesWithTag("input-Alergias-1").assertCountEquals(0)

        // POSITIVO: agregar otra entrada
        composeRule.onNodeWithTag("add-Alergias").performClick()
        composeRule.onAllNodesWithTag("input-Alergias-1").assertCountEquals(1)

        // Eliminar la segunda entrada
        composeRule.onNodeWithTag("remove-Alergias-1").performClick()
        composeRule.onAllNodesWithTag("input-Alergias-1").assertCountEquals(0)
        composeRule.onAllNodesWithTag("input-Alergias-0").assertCountEquals(1)

        // NEGATIVO: activar "Ninguna" -> oculta inputs y botón Agregar
        composeRule.onNodeWithTag("none-Alergias").performClick()
        composeRule.onAllNodesWithTag("input-Alergias-0").assertCountEquals(0)
        composeRule.onAllNodesWithTag("add-Alergias").assertCountEquals(0)
    }
}
