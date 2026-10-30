package gt.uvg.pmproject.ui.navigation

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import gt.uvg.pmproject.PMProjectApplication
import gt.uvg.pmproject.ui.screens.home.HomeRoute
import gt.uvg.pmproject.ui.screens.home.HomeViewModel
import gt.uvg.pmproject.ui.screens.quotationeditor.QuotationEditorRoute
import gt.uvg.pmproject.ui.screens.quotationeditor.QuotationEditorViewModel
import gt.uvg.pmproject.ui.screens.summary.SummaryRoute
import gt.uvg.pmproject.ui.screens.summary.SummaryViewModel
import kotlinx.serialization.Serializable

@Serializable
sealed interface PMProjectNavKey : NavKey {
    @Serializable
    data object Home : PMProjectNavKey

    @Serializable
    data class QuotationEditor(val quotationId: String? = null) : PMProjectNavKey

    @Serializable
    data class Summary(val quotationId: String) : PMProjectNavKey
}

@Composable
fun PMProjectNavigation(modifier: Modifier = Modifier) {
    val container = (LocalContext.current.applicationContext as PMProjectApplication).container
    val quotationRepository = container.quotationRepository
    val favoriteRepository = container.favoriteRepository
    val backStack = rememberNavBackStack(PMProjectNavKey.Home)

    BackHandler(enabled = backStack.size > 1) {
        backStack.removeLastOrNull()
    }

    NavDisplay(
        backStack = backStack,
        modifier = modifier,
        entryDecorators = listOf(
            rememberSaveableStateHolderNavEntryDecorator(),
            rememberViewModelStoreNavEntryDecorator()
        ),
        entryProvider = entryProvider {
            entry<PMProjectNavKey.Home> {
                HomeRoute(
                    viewModel = viewModel(
                        factory = HomeViewModel.factory(quotationRepository, favoriteRepository)
                    ),
                    onNewQuotationClick = { backStack.add(PMProjectNavKey.QuotationEditor()) },
                    onQuotationClick = { quotationId ->
                        backStack.add(PMProjectNavKey.Summary(quotationId))
                    }
                )
            }
            entry<PMProjectNavKey.QuotationEditor> { key ->
                QuotationEditorRoute(
                    viewModel = viewModel(
                        factory = QuotationEditorViewModel.factory(
                            key.quotationId,
                            quotationRepository,
                            favoriteRepository
                        )
                    ),
                    onBack = { backStack.removeLastOrNull() },
                    onSaved = { savedId ->
                        backStack.removeLastOrNull()
                        if (backStack.lastOrNull() == PMProjectNavKey.Summary(savedId)) {
                            backStack.removeLastOrNull()
                        }
                        backStack.add(PMProjectNavKey.Summary(savedId))
                    }
                )
            }
            entry<PMProjectNavKey.Summary> { key ->
                SummaryRoute(
                    viewModel = viewModel(
                        factory = SummaryViewModel.factory(
                            key.quotationId,
                            quotationRepository,
                            favoriteRepository
                        )
                    ),
                    onBack = { backStack.removeLastOrNull() },
                    onEditClick = { quotationId ->
                        backStack.add(PMProjectNavKey.QuotationEditor(quotationId))
                    }
                )
            }
        }
    )
}
