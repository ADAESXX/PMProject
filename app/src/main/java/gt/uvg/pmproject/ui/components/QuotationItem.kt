package gt.uvg.pmproject.ui.components

import androidx.compose.foundation.background
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
import androidx.compose.ui.unit.dp
import gt.uvg.pmproject.model.Quotation
import gt.uvg.pmproject.model.QuotationStatus
import gt.uvg.pmproject.ui.common.formatCurrency
import gt.uvg.pmproject.ui.common.formatDate

@Composable
fun QuotationItem(quotation: Quotation, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(12.dp))
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Text(quotation.client, style = MaterialTheme.typography.bodyMedium)
            Text(
                "${quotation.eventType.label()} · ${formatDate(quotation.date)}",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Column(horizontalAlignment = Alignment.End) {
            Text(
                formatCurrency(quotation.amount),
                style = MaterialTheme.typography.bodyMedium,
                color = statusColor(quotation.status)
            )
            StatusBadge(status = quotation.status)
        }
    }
}

@Composable
fun StatusBadge(status: QuotationStatus, modifier: Modifier = Modifier) {
    val color = statusColor(status)
    Text(
        text = status.label().uppercase(),
        style = MaterialTheme.typography.labelSmall,
        color = color,
        modifier = modifier
            .background(color.copy(alpha = 0.15f), RoundedCornerShape(8.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp)
    )
}
