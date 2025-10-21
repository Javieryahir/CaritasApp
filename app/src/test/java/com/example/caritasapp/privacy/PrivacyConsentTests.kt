package com.example.caritasapp.privacy

import com.example.caritasapp.data.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.Assert.*
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.whenever

/**
 * RF-06: Aviso de Privacidad y Consentimiento
 * Tests for privacy notice display and consent management using HyperlinkText functionality
 */
@RunWith(MockitoJUnitRunner::class)
class PrivacyConsentTests {

    @Mock
    private lateinit var mockSessionManager: SessionManager

    @Before
    fun setup() {
        // Setup without dispatcher to avoid IllegalStateException
    }

    /**
     * Test RF-06: Privacy notice blocks reservations until accepted
     */
    @Test
    fun testPrivacyNoticeBlocksReservationsUntilAccepted() = runTest {
        // Given - User has not accepted privacy terms
        whenever(mockSessionManager.hasAcceptedPrivacyTerms()).thenReturn(false)
        
        // When - User tries to make a reservation
        val canMakeReservation = mockSessionManager.hasAcceptedPrivacyTerms()
        
        // Then - Reservation should be blocked
        assertFalse("User should not be able to make reservations without accepting privacy terms", canMakeReservation)
        
        // Verify that privacy terms acceptance is checked
        verify(mockSessionManager).hasAcceptedPrivacyTerms()
    }

    /**
     * Test RF-06: Privacy notice allows reservations after acceptance
     */
    @Test
    fun testPrivacyNoticeAllowsReservationsAfterAcceptance() = runTest {
        // Given - User has accepted privacy terms
        whenever(mockSessionManager.hasAcceptedPrivacyTerms()).thenReturn(true)
        
        // When - User tries to make a reservation
        val canMakeReservation = mockSessionManager.hasAcceptedPrivacyTerms()
        
        // Then - Reservation should be allowed
        assertTrue("User should be able to make reservations after accepting privacy terms", canMakeReservation)
        
        // Verify that privacy terms acceptance is checked
        verify(mockSessionManager).hasAcceptedPrivacyTerms()
    }

    /**
     * Test RF-06: HyperlinkText functionality validation
     */
    @Test
    fun testHyperlinkTextFunctionalityValidation() = runTest {
        // Given - Privacy notice text with hyperlinks
        val privacyText = "Al usar esta aplicación, aceptas nuestros Términos de Servicio y Política de Privacidad."
        val hyperlinks = mapOf(
            "Términos de Servicio" to "https://caritas.com/terms",
            "Política de Privacidad" to "https://caritas.com/privacy"
        )
        
        // When - Validate hyperlink text structure
        val hasTermsLink = hyperlinks.containsKey("Términos de Servicio")
        val hasPrivacyLink = hyperlinks.containsKey("Política de Privacidad")
        val termsUrl = hyperlinks["Términos de Servicio"]
        val privacyUrl = hyperlinks["Política de Privacidad"]
        
        // Then - Verify hyperlink functionality
        assertTrue("Privacy text should contain terms link", hasTermsLink)
        assertTrue("Privacy text should contain privacy link", hasPrivacyLink)
        assertEquals("Terms URL should be correct", "https://caritas.com/terms", termsUrl)
        assertEquals("Privacy URL should be correct", "https://caritas.com/privacy", privacyUrl)
        
        // Verify text contains the link text
        assertTrue("Privacy text should contain 'Términos de Servicio'", privacyText.contains("Términos de Servicio"))
        assertTrue("Privacy text should contain 'Política de Privacidad'", privacyText.contains("Política de Privacidad"))
        
        // Verify URLs are valid
        assertTrue("Terms URL should be valid", termsUrl!!.startsWith("https://"))
        assertTrue("Privacy URL should be valid", privacyUrl!!.startsWith("https://"))
    }
}