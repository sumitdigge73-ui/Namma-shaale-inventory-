package com.piyush.namma_shaaleinventory.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.piyush.namma_shaaleinventory.ui.theme.*
import com.piyush.namma_shaaleinventory.ui.viewmodel.InventoryViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditStudentScreen(
    studentId: Int,
    viewModel: InventoryViewModel,
    onBack: () -> Unit
) {
    val studentState by viewModel.getStudentById(studentId).collectAsState(initial = null)
    val assignedAssets by viewModel.getAssetsAssignedToStudent(studentId).collectAsState(initial = emptyList())
    val allAssets by viewModel.allAssets.collectAsState(initial = emptyList())
    
    var name by remember { mutableStateOf("") }
    var stuId by remember { mutableStateOf("") }
    var grade by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    val selectedAssetIds = remember { mutableStateListOf<Int>() }

    LaunchedEffect(studentState) {
        studentState?.let {
            name = it.name
            stuId = it.studentId
            grade = it.grade
            email = it.email
        }
    }
    
    LaunchedEffect(assignedAssets) {
        selectedAssetIds.clear()
        selectedAssetIds.addAll(assignedAssets.map { it.id })
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Edit Student", color = TextPrimary) },
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
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            OutlinedTextField(
                value = stuId,
                onValueChange = { stuId = it },
                label = { Text("Student ID") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp)
            )

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Full Name") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp)
            )

            OutlinedTextField(
                value = grade,
                onValueChange = { grade = it },
                label = { Text("Grade") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp)
            )

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email Address") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))
            
            Text("Manage Assets", color = TextPrimary, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            
            // Show currently assigned + available assets
            val assignableAssets = allAssets.filter { 
                it.status == "Available" || it.assignedToStudentId == studentId 
            }

            if (assignableAssets.isEmpty()) {
                Text("No assets available for assignment.", color = TextSecondary, fontSize = 13.sp)
            } else {
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(assignableAssets) { asset ->
                        val isSelected = selectedAssetIds.contains(asset.id)
                        FilterChip(
                            selected = isSelected,
                            onClick = {
                                if (isSelected) selectedAssetIds.remove(asset.id)
                                else selectedAssetIds.add(asset.id)
                            },
                            label = { Text(asset.name) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = AccentBlue.copy(alpha = 0.2f),
                                selectedLabelColor = AccentBlue
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = {
                    if (name.isNotBlank() && stuId.isNotBlank()) {
                        viewModel.updateStudent(studentId, stuId, name, grade, email, selectedAssetIds.toList())
                        onBack()
                    }
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = AccentBlue),
                enabled = name.isNotBlank() && stuId.isNotBlank()
            ) {
                Text("Update Details", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}
