# base-arch — 架构核心模块

## 模块概述

`base-arch` 是项目的架构核心模块，提供完整的 MVVM 和 MVP 架构实现。它聚合了 `base-api`、`base-ui`、`base-rv-adapter`、`utils`、`navigation` 等模块，向上提供统一的基类和 ViewModel 体系。

## Maven 坐标

| 属性 | 值 |
|------|-----|
| **GroupId** | `io.github.heart-beats.baseproject` |
| **ArtifactId** | `base-arch` |
| **当前版本** | `0.0.4-SNAPSHOT` |

```groovy
implementation 'io.github.heart-beats.baseproject:base-arch:0.0.4-SNAPSHOT'
```

## 目录结构

```
arch/
├── build.gradle
└── src/main/java/com/hl/arch/
    ├── base/
    │   ├── BaseActivity.kt                    # Activity 基类
    │   ├── BaseNavigationFragment.kt           # Navigation Fragment 基类
    │   ├── ComposeBaseNavigationFragment.kt    # Compose Navigation Fragment 基类
    │   └── NavigationFragmentDelegate.kt       # Navigation Fragment 代理
    ├── loading/
    │   └── LoadingPopupProvider.kt             # 加载弹窗提供者
    ├── mvp/
    │   ├── MvpBaseActivity.kt                  # MVP Activity 基类
    │   ├── MvpBaseFragment.kt                  # MVP Fragment 基类
    │   ├── MvpBasePresenter.kt                 # MVP Presenter 基类
    │   └── MvpBaseView.kt                      # MVP View 接口
    ├── mvvm/
    │   ├── activity/
    │   │   ├── MvvmBaseActivity.kt             # MVVM Activity 基类
    │   │   └── ViewBindingMvvmBaseActivity.kt  # MVVM + ViewBinding Activity
    │   ├── fragment/
    │   │   ├── MvvmBaseFragment.kt             # MVVM Fragment 基类
    │   │   └── ViewBindingMvvmBaseFragment.kt  # MVVM + ViewBinding Fragment
    │   ├── liveData/
    │   │   └── EventLiveData.kt                # 事件 LiveData（一次性消费）
    │   ├── api/event/
    │   │   ├── UiEvent.kt                      # UI 事件（Loading/Dismiss/Error）
    │   │   └── RequestStateEvent.kt            # 请求状态事件
    │   ├── vm/
    │   │   ├── DispatcherVM.kt                 # 事件分发 ViewModel 基类
    │   │   ├── LiveDataVM.kt                   # LiveData 模式 ViewModel
    │   │   ├── FlowVM.kt                       # Flow 模式 ViewModel
    │   │   └── BaseFlowVM.kt                   # 增强 Flow ViewModel
    │   └── vmDelegate/
    │       ├── ViewModelLazy.kt                # ViewModel 懒加载代理
    │       ├── ViewModelDelegate.kt            # ViewModel 代理接口与默认实现
    │       ├── ActivityViewModelLazy.kt        # Activity 级别 ViewModel 代理
    │       └── FragmentViewModelLazy.kt        # Fragment 级别 ViewModel 代理
    └── utils/
        └── _Flow.kt                            # Flow 安全收集扩展
```

## 依赖关系

### 外部依赖
| 依赖 | 用途 |
|------|------|
| `androidx.collection:collection-ktx` | 集合扩展 |
| `androidx.compose.*` (bundle) | Jetpack Compose 全家桶 |

### 内部依赖
| 模块 | 关系 |
|------|------|
| `base-api` | 聚合依赖 —— 提供网络请求和 ApiLauncher |
| `base-ui` | 聚合依赖 —— 提供 Activity/Fragment 基础基类 |
| `base-rv-adapter` | 聚合依赖 —— 提供 RecyclerView 适配器 |
| `utils` | 聚合依赖 —— 提供工具类 |
| `navigation` | 依赖 —— 提供 Navigation 组件支持 |

## 对外接口

### Activity 基类体系

