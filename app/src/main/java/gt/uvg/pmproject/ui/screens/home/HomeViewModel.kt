package gt.uvg.pmproject.ui.screens.home

import androidx.lifecycle.ViewModel
import gt.uvg.pmproject.data.MockData
import gt.uvg.pmproject.model.Quotation
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class HomeViewModel: ViewModel(){
    private val _recentQuotations = MutableStateFlow(MockData.quotations)
    val recentQuotations: StateFlow<List<Quotation>> = _recentQuotations
}