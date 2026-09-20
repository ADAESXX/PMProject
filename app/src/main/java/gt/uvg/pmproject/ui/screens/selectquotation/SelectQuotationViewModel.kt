package gt.uvg.pmproject.ui.screens.selectquotation

import androidx.lifecycle.ViewModel
import gt.uvg.pmproject.data.MockData
import gt.uvg.pmproject.model.Quotation
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class SelectQuotationViewModel : ViewModel(){
    private val _quotations = MutableStateFlow(MockData.quotations)
    val quotations: StateFlow<List<Quotation>> = _quotations
}