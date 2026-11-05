package gt.uvg.pmproject

import org.junit.Test
import org.junit.Assert.*
import gt.uvg.pmproject.ui.screens.quotationeditor.validateClient

class QuotationWithEmptySpaces {
    @Test
    fun clientIsBlank() {
        assertEquals("El nombre del cliente es obligatorio", validateClient(""))
    }
    @Test
    fun clientNameTooShort() {
        assertEquals("Escribe al menos 3 caracteres", validateClient("Al"))
    }
    @Test
    fun clientNameIsCorrect() {
        assertNull(validateClient("Dulce Sandoval"))
    }
}