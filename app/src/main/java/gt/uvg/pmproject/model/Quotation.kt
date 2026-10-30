package gt.uvg.pmproject.model

import java.time.LocalDate

data class Quotation(
    val id: String,
    val client: String,
    val eventType: EventType,
    val date: LocalDate,
    val services: List<Service>,
    val status: QuotationStatus,
    val notes: String,
    val updatedAt: Long
) {
    val amount: Double get() = services.sumOf { it.subTotal }
}
