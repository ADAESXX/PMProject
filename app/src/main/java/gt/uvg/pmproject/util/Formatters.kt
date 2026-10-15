package gt.uvg.pmproject.util

import java.text.NumberFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.Locale

val AppLocale: Locale = Locale.forLanguageTag("es-GT")

fun formatCurrency(amount: Double): String =
    NumberFormat.getCurrencyInstance(AppLocale).format(amount)
fun formatDate(date: LocalDate): String =
    date.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM).withLocale(AppLocale))