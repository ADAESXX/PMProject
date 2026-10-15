package gt.uvg.pmproject.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColors = lightColorScheme(
    primary = Color(0xFF0F5C5C),
    onPrimary = Color(0xFFFFFFFF),
    secondary = Color(0xFFFFB74D),
    tertiary = Color(0xFF0FBA6B),
    background = Color(0xFFF0F4F5),
    onBackground = Color(0xFF191C1C),
    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF191C1C),
    onSurfaceVariant = Color(0xFF6B6B6B),
    surfaceContainer = Color(0xFFFFFFFF),
    surfaceContainerHigh = Color(0xFFFFFFFF),
    surfaceContainerHighest = Color(0xFFF3F1FA),
    outlineVariant = Color(0xFFDAD6EC),
    error = Color(0xFFD32F2F),
    inverseSurface = Color(0xFF000000),
    inverseOnSurface = Color(0xFFFFFFFF)
)

private val DarkColors = darkColorScheme(
    primary = Color(0xFF6FD6D6),
    onPrimary = Color(0xFF003737),
    secondary = Color(0xFFFFB74D),
    tertiary = Color(0xFF5FD9B5),
    background = Color(0xFF0F1415),
    onBackground = Color(0xFFE1E3E3),
    surface = Color(0xFF171D1E),
    onSurface = Color(0xFFE1E3E3),
    onSurfaceVariant = Color(0xFFB0BEC0),
    surfaceContainer = Color(0xFF1B2223),
    surfaceContainerHigh = Color(0xFF232A2B),
    surfaceContainerHighest = Color(0xFF2B3334),
    outlineVariant = Color(0xFF3F4849),
    error = Color(0xFFFFB4AB),
    inverseSurface = Color(0xFFE1E3E3),
    inverseOnSurface = Color(0xFF191C1C)
)

@Composable
fun QuotationsTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        typography = Typography,
        content = content
    )
}