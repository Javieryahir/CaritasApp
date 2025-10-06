package com.example.caritasapp

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performTextInput
import com.example.caritasapp.debug.TestTags
import org.junit.Rule
import org.junit.Test

class FullReservationFlowTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun fromLoginToWaitingPage_fullFlow() {
        // 1. Login
        composeRule.onNodeWithTag(TestTags.LoginGoogleButton).performClick()

        // 2. Seleccionar un albergue
        composeRule.onNodeWithTag(TestTags.ResShelterPickerButton).performClick()
        val shelterTag = TestTags.pickerLocation("Divina Providencia")
        composeRule.onNodeWithTag(shelterTag, useUnmergedTree = true).performClick()


        // 3. Confirmar albergue y navegar a HealthForms
        composeRule.onNodeWithTag(TestTags.ResConfirmShelterButton).performClick()

        // 4. Rellenar formulario y reservar
        val alergiaToggleTag = TestTags.hfToggle("Alergia")
        composeRule.onNodeWithTag(alergiaToggleTag).performClick()
        val alergiaFieldTag = TestTags.hfTextField("Alergia")
        composeRule.onNodeWithTag(alergiaFieldTag).performTextInput("Nueces")

        composeRule.onNodeWithTag(TestTags.HFReserveButton).performClick()

        // 5. Verificar que se llega a la WaitingPage
        composeRule.waitForIdle() // Espera a que la UI se estabilice
        composeRule.onNodeWithTag(TestTags.WaitingPageRoot).assertIsDisplayed()
        composeRule.onNodeWithTag(TestTags.WaitingPageConfirmButton).assertIsDisplayed()
    }
}
