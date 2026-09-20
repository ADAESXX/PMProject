package gt.uvg.pmproject.ui.screens.quotationeditor

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import gt.uvg.pmproject.model.Service
import gt.uvg.pmproject.ui.theme.mdBorder
import gt.uvg.pmproject.ui.theme.mdLavender
import gt.uvg.pmproject.ui.theme.mdPrimary

@Composable
private fun quotationFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedContainerColor = mdLavender,
    unfocusedContainerColor = mdLavender,
    focusedBorderColor = mdPrimary,
    unfocusedBorderColor = mdBorder
)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuotationEditorScreen(viewModel: QuotationEditorViewModel = viewModel()) {
    val quotation by viewModel.quotation.collectAsState()
    val total = quotation.services.sumOf { it.subTotal }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text("Modificar Cotización", color = mdPrimary, fontWeight = FontWeight.SemiBold)
                },
                navigationIcon = {
                    IconButton(onClick = { }) {
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
                .verticalScroll(rememberScrollState()) ,
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(1.dp, RoundedCornerShape(16.dp))
                    .background(Color.White, RoundedCornerShape(16.dp))
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text("Datos del Evento", style = MaterialTheme.typography.titleLarge)

                OutlinedTextField(
                    value = quotation.client,
                    onValueChange = { },
                    label = { Text("CLIENTE") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = quotation.eventType,
                    onValueChange = {},
                    label = { Text("TIPO DE EVENTO") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = quotation.date,
                    onValueChange = { },
                    label = { Text("FECHA") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(16.dp))
                Text("Servicios Seleccionados", style = MaterialTheme.typography.titleLarge)

                Spacer(Modifier.height(8.dp))
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    quotation.services.forEach { service ->
                        ServiceCard(service)
                    }
                }

                Spacer(Modifier.height(12.dp))
                OutlinedButton(
                    onClick = {  },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("+ Agregar servicio")
                }

                Spacer(Modifier.height(16.dp))

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("Notas Adicionales", style = MaterialTheme.typography.titleLarge)
                    OutlinedTextField(
                        value = "",
                        onValueChange = {},
                        placeholder = { Text("Incluir montaje un día antes del evento. Confirmar menú vegetariano.") },
                        colors = quotationFieldColors(),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp)
                    )
                }

                Spacer(Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Total Estimado", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
                    Text(
                        formatMoney(total),
                        style = MaterialTheme.typography.titleLarge,
                        color = mdPrimary,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Spacer(Modifier.height(12.dp))
                Button(
                    onClick = {},
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Black),
                    shape = RoundedCornerShape(28.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                ) {
                    Text("Guardar Nueva Cotización", color = Color.White)
                }
            }
        }
    }
}

@Composable
private fun ServiceCard(service: Service, onDelete: () -> Unit = {}) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(mdLavender, RoundedCornerShape(14.dp))
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(service.name, style = MaterialTheme.typography.bodyMedium)
                Text(service.provider, style = MaterialTheme.typography.labelSmall, color = Color.Gray)
            }
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = "Eliminar", tint = Color(0xFFD32F2F))
            }
        }

        Spacer(Modifier.height(8.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Column(Modifier.weight(1f)) {
                Text("CANT.", style = MaterialTheme.typography.labelSmall)
                OutlinedTextField(
                    value = service.quantity.toString(),
                    onValueChange = { },
                    modifier = Modifier.fillMaxWidth()
                )
            }
            Column(Modifier.weight(1f)) {
                Text("PRECIO UNIT.", style = MaterialTheme.typography.labelSmall)
                OutlinedTextField(
                    value = service.unitPrice.toString(),
                    onValueChange = {},
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        Spacer(Modifier.height(4.dp))
        Text(
            "SUBTOTAL  ${formatMoney(service.subTotal)}",
            style = MaterialTheme.typography.labelSmall,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.End
        )
    }
}

private fun formatMoney(amount: Double): String {
    return "$" + "%,.2f".format(amount)
}