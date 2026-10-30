package gt.uvg.pmproject.model

data class FavoriteQuotation(
    val quotationId: String,
    val client: String,
    val eventType: EventType,
    val amount: Double
)
