package com.piyush.namma_shaaleinventory.ui.screens

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Download
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.piyush.namma_shaaleinventory.ui.theme.*
import com.piyush.namma_shaaleinventory.ui.viewmodel.InventoryViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun ReportsScreen(viewModel: InventoryViewModel) {
    val context = LocalContext.current
    val totalAssets by viewModel.totalAssetCount.collectAsState(initial = 0)
    val availableItems by viewModel.availableItemsCount.collectAsState(initial = 0)
    val missingItems by viewModel.missingItemsCount.collectAsState(initial = 0)
    val needsRepair by viewModel.needsRepairCount.collectAsState(initial = 0)

    val healthPercentage = if (totalAssets > 0) {
        ((totalAssets - needsRepair - missingItems).toFloat() / totalAssets * 100).toInt()
    } else 100

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text("Reports", color = TextPrimary, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                Text("Asset health analysis.", color = TextSecondary, fontSize = 14.sp)
            }
            IconButton(
                onClick = {
                    val reportText = """
                        ShaaleSync Detailed Report
                        --------------------------
                        Overall Health: $healthPercentage%
                        Available Items: $availableItems
                        Missing/Lost: $missingItems
                        Repair Requests: $needsRepair
                        Total Inventory: $totalAssets
                        
                        Generated on: ${SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault()).format(Date())}
                    """.trimIndent()

                    val intent = Intent(Intent.ACTION_SEND).apply {
                        type = "text/plain"
                        putExtra(Intent.EXTRA_SUBJECT, "ShaaleSync Detailed Inventory Report")
                        putExtra(Intent.EXTRA_TEXT, reportText)
                    }
                    context.startActivity(Intent.createChooser(intent, "Download Report"))
                },
                modifier = Modifier.size(48.dp)
            ) {
                Icon(Icons.Default.Download, contentDescription = "Download", tint = AccentBlue)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            item {
                MobileReportCard("Overall Health", "$healthPercentage% healthy items", SuccessGreen)
            }
            item {
                MobileReportCard("Available Stock", "$availableItems items ready", AccentBlue)
            }
            item {
                MobileReportCard("Missing Log", "$missingItems items lost", ErrorRed)
            }
            item {
                MobileReportCard("Repair Request", "$needsRepair items need attention", WarningYellow)
            }
        }
    }
}

@Composable
fun MobileReportCard(title: String, value: String, color: Color) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = SurfaceDark),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier.padding(20.dp).fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(title, color = TextSecondary, fontSize = 12.sp)
                Spacer(modifier = Modifier.height(4.dp))
                Text(value, color = TextPrimary, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }
            Box(
                modifier = Modifier.size(8.dp).background(color, RoundedCornerShape(4.dp))
            )
        }
    }
}
