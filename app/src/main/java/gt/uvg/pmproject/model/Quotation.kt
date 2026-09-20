package gt.uvg.pmproject.model

import java.time.temporal.TemporalAmount

data class Quotation (
    val id: String,
    val client: String,
    val eventType: String,
    val date: String,
    val services: List<Service>,
    val status: String,
    val amount: Double
)