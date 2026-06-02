# 基础架构模块文档

## 📋 模块概述

基础架构模块是 BaseProject 的核心架构层，提供了完整的 Android 应用架构解决方案，包括 MVVM 架构、MVP 架构、网络请求、UI 基类、RecyclerView 适配器和工具类等核心功能。

## 📦 模块结构

```
base/
├── api/                             # 网络请求模块
│   ├── src/main/java/com/hl/api/
│   │   ├── RetrofitManager.kt       # Retrofit 管理器
│   │   ├── PublicResp.kt            # 通用响应体封装
│   │   ├── ApiEvent.kt              # API 事件定义
│   │   ├── ApiLauncher.kt           # API 启动器接口
│   │   ├── interceptor/             # 拦截器
│   │   │   ├── HttpLoggingInterceptor.kt
│   │   │   ├── LogProxy.kt
│   │   │   ├── MultiBaseUrlInterceptor.kt
│   │   │   └── RequestHeaderOrParamsInterceptor.kt
│   │   ├── convert/                 # 转换器
│   │   │   ├── ConverterFormat.kt
│   │   │   └── SmartConverterFactory.kt
│   │   ├── error/                   # 异常处理
│   │   │   └── ExceptionHandler.kt
│   │   ├── launcher/                # 启动器扩展
│   │   ├── monitor/                 # 网络监控
│   │   └── utils/                   # HTTP 工具
│   └── build.gradle
│
├── arch/                            # 架构核心模块
│   ├── src/main/java/com/hl/arch/
│   │   ├── mvvm/                    # MVVM 架构
│   │   │   ├── activity/            # Activity 基类
│   │   │   │   ├── MvvmBaseActivity.kt
│   │   │   │   └── ViewBindingMvvmBaseActivity.kt
│   │   │   ├── fragment/            # Fragment 基类
│   │   │   │   ├── MvvmBaseFragment.kt
│   │   │   │   └── ViewBindingMvvmBaseFragment.kt
│   │   │   ├── vm/                  # ViewModel 基类
│   │   │   │   ├── DispatcherVM.kt
│   │   │   │   ├── LiveDataVM.kt
│   │   │   │   ├── FlowVM.kt
│   │   │   │   └── BaseFlowVM.kt
│   │   │   ├── vmDelegate/          # ViewModel 代理（自定义 DI 机制）
│   │   │   │   ├── ViewModelLazy.kt
│   │   │   │   ├── ViewModelDelegate.kt
│   │   │   │   ├── ActivityViewModelLazy.kt
│   │   │   │   └── FragmentViewModelLazy.kt
│   │   │   ├── uievent/             # UI 事件
│   │   │   │   ├── UiEvent.kt
│   │   │   │   └── RequestStateEvent.kt
│   │   │   └── liveData/            # LiveData 扩展
│   │   │       └── EventLiveData.kt
│   │   ├── mvp/                     # MVP 架构
│   │   │   ├── MvpBaseActivity.kt
│   │   │   ├── MvpBaseFragment.kt
│   │   │   ├── MvpBasePresenter.kt
│   │   │   └── MvpBaseView.kt
│   │   ├── base/                    # 基础扩展
│   │   │   ├── BaseActivity.kt      # 增强 Activity 基类
│   │   │   ├── BaseNavigationFragment.kt  # Navigation Fragment 基类
│   │   │   ├── ComposeBaseNavigationFragment.kt  # Compose Navigation Fragment
│   │   │   └── NavigationFragmentDelegate.kt  # Navigation Fragment 代理
│   │   ├── loading/                 # 加载弹窗
│   │   │   └── LoadingPopupProvider.kt
│   │   └── utils/                   # Flow 扩展
│   │       └── _Flow.kt
│   └── build.gradle
│
├── ui/                              # UI 基础模块
│   ├── src/main/java/com/hl/ui/
│   │   ├── base/                    # Activity/Fragment 基类
│   │   │   ├── IPageInflate.kt
│   │   │   ├── BaseActivity.kt
│   │   │   ├── BaseFragment.kt
│   │   │   ├── ViewBindingBaseActivity.kt
│   │   │   ├── ViewBindingBaseFragment.kt
│   │   │   ├── ComposeBaseActivity.kt
│   │   │   ├── ComposeBaseFragment.kt
│   │   │   └── FragmentContainerActivity.kt
│   │   ├── bindingDelegate/         # ViewBinding 代理
│   │   │   ├── ViewBindingDelegate.kt
│   │   │   ├── ActivityViewBindingDelegate.kt
│   │   │   └── FragmentViewBindingDelegate.kt
│   │   └── utils/                   # UI 工具扩展
│   │       ├── _ActivityExt.kt
│   │       ├── _ActivityJump.kt
│   │       ├── _DensityUtils.kt
│   │       ├── _FindViewUtil.kt
│   │       ├── _SystemBar.kt
│   │       └── _View.kt
│   └── build.gradle.kts
│
├── app-res/                         # 公共资源模块
│   ├── src/main/res/
│   │   ├── values/
│   │   │   ├── colors.xml           # 颜色定义（通用颜色名，如 red, blue, green 等）
│   │   │   ├── _colors.xml          # 扩展颜色定义
│   │   │   ├── dimens.xml           # dp 尺寸定义
│   │   │   ├── dimens-px.xml        # px 尺寸定义
│   │   │   └── strings.xml          # 字符串定义
│   │   ├── anim/                    # 动画资源（8 个过渡动画）
│   │   ├── drawable/                # 图形资源（圆角背景等 Shape）
│   │   ├── drawable-xxhdpi/         # 位图资源（9 个图标）
│   │   ├── font/                    # 字体资源
│   │   │   └── hl_res_akrobat_bold.ttf
│   │   └── xml/
│   │       └── hl_res_public_file_paths.xml
│   └── build.gradle
│
├── rv-adapter/                      # RecyclerView 适配器模块
│   ├── src/main/java/com/hl/rvadapter/
│   │   ├── IDataOperate.kt          # 数据操作接口
│   │   ├── IDataType.kt             # 数据类型标识接口
│   │   ├── ItemViewType.kt          # 视图类型枚举
│   │   ├── binding/                 # ViewBinding 适配器体系
│   │   ├── normal/                  # 传统 ViewHolder 体系
│   │   ├── diffcallback/            # DiffUtil 回调
│   │   ├── drag/                    # 拖拽排序
│   │   └── utils/                   # 工具类
│   └── build.gradle.kts
│
└── utils/                           # 工具类模块
    ├── src/main/java/com/hl/utils/
    │   ├── BaseUtil.kt              # 全局 Application 持有
    │   ├── UtilsInitializer.kt      # AndroidX Startup 初始化器
    │   ├── MyCrashHandler.kt        # 全局崩溃处理器
    │   ├── DeviceInfoUtil.kt        # 设备信息获取
    │   ├── BuildVersionUtil.kt      # 构建版本工具
    │   ├── NetworkUtil.kt           # 网络状态检测
    │   ├── GrayUtil.kt              # 全局灰度模式
    │   ├── TimeUtil.kt              # 时间计算与计时
    │   ├── FileUtil.kt              # 文件工具
    │   ├── UrlParamUtil.kt          # URL 参数处理
    │   ├── Base64Util.kt            # Base64 编解码
    │   ├── DESUtil.kt               # DES 加解密
    │   ├── _RSAUtil.kt              # RSA 加解密
    │   ├── ClipboardHelper.kt       # 剪贴板工具
    │   ├── NotificationUtils.kt     # 通知工具
    │   ├── ReflectHelper.kt         # 反射辅助
    │   ├── _Proxy.kt                # 动态代理
    │   ├── PackageInstallerUtil.kt  # APK 安装
    │   ├── PaletteUtil.kt           # 调色板工具
    │   ├── AlignMiddleImageSpan.kt  # 文本图片 Span
    │   └── ... (30+ 扩展函数文件)
    └── build.gradle
```

