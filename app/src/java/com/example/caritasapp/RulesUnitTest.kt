package com.example.caritasapp

import org.junit.Assert.assertFalse
import org.junit.Test

class RulesUnitTest {

    @Test
    fun no_puede_reservar_sin_fechas() {
        val puede = puedeReservar(fechasSeleccionadas = false, cupo = 5)
        assertFalse(puede)
    }

    // Lógica de ejemplo: solo se puede reservar si hay fechas y cupo > 0
    private fun puedeReservar(fechasSeleccionadas: Boolean, cupo: Int): Boolean =
        fechasSeleccionadas && cupo > 0
}
