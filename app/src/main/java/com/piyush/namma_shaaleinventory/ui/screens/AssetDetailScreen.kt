package com.piyush.namma_shaaleinventory.ui.screens

import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.piyush.namma_shaaleinventory.data.HealthRecord
import com.piyush.namma_shaaleinventory.ui.components.StatusBadge
import com.piyush.namma_shaaleinventory.ui.theme.*
import com.piyush.namma_shaaleinventory.ui.viewmodel.InventoryViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AssetDetailScreen(
    assetId: Int,
    viewModel: InventoryViewModel,
    onBack: () -> Unit
) {
    val asset by viewModel.getAssetById(assetId).collectAsState(initial = null)
    val history by viewModel.getHealthHistory(assetId).collectAsState(initial = emptyList())
    val latestRecord = history.firstOrNull()

    var showUpdateDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(asset?.name ?: "Asset Detail", color = TextPrimary) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = TextPrimary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BackgroundDark)
            )
        },
        containerColor = BackgroundDark
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(24.dp)
        ) {
            asset?.let {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        if (it.photoUri != null) {
                            AsyncImage(
                                model = it.photoUri,
                                contentDescription = "Asset Photo",
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(240.dp)
                                    .clip(RoundedCornerShape(8.dp)),
                                contentScale = ContentScale.Crop
                            )
                            Spacer(modifier = Modifier.height(20.dp))
                        }
                        
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Column {
                                Text(text = it.name, style = MaterialTheme.typography.headlineSmall, color = TextPrimary, fontWeight = FontWeight.Bold)
                                Text(text = "ID: ${it.assetId}", color = AccentBlue, fontSize = 14.sp)
                            }
                            StatusBadge(latestRecord?.status ?: "Unknown")
                        }
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        Row(modifier = Modifier.fillMaxWidth()) {
                            DetailItem("Serial Number", it.serialNumber, Modifier.weight(1f))
                            DetailItem("Location", it.location, Modifier.weight(1f))
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        DetailItem("Category", it.category)
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "Health Check History", color = TextPrimary, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    Button(
                        onClick = { showUpdateDialog = true },
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = AccentBlue)
                    ) {
                        Text("Update Condition")
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(history) { record ->
                        HistoryItem(record)
                    }
                }
            }
        }

        if (showUpdateDialog) {
            HealthUpdateDialog(
                onDismiss = { showUpdateDialog = false },
                onConfirm = { status, note ->
                    viewModel.updateHealth(assetId, status, note)
                    showUpdateDialog = false
                }
            )
        }
    }
}

@Composable
fun DetailItem(label: String, value: String, modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        Text(label, color = TextSecondary, fontSize = 12.sp)
        Text(value, color = TextPrimary, fontSize = 15.sp, fontWeight = FontWeight.Medium)
    }
}

@Composable
fun HistoryItem(record: HealthRecord) {
    val sdf = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault())
    Card(
        modifier = Modifier.fillMaxWidth(), 
        colors = CardDefaults.cardColors(containerColor = SurfaceDark),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                StatusBadge(record.status)
                Text(text = sdf.format(Date(record.timestamp)), color = TextSecondary, fontSize = 12.sp)
            }
            record.note?.let {
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = it, color = TextPrimary, fontSize = 14.sp)
            }
        }
    }
}

@Composable
fun HealthUpdateDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, String) -> Unit
) {
    var selectedStatus by remember { mutableStateOf("Green") }
    var note by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = SurfaceDark,
        titleContentColor = TextPrimary,
        textContentColor = TextSecondary,
        title = { Text("Update Condition", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Text("Select Status:")
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    val statusList = listOf("Green", "Yellow", "Red", "Missing")
                    statusList.forEach { status ->
                        val color = when(status) {
                            "Green" -> SuccessGreen
                            "Yellow" -> WarningYellow
                            "Red" -> ErrorRed
                            "Missing" -> Color(0xFFEF4444)
                            else -> TextSecondary
                        }
                        
                        val isSelected = selectedStatus == status
                        
                        Surface(
                            onClick = { selectedStatus = status },
                            modifier = Modifier.weight(1f).height(36.dp),
                            shape = RoundedCornerShape(8.dp),
                            color = if (isSelected) color.copy(alpha = 0.2f) else Color(0xFF1E293B),
                            border = BorderStroke(1.dp, if (isSelected) color else Color(0xFF334155))
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = status,
                                    color = if (isSelected) color else TextSecondary,
                                    fontSize = 11.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    maxLines = 1
                                )
                            }
                        }
                    }
                }
                OutlinedTextField(
                    value = note,
                    onValueChange = { note = it },
                    label = { Text("Note (Reason/Issue)") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(selectedStatus, note) },
                colors = ButtonDefaults.buttonColors(containerColor = AccentBlue)
            ) {
                Text("Update")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = TextSecondary)
            }
        }
    )
}
