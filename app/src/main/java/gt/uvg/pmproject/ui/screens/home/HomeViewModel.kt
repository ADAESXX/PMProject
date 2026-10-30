package gt.uvg.pmproject.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import gt.uvg.pmproject.data.FavoriteRepository
import gt.uvg.pmproject.data.QuotationRepository
import gt.uvg.pmproject.model.FavoriteQuotation
import gt.uvg.pmproject.model.Quotation
import gt.uvg.pmproject.model.QuotationStatus
import gt.uvg.pmproject.ui.common.LOAD_ERROR_MESSAGE
import gt.uvg.pmproject.ui.common.LOAD_TIMEOUT_MS
import gt.uvg.pmproject.ui.common.OFFLINE_LOAD_MESSAGE
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class HomeUiState(
    val isLoading: Boolean = true,
    val quotations: List<Quotation> = emptyList(),
    val favorites: List<FavoriteQuotation> = emptyList(),
    val isOffline: Boolean = false,
    val errorMessage: String? = null
) {
    val draftCount: Int get() = quotations.count { it.status == QuotationStatus.DRAFT }
    val sentCount: Int get() = quotations.count { it.status == QuotationStatus.SENT }
    val approvedCount: Int get() = quotations.count { it.status == QuotationStatus.APPROVED }
}

class HomeViewModel(
    private val quotationRepository: QuotationRepository,
    private val favoriteRepository: FavoriteRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private var loadJob: Job? = null

    init {
        observeFavorites()
        loadQuotations()
    }

    fun onRetryClick() {
        loadQuotations()
    }

    private fun observeFavorites() {
        viewModelScope.launch {
            favoriteRepository.observeFavorites().collect { favorites ->
                _uiState.update { it.copy(favorites = favorites) }
            }
        }
    }

    private fun loadQuotations() {
        loadJob?.cancel()
        _uiState.update { it.copy(isLoading = true, errorMessage = null) }
        loadJob = viewModelScope.launch {
            val timeoutJob = launch {
                delay(LOAD_TIMEOUT_MS)
                _uiState.update { state ->
                    if (state.isLoading) {
                        state.copy(isLoading = false, errorMessage = OFFLINE_LOAD_MESSAGE)
                    } else {
                        state
                    }
                }
            }
            quotationRepository.observeQuotations()
                .catch {
                    timeoutJob.cancel()
                    _uiState.update { state ->
                        state.copy(isLoading = false, errorMessage = LOAD_ERROR_MESSAGE)
                    }
                }
                .collect { snapshot ->
                    timeoutJob.cancel()
                    _uiState.update { state ->
                        state.copy(
                            isLoading = false,
                            quotations = snapshot.quotations,
                            isOffline = snapshot.isFromCache,
                            errorMessage = null
                        )
                    }
                }
        }
    }

    companion object {
        fun factory(
            quotationRepository: QuotationRepository,
            favoriteRepository: FavoriteRepository
        ) = viewModelFactory {
            initializer { HomeViewModel(quotationRepository, favoriteRepository) }
        }
    }
}
