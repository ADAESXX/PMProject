package gt.uvg.pmproject.ui.components
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import gt.uvg.pmproject.ui.theme.mdIndicator
import gt.uvg.pmproject.ui.theme.mdPrimary

@Composable
fun BottomNavBar(selectedTab: String, onTabSelected: (String) -> Unit = {}) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(mdPrimary)
            .padding(vertical = 20.dp, horizontal = 16.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        BottomNavItem(
            icon = Icons.Default.Home,
            label = "Inicio",
            selected = selectedTab == "home",
            onClick = { onTabSelected("home") }
        )
        BottomNavItem(
            icon = Icons.Default.Description,
            label = "Cotizaciones",
            selected = selectedTab == "quotations",
            onClick = { onTabSelected("quotations") }
        )
    }
}

@Composable
private fun BottomNavItem(
    icon: ImageVector,
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(RoundedCornerShape(24.dp))
            .background(if (selected) mdIndicator else Color.Transparent)
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Icon(
            icon,
            contentDescription = label,
            tint = if (selected) mdPrimary else Color.White
        )
        Spacer(Modifier.width(6.dp))
        Text(
            label,
            color = if (selected) mdPrimary else Color.White,
            style = MaterialTheme.typography.labelSmall
        )
    }
}