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
    
    // 1. 处理全局通知 (仅在顶层消费)
    val notificationVm: NotificationViewModel = hiltViewModel()
    val snackbarHostState = remember { SnackbarHostState() }
    LaunchedEffect(Unit) {
        notificationVm.notifications.collectLatest { snackbarHostState.showSnackbar(it) }
    }

    // 2. 处理全局登录弹窗与状态
    val authVm: AuthViewModel = hiltViewModel()
    val loginState by authVm.loginState.collectAsStateWithLifecycle()
    var showLoginDialog by rememberSaveable { mutableStateOf(false) }
    LaunchedEffect(loginState) {
        if (loginState is LoginState.Login) showLoginDialog = false
    }

    // 3. 处理底栏 Badge (借用 HomeViewModel 提供的逻辑流)
    val homeVm: HomeViewModel = hiltViewModel()
    val showBadge by homeVm.shouldShowMessageBadge.collectAsStateWithLifecycle()
    
    Scaffold(
        topBar = { CenterAlignedTopAppBar(title = { Text(selectedScreen.label, fontWeight = FontWeight.Bold) }) },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        bottomBar = {
            NavigationBar {
                listOf(Screen.Home, Screen.Message, Screen.Mine).forEach { screen ->
                    NavigationBarItem(
                        icon = { 
                            BadgedBox(badge = { if (screen is Screen.Message && showBadge) Badge() }) {
                                Icon(screen.icon, contentDescription = null)
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
            // 核心改进：子页面不再透传 ViewModel，内部通过 hiltViewModel() 自动获取单例
            when (selectedScreen) {
                is Screen.Home -> HomeScreen(onLoginClick = { showLoginDialog = true })
                is Screen.Message -> MessageScreen(onLoginClick = { showLoginDialog = true })
                is Screen.Mine -> MineScreen(onLoginClick = { showLoginDialog = true })
            }
        }
    }

    if (showLoginDialog) {
        LoginDialog(
            state = loginState,
            onConfirm = { name -> authVm.login(name) },
            onDismiss = { showLoginDialog = false }
        )
    }
}
