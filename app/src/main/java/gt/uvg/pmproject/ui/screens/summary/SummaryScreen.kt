package gt.uvg.pmproject.ui.screens.summary

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import gt.uvg.pmproject.model.Quotation
import gt.uvg.pmproject.model.Service
import gt.uvg.pmproject.ui.common.formatCurrency
import gt.uvg.pmproject.ui.common.formatDate
import gt.uvg.pmproject.ui.components.LoadingView
import gt.uvg.pmproject.ui.components.MessageView
import gt.uvg.pmproject.ui.components.NoticeBanner
import gt.uvg.pmproject.ui.components.StatusBadge
import gt.uvg.pmproject.ui.components.label
import gt.uvg.pmproject.ui.theme.statusDraft

private const val TAX_RATE = 0.21

@Composable
fun SummaryRoute(
    viewModel: SummaryViewModel,
    onBack: () -> Unit,
    onEditClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    SummaryScreen(
        uiState = uiState,
        onBack = onBack,
        onEditClick = onEditClick,
        onFavoriteClick = viewModel::onFavoriteClick,
        onAdvanceStatusClick = viewModel::onAdvanceStatusClick,
        onRetryClick = viewModel::onRetryClick,
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SummaryScreen(
    uiState: SummaryUiState,
    onBack: () -> Unit,
    onEditClick: (String) -> Unit,
    onFavoriteClick: () -> Unit,
    onAdvanceStatusClick: () -> Unit,
    onRetryClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val quotation = uiState.quotation
    Scaffold(
        modifier = modifier,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Resumen de Cotización",
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.SemiBold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                },
                actions = {
                    if (quotation != null) {
                        IconButton(onClick = onFavoriteClick) {
                            Icon(
                                imageVector = if (uiState.isFavorite) {
                                    Icons.Default.Star
                                } else {
                                    Icons.Default.StarBorder
                                },
                                contentDescription = if (uiState.isFavorite) {
                                    "Quitar de favoritas"
                                } else {
                                    "Marcar como favorita"
                                },
                                tint = if (uiState.isFavorite) {
                                    statusDraft
                                } else {
                                    MaterialTheme.colorScheme.onSurfaceVariant
                                }
                            )
                        }
                    }
                }
            )
        }
    ) { innerPadding ->
        when {
            uiState.isLoading -> LoadingView(Modifier.padding(innerPadding))
            quotation == null -> MessageView(
                message = uiState.errorMessage ?: "No se pudo cargar la cotización.",
                modifier = Modifier.padding(innerPadding),
                actionLabel = "Reintentar",
                onActionClick = onRetryClick
            )

            else -> SummaryContent(
                quotation = quotation,
                uiState = uiState,
                onEditClick = { onEditClick(quotation.id) },
                onAdvanceStatusClick = onAdvanceStatusClick,
                modifier = Modifier.padding(innerPadding)
            )
        }
    }
}

@Composable
private fun SummaryContent(
    quotation: Quotation,
    uiState: SummaryUiState,
    onEditClick: () -> Unit,
    onAdvanceStatusClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val subtotal = quotation.amount
    val taxes = subtotal * TAX_RATE
    val total = subtotal + taxes
    val nextStatus = uiState.nextStatus

    Column(
        modifier = modifier
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        uiState.infoMessage?.let { message ->
            NoticeBanner(message = message)
            Spacer(Modifier.height(16.dp))
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(14.dp))
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(quotation.client, style = MaterialTheme.typography.titleLarge)
                    Text(
                        "${quotation.eventType.label()} · ${formatDate(quotation.date)}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                StatusBadge(status = quotation.status)
            }

            Spacer(Modifier.height(20.dp))
            Text("Servicios Solicitados", style = MaterialTheme.typography.titleLarge)

            Spacer(Modifier.height(8.dp))
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                quotation.services.forEach { service ->
                    SummaryServiceRow(service)
                }
            }

            Spacer(Modifier.height(16.dp))
            HorizontalDivider()

            Spacer(Modifier.height(12.dp))
            SummaryLine(label = "Subtotal", value = subtotal)
            SummaryLine(label = "Impuestos (21%)", value = taxes)
            Spacer(Modifier.height(4.dp))
            SummaryLine(label = "Total Final", value = total, emphasized = true)
        }

        if (quotation.notes.isNotBlank()) {
            Spacer(Modifier.height(16.dp))
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(14.dp))
                    .padding(16.dp)
            ) {
                Text("Notas", style = MaterialTheme.typography.titleLarge)
                Text(
                    quotation.notes,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(Modifier.height(24.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            OutlinedButton(
                onClick = onEditClick,
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(28.dp)
            ) {
                Text("Editar")
            }
            if (nextStatus != null) {
                Button(
                    onClick = onAdvanceStatusClick,
                    enabled = !uiState.isUpdating,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(28.dp)
                ) {
                    Text("Marcar como ${nextStatus.label().lowercase()}")
                }
            }
        }
    }
}

@Composable
private fun SummaryServiceRow(service: Service, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Text(
                "${service.name} x${service.quantity}",
                style = MaterialTheme.typography.bodyMedium
            )
            if (service.provider.isNotBlank()) {
                Text(
                    service.provider,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        Text(formatCurrency(service.subTotal), style = MaterialTheme.typography.bodyMedium)
    }
}

@Composable
private fun SummaryLine(
    label: String,
    value: Double,
    modifier: Modifier = Modifier,
    emphasized: Boolean = false
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            label,
            style = if (emphasized) {
                MaterialTheme.typography.titleLarge
            } else {
                MaterialTheme.typography.bodyMedium
            }
        )
        Text(
            formatCurrency(value),
            style = if (emphasized) {
                MaterialTheme.typography.titleLarge
            } else {
                MaterialTheme.typography.bodyMedium
            },
            color = if (emphasized) MaterialTheme.colorScheme.primary else Color.Unspecified
        )
    }
}
