package com.sample.noeventbus.ui.compose

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sample.noeventbus.domain.model.LoginState
import com.sample.noeventbus.ui.viewmodel.AuthViewModel
import com.sample.noeventbus.ui.viewmodel.MessageViewModel
import com.sample.noeventbus.ui.viewmodel.ProfileViewModel
import com.sample.noeventbus.ui.viewmodel.SettingsViewModel

@Composable
fun ModuleProfile(profileVm: ProfileViewModel = hiltViewModel()) {
    val user by profileVm.user.collectAsStateWithLifecycle()
    val isUpdating by profileVm.isUpdating.collectAsStateWithLifecycle()
    val loginState by profileVm.loginState.collectAsStateWithLifecycle()
    
    val isLoggedIn = loginState is LoginState.Login
    
    ElevatedCard(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("个人资料", style = MaterialTheme.typography.titleSmall)
            Text("当前名称: ${user.name}")
            Spacer(modifier = Modifier.height(8.dp))
            
            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = { profileVm.updateNickname("User_" + (1000..9999).random()) },
                enabled = isLoggedIn && !isUpdating
            ) {
                if (isUpdating) {
                    CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                } else {
                    val label = if (isLoggedIn) "修改nickname (异步+持久化)" else "请先登录后修改"
                    Text(label)
                }
            }
        }
    }
}

@Composable
fun ModuleAuth(
    authVm: AuthViewModel = hiltViewModel(),
    onLoginClick: () -> Unit
) {
    val state by authVm.loginState.collectAsStateWithLifecycle()
    
    ElevatedCard(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("账户管理", style = MaterialTheme.typography.titleSmall)
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                if (state is LoginState.Logout) {
                    Button(
                        modifier = Modifier.weight(1f), 
                        onClick = onLoginClick
                    ) { 
                        Text("立即登录") 
                    }
                } else {
                    Button(
                        modifier = Modifier.weight(1f), 
                        onClick = { authVm.logout() }, 
                        enabled = state is LoginState.Login, 
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                    ) { 
                        Text("退出登录") 
                    }
                }
            }
        }
    }
}

@Composable
fun LoginDialog(
    state: LoginState,
    onConfirm: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var name by remember { mutableStateOf("") }
    val isLoading = state is LoginState.Loading

    AlertDialog(
        onDismissRequest = { if (!isLoading) onDismiss() },
        title = { Text("欢迎回来") },
        text = {
            Column {
                Text("请输入您的昵称：")
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("昵称") },
                    singleLine = true,
                    enabled = !isLoading,
                    modifier = Modifier.fillMaxWidth()
                )
                if (isLoading) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        CircularProgressIndicator(modifier = Modifier.size(24.dp))
                        Spacer(modifier = Modifier.width(12.dp))
                        Text("模拟异步验证中...")
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(name) },
                enabled = name.isNotBlank() && !isLoading
            ) {
                Text("登录")
            }
        },
        dismissButton = {
            if (!isLoading) {
                TextButton(onClick = onDismiss) { Text("取消") }
            }
        }
    )
}

@Composable
fun ModuleMessages(msgVm: MessageViewModel = hiltViewModel()) {
    ElevatedCard(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("消息模拟器 (EventBus 传统痛点)", style = MaterialTheme.typography.titleSmall)
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(modifier = Modifier.weight(1f), onClick = { msgVm.addMessage() }) { Text("产生消息") }
                OutlinedButton(modifier = Modifier.weight(1f), onClick = { msgVm.clearMessages() }) { Text("清空") }
            }
        }
    }
}

@Composable
fun ModuleSettings(settingsVm: SettingsViewModel = hiltViewModel()) {
    val theme by settingsVm.theme.collectAsStateWithLifecycle()
    ElevatedCard(modifier = Modifier.fillMaxWidth()) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
            Text("深色模式")
            Switch(checked = theme == com.sample.noeventbus.domain.model.AppTheme.Dark, onCheckedChange = { settingsVm.toggleTheme() })
        }
    }
}
