package com.piyush.namma_shaaleinventory.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

// 1. Define the entities (tables) and version
// This fulfills the requirement to store both Asset and Health history.
@Database(entities = [Asset::class, HealthRecord::class, Student::class], version = 2, exportSchema = false)
abstract class SchoolInventoryDatabase : RoomDatabase() {

    // 2. Connect the DAO interface
    abstract fun assetDao(): AssetDao

    // 3. Create the Database Instance (Singleton)
    companion object {
        @Volatile
        private var INSTANCE: SchoolInventoryDatabase? = null

        fun getDatabase(context: Context): SchoolInventoryDatabase {
            // If the INSTANCE is not null, then return it,
            // if it is, then create the database
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    SchoolInventoryDatabase::class.java,
                    "school_inventory_database"
                )
                .fallbackToDestructiveMigration(true)
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}