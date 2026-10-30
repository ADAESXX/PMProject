package gt.uvg.pmproject.ui.common

import java.text.NumberFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.Locale

private val appLocale: Locale = Locale.forLanguageTag("es-GT")

fun formatCurrency(amount: Double): String =
    NumberFormat.getCurrencyInstance(appLocale).format(amount)

fun formatDate(date: LocalDate): String =
    date.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM).withLocale(appLocale))
