package com.piyush.namma_shaaleinventory.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.piyush.namma_shaaleinventory.data.Student
import com.piyush.namma_shaaleinventory.ui.theme.*
import com.piyush.namma_shaaleinventory.ui.viewmodel.InventoryViewModel

@Composable
fun StudentDatabaseScreen(
    viewModel: InventoryViewModel,
    onAddStudentClick: () -> Unit,
    onEditStudentClick: (Int) -> Unit
) {
    val students by viewModel.allStudents.collectAsState(initial = emptyList())
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
                    "Students",
                    color = TextPrimary,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    "Manage assignments",
                    color = TextSecondary,
                    fontSize = 14.sp
                )
            }
            IconButton(
                onClick = onAddStudentClick,
                modifier = Modifier.clip(RoundedCornerShape(8.dp)).background(AccentBlue)
            ) {
                Icon(Icons.Default.PersonAdd, contentDescription = "Add", tint = Color.White)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        TextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("Search students...", color = TextSecondary) },
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
            val filteredStudents = students.filter { 
                it.name.contains(searchQuery, ignoreCase = true) || 
                it.studentId.contains(searchQuery, ignoreCase = true) 
            }
            items(filteredStudents) { student ->
                MobileStudentCard(student, viewModel, onEditStudentClick)
            }
        }
    }
}

@Composable
fun MobileStudentCard(
    student: Student, 
    viewModel: InventoryViewModel,
    onEditClick: (Int) -> Unit
) {
    val assignedAssets by viewModel.getAssetsAssignedToStudent(student.id).collectAsState(initial = emptyList())
    var showMenu by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = SurfaceDark),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (student.profilePhotoUri != null) {
                    AsyncImage(
                        model = student.profilePhotoUri,
                        contentDescription = null,
                        modifier = Modifier.size(40.dp).clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Box(
                        modifier = Modifier.size(40.dp).clip(CircleShape).background(Color(0xFF1E293B)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Person, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(20.dp))
                    }
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(student.name, color = TextPrimary, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    Text(student.studentId, color = AccentBlue, fontSize = 12.sp)
                }
                Spacer(modifier = Modifier.weight(1f))
                
                Box {
                    IconButton(onClick = { showMenu = true }) {
                        Icon(Icons.Default.MoreHoriz, contentDescription = "Options", tint = TextSecondary)
                    }
                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false },
                        modifier = Modifier.background(SurfaceDark)
                    ) {
                        DropdownMenuItem(
                            text = { Text("Edit Student", color = TextPrimary) },
                            onClick = { 
                                showMenu = false
                                onEditClick(student.id)
                            },
                            leadingIcon = { Icon(Icons.Default.Edit, contentDescription = null, tint = AccentBlue) }
                        )
                        DropdownMenuItem(
                            text = { Text("Delete Student", color = ErrorRed) },
                            onClick = { 
                                showMenu = false
                                showDeleteDialog = true
                            },
                            leadingIcon = { Icon(Icons.Default.Delete, contentDescription = null, tint = ErrorRed) }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(modifier = Modifier.fillMaxWidth()) {
                Icon(Icons.Default.School, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(12.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Grade ${student.grade}", color = TextSecondary, fontSize = 12.sp)
                Spacer(modifier = Modifier.width(16.dp))
                Icon(Icons.Default.Email, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(12.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(student.email, color = TextSecondary, fontSize = 12.sp)
            }

            if (assignedAssets.isNotEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))
                Text("ASSIGNED ASSETS", color = TextSecondary, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(6.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    assignedAssets.take(3).forEach { asset ->
                        Surface(
                            color = Color(0xFF1E293B),
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text(
                                asset.assetId,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                color = AccentBlue,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                    if (assignedAssets.size > 3) {
                        Text("+${assignedAssets.size - 3}", color = TextSecondary, fontSize = 10.sp, modifier = Modifier.align(Alignment.CenterVertically))
                    }
                }
            }
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            containerColor = SurfaceDark,
            title = { Text("Delete Student?", color = TextPrimary) },
            text = { Text("This will remove the student and unassign all their assets. This action cannot be undone.", color = TextSecondary) },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteStudent(student.id)
                        showDeleteDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ErrorRed)
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancel", color = TextSecondary)
                }
            }
        )
    }
}
