package gt.uvg.pmproject.ui.screens.summary

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import gt.uvg.pmproject.model.Service
import gt.uvg.pmproject.util.formatCurrency
import gt.uvg.pmproject.util.formatDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SummaryScreen(
    quotationId: String,
    onClose: () -> Unit = {},
    viewModel: SummaryViewModel = viewModel(
        factory = SummaryViewModel.factory(quotationId)
    )
) {
    val quotation by viewModel.quotation.collectAsState()
    val total = quotation.services.sumOf { it.subTotal }
    val subtotal = total / 1.12
    val taxes = total - subtotal

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Resumen de Cotización") },
                navigationIcon = {
                    IconButton(onClick = onClose) {
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
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(quotation.client,
                        style = MaterialTheme.typography.titleLarge)
                    Text(
                        listOfNotNull(
                            quotation.eventType?.label,
                            quotation.date?.let(::formatDate)
                        ).joinToString(" · "),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    HorizontalDivider()
                    Text("Servicios Solicitados",
                        style = MaterialTheme.typography.titleLarge)
                    quotation.services.forEach { SummaryServiceRow(it) }
                    HorizontalDivider()
                    SummaryLine("Subtotal", subtotal)
                    SummaryLine("Impuestos (12%)", taxes)
                    SummaryLine("Total Final", total, emphasized = true)
                }
            }
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("Términos y Condiciones",
                        style = MaterialTheme.typography.titleLarge)
                    Text(
                        "Cotización válida por 15 días. " +
                                "Para confirmar la reserva, se requiere un anticipo " +
                                "del 50% del total. Las cancelaciones con menos de " +
                                "7 días de antelación incurrirán en una " +
                                "penalización del 25%.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
private fun SummaryServiceRow(service: Service) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(service.name,
                style = MaterialTheme.typography.bodyMedium)
            Text(service.provider,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Text(formatCurrency(service.subTotal),
            style = MaterialTheme.typography.bodyMedium)
    }
}

@Composable
private fun SummaryLine(
    label: String,
    value: Double,
    emphasized: Boolean = false
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            label,
            style = if (emphasized) MaterialTheme.typography.titleLarge
            else MaterialTheme.typography.bodyMedium,
            modifier = Modifier.weight(1f)
        )
        Text(
            formatCurrency(value),
            style = if (emphasized) MaterialTheme.typography.titleLarge
            else MaterialTheme.typography.bodyMedium,
            color = if (emphasized) MaterialTheme.colorScheme.primary
            else MaterialTheme.colorScheme.onSurface
        )
    }
}