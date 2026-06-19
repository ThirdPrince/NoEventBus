package com.sample.noeventbus.ui.compose

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sample.noeventbus.domain.model.LoginState
import com.sample.noeventbus.ui.viewmodel.AuthViewModel
import com.sample.noeventbus.ui.viewmodel.HomeViewModel
import com.sample.noeventbus.ui.viewmodel.NotificationViewModel
import kotlinx.coroutines.flow.collectLatest

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
    val authVm: AuthViewModel = hiltViewModel()
    val notificationVm: NotificationViewModel = hiltViewModel()
    
    val snackbarHostState = remember { SnackbarHostState() }
    val loginState by authVm.loginState.collectAsStateWithLifecycle()
    
    // 全局登录弹窗状态
    var showLoginDialog by rememberSaveable { mutableStateOf(false) }

    // 1. 监听通知流
    LaunchedEffect(Unit) {
        notificationVm.notifications.collectLatest { message ->
            snackbarHostState.showSnackbar(message)
        }
    }

    // 2. 响应式联动：登录成功自动关闭全局弹窗
    LaunchedEffect(loginState) {
        if (loginState is LoginState.Login) {
            showLoginDialog = false
        }
    }
    
    val showBadge by homeVm.shouldShowMessageBadge.collectAsStateWithLifecycle()
    
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(selectedScreen.label, fontWeight = FontWeight.Bold) }
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        bottomBar = {
            NavigationBar {
                val items = listOf(Screen.Home, Screen.Message, Screen.Mine)
                items.forEach { screen ->
                    NavigationBarItem(
                        icon = { 
                            BadgedBox(badge = {
                                if (screen is Screen.Message && showBadge) Badge() 
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
                is Screen.Home -> HomeScreen(
                    homeVm = homeVm, 
                    onLoginClick = { showLoginDialog = true }
                )
                is Screen.Message -> MessageScreen(
                    onLoginClick = { showLoginDialog = true }
                )
                is Screen.Mine -> MineScreen(
                    onLoginClick = { showLoginDialog = true }
                )
            }
        }
    }

    // 渲染全局登录弹窗
    if (showLoginDialog) {
        LoginDialog(
            state = loginState,
            onConfirm = { name -> authVm.login(name) },
            onDismiss = { showLoginDialog = false }
        )
    }
}
