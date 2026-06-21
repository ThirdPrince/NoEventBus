# NoEventBus：现代 Android 响应式状态流架构实战

> **“跨页面联动不应该靠‘互相通知’，而应该靠稳定的数据源和清晰的状态管道。”**

本项目是一个基于 **Clean Architecture + Flow + Compose** 的深度实战 Demo。它通过“登录-消息-主题”的完整联动场景，展示了如何彻底弃用 EventBus、LocalBroadcast 等隐式通信手段，构建一个“状态自驱动”的现代化应用。

---

## 🏗 核心架构：逻辑下沉与状态自驱动

本项目严格遵循 **Single Source of Truth (SSOT)** 原则，通过以下链路实现数据流转：

```text
DataStore (持久化) / 内存状态
        ↓ (暴露)
Repository (Flow / suspend)
        ↓ (组合与分发)
ViewModel (StateFlow / SharedFlow)
        ↓ (订阅)
Compose UI (collectAsStateWithLifecycle)
```

### 🌟 五大核心设计准则

1. **Repository 作为状态源**：不再提供单次查询的 `isLogin()`，而是暴露持续观察的 `val loginState: Flow<LoginState>`。
2. **ViewModel 的“化学反应”**：使用 `combine` 算子处理复杂逻辑（如：仅在已登录且消息数 > 0 时显示红点）。
3. **副作用的显式收口**：一次性事件（如 Snackbar）通过 `SharedFlow` 承载，由 UI 顶层统一消费。
4. **声明式 UI 联动**：弹窗的关闭不是被“指令”控制，而是“登录成功”状态下的自然结果。
5. **动态流嫁接**：利用 `flatMapLatest` 让消息数据源自动根据当前登录用户进行动态切换。

---

## 🛠 关键实战代码

### 1. 业务逻辑下沉 (ViewModel Combine)
红点显示规则不是 UI 判断出来的，而是一个稳定的状态推导：

```kotlin
// HomeViewModel.kt
val shouldShowMessageBadge: StateFlow<Boolean> = combine(
    authRepository.loginState,
    messageRepository.count
) { login, count ->
    // 只有 已登录 且 计数 > 0 才满足红点显示条件
    login is LoginState.Login && count > 0
}.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)
```

### 2. 跨模块动态绑定 (flatMapLatest)
消息流会自动“跟踪”登录状态变化，无需任何 `LoginEvent` 通告：

```kotlin
// MessageRepositoryImpl.kt
override val count: Flow<Int> = authRepository.loginState.flatMapLatest { state ->
    if (state is LoginState.Login) {
        messageDataSource.getMessageCount(state.token) // 自动切换到该用户的数据源
    } else {
        flowOf(0) // 退出登录后流自动归零
    }
}
```

### 3. 全局通知系统 (SharedFlow)
取代 EventBus 发送全局 Toast 的混乱逻辑：

```kotlin
// UI 顶层统一消费 (NoEventBusMain.kt)
LaunchedEffect(Unit) {
    notificationVm.notifications.collectLatest { message ->
        snackbarHostState.showSnackbar(message)
    }
}
```

---

## 📝 架构对比映射表

| 场景 | ❌ 旧方案 (EventBus/广播) | ✅ 现代替代方案 |
|---|---|---|
| UI 状态更新 | `post(StateChangedEvent)` | `StateFlow` + `collect` |
| 跨页面同步 | `post(DataUpdatedEvent)` | 共享单例 `Repository` + `Flow` |
| 一次性提示 | `post(ToastEvent)` | `NotificationRepo` + `SharedFlow` |
| 粘性事件 | `StickyEvent` | **DataStore** 持久化 + `initialValue` |
| 组件联动 | 嵌套的监听回调 | `combine` / `flatMapLatest` 逻辑合并 |

---

## 🏁 总结

**不要再试图去“通知”别人，请去优化你的数据源。** 

当状态在设计的管道中自动流动时，App 的行为将变得**高度确定且可预测**。这才是构建现代化、高性能 Android 应用的终极方案。
