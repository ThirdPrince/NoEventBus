package com.sample.noeventbus.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sample.noeventbus.domain.model.LoginState
import com.sample.noeventbus.ui.viewmodel.HomeViewModel

/**
 * 现代架构设计说明：不再使用 EventBus / 总线思想
 *
 * 1. 事件源 (Single Sources of Truth - Repositories):
 *    - AuthRepository         : 驱动全局登录/登出状态 (Flow<LoginState>)
 *    - UserRepository         : 驱动用户资料/昵称更新 (Flow<User>)
 *    - MessageRepository      : 驱动未读消息计数 (Flow<Int>)
 *    - ThemeRepository        : 驱动全局深色模式切换 (Flow<AppTheme>)
 *    - NotificationRepository : 驱动全局一次性 UI 通知 (SharedFlow<String>)
 *
 * 2. 观察者模块 (Observer Modules):
 *    - HomeScreen             : 综合观察者，反映 User, Auth, Message 的汇总状态
 *    - MessageScreen          : 垂直观察者，仅在登录状态下消费 Message 计数
 *    - NavigationBar (底栏)    : 逻辑观察者，组合 Login + Message 计算红点显隐
 *    - NoEventBusDemoApp      : 全局观察者，监听通知流弹出 Snackbar
 *    - MainActivity           : 主题观察者，实时响应深色模式切换
 *
 * 3. 状态持有者 (State Holders):
 *    - Data 层 (DataSource)    : 负责持久化状态 (DataStore)
 *    - UI 层 (ViewModels)      : 负责将原始 Flow 转换为 UI 可用的 StateFlow
 */

@Composable
fun HomeScreen(homeVm: HomeViewModel) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        StatusDashboard(homeVm)
        
        Card(
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Text(
                text = "架构演示：当您退出登录，消息红点将依据 HomeViewModel 中的 combine 逻辑自动消失。",
                modifier = Modifier.padding(16.dp),
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
fun MessageScreen(homeVm: HomeViewModel) {
    val displayCount by homeVm.displayMessageCount.collectAsStateWithLifecycle()
    
    Column(
        modifier = Modifier.fillMaxSize().padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (displayCount != null) {
            Icon(Icons.Default.Email, null, modifier = Modifier.size(100.dp), tint = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.height(16.dp))
            Text("您有 $displayCount 条未读消息", style = MaterialTheme.typography.headlineMedium)
            Spacer(modifier = Modifier.height(8.dp))
            Text("数据实时同步自 MessageRepository", color = MaterialTheme.colorScheme.secondary)
        } else {
            Icon(Icons.Default.Lock, null, modifier = Modifier.size(100.dp), tint = MaterialTheme.colorScheme.error)
            Spacer(modifier = Modifier.height(16.dp))
            Text("受限内容", style = MaterialTheme.typography.headlineMedium)
            Text("请先在『我的』页面登录后查看消息", textAlign = TextAlign.Center, color = MaterialTheme.colorScheme.secondary)
        }
    }
}

@Composable
fun MineScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Module 组件定义在 Components.kt 中
        ModuleAuth()
        ModuleProfile()
        ModuleMessages()
        ModuleSettings()
    }
}

@Composable
fun StatusDashboard(homeVm: HomeViewModel) {
    val user by homeVm.user.collectAsStateWithLifecycle()
    val loginState by homeVm.loginState.collectAsStateWithLifecycle()
    val displayCount by homeVm.displayMessageCount.collectAsStateWithLifecycle()

    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("实时状态看板", style = MaterialTheme.typography.titleMedium)
            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
            Text("👤 用户: ${user.name}")
            Text("✉️ 消息: ${displayCount ?: "未登录隐藏"}")
            Text("🔐 状态: ${if (loginState is LoginState.Login) "✅ 已登录" else "❌ 未登录"}")
        }
    }
}
