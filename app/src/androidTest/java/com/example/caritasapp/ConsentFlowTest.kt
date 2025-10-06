package com.example.caritasapp

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LoginFlowTest { // <-- He renombrado la clase para que sea más descriptiva

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    /**
     * Este test verifica el flujo de login.
     * 1. Confirma que la pantalla de login se muestra esperando por el botón de Google.
     * 2. Hace clic en el botón "Continuar con Google".
     * 3. Confirma que la navegación fue exitosa verificando la presencia de un
     *    componente de la siguiente pantalla (ReservationPage).
     */
    @Test
    fun loginScreen_clickOnGoogleButton_navigatesToNextScreen() {
        // --- PASO 1: ESPERAR Y VERIFICAR EL BOTÓN DE LOGIN ---
        // Usamos el testTag que acabamos de añadir en LoginScreen.kt.
        val googleButtonTag = "google-login-button"

        try {
            // Esperamos hasta 15 segundos a que el botón aparezca.
            composeTestRule.waitUntil(timeoutMillis = 15_000) {
                composeTestRule.onAllNodesWithTag(googleButtonTag).fetchSemanticsNodes().isNotEmpty()
            }
        } catch (e: ComposeTimeoutException) {
            // Si el botón no aparece, imprimimos la UI actual para depurar.
            composeTestRule.onRoot().printToLog("LoginScreenError")
            throw AssertionError(
                "El botón de Google ('$googleButtonTag') nunca apareció. Revisa el Logcat con la etiqueta 'LoginScreenError'.", e
            )
        }

        // --- PASO 2: HACER CLIC EN EL BOTÓN ---
        composeTestRule.onNodeWithTag(googleButtonTag).performClick()

        // --- PASO 3: VERIFICAR LA NAVEGACIÓN EXITOSA ---
        // Después de hacer clic, el navController debería llevarnos a la pantalla de reservaciones.
        // La mejor manera de confirmarlo es buscando un componente que SÓLO existe en esa pantalla,
        // como el botón de filtros que tiene el testTag "btn-filtros".
        val filtersButtonTag = "btn-filtros"

        try {
            composeTestRule.waitUntil(timeoutMillis = 10_000) {
                composeTestRule.onAllNodesWithTag(filtersButtonTag).fetchSemanticsNodes().isNotEmpty()
            }
        } catch (e: ComposeTimeoutException) {
            composeTestRule.onRoot().printToLog("NavigationError")
            throw AssertionError(
                "Después de hacer clic en login, nunca se llegó a la pantalla de reservaciones (no se encontró '$filtersButtonTag'). Revisa el Logcat con 'NavigationError'.", e
            )
        }

        // Si llegamos hasta aquí, la prueba es un éxito.
        composeTestRule.onNodeWithTag(filtersButtonTag).assertIsDisplayed()
    }
}
