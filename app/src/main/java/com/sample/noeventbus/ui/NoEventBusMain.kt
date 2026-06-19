package com.sample.noeventbus.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sample.noeventbus.domain.model.LoginState

sealed class Screen(val route: String, val label: String, val icon: ImageVector) {
    object Home : Screen("home", "首页", Icons.Default.Home)
    object Message : Screen("message", "消息", Icons.Default.Email)
    object Mine : Screen("mine", "我的", Icons.Default.Person)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoEventBusDemoApp() {
    var selectedScreen by remember { mutableStateOf<Screen>(Screen.Home) }
    val homeVm: HomeViewModel = hiltViewModel()
    
    // 观察合并后的红点状态
    val showBadge by homeVm.shouldShowMessageBadge.collectAsStateWithLifecycle()
    
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(selectedScreen.label, fontWeight = FontWeight.Bold) }
            )
        },
        bottomBar = {
            NavigationBar {
                val items = listOf(Screen.Home, Screen.Message, Screen.Mine)
                items.forEach { screen ->
                    NavigationBarItem(
                        icon = { 
                            BadgedBox(badge = {
                                // 只有在此状态为 true 时才显示红点
                                if (screen is Screen.Message && showBadge) {
                                    Badge() 
                                }
                            }) {
                                Icon(screen.icon, contentDescription = screen.label)
                            }
                        },
                        label = { Text(screen.label) },
                        selected = selectedScreen == screen,
                        onClick = { selectedScreen = screen }
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            when (selectedScreen) {
                is Screen.Home -> HomeScreen(homeVm)
                is Screen.Message -> MessageScreen(homeVm)
                is Screen.Mine -> MineScreen()
            }
        }
    }
}
