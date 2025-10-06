package com.example.caritasapp

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class FiltersTest {

    @get:Rule val compose = createAndroidComposeRule<MainActivity>()

    @Test
    fun filtro_psicologo_filtra_lista() {
        // Abre el panel de filtros (botón con testTag en la TopBar)
        compose.onNodeWithTag("btn-filtros").performClick()

        // Activa el servicio "Psicólogo"
        compose.onNodeWithText("Psicólogo").performClick()

        // Aplica los filtros (botón que añadimos en la hoja)
        compose.onNodeWithTag("btn-aplicar-filtros").performClick()

        // Abre la lista de albergues (botón grande “Albergues Disponibles”)
        compose.onNodeWithTag("btn-albergues").performClick()

        // Debe haber al menos 1 tarjeta visible
        compose.onAllNodesWithTag("card-albergue").assertCountGreaterThan(0)
    }
}