## 🎯 核心功能模块

### 🌐 API 模块 (api/)

基于 Retrofit 封装的网络请求模块，提供统一的 HTTP 客户端创建、多域名支持、公共请求头/参数注入等能力。

**核心类：RetrofitManager**

```kotlin
// 创建 Retrofit 接口实例（带单例缓存）
val apiService = RetrofitManager.buildRetrofit<ApiService>(
    baseUrl = "https://api.example.com/",
    logProxy = ...,
    publicHeaderOrParamsBlock = {
        addHeaderParam("Authorization", "Bearer token")
        addParam("platform", "android")
    },
    okHttpBuilderBlock = {
        addInterceptor(multiBaseUrlInterceptor)
    }
)
```

**内置拦截器：**

- `RequestHeaderOrParamsInterceptor` — 公共请求头/参数拦截器，支持动态修改
- `MultiBaseUrlInterceptor` — 多域名拦截器，通过 `@Headers("Domain-Name: xxx")` 切换域名
- `HttpLoggingInterceptor` — HTTP 日志拦截器

### 🏗️ 架构模块 (arch/)

#### MVVM 架构实现

项目**不使用 Hilt/Dagger**，而是通过自定义 `ViewModelLazy` + `ViewModelDelegate` 实现 ViewModel 创建通知机制。关键组件如下：

