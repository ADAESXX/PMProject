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
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import gt.uvg.pmproject.model.Service
import gt.uvg.pmproject.ui.theme.mdPrimary

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

    val subtotal = quotation.services.sumOf { it.subTotal }
    val taxes = subtotal * 0.21
    val total = subtotal + taxes

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Resumen de Cotización", color= mdPrimary, fontWeight = FontWeight. SemiBold) },
                navigationIcon = {
                    IconButton(onClick = onClose) {
                        Icon(Icons.Default.Close, contentDescription = "Cerrar")
                    }
                }
            )
        }

    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White, RoundedCornerShape(14.dp))
                    .padding(16.dp)
            ) {
                Text(quotation.client, style = MaterialTheme.typography.titleLarge)
                Text(quotation.eventType, style = MaterialTheme.typography.bodyMedium, color = Color.Gray)

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

            Spacer(Modifier.height(24.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White, RoundedCornerShape(14.dp))
                    .padding(16.dp)
            ) {
                Text("Términos y Condiciones", style = MaterialTheme.typography.titleLarge)
                Text(
                    "Cotización válida por 15 días. Para confirmar la reserva, se requiere un anticipo del 50% del total. Las cancelaciones con menos de 7 días de antelación incurrirán en una penalización del 25%.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray,

                )
            }

            Spacer(Modifier.height(24.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedButton(
                    onClick = { },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(28.dp)
                ) {
                    Text("Exportar PDF")
                }
                Button(
                    onClick = {},
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Black),
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(28.dp)
                ) {
                    Text("Enviar por correo")
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
        Column {
            Text(service.name, style = MaterialTheme.typography.bodyMedium)
            Text(service.provider, style = MaterialTheme.typography.labelSmall, color = Color.Gray)
        }
        Text(formatMoney(service.subTotal), style = MaterialTheme.typography.bodyMedium)
    }
}

@Composable
private fun SummaryLine(label: String, value: Double, emphasized: Boolean = false) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            label,
            style = if (emphasized) MaterialTheme.typography.titleLarge else MaterialTheme.typography.bodyMedium
        )
        Text(
            formatMoney(value),
            style = if (emphasized) MaterialTheme.typography.titleLarge else MaterialTheme.typography.bodyMedium,
            color = if (emphasized) mdPrimary else Color.Unspecified
        )
    }
}

private fun formatMoney(amount: Double): String {
    return "$" + "%,.2f".format(amount)
}