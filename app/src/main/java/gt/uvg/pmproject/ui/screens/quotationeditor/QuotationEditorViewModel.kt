package gt.uvg.pmproject.ui.screens.quotationeditor

import androidx.lifecycle.ViewModel
import gt.uvg.pmproject.data.MockData
import gt.uvg.pmproject.model.Quotation
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class QuotationEditorViewModel : ViewModel(){
    private val _quotation = MutableStateFlow(MockData.quotations.first())
    val quotation: StateFlow<Quotation> = _quotation
}