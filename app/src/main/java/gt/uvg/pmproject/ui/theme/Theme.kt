package gt.uvg.pmproject.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
private val LightColors = lightColorScheme(
    primary = mdPrimary,
    onPrimary = mdOnPrimary,
    background = mdBackground,
    surface = mdSurface,
)

@Composable
fun QuotationsTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = LightColors,
        typography = Typography,
        content = content
    )
}