package gt.uvg.pmproject

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import gt.uvg.pmproject.ui.navigation.PMProjectNavigation
import gt.uvg.pmproject.ui.theme.QuotationsTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            QuotationsTheme {
                PMProjectNavigation()
            }
        }
    }
}
