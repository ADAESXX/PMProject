package gt.uvg.pmproject.ui.screens.quotationeditor

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import gt.uvg.pmproject.model.EventType
import gt.uvg.pmproject.model.Service
import gt.uvg.pmproject.util.formatCurrency
import gt.uvg.pmproject.util.formatDate
import java.time.Instant
import java.time.ZoneOffset

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuotationEditorScreen(
    quotationId: String,
    onClose: () -> Unit = {},
    onSave: () -> Unit = {},
    viewModel: QuotationEditorViewModel = viewModel(
        factory = QuotationEditorViewModel.factory(quotationId)
    )
) {
    val quotation by viewModel.quotation.collectAsState()
    val focusManager = LocalFocusManager.current
    var showDatePicker by remember { mutableStateOf(false) }
    var eventMenuExpanded by remember { mutableStateOf(false) }
    var showExitDialog by remember { mutableStateOf(false) }
    var showSaveDialog by remember { mutableStateOf(false) }
    val total = quotation.services.sumOf { it.subTotal }
    val isValid = viewModel.isFormValid

    fun requestExit() {
        if (viewModel.hasUnsavedChanges) showExitDialog = true else onClose()
    }
    BackHandler { requestExit() }

    if (showExitDialog) {
        AlertDialog(
            onDismissRequest = { showExitDialog = false },
            title = { Text("Cambios sin guardar") },
            text = { Text("¿Deseas salir? Los cambios realizados se perderán.") },
            confirmButton = {
                TextButton(onClick = {
                    showExitDialog = false
                    onClose()
                }) { Text("Descartar") }
            },
            dismissButton = {
                TextButton(onClick = { showExitDialog = false }) {
                    Text("Continuar editando")
                }
            }
        )
    }

    if (showSaveDialog) {
        AlertDialog(
            onDismissRequest = { showSaveDialog = false },
            title = { Text("Cotización validada") },
            text = { Text("Los datos son válidos, pero no existe persistencia implementada. No se ha guardado la cotización.") },
            confirmButton = {
                TextButton(onClick = { showSaveDialog = false }) {
                    Text("Entendido")
                }
            }
        )
    }

    if (showDatePicker) {
        val initialMillis = quotation.date
            ?.atStartOfDay(ZoneOffset.UTC)
            ?.toInstant()
            ?.toEpochMilli()
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = initialMillis
        )
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        val selected = Instant.ofEpochMilli(millis)
                            .atZone(ZoneOffset.UTC).toLocalDate()
                        viewModel.updateDate(selected)
                    }
                    showDatePicker = false
                }) { Text("Aceptar") }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Cancelar")
                }
            }
        ) { DatePicker(state = datePickerState) }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(if (viewModel.isNewQuotation) "Nueva Cotización" else "Editar Cotización")
                },
                navigationIcon = {
                    IconButton(onClick = ::requestExit) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Regresar")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier.padding(innerPadding).fillMaxSize()
                .verticalScroll(rememberScrollState()).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("Datos del Evento", style = MaterialTheme.typography.titleLarge)
            OutlinedTextField(
                value = quotation.client,
                onValueChange = viewModel::updateClient,
                label = { Text("Cliente *") },
                singleLine = true,
                isError = quotation.client.isBlank(),
                supportingText = {
                    if (quotation.client.isBlank()) Text("El cliente es obligatorio")
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = { focusManager.clearFocus() }
                ),
                modifier = Modifier.fillMaxWidth()
            )
            ExposedDropdownMenuBox(
                expanded = eventMenuExpanded,
                onExpandedChange = { eventMenuExpanded = it }
            ) {
                OutlinedTextField(
                    value = quotation.eventType?.label ?: "",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Tipo de evento *") },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(
                            expanded = eventMenuExpanded
                        )
                    },
                    isError = quotation.eventType == null,
                    modifier = Modifier.fillMaxWidth().menuAnchor()
                )
                ExposedDropdownMenu(
                    expanded = eventMenuExpanded,
                    onDismissRequest = { eventMenuExpanded = false }
                ) {
                    EventType.entries.forEach { eventType ->
                        DropdownMenuItem(
                            text = { Text(eventType.label) },
                            onClick = {
                                viewModel.updateEventType(eventType)
                                eventMenuExpanded = false
                            }
                        )
                    }
                }
            }
            OutlinedTextField(
                value = quotation.date?.let(::formatDate) ?: "",
                onValueChange = {},
                readOnly = true,
                label = { Text("Fecha del evento *") },
                trailingIcon = {
                    TextButton(onClick = { showDatePicker = true }) {
                        Text("Elegir")
                    }
                },
                modifier = Modifier.fillMaxWidth()
                    .clickable { showDatePicker = true }
            )
            HorizontalDivider()
            Text("Servicios Seleccionados",
                style = MaterialTheme.typography.titleLarge)
            if (quotation.services.isEmpty()) {
                Text("No hay servicios seleccionados.",
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
            } else {
                quotation.services.forEach { ServiceCard(it) }
            }
            HorizontalDivider()
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Total Estimado",
                    style = MaterialTheme.typography.titleMedium)
                Text(formatCurrency(total),
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.SemiBold)
            }
            Button(
                onClick = {
                    if (viewModel.getValidatedQuotation() != null) {
                        showSaveDialog = true
                    }
                },
                enabled = isValid,
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = RoundedCornerShape(28.dp)
            ) { Text("Validar Cotización") }
        }
    }
}

@Composable
private fun ServiceCard(service: Service) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(service.name, style = MaterialTheme.typography.titleMedium)
            Text(service.provider,
                color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text("Cantidad: ${service.quantity}")
            Text("Precio unitario: ${formatCurrency(service.unitPrice)}")
            Text("Subtotal: ${formatCurrency(service.subTotal)}",
                color = MaterialTheme.colorScheme.primary)
        }
    }
}