```
BaseActivity (com.hl.ui)
    └── BaseActivity (com.hl.arch)                    ── 增加状态栏色/主题色
        ├── MvpBaseActivity<Presenter>                 ── MVP 模式
        ├── MvvmBaseActivity                           ── MVVM 模式
        │   └── ViewBindingMvvmBaseActivity<Binding>   ── MVVM + ViewBinding
```

### Fragment 基类体系

```
BaseFragment (com.hl.ui)
    └── BaseNavigationFragment                         ── Navigation 集成
        ├── ComposeBaseNavigationFragment              ── Compose + Navigation
        ├── MvpBaseFragment<Presenter>                 ── MVP 模式
        ├── MvvmBaseFragment                           ── MVVM 模式
        │   └── ViewBindingMvvmBaseFragment<Binding>   ── MVVM + ViewBinding
```

### ViewModel 基类体系

```
ViewModel
    └── DispatcherVM                    ── 集成 ApiLauncher，事件分发
        ├── LiveDataVM                  ── LiveData 响应式
        ├── FlowVM                      ── Flow 响应式
        │   └── BaseFlowVM              ── 增强 Flow（StateFlow, SharedFlow）
```

### 关键抽象方法

```kotlin
// BaseActivity
abstract class BaseActivity : com.hl.ui.base.BaseActivity() {
    protected open fun getStatusBarColor(): Int  // 默认返回 colorTitlePrimary
    protected fun changeStatusBarStyleFromBitmap(bitmap: Bitmap, ...)
}

// ViewBindingMvvmBaseActivity<Binding>
abstract class ViewBindingMvvmBaseActivity<Binding : ViewBinding>
    : MvvmBaseActivity(), ViewBindingDelegate<Binding>

// DispatcherVM
abstract class DispatcherVM : ViewModel(), ApiLauncher {
    val apiEventFailedLiveData: EventLiveData<ApiEvent.Failed>
    val apiEventFailedFlow: MutableSharedFlow<ApiEvent.Failed>
}

// LiveDataVM
abstract class LiveDataVM : DispatcherVM() {
    protected fun <BODY> apiLaunch(
        needLoading: Boolean, needDispatchFailEvent: Boolean,
        reqBlock: suspend CoroutineScope.() -> PublicResp<BODY>,
        onFail: ..., onSuccess: ...
    )
    protected fun <BODY> createApiLaunchLiveData(...): LiveData<BODY?>
}

// BaseFlowVM
abstract class BaseFlowVM : FlowVM() {
    val requestStateEventFlow: SharedFlow<RequestStateEvent>
    protected fun <BODY> apiFlow(...): Flow<PublicResp<BODY>?>
    protected fun <BODY> apiStateFlow(...): StateFlow<PublicResp<BODY>?>
    protected fun <T> MutableStateFlow<T>.toStateFlow(): StateFlow<T>
}
```

### UiEvent / RequestStateEvent

```kotlin
sealed class UiEvent {
    data class UiShowLoading(var showMsg: CharSequence = "") : UiEvent()
    object UiDismissLoading : UiEvent()
    data class UiShowException(var throwable: Throwable) : UiEvent()
}

sealed class RequestStateEvent {
    data class LoadingEvent(var showMsg: CharSequence) : RequestStateEvent()
    object CompletedEvent : RequestStateEvent()
    data class ErrorEvent(var throwable: Throwable) : RequestStateEvent()
}
```

### ViewModelDelegate

```kotlin
interface ViewModelDelegate {
    var lifecycleOwnerName: String
    fun LifecycleOwner.registerOnViewModelCreated(viewModelStoreOwner: ViewModelStoreOwner)
    fun <VM> onViewModelCreated(vm: VM, lifecycleOwner: LifecycleOwner)  // 默认实现，可覆写
    fun onLiveDataVMCreated(liveDataVM: LiveDataVM)  // 默认实现，可覆写
    fun onFlowVMCreated(flowVM: FlowVM)  // 默认实现，可覆写
    fun onShowLoading(msg: CharSequence)  // 默认调用 getLoadingPopup()?.show()
    fun onShowError(throwable: Throwable)  // 默认打印日志
    fun onDismissLoading()  // 默认调用 getLoadingPopup()?.smartDismiss()
}

// 默认实现类，自动监听 ViewModel 的 UI 事件并分发
class BaseViewModelDelegate : ViewModelDelegate
```