| 组件 | 文件 | 作用 |
|------|------|------|
| `ViewModelLazy` | `androidx.lifecycle` (包名覆写) | 自定义 ViewModel 懒加载，在 ViewModel 被添加到 `ViewModelStore` 后发出创建通知 |
| `ActivityViewModelLazy` | `androidx.activity` (包名覆写) | 覆写官方 `ComponentActivity.viewModels()` 扩展，注入自定义 `ViewModelLazy` |
| `FragmentViewModelLazy` | `androidx.fragment.app` (包名覆写) | 覆写官方 `Fragment.viewModels()` 和 `Fragment.activityViewModels()` |
| `ViewModelDelegate` | `com.hl.arch.mvvm.vmDelegate` | ViewModel 创建回调接口，供 Activity/Fragment 实现以监听 ViewModel 创建事件 |
| `DispatcherVM` | `com.hl.arch.mvvm.vm` | ViewModel 基类，通过 `viewModelOnCreateSharedFlow` 分发创建事件给 `ViewModelDelegate` |

**ViewModel 基类体系：**

```
ViewModel
    └── DispatcherVM                    ── 集成 ApiLauncher，事件分发
        ├── LiveDataVM                  ── LiveData 响应式
        ├── FlowVM                      ── Flow 响应式
        │   └── BaseFlowVM              ── 增强 Flow（StateFlow, SharedFlow）
```

**Activity/Fragment 基类体系：**

```
BaseActivity (com.hl.ui)
    └── BaseActivity (com.hl.arch)      ── 增加状态栏色/主题色
        ├── MvpBaseActivity<Presenter>  ── MVP 模式
        ├── MvvmBaseActivity            ── MVVM 模式
        │   └── ViewBindingMvvmBaseActivity<Binding>  ── MVVM + ViewBinding
```

```
BaseFragment (com.hl.ui)
    └── BaseNavigationFragment          ── Navigation 集成
        ├── ComposeBaseNavigationFragment ── Compose + Navigation
        ├── MvpBaseFragment<Presenter>   ── MVP 模式
        ├── MvvmBaseFragment            ── MVVM 模式
        │   └── ViewBindingMvvmBaseFragment<Binding>  ── MVVM + ViewBinding
```

**UI 事件封装：**

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

### 🎨 资源模块 (app-res/)

纯资源模块，包含颜色、尺寸、动画、图形、字体等资源。所有资源均以 `hl_res_` 或 `hl_` 为前缀。

**颜色系统：** 使用通用颜色名（如 `red`, `blue`, `green`, `orange`, `white`, `black`, `grey` 等），完整定义见 [colors.xml](app-res/src/main/res/values/colors.xml)

**尺寸系统：** 使用 `dp_xx` / `sp_xx` 命名格式（如 `dp_16`, `sp_14`），完整定义见 [dimens.xml](app-res/src/main/res/values/dimens.xml)

**动画资源：** 包含 fade_in/fade_out、slide_in/slide_out (left/right/bottom) 共 8 个过渡动画

**字体：** `hl_res_akrobat_bold.ttf`

### 🛠️ 工具类模块 (utils/)

提供设备信息、网络状态、时间处理、文件操作、崩溃捕获、反射代理、灰度模式、软键盘管理等 70+ 工具函数和扩展方法。

