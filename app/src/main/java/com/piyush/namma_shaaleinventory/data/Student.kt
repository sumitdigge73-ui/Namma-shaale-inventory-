package com.piyush.namma_shaaleinventory.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "students")
data class Student(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val studentId: String, // e.g., "STU-001"
    val name: String,
    val grade: String,
    val email: String,
    val profilePhotoUri: String? = null
)