### MVP 接口

```kotlin
interface MvpBaseView {
    fun showLoading()
    fun dismissLoading()
    fun showEmpty(vararg str: String)
    fun showError(vararg str: String)
    fun dealNetError(code: Int, e: Throwable)
    fun showMsg(msg: String)
    fun getAttachContext(): Context
}

abstract class MvpBasePresenter<View> {
    protected var view: View?
    fun detachView(view: Any)      // 绑定 View（通过 as? 安全转换）
    open fun unDetachView()        // 解绑 View
    fun hasView(): Boolean         // 判断 View 是否有效（检查 Activity/Fragment 生命周期）
}
```

## 构建与测试

```bash
# 构建模块
./gradlew :base-arch:assemble

# 发布到本地 Maven
./gradlew :base-arch:publishToMavenLocal
```

## 使用示例

### MVVM + ViewBinding + LiveData

```kotlin
// 1. 定义 ViewModel
class HomeViewModel : LiveDataVM() {
    private val repository = Repository()
    
    val bannerLiveData = createApiLaunchLiveData<List<BannerData>> {
        val resp = repository.getBannerList()
        resp.dispatchApiEvent(
            onFail = { code, msg -> XLog.e("Home", "$code: $msg") },
            onSuccess = { resp.respBody }
        )
    }
    
    fun loadBanners() {
        apiLaunch(needLoading = true, needDispatchFailEvent = true,
            reqBlock = { repository.getBannerList() },
            onFail = { code, msg -> XLog.e("Home", "$code: $msg") },
            onSuccess = { resp -> bannerLiveData.setSafeValue(resp?.respBody) }
        )
    }
}

// 2. 创建 Fragment
class HomeFragment : ViewBindingMvvmBaseFragment<FragmentHomeBinding>() {
    private val viewModel by viewModels<HomeViewModel>()
    
    override fun Binding.onViewCreated(savedInstanceState: Bundle?) {
        // 初始化视图
    }
}
```

### MVVM + BaseFlowVM

```kotlin
class UserViewModel : BaseFlowVM() {
    private val repository = UserRepository()

    // 方式 1：使用 apiFlow 获取 Flow（自动处理加载状态和错误）
    val usersFlow = apiFlow(
        needLoading = true,
        reqBlock = { repository.getUsers() }
    )

    // 方式 2：使用 apiStateFlow 获取 StateFlow
    val usersStateFlow = apiStateFlow(
        needLoading = true,
        reqBlock = { repository.getUsers() }
    )
}

// Fragment 中安全收集
class UserFragment : ViewBindingMvvmBaseFragment<FragmentUserBinding>() {
    private val viewModel by viewModels<UserViewModel>()

    override fun Binding.onViewCreated(savedInstanceState: Bundle?) {
        lifecycleScope.launch {
            viewModel.usersFlow.repeatSafeCollect(viewLifecycleOwner) { resp ->
                // 处理 PublicResp<List<User>> 数据
            }
        }

        // 或使用 apiRespRepeatSafeCollect 自动处理成功/失败
        viewModel.usersStateFlow.apiRespRepeatSafeCollect(
            viewLifecycleOwner = viewLifecycleOwner,
            isSuccess = { code() == "0" },
            onFail = { code, msg -> showError(msg) },
            onSuccess = { users -> updateUI(users) }
        )
    }
}
```

### Flow 安全收集扩展

```kotlin
// 在 Fragment 中安全收集 Flow，自动处理生命周期
lifecycleScope.launchWhenStarted {
    viewModel.dataFlow.repeatSafeCollectLatest(viewLifecycleOwner) { value ->
        // 当 Fragment 可见时才会收到数据
    }
}

// 对 API 响应 Flow 的安全收集
viewModel.apiFlow.apiRespRepeatSafeCollect(
    viewLifecycleOwner = viewLifecycleOwner,
    isSuccess = { code() == "0" },
    onFail = { code, msg -> showError(msg) },
    onSuccess = { data -> updateUI(data) }
)
```
