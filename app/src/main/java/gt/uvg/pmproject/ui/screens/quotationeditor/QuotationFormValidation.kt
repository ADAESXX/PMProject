package gt.uvg.pmproject.ui.screens.quotationeditor

import gt.uvg.pmproject.model.EventType
import java.time.LocalDate

private const val MIN_CLIENT_LENGTH = 3

fun validateClient(value: String): String? = when {
    value.isBlank() -> "El nombre del cliente es obligatorio"
    value.trim().length < MIN_CLIENT_LENGTH -> "Escribe al menos $MIN_CLIENT_LENGTH caracteres"
    else -> null
}

fun validateEventType(value: EventType?): String? =
    if (value == null) "Selecciona el tipo de evento" else null

fun validateDate(value: LocalDate?): String? =
    if (value == null) "La fecha es obligatoria" else null

fun validateServiceName(value: String): String? =
    if (value.isBlank()) "Indica el nombre del servicio" else null

fun validateQuantity(value: String): String? {
    val quantity = value.trim().toIntOrNull()
    return if (quantity == null || quantity < 1) "Ingresa un entero mayor a 0" else null
}

fun validateUnitPrice(value: String): String? {
    val price = value.trim().toDoubleOrNull()
    return if (price == null || price <= 0.0) "Ingresa un precio mayor a 0" else null
}
