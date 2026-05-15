package com.piyush.namma_shaaleinventory.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.piyush.namma_shaaleinventory.data.Asset
import com.piyush.namma_shaaleinventory.ui.theme.*
import com.piyush.namma_shaaleinventory.ui.viewmodel.InventoryViewModel

@Composable
fun AssetInventoryScreen(
    viewModel: InventoryViewModel,
    onAddAssetClick: () -> Unit,
    onAssetClick: (Int) -> Unit,
    onBulkScanClick: () -> Unit
) {
    val assets by viewModel.allAssets.collectAsState(initial = emptyList())
    var searchQuery by remember { mutableStateOf("") }

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
                Text(
                    "Asset Inventory",
                    color = TextPrimary,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    "Manage equipment",
                    color = TextSecondary,
                    fontSize = 14.sp
                )
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                IconButton(
                    onClick = onBulkScanClick,
                    modifier = Modifier.clip(RoundedCornerShape(8.dp)).background(SurfaceDark)
                ) {
                    Icon(Icons.Default.QrCodeScanner, contentDescription = "Scan", tint = TextPrimary)
                }
                IconButton(
                    onClick = onAddAssetClick,
                    modifier = Modifier.clip(RoundedCornerShape(8.dp)).background(AccentBlue)
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add", tint = Color.White)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        TextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("Search assets...", color = TextSecondary) },
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp)),
            colors = TextFieldDefaults.colors(
                unfocusedContainerColor = SurfaceDark,
                focusedContainerColor = SurfaceDark,
                unfocusedIndicatorColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent,
                focusedTextColor = TextPrimary,
                unfocusedTextColor = TextPrimary
            ),
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = TextSecondary) }
        )

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(bottom = 16.dp)
        ) {
            val filteredAssets = assets.filter { 
                it.name.contains(searchQuery, ignoreCase = true) || 
                it.assetId.contains(searchQuery, ignoreCase = true) 
            }
            items(filteredAssets) { asset ->
                MobileAssetCard(asset, onAssetClick)
            }
        }
    }
}

@Composable
fun MobileAssetCard(asset: Asset, onClick: (Int) -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = { onClick(asset.id) },
        colors = CardDefaults.cardColors(containerColor = SurfaceDark),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFF1E293B)),
                contentAlignment = Alignment.Center
            ) {
                val icon = when(asset.category) {
                    "Technology" -> Icons.Default.Laptop
                    "Science Lab" -> Icons.Default.Science
                    "Furniture" -> Icons.Default.Chair
                    else -> Icons.Default.Inventory
                }
                Icon(icon, contentDescription = null, tint = AccentBlue)
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(asset.name, color = TextPrimary, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                Text(asset.assetId, color = TextSecondary, fontSize = 12.sp)
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.LocationOn, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(10.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(asset.location, color = TextSecondary, fontSize = 12.sp)
                }
            }

            val statusColor = when (asset.status) {
                "Available" -> SuccessGreen
                "Assigned" -> AccentBlue
                "Missing" -> ErrorRed
                else -> TextSecondary
            }
            
            Surface(
                color = statusColor.copy(alpha = 0.1f),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, statusColor.copy(alpha = 0.5f))
            ) {
                Text(
                    asset.status,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                    color = statusColor,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}
