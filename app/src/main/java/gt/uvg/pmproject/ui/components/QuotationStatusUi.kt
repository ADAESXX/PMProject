package gt.uvg.pmproject.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import gt.uvg.pmproject.model.QuotationStatus
import gt.uvg.pmproject.ui.theme.statusApproved
import gt.uvg.pmproject.ui.theme.statusDraft
import gt.uvg.pmproject.ui.theme.statusSent

fun QuotationStatus.label(): String = when (this) {
    QuotationStatus.DRAFT -> "Borrador"
    QuotationStatus.SENT -> "Enviada"
    QuotationStatus.APPROVED -> "Aprobada"
}

@Composable
fun statusColor(status: QuotationStatus): Color = when (status) {
    QuotationStatus.APPROVED -> statusApproved
    QuotationStatus.SENT -> statusSent
    QuotationStatus.DRAFT -> statusDraft
}
