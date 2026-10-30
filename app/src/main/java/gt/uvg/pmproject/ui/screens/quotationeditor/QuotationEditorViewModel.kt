package gt.uvg.pmproject.ui.screens.quotationeditor

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import gt.uvg.pmproject.data.FavoriteRepository
import gt.uvg.pmproject.data.QuotationRepository
import gt.uvg.pmproject.data.SaveOutcome
import gt.uvg.pmproject.model.EventType
import gt.uvg.pmproject.model.FavoriteQuotation
import gt.uvg.pmproject.model.Quotation
import gt.uvg.pmproject.model.QuotationStatus
import gt.uvg.pmproject.model.Service
import gt.uvg.pmproject.ui.common.DETAIL_ERROR_MESSAGE
import gt.uvg.pmproject.ui.common.NOT_FOUND_MESSAGE
import gt.uvg.pmproject.ui.common.SAVE_ERROR_MESSAGE
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.util.UUID

data class ServiceForm(
    val id: String,
    val name: String = "",
    val provider: String = "",
    val quantity: String = "1",
    val unitPrice: String = "",
    val nameTouched: Boolean = false,
    val quantityTouched: Boolean = false,
    val unitPriceTouched: Boolean = false,

    ) {
    val nameError: String? get() = if (nameTouched) validateServiceName(name) else null
    val quantityError: String? get() = if (quantityTouched) validateQuantity(quantity) else null
    val unitPriceError: String?
        get() = if (unitPriceTouched) validateUnitPrice(unitPrice) else null
    val isValid: Boolean
        get() = validateServiceName(name) == null &&
                validateQuantity(quantity) == null &&
                validateUnitPrice(unitPrice) == null

    fun toService(): Service = Service(
        id = id,
        name = name.trim(),
        provider = provider.trim(),
        quantity = quantity.trim().toIntOrNull() ?: 0,
        unitPrice = unitPrice.trim().toDoubleOrNull() ?: 0.0
    )
}

data class QuotationEditorUiState(
    val isLoading: Boolean = false,
    val loadError: String? = null,
    val client: String = "",
    val eventType: EventType? = null,
    val date: LocalDate? = null,
    val notes: String = "",
    val services: List<ServiceForm> = emptyList(),
    val clientTouched: Boolean = false,
    val isSaving: Boolean = false,
    val saveError: String? = null,
    val savedOutcome: SaveOutcome? = null
) {
    val clientError: String? get() = if (clientTouched) validateClient(client) else null

    val isFormValid: Boolean
        get() = validateClient(client) == null &&
                validateEventType(eventType) == null &&
                validateDate(date) == null &&
                services.isNotEmpty() &&
                services.all { it.isValid }

    val total: Double
        get() = services.sumOf { form ->
            val service = form.toService()
            service.subTotal
        }
}

class QuotationEditorViewModel(
    private val quotationId: String?,
    private val quotationRepository: QuotationRepository,
    private val favoriteRepository: FavoriteRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        QuotationEditorUiState(services = listOf(ServiceForm(id = newServiceId())))
    )
    val uiState: StateFlow<QuotationEditorUiState> = _uiState.asStateFlow()

    private var loadedStatus: QuotationStatus = QuotationStatus.DRAFT

    init {
        if (quotationId != null) loadQuotation(quotationId)
    }

    fun onClientChange(value: String) =
        _uiState.update { it.copy(client = value, clientTouched = true) }

    fun onEventTypeSelect(value: EventType) = _uiState.update { it.copy(eventType = value) }

    fun onDateSelect(value: LocalDate) = _uiState.update { it.copy(date = value) }

    fun onNotesChange(value: String) = _uiState.update { it.copy(notes = value) }

    fun onServiceNameChange(serviceId: String, value: String) =
        updateService(serviceId) { it.copy(name = value, nameTouched = true) }

    fun onServiceProviderChange(serviceId: String, value: String) =
        updateService(serviceId) { it.copy(provider = value) }

    fun onServiceQuantityChange(serviceId: String, value: String) =
        updateService(serviceId) { it.copy(quantity = value, quantityTouched = true) }

    fun onServiceUnitPriceChange(serviceId: String, value: String) =
        updateService(serviceId) { it.copy(unitPrice = value, unitPriceTouched = true) }

    fun onAddServiceClick() = _uiState.update {
        it.copy(services = it.services + ServiceForm(id = newServiceId()))
    }

    fun onRemoveServiceClick(serviceId: String) = _uiState.update { state ->
        state.copy(services = state.services.filterNot { it.id == serviceId })
    }

    fun onRetryLoadClick() {
        if (quotationId != null) loadQuotation(quotationId)
    }

    fun onSaveClick() {
        val state = _uiState.value
        if (!state.isFormValid || state.isSaving) return
        val eventType = state.eventType ?: return
        val date = state.date ?: return
        _uiState.update { it.copy(isSaving = true, saveError = null) }
        viewModelScope.launch {
            try {
                val quotation = Quotation(
                    id = quotationId ?: "",
                    client = state.client.trim(),
                    eventType = eventType,
                    date = date,
                    services = state.services.map { it.toService() },
                    status = loadedStatus,
                    notes = state.notes.trim(),
                    updatedAt = System.currentTimeMillis()
                )
                val outcome = quotationRepository.saveQuotation(quotation)

                favoriteRepository.updateFavorite(
                    FavoriteQuotation(
                        quotationId = outcome.quotationId,
                        client = quotation.client,
                        eventType = quotation.eventType,
                        amount = quotation.amount
                    )
                )
                _uiState.update { it.copy(isSaving = false, savedOutcome = outcome) }
            } catch (cancellation: CancellationException) {
                throw cancellation
            } catch (exception: Exception) {
                _uiState.update { it.copy(isSaving = false, saveError = SAVE_ERROR_MESSAGE) }
            }
        }
    }

    private fun updateService(serviceId: String, transform: (ServiceForm) -> ServiceForm) {
        _uiState.update { state ->
            state.copy(
                services = state.services.map { if (it.id == serviceId) transform(it) else it }
            )
        }
    }

    private fun loadQuotation(id: String) {
        _uiState.update { it.copy(isLoading = true, loadError = null) }
        viewModelScope.launch {
            try {
                val quotation = quotationRepository.getQuotation(id)
                if (quotation == null) {
                    _uiState.update { it.copy(isLoading = false, loadError = NOT_FOUND_MESSAGE) }
                } else {
                    loadedStatus = quotation.status
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            client = quotation.client,
                            eventType = quotation.eventType,
                            date = quotation.date,
                            notes = quotation.notes,
                            services = quotation.services.map { service ->
                                ServiceForm(
                                    id = service.id.ifBlank { newServiceId() },
                                    name = service.name,
                                    provider = service.provider,
                                    quantity = service.quantity.toString(),
                                    unitPrice = service.unitPrice.toString()
                                )
                            }
                        )
                    }
                }
            } catch (cancellation: CancellationException) {
                throw cancellation
            } catch (exception: Exception) {
                _uiState.update { it.copy(isLoading = false, loadError = DETAIL_ERROR_MESSAGE) }
            }
        }
    }

    private fun newServiceId(): String = UUID.randomUUID().toString()

    companion object {
        fun factory(
            quotationId: String?,
            quotationRepository: QuotationRepository,
            favoriteRepository: FavoriteRepository
        ) = viewModelFactory {
            initializer { QuotationEditorViewModel(quotationId, quotationRepository, favoriteRepository) }
        }
    }
}