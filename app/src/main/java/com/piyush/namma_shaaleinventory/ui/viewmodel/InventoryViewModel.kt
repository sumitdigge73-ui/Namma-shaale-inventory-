package com.piyush.namma_shaaleinventory.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.piyush.namma_shaaleinventory.data.Asset
import com.piyush.namma_shaaleinventory.data.AssetDao
import com.piyush.namma_shaaleinventory.data.HealthRecord
import com.piyush.namma_shaaleinventory.data.Student
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class InventoryViewModel(private val assetDao: AssetDao) : ViewModel() {

    // Assets
    val allAssets: Flow<List<Asset>> = assetDao.getAllAssets()
    val totalAssetCount: Flow<Int> = assetDao.getTotalAssetCount()
    val availableItemsCount: Flow<Int> = assetDao.getAvailableItemsCount()
    val missingItemsCount: Flow<Int> = assetDao.getMissingItemsCount()
    val needsRepairCount: Flow<Int> = assetDao.getNeedsRepairCount()
    val repairRequestedAssets: Flow<List<Asset>> = assetDao.getRepairRequestedAssets()
    val availableAssets: Flow<List<Asset>> = assetDao.getAllAssets() // We'll filter in UI or here

    // Students
    val allStudents: Flow<List<Student>> = assetDao.getAllStudents()
    val activeStudentsCount: Flow<Int> = assetDao.getActiveStudentsCount()

    fun getStudentById(id: Int): Flow<Student?> = assetDao.getStudentById(id)

    fun getAssetById(id: Int): Flow<Asset?> = assetDao.getAssetById(id)

    fun getAssetBySerialNumber(serialNumber: String): Flow<Asset?> = 
        assetDao.getAssetBySerialNumber(serialNumber)

    fun getAssetsAssignedToStudent(studentId: Int): Flow<List<Asset>> =
        assetDao.getAssetsAssignedToStudent(studentId)

    // Health History
    fun getHealthHistory(assetId: Int): Flow<List<HealthRecord>> = 
        assetDao.getHealthRecordsForAsset(assetId)

    fun getLatestStatus(assetId: Int): Flow<HealthRecord?> =
        assetDao.getLatestHealthRecordForAsset(assetId)

    fun addAsset(
        name: String, 
        assetId: String, 
        serialNumber: String, 
        category: String, 
        location: String, 
        photoUri: String? = null
    ) {
        viewModelScope.launch {
            val asset = Asset(
                name = name, 
                assetId = assetId,
                serialNumber = serialNumber, 
                category = category,
                location = location,
                status = "Available",
                photoUri = photoUri
            )
            val id = assetDao.insertAsset(asset)
            // Initial health check
            assetDao.insertHealthRecord(
                HealthRecord(assetId = id.toInt(), status = "Green", note = "Initial Registration")
            )
        }
    }

    fun updateHealth(assetId: Int, status: String, note: String? = null) {
        viewModelScope.launch {
            assetDao.insertHealthRecord(
                HealthRecord(assetId = assetId, status = status, note = note)
            )
            // Sync asset status with health check
            if (status == "Missing") {
                assetDao.updateAssetStatus(assetId, "Missing")
            } else {
                // If it was missing and now it's Green/Yellow/Red, it's found!
                // We mark it as Available (unassigned) for safety so it can be re-audited
                assetDao.updateAssetStatus(assetId, "Available")
            }
        }
    }

    fun addStudent(studentId: String, name: String, grade: String, email: String, assetIdsToAssign: List<Int> = emptyList()) {
        viewModelScope.launch {
            val student = Student(studentId = studentId, name = name, grade = grade, email = email)
            val newStudentId = assetDao.insertStudent(student).toInt()
            assetIdsToAssign.forEach { assetId ->
                assetDao.assignAssetToStudent(assetId, newStudentId)
            }
        }
    }

    fun updateStudent(id: Int, studentId: String, name: String, grade: String, email: String, assetIdsToAssign: List<Int> = emptyList()) {
        viewModelScope.launch {
            val student = Student(id = id, studentId = studentId, name = name, grade = grade, email = email)
            assetDao.insertStudent(student)
            
            // For updates, we first clear then re-assign (simple approach)
            assetDao.unassignAssetsFromStudent(id)
            assetIdsToAssign.forEach { assetId ->
                assetDao.assignAssetToStudent(assetId, id)
            }
        }
    }

    fun deleteStudent(studentId: Int) {
        viewModelScope.launch {
            assetDao.unassignAssetsFromStudent(studentId)
            assetDao.deleteStudentById(studentId)
        }
    }

    fun seedSampleData() {
        viewModelScope.launch {
            // Check if already seeded
            val currentAssets = assetDao.getAllAssets().first()
            if (currentAssets.isNotEmpty()) return@launch

            // Add Students
            val s1 = assetDao.insertStudent(Student(studentId = "STU-001", name = "Arjun Sharma", grade = "10th", email = "arjun@school.edu"))
            val s2 = assetDao.insertStudent(Student(studentId = "STU-002", name = "Priya Patel", grade = "12th", email = "priya@school.edu"))
            val s3 = assetDao.insertStudent(Student(studentId = "STU-003", name = "Rahul Varma", grade = "9th", email = "rahul@school.edu"))

            // Add Assets
            assetDao.insertAsset(Asset(assetId = "AST-001", name = "Dell Latitude 5420", serialNumber = "CN-0XG123", category = "Technology", location = "Lab 1", status = "Assigned", assignedToStudentId = s1.toInt()))
            assetDao.insertAsset(Asset(assetId = "AST-002", name = "Microscope NX-45", serialNumber = "SNC-8821", category = "Science Lab", location = "Lab 3", status = "Available"))
            assetDao.insertAsset(Asset(assetId = "AST-003", name = "Ergonomic Desk Chair", serialNumber = "FUR-001", category = "Furniture", location = "Admin Office", status = "Available"))
            assetDao.insertAsset(Asset(assetId = "AST-004", name = "iPad Air 4th Gen", serialNumber = "DLX-PP12", category = "Technology", location = "Class 10A", status = "Assigned", assignedToStudentId = s2.toInt()))
            assetDao.insertAsset(Asset(assetId = "AST-005", name = "Physics Kit B", serialNumber = "PHY-990", category = "Science Lab", location = "Unknown", status = "Missing"))
        }
    }
}

class InventoryViewModelFactory(private val assetDao: AssetDao) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(InventoryViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return InventoryViewModel(assetDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
