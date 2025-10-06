package com.example.caritasapp

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import com.example.caritasapp.debug.TestTags
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class ReservationFeaturesTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<MainActivity>()

    @Before
    fun navigateToReservationPage() {
        // Pre-condición: Navegar a ReservationPage para cada prueba en esta clase
        composeRule.onNodeWithTag(TestTags.LoginGoogleButton).performClick()
    }

    @Test
    fun filterSheet_opens_selects_and_closes() {
        // 1. Act: Abrir el sheet de filtros
        composeRule.onNodeWithTag(TestTags.ResFiltersButton).performClick()
        composeRule.onNodeWithTag(TestTags.ResFiltersSheetRoot).assertIsDisplayed()

        // 2. Act: Seleccionar dos servicios
        composeRule.onNodeWithTag(TestTags.filterService("Desayuno"), useUnmergedTree = true).performClick()
        composeRule.onNodeWithTag(TestTags.filterService("Duchas"), useUnmergedTree = true).performClick()

        // 3. Act: Cerrar el sheet
        composeRule.onNodeWithTag(TestTags.ResFiltersCloseBtn).performClick()

        // 4. Assert: El sheet ya no existe
        composeRule.onNodeWithTag(TestTags.ResFiltersSheetRoot).assertDoesNotExist()
    }

    @Test
    fun shelterPickerSheet_opens_selects_and_confirms() {
        // 1. Act: Abrir el selector de albergues
        composeRule.onNodeWithTag(TestTags.ResShelterPickerButton).performClick()
        composeRule.onNodeWithTag(TestTags.ResPickerSheetRoot).assertIsDisplayed()

        // 2. Act: Seleccionar un albergue
        val shelterTag = TestTags.pickerLocation("Divina Providencia")
        composeRule.onNodeWithTag(shelterTag, useUnmergedTree = true).performClick()


        // 3. Assert: El sheet se cierra (implícitamente al hacer clic)
        composeRule.onNodeWithTag(TestTags.ResPickerSheetRoot).assertDoesNotExist()
    }
}
