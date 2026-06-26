package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.example.data.AppDatabase
import com.example.data.HisbaRepository
import com.example.ui.HisbaViewModel
import com.example.ui.components.StarryBackground
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.SplashScreen
import com.example.ui.theme.HisbaTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val context = LocalContext.current
            val database = AppDatabase.getDatabase(context)
            val repository = HisbaRepository(database.hisbaDao())
            
            val viewModel: HisbaViewModel by viewModels { HisbaViewModel.Factory(repository) }

            var showSplash by remember { mutableStateOf(true) }

            HisbaTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Box(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
                        StarryBackground()
                        if (showSplash) {
                            SplashScreen(onTimeout = { showSplash = false })
                        } else {
                            DashboardScreen(viewModel)
                        }
                    }
                }
            }
        }
    }
}
