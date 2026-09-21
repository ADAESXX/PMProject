package gt.uvg.pmproject.ui.navigation

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import gt.uvg.pmproject.ui.screens.home.HomeScreen
import gt.uvg.pmproject.ui.screens.quotationeditor.QuotationEditorScreen
import gt.uvg.pmproject.ui.screens.selectquotation.SelectQuotationScreen
import gt.uvg.pmproject.ui.screens.summary.SummaryScreen
import kotlinx.serialization.Serializable

@Serializable
sealed interface PMProjectNavKey : NavKey {
    @Serializable
    data object Home : PMProjectNavKey

    @Serializable
    data object SelectQuotation : PMProjectNavKey

    @Serializable
    data class QuotationEditor(val quotationId: String) : PMProjectNavKey

    @Serializable
    data class Summary(val quotationId: String) : PMProjectNavKey
}

@Composable
fun PMProjectNavigation(modifier: Modifier = Modifier) {
    val backStack = rememberNavBackStack(PMProjectNavKey.Home)

    BackHandler(enabled = backStack.size > 1) {
        backStack.removeLastOrNull()
    }

    NavDisplay(
        backStack = backStack,
        modifier = modifier,
        entryProvider = entryProvider {
            entry<PMProjectNavKey.Home> {
                HomeScreen(
                    onNavigateToQuotations = { backStack.add(PMProjectNavKey.SelectQuotation) },
                    onQuotationClick = { quotationId ->
                        backStack.add(PMProjectNavKey.QuotationEditor(quotationId))
                    }
                )
            }
            entry<PMProjectNavKey.SelectQuotation> {
                SelectQuotationScreen(
                    onClose = { backStack.removeLastOrNull() },
                    onNavigateHome = { backStack.removeLastOrNull() },
                    onSelectQuotation = { quotationId ->
                        backStack.add(PMProjectNavKey.QuotationEditor(quotationId))
                    },
                    onCreateFromScratch = {
                        backStack.add(PMProjectNavKey.QuotationEditor(quotationId = "new"))
                    }
                )
            }
            entry<PMProjectNavKey.QuotationEditor> { key ->
                QuotationEditorScreen(
                    quotationId = key.quotationId,
                    onClose = { backStack.removeLastOrNull() },
                    onSave = { backStack.add(PMProjectNavKey.Summary(key.quotationId)) }
                )
            }
            entry<PMProjectNavKey.Summary> { key ->
                SummaryScreen(
                    quotationId = key.quotationId,
                    onClose = {
                        backStack.clear()
                        backStack.add(PMProjectNavKey.Home)
                    }
                )
            }
        }
    )
}