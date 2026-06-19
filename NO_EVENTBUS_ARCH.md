# 深度解耦：从“事件总线”到“自驱动状态流”的彻底革命

> 为什么现代 Android 开发不再需要 EventBus？本项目通过一个完整的“登录-消息-资料”联动案例，展示了基于 Flow + Repository + DataStore 的终极替代方案。

---

## 一、 核心理念：从“通知”到“观察”

传统的 **EventBus** 是命令式的：
1. **发送方**：`post(LoginEvent)` —— “我登录了，你们快去刷新！”
2. **接收方**：各组件手动监听并刷新。

**现代自驱动架构** 是声明式的：
1. **Repository**：作为“水源”，维护一个 `Flow<State>`。
2. **UI 组件**：作为“水龙头”，直接盯着“水源”。**只要水变了，看到的人自然会变。**

---

## 二、 架构驱动地图：谁在驱动？谁在观察？

在本项目中，我们通过 Hilt 建立了清晰的显式依赖契约：

| 业务维度 | 事件源 (Repository) | 核心驱动状态 (Flow) | 观察者 (Observers) |
| :--- | :--- | :--- | :--- |
| **身份认证** | `AuthRepository` | `Flow<LoginState>` | 全局登录弹窗、权限拦截、消息流绑定 |
| **用户资料** | `UserRepository` | `Flow<User>` | 个人中心、首页看板 |
| **消息系统** | `MessageRepository` | `Flow<Int>` | 底部 Badge 红点、消息详情页 |
| **系统提示** | `NotificationRepo` | `SharedFlow<String>` | 全局 Snackbar 通知 (替代 Toast 总线) |

---

## 三、 进阶黑科技：如何干掉总线？

### 1. 动态流嫁接：消息自动跟踪用户
这是终结“登录后发消息通知”最优雅的方式。利用 `flatMapLatest`，让消息流根据登录状态自动切换“管道”。

```kotlin
// MessageRepositoryImpl.kt
override val count: Flow<Int> = authRepository.loginState.flatMapLatest { state ->
    if (state is LoginState.Login) {
        // 已登录：自动流向该用户的持久化 DataStore
        messageDataSource.getMessageCount(state.token)
    } else {
        // 未登录：流向常数 0，红点和详情自动消失
        flowOf(0)
    }
}
```

### 2. 状态逻辑组合：消灭 UI 层的 if-else
不要在 UI 里判断显示红点的条件。在 ViewModel 中利用 `combine` 算子完成“状态化学反应”。

```kotlin
// HomeViewModel.kt
val shouldShowMessageBadge: StateFlow<Boolean> = combine(
    authRepository.loginState,
    messageRepository.count
) { login, count ->
    // 逻辑：已登录 且 计数 > 0 才显示红点
    login is LoginState.Login && count > 0
}.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)
```

### 3. 持久化驱动：彻底解决“粘性事件”
EventBus 的 Sticky Event 极不稳定。我们直接使用 **DataStore**。
- **改名**：写入磁盘 -> Flow 发射 -> 全局同步。
- **断电恢复**：App 重启瞬间，`initialValue` 自动加载磁盘快照，逻辑 100% 闭环。

### 4. 响应式副作用：弹窗自动关闭
对话框显示是 UI 状态（`remember`），它的消失是“业务状态达到目标”的结果。

```kotlin
// Components.kt
var showLoginDialog by rememberSaveable { mutableStateOf(false) }

// 响应式联动：一旦 loginState 变为 Login，UI 自动感知并关闭弹窗
LaunchedEffect(loginState) {
    if (loginState is LoginState.Login) {
        showLoginDialog = false
    }
}
```

---

## 四、 为什么“总线思想”不该有？

1. **显式胜过隐式**：依赖关系在构造函数中一眼可见，重构时 IDE 会告诉你哪里会坏。
2. **生命周期安全**：使用 `collectAsStateWithLifecycle`，App 进入后台自动停止收集，极致省电。
3. **确定性**：逻辑沉淀在声明式的管道中，你可以轻松编写 Unit Test 验证 `combine` 逻辑，而无需运行整个 Event 系统。

**总结：不要试图去“通知”别人，请优化你的“数据源”。让状态在设计的管道中流动，是 Android 架构的终极优雅。**
