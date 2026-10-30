package gt.uvg.pmproject.ui.screens.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import gt.uvg.pmproject.ui.common.CACHE_NOTICE_MESSAGE
import gt.uvg.pmproject.ui.components.FavoriteItem
import gt.uvg.pmproject.ui.components.LoadingView
import gt.uvg.pmproject.ui.components.MessageView
import gt.uvg.pmproject.ui.components.NoticeBanner
import gt.uvg.pmproject.ui.components.QuotationItem
import gt.uvg.pmproject.ui.components.SummaryChip

@Composable
fun HomeRoute(
    viewModel: HomeViewModel,
    onNewQuotationClick: () -> Unit,
    onQuotationClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    HomeScreen(
        uiState = uiState,
        onNewQuotationClick = onNewQuotationClick,
        onQuotationClick = onQuotationClick,
        onRetryClick = viewModel::onRetryClick,
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    uiState: HomeUiState,
    onNewQuotationClick: () -> Unit,
    onQuotationClick: (String) -> Unit,
    onRetryClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Eventos Master",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.primary
                )
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onNewQuotationClick,
                icon = { Icon(Icons.Default.Add, contentDescription = null) },
                text = { Text("Nueva cotización") }
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            Text("¡Hola de nuevo!", style = MaterialTheme.typography.titleLarge)
            Spacer(Modifier.height(8.dp))
            Text("Aquí está tu resumen de hoy.", style = MaterialTheme.typography.bodyMedium)

            Spacer(Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                SummaryChip(
                    number = uiState.draftCount.toString(),
                    label = "Borradores",
                    modifier = Modifier.weight(1f)
                )
                SummaryChip(
                    number = uiState.sentCount.toString(),
                    label = "Enviadas",
                    modifier = Modifier.weight(1f)
                )
                SummaryChip(
                    number = uiState.approvedCount.toString(),
                    label = "Aprobadas",
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(Modifier.height(16.dp))
            if (uiState.isOffline) {
                NoticeBanner(message = CACHE_NOTICE_MESSAGE)
                Spacer(Modifier.height(16.dp))
            }

            LazyColumn(
                contentPadding = PaddingValues(bottom = 88.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                if (uiState.favorites.isNotEmpty()) {
                    item(key = "favorites-header") {
                        Text("Favoritas", style = MaterialTheme.typography.titleLarge)
                    }
                    items(uiState.favorites, key = { "favorite-${it.quotationId}" }) { favorite ->
                        FavoriteItem(
                            favorite = favorite,
                            modifier = Modifier.clickable { onQuotationClick(favorite.quotationId) }
                        )
                    }
                }
                item(key = "recent-header") {
                    Text("Actividad Reciente", style = MaterialTheme.typography.titleLarge)
                }
                when {
                    uiState.isLoading -> item(key = "loading") { LoadingView() }
                    uiState.errorMessage != null && uiState.quotations.isEmpty() ->
                        item(key = "error") {
                            MessageView(
                                message = uiState.errorMessage,
                                actionLabel = "Reintentar",
                                onActionClick = onRetryClick
                            )
                        }

                    uiState.quotations.isEmpty() -> item(key = "empty") {
                        MessageView(message = "Aún no hay cotizaciones. Crea la primera con el botón Nueva cotización.")
                    }

                    else -> items(uiState.quotations, key = { it.id }) { quotation ->
                        QuotationItem(
                            quotation = quotation,
                            modifier = Modifier.clickable { onQuotationClick(quotation.id) }
                        )
                    }
                }
            }
        }
    }
}