详细文档见 [utils/README.md](../utils/README.md)

### 📋 RecyclerView 适配器模块 (rv-adapter/)

提供两套完整的 RecyclerView 适配器框架：传统的 ViewHolder 模式和基于 ViewBinding 的现代模式。

详细文档见 [rv-adapter/README.md](../rv-adapter/README.md)

## 🚀 快速开始

### 1. 添加依赖

```groovy
// 通过 Maven Central 集成
dependencies {
    implementation 'io.github.heart-beats.baseproject:base-arch:0.0.4-SNAPSHOT'
}

// base-arch 已聚合 base-api, base-ui, base-rv-adapter, utils, navigation
// 如需单独使用某模块：
// implementation 'io.github.heart-beats.baseproject:base-api:0.0.4-SNAPSHOT'
// implementation 'io.github.heart-beats.baseproject:utils:0.0.4-SNAPSHOT'
```

### 2. 创建 ViewModel

```kotlin
// 继承 LiveDataVM（支持 API 请求封装）
class UserViewModel : LiveDataVM() {
    private val repository = UserRepository()

    val usersLiveData = createApiLaunchLiveData<List<User>> {
        val resp = repository.getUsers()
        resp.dispatchApiEvent(
            onFail = { code, msg -> XLog.e("User", "$code: $msg") },
            onSuccess = { resp.respBody }
        )
    }

    fun loadUsers() {
        apiLaunch(needLoading = true, needDispatchFailEvent = true,
            reqBlock = { repository.getUsers() },
            onFail = { code, msg -> },
            onSuccess = { resp -> usersLiveData.setSafeValue(resp?.respBody) }
        )
    }
}
```

### 3. 创建 Activity

```kotlin
class UserActivity : ViewBindingMvvmBaseActivity<ActivityUserBinding>() {
    override fun Binding.onViewCreated(savedInstanceState: Bundle?) {
        // 视图初始化
        recyclerView.layoutManager = LinearLayoutManager(this@UserActivity)
    }
}
```

## 🔧 配置说明

### ProGuard 配置

```proguard
# 架构组件 ProGuard 规则
-keep class com.hl.arch.** { *; }
-keep class com.hl.api.** { *; }
-keep class com.hl.utils.** { *; }

# ViewModel 相关
-keep class * extends androidx.lifecycle.ViewModel {
    <init>(...);
}
```

## 🎯 最佳实践

### 1. 架构分层

```
┌─────────────────┐
│     UI Layer    │  ← Activity/Fragment/Compose（通过 by viewModels<>() 获取 VM）
├─────────────────┤
│   ViewModel     │  ← 状态管理、业务逻辑（继承 LiveDataVM / FlowVM）
├─────────────────┤
│   Repository    │  ← 数据源管理（调用 RetrofitManager + ApiLauncher）
├─────────────────┤
│   Data Source   │  ← 网络、数据库、文件
└─────────────────┘
```

### 2. 统一错误处理

`DispatcherVM` 内置 `apiEventFailedLiveData`，在 `MvvmBaseActivity` 中自动通过 `ViewModelDelegate` 监听错误事件：

```kotlin
abstract class MvvmBaseActivity : BaseActivity(), ViewModelDelegate {
    override fun onShowError(throwable: Throwable) {
        // 默认弹 Toast，可覆写
        showMsg(throwable.message ?: "请求失败")
    }

    override fun onShowLoading(msg: CharSequence) {
        // 默认显示加载中
    }

    override fun onDismissLoading() {
        // 默认隐藏加载
    }
}
```

## 📚 相关文档

### 子模块文档
- [base-api 网络请求模块](api/README.md) — Retrofit 封装、多域名、拦截器
- [base-arch 架构核心模块](arch/README.md) — MVVM/MVP 架构、ViewModel 体系
- [base-ui UI 基础模块](ui/README.md) — Activity/Fragment 基类、ViewBinding、系统栏
- [base-app-res 公共资源模块](app-res/README.md) — 颜色、尺寸、动画、字体
- [base-rv-adapter RecyclerView 适配器模块](rv-adapter/README.md) — ViewBinding 适配器、多类型
- [utils 工具类模块](../utils/README.md) — 设备信息、网络、时间、灰度模式
