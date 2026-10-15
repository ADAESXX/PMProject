package gt.uvg.pmproject.ui.screens.quotationeditor

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import gt.uvg.pmproject.data.MockData
import gt.uvg.pmproject.model.EventType
import gt.uvg.pmproject.model.Quotation
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.time.LocalDate

private fun emptyQuotation(id: String) = Quotation(
    id = id,
    client = "",
    eventType = null,
    date = null,
    services = emptyList(),
    status = "Borrador",
    amount = 0.0
)

class QuotationEditorViewModel(quotationId: String) : ViewModel() {
    private val originalQuotation = if (quotationId == "new") {
        emptyQuotation(quotationId)
    } else {
        MockData.quotations.firstOrNull { it.id == quotationId }
            ?: emptyQuotation(quotationId)
    }

    private val _quotation = MutableStateFlow(originalQuotation)
    val quotation: StateFlow<Quotation> = _quotation
    val isNewQuotation: Boolean = quotationId == "new"

    val isFormValid: Boolean
        get() = _quotation.value.let {
            it.client.isNotBlank() && it.eventType != null && it.date != null
        }

    val hasUnsavedChanges: Boolean
        get() = _quotation.value != originalQuotation

    fun updateClient(value: String) {
        _quotation.value = _quotation.value.copy(client = value)
    }

    fun updateEventType(value: EventType) {
        _quotation.value = _quotation.value.copy(eventType = value)
    }

    fun updateDate(value: LocalDate) {
        _quotation.value = _quotation.value.copy(date = value)
    }

    fun getValidatedQuotation(): Quotation? {
        if (!isFormValid) return null
        val current = _quotation.value
        return current.copy(
            client = current.client.trim(),
            amount = current.services.sumOf { it.subTotal }
        )
    }

    companion object {
        fun factory(quotationId: String) = viewModelFactory {
            initializer { QuotationEditorViewModel(quotationId) }
        }
    }
}