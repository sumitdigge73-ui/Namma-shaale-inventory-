package com.piyush.namma_shaaleinventory.ui.screens

import android.content.Intent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.piyush.namma_shaaleinventory.ui.theme.*
import com.piyush.namma_shaaleinventory.ui.viewmodel.InventoryViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun DashboardScreen(
    viewModel: InventoryViewModel,
    modifier: Modifier = Modifier
) {
    val totalAssets by viewModel.totalAssetCount.collectAsState(initial = 0)
    val activeStudents by viewModel.activeStudentsCount.collectAsState(initial = 0)
    val availableItems by viewModel.availableItemsCount.collectAsState(initial = 0)
    val missingItems by viewModel.missingItemsCount.collectAsState(initial = 0)
    val context = LocalContext.current

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Column {
                Text(
                    "Dashboard Overview",
                    color = TextPrimary,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    "Real-time inventory insights.",
                    color = TextSecondary,
                    fontSize = 14.sp
                )
            }
        }

        item {
            // Mobile optimized stats grid
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    StatCard(
                        title = "Total Assets",
                        value = totalAssets.toString(),
                        icon = Icons.Default.Inventory2,
                        modifier = Modifier.weight(1f)
                    )
                    StatCard(
                        title = "Active Students",
                        value = activeStudents.toString(),
                        icon = Icons.Default.Groups,
                        modifier = Modifier.weight(1f)
                    )
                }
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    StatCard(
                        title = "Available",
                        value = availableItems.toString(),
                        icon = Icons.Default.CheckCircle,
                        iconColor = SuccessGreen,
                        modifier = Modifier.weight(1f)
                    )
                    StatCard(
                        title = "Missing/Lost",
                        value = missingItems.toString(),
                        icon = Icons.Default.Error,
                        iconColor = ErrorRed,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        item {
            AssetUsageTrendsChart(modifier = Modifier.fillMaxWidth().height(250.dp))
        }

        item {
            DistributionByCategoryChart(modifier = Modifier.fillMaxWidth())
        }

        item {
            Button(
                onClick = {
                    val reportText = """
                        ShaaleSync Inventory Report
                        ---------------------------
                        Total Assets: $totalAssets
                        Active Students: $activeStudents
                        Available Items: $availableItems
                        Missing/Lost: $missingItems
                        
                        Generated on: ${SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault()).format(Date())}
                    """.trimIndent()

                    val intent = Intent(Intent.ACTION_SEND).apply {
                        type = "text/plain"
                        putExtra(Intent.EXTRA_SUBJECT, "Inventory Summary")
                        putExtra(Intent.EXTRA_TEXT, reportText)
                    }
                    context.startActivity(Intent.createChooser(intent, "Export Report"))
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3B82F6))
            ) {
                Icon(Icons.Default.Upload, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Export Report", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun StatCard(
    title: String,
    value: String,
    icon: ImageVector,
    iconColor: Color = Color(0xFF3B82F6),
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = SurfaceDark),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(title, color = TextSecondary, fontSize = 12.sp)
                Icon(icon, contentDescription = null, tint = iconColor, modifier = Modifier.size(18.dp))
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(value, color = TextPrimary, fontSize = 22.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun AssetUsageTrendsChart(modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = SurfaceDark),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Usage Trends", color = TextPrimary, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.Bottom
            ) {
                val data = listOf(65, 80, 45, 95, 90, 30, 60)
                val days = listOf("M", "T", "W", "T", "F", "S", "S")
                
                data.forEachIndexed { index, value ->
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(
                            modifier = Modifier
                                .width(24.dp)
                                .fillMaxHeight(value / 100f)
                                .clip(RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
                                .background(if (index == 3) Color(0xFF3B82F6) else Color(0xFF1E293B))
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(days[index], color = TextSecondary, fontSize = 10.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun DistributionByCategoryChart(modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = SurfaceDark),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Category Breakdown", color = TextPrimary, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.size(120.dp), contentAlignment = Alignment.Center) {
                    Canvas(modifier = Modifier.size(100.dp)) {
                        drawArc(Color(0xFF3B82F6), 0f, 120f, false, style = Stroke(width = 30f))
                        drawArc(Color(0xFF2DD4BF), 120f, 80f, false, style = Stroke(width = 30f))
                        drawArc(Color(0xFF818CF8), 200f, 60f, false, style = Stroke(width = 30f))
                        drawArc(Color(0xFFC084FC), 260f, 50f, false, style = Stroke(width = 30f))
                        drawArc(Color(0xFFF472B6), 310f, 50f, false, style = Stroke(width = 30f))
                    }
                }
                
                Spacer(modifier = Modifier.width(24.dp))
                
                val categories = listOf(
                    "Tech" to Color(0xFF3B82F6),
                    "Science" to Color(0xFF2DD4BF),
                    "Books" to Color(0xFF818CF8),
                    "Furniture" to Color(0xFFC084FC),
                    "Art" to Color(0xFFF472B6)
                )
                
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    categories.forEach { (name, color) ->
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(color))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(name, color = TextSecondary, fontSize = 12.sp)
                        }
                    }
                }
            }
        }
    }
}
