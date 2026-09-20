package gt.uvg.pmproject.ui.screens.quotationeditor

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import gt.uvg.pmproject.data.MockData
import gt.uvg.pmproject.model.Quotation
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class QuotationEditorViewModel(quotationId: String) : ViewModel() {
    private val _quotation = MutableStateFlow(
        MockData.quotations.firstOrNull { it.id == quotationId } ?: MockData.quotations.first()
    )
    val quotation: StateFlow<Quotation> = _quotation

    companion object {
        fun factory(quotationId: String) = viewModelFactory {
            initializer { QuotationEditorViewModel(quotationId) }
        }
    }
}