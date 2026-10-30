package gt.uvg.pmproject.ui.screens.summary

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import gt.uvg.pmproject.data.FavoriteRepository
import gt.uvg.pmproject.data.QuotationRepository
import gt.uvg.pmproject.model.FavoriteQuotation
import gt.uvg.pmproject.model.Quotation
import gt.uvg.pmproject.model.QuotationStatus
import gt.uvg.pmproject.ui.common.DETAIL_ERROR_MESSAGE
import gt.uvg.pmproject.ui.common.NOT_FOUND_MESSAGE
import gt.uvg.pmproject.ui.common.PENDING_SYNC_MESSAGE
import gt.uvg.pmproject.ui.common.SAVE_ERROR_MESSAGE
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

private const val STATUS_UPDATED_MESSAGE = "Estado actualizado."

data class SummaryUiState(
    val isLoading: Boolean = true,
    val quotation: Quotation? = null,
    val isFavorite: Boolean = false,
    val isUpdating: Boolean = false,
    val errorMessage: String? = null,
    val infoMessage: String? = null
) {
    val nextStatus: QuotationStatus?
        get() = when (quotation?.status) {
            QuotationStatus.DRAFT -> QuotationStatus.SENT
            QuotationStatus.SENT -> QuotationStatus.APPROVED
            else -> null
        }
}

class SummaryViewModel(
    private val quotationId: String,
    private val quotationRepository: QuotationRepository,
    private val favoriteRepository: FavoriteRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SummaryUiState())
    val uiState: StateFlow<SummaryUiState> = _uiState.asStateFlow()

    init {
        loadQuotation()
        observeFavorite()
    }

    fun onRetryClick() {
        loadQuotation()
    }

    fun onFavoriteClick() {
        val quotation = _uiState.value.quotation ?: return
        viewModelScope.launch {
            if (_uiState.value.isFavorite) {
                favoriteRepository.removeFavorite(quotation.id)
            } else {
                favoriteRepository.addFavorite(
                    FavoriteQuotation(
                        quotationId = quotation.id,
                        client = quotation.client,
                        eventType = quotation.eventType,
                        amount = quotation.amount
                    )
                )
            }
        }
    }

    fun onAdvanceStatusClick() {
        val state = _uiState.value
        val next = state.nextStatus ?: return
        if (state.isUpdating) return
        _uiState.update { it.copy(isUpdating = true, infoMessage = null) }
        viewModelScope.launch {
            try {
                val outcome = quotationRepository.updateStatus(quotationId, next)
                _uiState.update { current ->
                    current.copy(
                        isUpdating = false,
                        quotation = current.quotation?.copy(status = next),
                        infoMessage = if (outcome.isPending) {
                            PENDING_SYNC_MESSAGE
                        } else {
                            STATUS_UPDATED_MESSAGE
                        }
                    )
                }
            } catch (cancellation: CancellationException) {
                throw cancellation
            } catch (exception: Exception) {
                _uiState.update { it.copy(isUpdating = false, infoMessage = SAVE_ERROR_MESSAGE) }
            }
        }
    }

    private fun observeFavorite() {
        viewModelScope.launch {
            favoriteRepository.observeIsFavorite(quotationId).collect { isFavorite ->
                _uiState.update { it.copy(isFavorite = isFavorite) }
            }
        }
    }

    private fun loadQuotation() {
        _uiState.update { it.copy(isLoading = true, errorMessage = null) }
        viewModelScope.launch {
            try {
                val quotation = quotationRepository.getQuotation(quotationId)
                _uiState.update { state ->
                    if (quotation == null) {
                        state.copy(isLoading = false, errorMessage = NOT_FOUND_MESSAGE)
                    } else {
                        state.copy(isLoading = false, quotation = quotation)
                    }
                }
            } catch (cancellation: CancellationException) {
                throw cancellation
            } catch (exception: Exception) {
                _uiState.update { it.copy(isLoading = false, errorMessage = DETAIL_ERROR_MESSAGE) }
            }
        }
    }

    companion object {
        fun factory(
            quotationId: String,
            quotationRepository: QuotationRepository,
            favoriteRepository: FavoriteRepository
        ) = viewModelFactory {
            initializer {
                SummaryViewModel(quotationId, quotationRepository, favoriteRepository)
            }
        }
    }
}
