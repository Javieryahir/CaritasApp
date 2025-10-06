package com.example.caritasapp

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import com.example.caritasapp.debug.TestTags
import org.junit.Rule
import org.junit.Test

class LoginNavigationTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun whenClickGoogleButton_navigatesToReservationPage() {
        // 1. Arrange: Estamos en la pantalla de Login
        composeRule.onNodeWithTag(TestTags.LoginGoogleButton).assertIsDisplayed()

        // 2. Act: Hacemos clic en el botón de Google
        composeRule.onNodeWithTag(TestTags.LoginGoogleButton).performClick()

        // 3. Assert: Se navega a ReservationPage y sus controles clave son visibles
        composeRule.onNodeWithTag(TestTags.ResFiltersButton).assertIsDisplayed()
        composeRule.onNodeWithTag(TestTags.ResShelterPickerButton).assertIsDisplayed()
    }
}
