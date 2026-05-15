package com.piyush.namma_shaaleinventory.ui

import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.piyush.namma_shaaleinventory.ui.components.MainLayout
import com.piyush.namma_shaaleinventory.ui.screens.*
import com.piyush.namma_shaaleinventory.ui.theme.TextPrimary
import com.piyush.namma_shaaleinventory.ui.viewmodel.InventoryViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@Composable
fun InventoryApp(viewModel: InventoryViewModel) {
    val navController = rememberNavController()
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    MainLayout(
        currentRoute = currentRoute,
        onNavigate = { route ->
            navController.navigate(route) {
                launchSingleTop = true
                popUpTo("dashboard") { saveState = true }
                restoreState = true
            }
        },
        onNotificationClick = {
            scope.launch {
                val missingCount = viewModel.missingItemsCount.first()
                val repairCount = viewModel.needsRepairCount.first()
                
                val message = when {
                    missingCount > 0 -> "Alert: $missingCount items are currently missing!"
                    repairCount > 0 -> "Alert: $repairCount items need repair attention."
                    else -> "All school assets are accounted for. Good job!"
                }
                Toast.makeText(context, message, Toast.LENGTH_LONG).show()
            }
        }
    ) {
        NavHost(navController = navController, startDestination = "dashboard") {
            composable("dashboard") {
                DashboardScreen(viewModel = viewModel)
            }
            composable("assets") {
                AssetInventoryScreen(
                    viewModel = viewModel,
                    onAddAssetClick = { navController.navigate("add_asset") },
                    onAssetClick = { id -> navController.navigate("asset_detail/$id") },
                    onBulkScanClick = { navController.navigate("scanner") }
                )
            }
            composable("students") {
                StudentDatabaseScreen(
                    viewModel = viewModel,
                    onAddStudentClick = { navController.navigate("add_student") },
                    onEditStudentClick = { id -> navController.navigate("edit_student/$id") }
                )
            }
            composable("add_student") {
                AddStudentScreen(
                    viewModel = viewModel,
                    onBack = { navController.popBackStack() }
                )
            }
            composable(
                route = "edit_student/{studentId}",
                arguments = listOf(navArgument("studentId") { type = NavType.IntType })
            ) { backStackEntry ->
                val studentId = backStackEntry.arguments?.getInt("studentId") ?: 0
                EditStudentScreen(
                    studentId = studentId,
                    viewModel = viewModel,
                    onBack = { navController.popBackStack() }
                )
            }
            composable("scanner") {
                var isProcessing by remember { mutableStateOf(false) }
                ScannerScreen(
                    onBarcodeDetected = { barcode ->
                        if (!isProcessing) {
                            isProcessing = true
                            scope.launch {
                                val asset = viewModel.getAssetBySerialNumber(barcode).first()
                                if (asset != null) {
                                    navController.navigate("asset_detail/${asset.id}") {
                                        popUpTo("dashboard")
                                    }
                                } else {
                                    Toast.makeText(context, "Asset not found: $barcode", Toast.LENGTH_SHORT).show()
                                    isProcessing = false
                                }
                            }
                        }
                    },
                    onBack = { navController.popBackStack() }
                )
            }
            composable("add_asset") {
                AddAssetScreen(
                    viewModel = viewModel,
                    onBack = { navController.popBackStack() }
                )
            }
            composable(
                route = "asset_detail/{assetId}",
                arguments = listOf(navArgument("assetId") { type = NavType.IntType })
            ) { backStackEntry ->
                val assetId = backStackEntry.arguments?.getInt("assetId") ?: 0
                AssetDetailScreen(
                    assetId = assetId,
                    viewModel = viewModel,
                    onBack = { navController.popBackStack() }
                )
            }
            composable("ai_assistant") {
                AIAssistantScreen()
            }
            composable("reports") {
                ReportsScreen(viewModel = viewModel)
            }
            composable("settings") {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Settings", color = TextPrimary)
                }
            }
        }
    }
}
