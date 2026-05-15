package com.piyush.namma_shaaleinventory

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.viewmodel.compose.viewModel
import com.piyush.namma_shaaleinventory.data.SchoolInventoryDatabase
import com.piyush.namma_shaaleinventory.ui.InventoryApp
import com.piyush.namma_shaaleinventory.ui.theme.NammaShaaleInventoryTheme
import com.piyush.namma_shaaleinventory.ui.viewmodel.InventoryViewModel
import com.piyush.namma_shaaleinventory.ui.viewmodel.InventoryViewModelFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        val database = SchoolInventoryDatabase.getDatabase(this)
        val factory = InventoryViewModelFactory(database.assetDao())
        
        setContent {
            NammaShaaleInventoryTheme {
                val viewModel: InventoryViewModel = viewModel(factory = factory)
                
                // Seed initial data for demonstration if empty
                LaunchedEffect(Unit) {
                    viewModel.seedSampleData()
                }

                InventoryApp(viewModel = viewModel)
            }
        }
    }
}
