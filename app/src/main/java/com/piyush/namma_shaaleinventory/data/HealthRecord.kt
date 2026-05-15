package com.piyush.namma_shaaleinventory.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "health_records",
    foreignKeys = [
        ForeignKey(
            entity = Asset::class,
            parentColumns = ["id"],
            childColumns = ["assetId"],
            onDelete = ForeignKey.CASCADE // If asset is deleted, delete its history
        )
    ]
)
data class HealthRecord(
    @PrimaryKey(autoGenerate = true) val recordId: Int = 0,
    val assetId: Int,
    val status: String, // "Green", "Yellow", or "Red"
    val timestamp: Long = System.currentTimeMillis(),
    val note: String? = null // For things like "Football lost during match"
)