package com.sample.noeventbus.ui.compose

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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sample.noeventbus.domain.model.LoginState
import com.sample.noeventbus.ui.viewmodel.HomeViewModel
import com.sample.noeventbus.ui.viewmodel.MessageViewModel

@Composable
fun HomeScreen(
    onLoginClick: () -> Unit,
    homeVm: HomeViewModel = hiltViewModel() // 内部注入，不再从顶层透传
) {
    val loginState by homeVm.loginState.collectAsStateWithLifecycle()
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        StatusDashboard(homeVm)
        
        if (loginState is LoginState.Logout) {
            Button(onClick = onLoginClick, modifier = Modifier.fillMaxWidth()) {
                Text("立即登录体验完整功能")
            }
        }
        
        Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
            Text(
                text = "架构说明：每个页面现在都拥有独立的 ViewModel。状态同步依然通过底层的 Repository 实现，彻底解耦。",
                modifier = Modifier.padding(16.dp),
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
fun MessageScreen(
    onLoginClick: () -> Unit,
    msgVm: MessageViewModel = hiltViewModel()
) {
    val displayCount by msgVm.displayMessageCount.collectAsStateWithLifecycle()
    
    Column(
        modifier = Modifier.fillMaxSize().padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (displayCount != null) {
            Icon(Icons.Default.Email, null, modifier = Modifier.size(100.dp), tint = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.height(16.dp))
            Text("您有 $displayCount 条未读消息", style = MaterialTheme.typography.headlineMedium)
            Text("数据已与当前用户账号绑定", color = MaterialTheme.colorScheme.secondary)
        } else {
            Icon(Icons.Default.Lock, null, modifier = Modifier.size(100.dp), tint = MaterialTheme.colorScheme.error)
            Spacer(modifier = Modifier.height(16.dp))
            Text("受限内容", style = MaterialTheme.typography.headlineMedium)
            Text("请先登录后查看您的个人消息", textAlign = TextAlign.Center, color = MaterialTheme.colorScheme.secondary)
            Spacer(modifier = Modifier.height(24.dp))
            Button(onClick = onLoginClick) { Text("去登录") }
        }
    }
}

@Composable
fun MineScreen(onLoginClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        ModuleAuth(onLoginClick = onLoginClick)
        ModuleProfile()
        ModuleMessages()
        ModuleSettings()
    }
}

@Composable
fun StatusDashboard(homeVm: HomeViewModel = hiltViewModel()) {
    val user by homeVm.user.collectAsStateWithLifecycle()
    val loginState by homeVm.loginState.collectAsStateWithLifecycle()
    val displayCount by homeVm.displayMessageCount.collectAsStateWithLifecycle()

    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("实时状态看板", style = MaterialTheme.typography.titleMedium)
            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
            Text("👤 用户: ${user.name}")
            Text("✉️ 消息: ${displayCount ?: "隐藏 (未登录)"}")
            Text("🔐 状态: ${if (loginState is LoginState.Login) "✅ 已登录" else "❌ 未登录"}")
        }
    }
}
