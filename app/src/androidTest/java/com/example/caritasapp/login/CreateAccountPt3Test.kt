package com.example.caritasapp.login

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performTextClearance
import androidx.compose.ui.test.performTextInput
import androidx.navigation.compose.rememberNavController
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import androidx.test.ext.junit.runners.AndroidJUnit4

/**
 * Prueba de UI para CreateAccountPt3 (validación del código):
 *  - Negativo: con menos de 4 dígitos, "Confirmar" permanece deshabilitado.
 *  - Positivo: con 4+ dígitos, "Confirmar" se habilita.
 */
@RunWith(AndroidJUnit4::class)
class CreateAccountPt3Test {

    @get:Rule
    val composeRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun confirmButton_enables_only_when_code_has_4_or_more_digits() {
        composeRule.setContent {
            val nav = rememberNavController()
            CreateAccountPt3(navController = nav)
        }

        // Inicialmente: deshabilitado
        composeRule.onNodeWithTag("confirmButton").assertIsNotEnabled()

        // Menos de 4 dígitos -> sigue deshabilitado
        composeRule.onNodeWithTag("codeField").performTextInput("12")
        composeRule.onNodeWithTag("confirmButton").assertIsNotEnabled()

        // Con 4 dígitos -> se habilita
        composeRule.onNodeWithTag("codeField").performTextClearance()
        composeRule.onNodeWithTag("codeField").performTextInput("1234")
        composeRule.onNodeWithTag("confirmButton").assertIsEnabled()
    }
}
