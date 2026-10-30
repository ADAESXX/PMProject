package gt.uvg.pmproject.data

import gt.uvg.pmproject.model.Quotation
import gt.uvg.pmproject.model.QuotationStatus
import kotlinx.coroutines.flow.Flow

data class QuotationsSnapshot(
    val quotations: List<Quotation>,
    val isFromCache: Boolean
)

data class SaveOutcome(
    val quotationId: String,
    val isPending: Boolean
)

class DataUnavailableException : Exception("Data could not be loaded in time")

interface QuotationRepository {
    fun observeQuotations(): Flow<QuotationsSnapshot>

    suspend fun getQuotation(quotationId: String): Quotation?

    suspend fun saveQuotation(quotation: Quotation): SaveOutcome

    suspend fun updateStatus(quotationId: String, status: QuotationStatus): SaveOutcome
}
