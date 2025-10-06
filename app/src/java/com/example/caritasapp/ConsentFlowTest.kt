package com.example.caritasapp

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ConsentFlowTest {

    @get:Rule val compose = createAndroidComposeRule<MainActivity>()

    @Test
    fun consentimiento_requerido_y_luego_permitido() {
        compose.onNodeWithTag("dialog-aviso").assertIsDisplayed()
        compose.onNodeWithTag("btn-aceptar-aviso").performClick()
        compose.onNodeWithTag("dialog-aviso").assertDoesNotExist()
    }
}
