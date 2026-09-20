package gt.uvg.pmproject.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import gt.uvg.pmproject.model.Quotation
import gt.uvg.pmproject.ui.theme.statusApproved
import gt.uvg.pmproject.ui.theme.statusDraft
import gt.uvg.pmproject.ui.theme.statusSent


@Composable
fun QuotationItem(quotation: Quotation) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(12.dp))
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Text(quotation.client, style = MaterialTheme.typography.bodyMedium)
            Text(
                "${quotation.eventType} · ${quotation.date}",
                style = MaterialTheme.typography.labelSmall,
                color = Color.Gray
            )
        }
        Column(horizontalAlignment = Alignment.End) {
            Text(
                "$${"%,.0f".format(quotation.amount)}",
                style = MaterialTheme.typography.bodyMedium,
                color = statusColor(quotation.status)
            )
            StatusBadge(status = quotation.status)
        }
    }
}

@Composable
fun StatusBadge(status: String) {
    val color = statusColor(status)
    Text(
        text = status.uppercase(),
        style = MaterialTheme.typography.labelSmall,
        color = color,
        modifier = Modifier
            .background(color.copy(alpha = 0.15f), RoundedCornerShape(8.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp)
    )
}

@Composable
fun statusColor(status: String): Color = when (status) {
    "Aprobada" -> statusApproved
    "Enviada" -> statusSent
    else -> statusDraft
}