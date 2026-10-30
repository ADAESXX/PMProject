package gt.uvg.pmproject.ui.screens.quotationeditor

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import gt.uvg.pmproject.model.EventType
import gt.uvg.pmproject.ui.common.PENDING_SYNC_MESSAGE
import gt.uvg.pmproject.ui.common.formatCurrency
import gt.uvg.pmproject.ui.common.formatDate
import gt.uvg.pmproject.ui.components.LoadingView
import gt.uvg.pmproject.ui.components.MessageView
import gt.uvg.pmproject.ui.components.label
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset

@Composable
fun QuotationEditorRoute(
    viewModel: QuotationEditorViewModel,
    onBack: () -> Unit,
    onSaved: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    QuotationEditorScreen(
        uiState = uiState,
        onBack = onBack,
        onSaved = onSaved,
        onClientChange = viewModel::onClientChange,
        onEventTypeSelect = viewModel::onEventTypeSelect,
        onDateSelect = viewModel::onDateSelect,
        onNotesChange = viewModel::onNotesChange,
        onServiceNameChange = viewModel::onServiceNameChange,
        onServiceProviderChange = viewModel::onServiceProviderChange,
        onServiceQuantityChange = viewModel::onServiceQuantityChange,
        onServiceUnitPriceChange = viewModel::onServiceUnitPriceChange,
        onAddServiceClick = viewModel::onAddServiceClick,
        onRemoveServiceClick = viewModel::onRemoveServiceClick,
        onSaveClick = viewModel::onSaveClick,
        onRetryLoadClick = viewModel::onRetryLoadClick,
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuotationEditorScreen(
    uiState: QuotationEditorUiState,
    onBack: () -> Unit,
    onSaved: (String) -> Unit,
    onClientChange: (String) -> Unit,
    onEventTypeSelect: (EventType) -> Unit,
    onDateSelect: (LocalDate) -> Unit,
    onNotesChange: (String) -> Unit,
    onServiceNameChange: (String, String) -> Unit,
    onServiceProviderChange: (String, String) -> Unit,
    onServiceQuantityChange: (String, String) -> Unit,
    onServiceUnitPriceChange: (String, String) -> Unit,
    onAddServiceClick: () -> Unit,
    onRemoveServiceClick: (String) -> Unit,
    onSaveClick: () -> Unit,
    onRetryLoadClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val savedOutcome = uiState.savedOutcome

    LaunchedEffect(savedOutcome) {
        if (savedOutcome != null && !savedOutcome.isPending) {
            onSaved(savedOutcome.quotationId)
        }
    }

    if (savedOutcome != null && savedOutcome.isPending) {
        AlertDialog(
            onDismissRequest = { onSaved(savedOutcome.quotationId) },
            title = { Text("Sin conexión") },
            text = { Text(PENDING_SYNC_MESSAGE) },
            confirmButton = {
                TextButton(onClick = { onSaved(savedOutcome.quotationId) }) {
                    Text("Entendido")
                }
            }
        )
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Datos de la Cotización",
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.SemiBold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { innerPadding ->
        when {
            uiState.isLoading -> LoadingView(Modifier.padding(innerPadding))
            uiState.loadError != null -> MessageView(
                message = uiState.loadError,
                modifier = Modifier.padding(innerPadding),
                actionLabel = "Reintentar",
                onActionClick = onRetryLoadClick
            )

            else -> Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(16.dp))
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text("Datos del Evento", style = MaterialTheme.typography.titleLarge)
                    FormField(
                        label = "CLIENTE",
                        value = uiState.client,
                        error = uiState.clientError,
                        onValueChange = onClientChange,
                        imeAction = ImeAction.Done,
                        capitalization = KeyboardCapitalization.Words
                    )
                    EventTypeSelector(
                        selected = uiState.eventType,
                        onSelect = onEventTypeSelect
                    )
                    DateField(
                        date = uiState.date,
                        onDateSelect = onDateSelect
                    )
                }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(16.dp))
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text("Servicios Seleccionados", style = MaterialTheme.typography.titleLarge)
                    uiState.services.forEach { service ->
                        ServiceCard(
                            service = service,
                            onNameChange = { onServiceNameChange(service.id, it) },
                            onProviderChange = { onServiceProviderChange(service.id, it) },
                            onQuantityChange = { onServiceQuantityChange(service.id, it) },
                            onUnitPriceChange = { onServiceUnitPriceChange(service.id, it) },
                            onRemoveClick = { onRemoveServiceClick(service.id) }
                        )
                    }
                    if (uiState.services.isEmpty()) {
                        Text(
                            "Agrega al menos un servicio.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                    OutlinedButton(
                        onClick = onAddServiceClick,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("+ Agregar servicio")
                    }
                }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(16.dp))
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("Notas Adicionales", style = MaterialTheme.typography.titleLarge)
                    OutlinedTextField(
                        value = uiState.notes,
                        onValueChange = onNotesChange,
                        placeholder = { Text("Incluir montaje un día antes del evento.") },
                        keyboardOptions = KeyboardOptions(
                            capitalization = KeyboardCapitalization.Sentences,
                            keyboardType = KeyboardType.Text,
                            imeAction = ImeAction.Done
                        ),
                        maxLines = 4,
                        colors = quotationFieldColors(),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp)
                    )
                    Spacer(Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            "Total Estimado",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            formatCurrency(uiState.total),
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                uiState.saveError?.let { message ->
                    Text(
                        message,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.error
                    )
                }

                Button(
                    onClick = onSaveClick,
                    enabled = uiState.isFormValid && !uiState.isSaving,
                    shape = RoundedCornerShape(28.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                ) {
                    Text(
                        text = when {
                            uiState.isSaving -> "Guardando..."
                            uiState.isFormValid -> "Guardar Cotización"
                            else -> "Completa los campos obligatorios"
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun quotationFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedContainerColor = MaterialTheme.colorScheme.surface,
    unfocusedContainerColor = MaterialTheme.colorScheme.surface,
    focusedBorderColor = MaterialTheme.colorScheme.primary,
    unfocusedBorderColor = MaterialTheme.colorScheme.outline
)

@Composable
private fun FormField(
    label: String,
    value: String,
    error: String?,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    keyboardType: KeyboardType = KeyboardType.Text,
    imeAction: ImeAction = ImeAction.Next,
    capitalization: KeyboardCapitalization = KeyboardCapitalization.None
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        isError = error != null,
        supportingText = { if (error != null) Text(error) },
        singleLine = true,
        keyboardOptions = KeyboardOptions(
            capitalization = capitalization,
            keyboardType = keyboardType,
            imeAction = imeAction
        ),
        colors = quotationFieldColors(),
        modifier = modifier.fillMaxWidth()
    )
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun EventTypeSelector(
    selected: EventType?,
    onSelect: (EventType) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            "TIPO DE EVENTO",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            EventType.entries.forEach { type ->
                FilterChip(
                    selected = type == selected,
                    onClick = { onSelect(type) },
                    label = { Text(type.label()) }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DateField(
    date: LocalDate?,
    onDateSelect: (LocalDate) -> Unit,
    modifier: Modifier = Modifier
) {
    var showPicker by rememberSaveable { mutableStateOf(false) }

    Box(modifier = modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = date?.let { formatDate(it) } ?: "",
            onValueChange = {},
            readOnly = true,
            singleLine = true,
            label = { Text("FECHA DEL EVENTO") },
            trailingIcon = {
                Icon(Icons.Default.CalendarMonth, contentDescription = "Seleccionar fecha")
            },
            colors = quotationFieldColors(),
            modifier = Modifier.fillMaxWidth()
        )
        Box(
            modifier = Modifier
                .matchParentSize()
                .clickable { showPicker = true }
        )
    }

    if (showPicker) {
        val pickerState = rememberDatePickerState(
            initialSelectedDateMillis = date?.atStartOfDay(ZoneOffset.UTC)?.toInstant()
                ?.toEpochMilli()
        )
        DatePickerDialog(
            onDismissRequest = { showPicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        pickerState.selectedDateMillis?.let { millis ->
                            onDateSelect(
                                Instant.ofEpochMilli(millis).atZone(ZoneOffset.UTC).toLocalDate()
                            )
                        }
                        showPicker = false
                    }
                ) {
                    Text("Aceptar")
                }
            },
            dismissButton = {
                TextButton(onClick = { showPicker = false }) {
                    Text("Cancelar")
                }
            }
        ) {
            DatePicker(state = pickerState)
        }
    }
}

@Composable
private fun ServiceCard(
    service: ServiceForm,
    onNameChange: (String) -> Unit,
    onProviderChange: (String) -> Unit,
    onQuantityChange: (String) -> Unit,
    onUnitPriceChange: (String) -> Unit,
    onRemoveClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(14.dp))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Servicio", style = MaterialTheme.typography.labelSmall)
            IconButton(onClick = onRemoveClick) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = "Eliminar servicio",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
        FormField(
            label = "NOMBRE DEL SERVICIO",
            value = service.name,
            error = service.nameError,
            onValueChange = onNameChange,
            capitalization = KeyboardCapitalization.Sentences
        )
        FormField(
            label = "PROVEEDOR (opcional)",
            value = service.provider,
            error = null,
            onValueChange = onProviderChange,
            capitalization = KeyboardCapitalization.Words
        )
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            FormField(
                label = "CANT.",
                value = service.quantity,
                error = service.quantityError,
                onValueChange = onQuantityChange,
                modifier = Modifier.weight(1f),
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Next
            )
            FormField(
                label = "PRECIO UNIT.",
                value = service.unitPrice,
                error = service.unitPriceError,
                onValueChange = onUnitPriceChange,
                modifier = Modifier.weight(1f),
                keyboardType = KeyboardType.Decimal,
                imeAction = ImeAction.Done
            )
        }
        Text(
            "SUBTOTAL  ${formatCurrency(service.toService().subTotal)}",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.fillMaxWidth()
        )
    }
}
