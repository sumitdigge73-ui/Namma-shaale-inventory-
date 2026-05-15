package com.piyush.namma_shaaleinventory.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface AssetDao {
    // Assets
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAsset(asset: Asset): Long

    @Query("SELECT * FROM assets")
    fun getAllAssets(): Flow<List<Asset>>

    @Query("SELECT * FROM assets WHERE id = :id")
    fun getAssetById(id: Int): Flow<Asset?>

    @Query("SELECT * FROM assets WHERE serialNumber = :serialNumber LIMIT 1")
    fun getAssetBySerialNumber(serialNumber: String): Flow<Asset?>

    @Query("SELECT COUNT(*) FROM assets")
    fun getTotalAssetCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM assets WHERE status = 'Available'")
    fun getAvailableItemsCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM assets WHERE status = 'Missing'")
    fun getMissingItemsCount(): Flow<Int>

    // Students
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStudent(student: Student): Long

    @Query("SELECT * FROM students")
    fun getAllStudents(): Flow<List<Student>>

    @Query("SELECT * FROM students WHERE id = :id")
    fun getStudentById(id: Int): Flow<Student?>

    @Query("SELECT COUNT(*) FROM students")
    fun getActiveStudentsCount(): Flow<Int>

    @Query("SELECT * FROM assets WHERE assignedToStudentId = :studentId")
    fun getAssetsAssignedToStudent(studentId: Int): Flow<List<Asset>>

    @Query("DELETE FROM students WHERE id = :id")
    suspend fun deleteStudentById(id: Int)

    @Query("UPDATE assets SET assignedToStudentId = NULL, status = 'Available' WHERE assignedToStudentId = :studentId")
    suspend fun unassignAssetsFromStudent(studentId: Int)

    @Query("UPDATE assets SET status = :status WHERE id = :assetId")
    suspend fun updateAssetStatus(assetId: Int, status: String)

    @Query("UPDATE assets SET assignedToStudentId = :studentId, status = 'Assigned' WHERE id = :assetId")
    suspend fun assignAssetToStudent(assetId: Int, studentId: Int)

    // Health Records (History)
    @Insert
    suspend fun insertHealthRecord(record: HealthRecord)

    @Query("SELECT * FROM health_records WHERE assetId = :assetId ORDER BY timestamp DESC")
    fun getHealthRecordsForAsset(assetId: Int): Flow<List<HealthRecord>>

    @Query("SELECT * FROM health_records WHERE assetId = :assetId ORDER BY timestamp DESC LIMIT 1")
    fun getLatestHealthRecordForAsset(assetId: Int): Flow<HealthRecord?>

    @Query("""
        SELECT COUNT(*) FROM assets 
        INNER JOIN health_records ON assets.id = health_records.assetId 
        WHERE health_records.recordId IN (SELECT MAX(recordId) FROM health_records GROUP BY assetId) 
        AND health_records.status = 'Red'
    """)
    fun getNeedsRepairCount(): Flow<Int>

    @Query("""
        SELECT * FROM assets 
        INNER JOIN health_records ON assets.id = health_records.assetId 
        WHERE health_records.recordId IN (SELECT MAX(recordId) FROM health_records GROUP BY assetId) 
        AND health_records.status = 'Red'
    """)
    fun getRepairRequestedAssets(): Flow<List<Asset>>
}
