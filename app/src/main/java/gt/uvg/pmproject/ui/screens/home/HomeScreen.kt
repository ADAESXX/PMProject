package gt.uvg.pmproject.ui.screens.home

import android.R.attr.tint
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import gt.uvg.pmproject.ui.components.BottomNavBar
import gt.uvg.pmproject.ui.components.NewQuotationButton
import gt.uvg.pmproject.ui.components.QuotationItem
import gt.uvg.pmproject.ui.components.SummaryChip
import gt.uvg.pmproject.ui.theme.mdBackground
import gt.uvg.pmproject.ui.theme.mdPrimary
import gt.uvg.pmproject.data.MockData

/*
* En este código se uso IA para resolver el problema que se tenía con TopAppBar
* Reponsable: Abigail Escobar
* Fecha: 19/09/2026
* */

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateToQuotations: () -> Unit = {},
    onQuotationClick: (String) -> Unit = {},
    viewModel: HomeViewModel = viewModel()
) {
    val quotations by viewModel.recentQuotations.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(
                    text="Eventos Mastes",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                ) },
                actions = {
                    IconButton(onClick = { }) {
                        Icon(Icons.Default.Notifications, contentDescription = "Notificaciones")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = mdPrimary
                )
            )
        },
        bottomBar = {
            BottomNavBar(
                selectedTab = "home",
                onTabSelected = { tab -> if (tab == "quotations") onNavigateToQuotations() }
            )
        },
        containerColor = mdBackground
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
            )
            {
                SummaryChip(number = "12", label = "Activas", modifier = Modifier.weight(1f))
                SummaryChip(number = "5", label = "Aprobadas", modifier = Modifier.weight(1f))
                SummaryChip(number = "3", label = "Borradores", modifier = Modifier.weight(1f))
            }

            Spacer(Modifier.height(16.dp))
            NewQuotationButton(onClick = onNavigateToQuotations)

            Spacer(Modifier.height(24.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Actividad Reciente", style = MaterialTheme.typography.titleLarge)
                Text(
                    "Ver todas",
                    style = MaterialTheme.typography.labelSmall,
                    color = mdPrimary,
                    modifier = Modifier.clickable {  }
                )
            }

            Spacer(Modifier.height(8.dp))
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                items(quotations) { quotation ->
                    QuotationItem(
                        quotation,
                        modifier = Modifier.clickable { onQuotationClick(quotation.id) }
                    )
                }
            }
        }
    }
}