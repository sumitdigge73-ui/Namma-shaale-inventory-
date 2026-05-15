package com.piyush.namma_shaaleinventory.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.piyush.namma_shaaleinventory.ui.theme.BackgroundDark
import com.piyush.namma_shaaleinventory.ui.theme.SurfaceDark
import com.piyush.namma_shaaleinventory.ui.theme.TextPrimary
import com.piyush.namma_shaaleinventory.ui.theme.TextSecondary

enum class NavItem(val title: String, val icon: ImageVector, val route: String) {
    Overview("Overview", Icons.Default.Dashboard, "dashboard"),
    Assets("Assets", Icons.Default.Inventory, "assets"),
    Students("Students", Icons.Default.People, "students"),
    BarcodeScan("Scan", Icons.Default.QrCodeScanner, "scanner"),
    Reports("Reports", Icons.Default.BarChart, "reports")
}

@Composable
fun MainLayout(
    currentRoute: String?,
    onNavigate: (String) -> Unit,
    onNotificationClick: () -> Unit,
    content: @Composable (PaddingValues) -> Unit
) {
    Scaffold(
        topBar = { MobileTopBar(onNotificationClick) },
        bottomBar = {
            NavigationBar(
                containerColor = SurfaceDark,
                contentColor = TextSecondary,
                tonalElevation = 8.dp
            ) {
                NavItem.entries.forEach { item ->
                    val isSelected = currentRoute == item.route
                    NavigationBarItem(
                        selected = isSelected,
                        onClick = { onNavigate(item.route) },
                        icon = {
                            Icon(
                                item.icon,
                                contentDescription = item.title,
                                tint = if (isSelected) Color(0xFF3B82F6) else TextSecondary
                            )
                        },
                        label = {
                            Text(
                                item.title,
                                color = if (isSelected) TextPrimary else TextSecondary,
                                fontSize = 10.sp
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            indicatorColor = Color(0xFF1E293B)
                        )
                    )
                }
            }
        },
        containerColor = BackgroundDark
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {
            content(PaddingValues(0.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MobileTopBar(onNotificationClick: () -> Unit) {
    CenterAlignedTopAppBar(
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.FlashOn,
                    contentDescription = null,
                    tint = Color(0xFF3B82F6),
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    "ShaaleSync",
                    color = TextPrimary,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        actions = {
            IconButton(onClick = onNotificationClick) {
                BadgedBox(badge = { Badge(containerColor = Color(0xFF3B82F6)) {} }) {
                    Icon(Icons.Default.Notifications, contentDescription = "Notifications", tint = TextSecondary)
                }
            }
        },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = SurfaceDark
        )
    )
}
