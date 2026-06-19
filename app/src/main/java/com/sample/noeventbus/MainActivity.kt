package com.sample.noeventbus

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sample.noeventbus.domain.model.AppTheme
import com.sample.noeventbus.ui.compose.NoEventBusDemoApp
import com.sample.noeventbus.ui.theme.NoEventBusTheme
import com.sample.noeventbus.ui.viewmodel.SettingsViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val settingsVm: SettingsViewModel = hiltViewModel()
            val theme by settingsVm.theme.collectAsStateWithLifecycle()
            
            NoEventBusTheme(darkTheme = theme == AppTheme.Dark) {
                NoEventBusDemoApp()
            }
        }
    }
}
