package gt.uvg.pmproject.ui.screens.quotationeditor

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import gt.uvg.pmproject.data.MockData
import gt.uvg.pmproject.model.Quotation
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

private fun emptyQuotation(id: String)= Quotation(
    id = id,
    client= "",
    eventType = "",
    date="",
    services= emptyList(),
    status = "Borrador",
    amount = 0.0

)
class QuotationEditorViewModel(quotationId: String) : ViewModel() {
    private val _quotation = MutableStateFlow(
        if(quotationId == "new"){
            emptyQuotation(quotationId)
        }else{
            MockData.quotations.firstOrNull{ it.id == quotationId} ?: emptyQuotation(quotationId)
        }
    )
    val quotation: StateFlow<Quotation> = _quotation

    companion object {
        fun factory(quotationId: String) = viewModelFactory {
            initializer { QuotationEditorViewModel(quotationId) }
        }
    }
}