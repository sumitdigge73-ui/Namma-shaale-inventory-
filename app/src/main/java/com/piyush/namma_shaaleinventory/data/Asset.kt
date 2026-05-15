package com.piyush.namma_shaaleinventory.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "assets")
data class Asset(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val assetId: String, // e.g., "AST-001"
    val name: String,
    val serialNumber: String,
    val category: String, // e.g., "Technology", "Science Lab"
    val location: String, // e.g., "Lab 1", "Class 10A"
    val status: String, // "Available", "Assigned", "Missing"
    val assignedToStudentId: Int? = null, // FK to Student.id
    val photoUri: String? = null
)
