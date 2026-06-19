package com.sample.noeventbus.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sample.noeventbus.domain.model.AppTheme
import com.sample.noeventbus.domain.model.LoginState

@Composable
fun ModuleProfile(profileVm: ProfileViewModel = hiltViewModel()) {
    val user by profileVm.user.collectAsStateWithLifecycle()
    val isUpdating by profileVm.isUpdating.collectAsStateWithLifecycle()
    
    ElevatedCard(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("个人资料", style = MaterialTheme.typography.titleSmall)
            Text("当前名称: ${user.name}")
            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = { profileVm.updateName("User_" + (1000..9999).random()) },
                enabled = !isUpdating
            ) {
                if (isUpdating) CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                else Text("随机改名")
            }
        }
    }
}

@Composable
fun ModuleAuth(authVm: AuthViewModel = hiltViewModel()) {
    val state by authVm.loginState.collectAsStateWithLifecycle()
    
    ElevatedCard(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("账户管理", style = MaterialTheme.typography.titleSmall)
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(modifier = Modifier.weight(1f), onClick = { authVm.login() }, enabled = state is LoginState.Logout) { Text("登录") }
                Button(modifier = Modifier.weight(1f), onClick = { authVm.logout() }, enabled = state is LoginState.Login, colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)) { Text("退出登录") }
            }
        }
    }
}

@Composable
fun ModuleMessages(msgVm: MessageViewModel = hiltViewModel()) {
    ElevatedCard(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("消息模拟", style = MaterialTheme.typography.titleSmall)
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
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = androidx.compose.ui.Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
            Text("深色模式")
            Switch(checked = theme == AppTheme.Dark, onCheckedChange = { settingsVm.toggleTheme() })
        }
    }
}
