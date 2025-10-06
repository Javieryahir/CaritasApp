package com.example.caritasapp

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import com.example.caritasapp.debug.TestTags
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class HealthFormInteractionTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<ComponentActivity>()

    @Before
    fun setup() {
        // Carga solo la pantalla HealthForms para una prueba rápida y aislada
        composeRule.setContent {
            HealthFormsScreen()
        }
    }

    @Test
    fun toggleField_enablesAndAllowsInput() {
        val alergiaFieldTag = TestTags.hfTextField("Alergia")
        val alergiaToggleTag = TestTags.hfToggle("Alergia")

        // 1. Assert: El campo de texto está inicialmente deshabilitado
        composeRule.onNodeWithTag(alergiaFieldTag).assertIsNotEnabled()

        // 2. Act: Se hace clic en el toggle
        composeRule.onNodeWithTag(alergiaToggleTag).performClick()

        // 3. Assert: El campo ahora está habilitado y se puede escribir en él
        composeRule.onNodeWithTag(alergiaFieldTag).assertIsEnabled()
        composeRule.onNodeWithTag(alergiaFieldTag).performTextInput("Polen")
        composeRule.onNodeWithTag(alergiaFieldTag).assertTextContains("Polen")

        // 4. Assert: El botón de reserva principal existe
        composeRule.onNodeWithTag(TestTags.HFReserveButton).assertIsDisplayed()
    }
}
