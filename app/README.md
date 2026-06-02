# App 模块文档

## 📋 模块概述

App 模块是 BaseProject 的主应用模块，集成了完整的 Android 应用架构，包含演示页面、基础组件使用示例和各种功能模块的集成展示。

## 🏗️ 架构结构

```
app/src/main/java/com/hl/baseproject/
├── base/                           # 基础类库
│   ├── BaseFragment.kt           # Fragment 基类
│   ├── BaseComposeFragment.kt    # Compose Fragment 基类
│   ├── BaseViewModel.kt          # ViewModel 基类
│   └── FlowBaseViewModel.kt      # Flow 响应式 ViewModel 基类
│
├── compose/                        # Jetpack Compose 相关
│   ├── pages/                    # Compose 页面
│   │   ├── ComposeDemoFragment.kt
│   │   ├── ComposeLayoutsFragment.kt
│   │   ├── ComposeListFragment.kt
│   │   ├── ComposeNavigationFragment.kt
│   │   ├── ComposeSideEffectsFragment.kt
│   │   └── ComposeStateFragment.kt
│   │
│   ├── navigation/              # Compose 导航
│   │   ├── NavHost.kt
│   │   ├── Destinations.kt
│   │   ├── Screen1.kt - Screen3.kt
│   │   └── DeepLinkScreen.kt
│   │
│   ├── widgets/                 # Compose 组件
│   │   ├── ImageText.kt
│   │   ├── ImageTextCard.kt
│   │   ├── SearchBar.kt
│   │   ├── SmartImage.kt
│   │   ├── TabLayout.kt
│   │   ├── TitleCompose.kt
│   │   ├── VerticalDivider.kt
│   │   └── _Spacer.kt
│   │
│   ├── utils/                   # Compose 工具类
│   │   ├── _ComposeNavigation.kt
│   │   ├── _Flow.kt
│   │   └── _Modifier.kt
│   │
│   ├── Colors.kt                # 颜色主题
│   ├── Shapes.kt                # 形状主题
│   ├── Typographies.kt          # 字体主题
│   └── Themes.kt                # 整体主题
│
├── fragments/                     # Fragment 页面
│   ├── AppMainFragment.kt        # 应用主 Fragment
│   ├── MainFragment.kt           # 主 Fragment
│   ├── TestFragment.kt           # 测试 Fragment
│   ├── ShadowPluginFragment.kt   # 插件化演示 Fragment
│   └── home/                     # 首页相关
│       ├── HomeFragment.kt       # 首页 Fragment
│       ├── HomeMiddleFragment.kt # 首页中间 Fragment
│       └── banner/               # 首页轮播图
│           ├── BannerType.kt
│           ├── BannerViewHolder.kt
│           └── CommonBannerAdapter.kt
│
├── repository/                    # 数据仓库层
│   ├── Repository.kt             # 主仓库
│   └── network/
│       ├── MyApiEventProvider.kt # API 事件提供者
│       ├── RequestApiInterface.kt # API 接口定义
│       └── bean/                 # 数据模型
│           ├── BananerData.kt
│           ├── CheckPhoneBean.kt
│           ├── DecryptBean.kt
│           ├── EncryptBean.kt
│           ├── HomeArticleList.kt
│           └── WanAndroidPublicResp.kt
│
├── viewmodels/                    # ViewModel 层
│   ├── DataViewModel.kt          # 数据 ViewModel
│   ├── HomeViewModel.kt          # 首页 ViewModel
│   └── TestViewModel.kt          # 测试 ViewModel
│
├── configs/                       # 配置类
│   └── AppConfig.kt              # 应用配置
│
├── shadow/                        # 插件化相关
│   └── pps/                      # 插件进程服务
│       ├── TestPluginProcessService.kt
│       └── ZKYPluginProcessService.kt
│
├── web/                           # WebView 相关
│   └── WebViewNavigationFragment.kt
│
├── user/                          # 用户相关
│   ├── UserInfo.kt               # 用户信息
│   └── UserManager.kt            # 用户管理
│
├── MainActivity.kt               # 主 Activity
├── SplashActivity.kt             # 启动页 Activity
├── TestActivity.kt               # 测试 Activity
├── TestActivity2.kt              # 测试 Activity 2
├── MyApplication.kt              # 应用入口
├── SDKInitHelper.kt              # SDK 初始化帮助类
├── SDKPrepareHelper.kt           # SDK 预初始化帮助类
└── FullVideoView.kt              # 全屏视频 View
```

## 🎯 核心功能

### 1. 应用入口类

#### MyApplication.kt
应用的全局入口，负责：
- SDK 初始化
- 进程判断与初始化
- 插件进程恢复
- WebView 配置

```kotlin
class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        
        if (isMainProcess()) {
            SDKPrepareHelper.preInitSdk(this)
            SDKInitHelper.initSdk(this)
        } else if (isPluginProcess()) {
            // 插件进程初始化
            DynamicRuntime.recoveryRuntime(this)
        }
    }
}
```

### 2. 基础架构类

#### BaseFragment.kt
Fragment 基类，提供：
- ViewBinding 支持
- 生命周期管理
- 通用方法封装

#### BaseViewModel.kt
ViewModel 基类，提供：
- 状态管理
- 错误处理
- 加载状态控制

#### FlowBaseViewModel.kt
基于 Flow 的响应式 ViewModel，提供：
- Flow 数据流管理
- 状态收集
- 错误处理

### 3. Compose 架构

#### 主题系统
- **Colors.kt**: 颜色定义
- **Shapes.kt**: 形状定义
- **Typographies.kt**: 字体定义
- **Themes.kt**: 整体主题配置

#### 导航系统
- **NavHost.kt**: Compose 导航主机
- **Destinations.kt**: 导航目标定义
- 支持深层链接

#### 自定义组件
- **SmartImage.kt**: 智能图片组件
- **SearchBar.kt**: 搜索栏组件
- **TabLayout.kt**: 标签栏组件
- **ImageTextCard.kt**: 图文卡片组件

### 4. 数据层

#### Repository.kt
数据仓库，负责：
- 网络请求管理
- 数据缓存
- 错误处理

```kotlin
class Repository {
    private val apiService = RetrofitManager.buildRetrofit<RequestApiInterface>(
        baseUrl = "https://www.wanandroid.com/",
        isPrintLog = BuildConfig.DEBUG
    )
}
```

#### 数据模型
- **BannerData**: 轮播图数据
- **HomeArticleList**: 首页文章列表
- **WanAndroidPublicResp**: 网络响应封装

### 5. 功能页面

#### HomeFragment.kt
首页 Fragment，包含：
- 轮播图展示
- 文章列表
- 下拉刷新
- 上拉加载

#### ShadowPluginFragment.kt
插件化演示页面，展示：
- 插件加载
- 插件管理
- 插件通信

#### ComposeDemoFragment.kt
Compose 演示页面，包含：
- 基础组件演示
- 布局演示
- 状态管理演示

## 🔧 配置说明

### Maven 依赖集成

App 模块通过 **Composite Build** 或 **Maven Central** 两种方式依赖 `libs/` 下的模块：

```groovy
// 通过 Maven Central 直接集成（外部使用）
dependencies {
    // 架构组件
    implementation 'io.github.heart-beats.baseproject:base-arch:0.0.4-SNAPSHOT'
    
    // UI 组件
    implementation 'io.github.heart-beats.baseproject:uikit:0.0.4-SNAPSHOT'
    
    // SDK 集成（按需）
    implementation 'io.github.heart-beats.baseproject:shadow-init:0.0.4-SNAPSHOT'
    implementation 'io.github.heart-beats.baseproject:uni-mp:0.0.4-SNAPSHOT'
}
```

### build.gradle 配置

```gradle
android {
    compileSdk 34
    
    buildFeatures {
        viewBinding true
        compose true
        buildConfig true
    }
    
    composeOptions {
        kotlinCompilerExtensionVersion = '1.5.5'
    }
}
```

### 签名配置

```gradle
signingConfigs {
    realse {
        storeFile file('./license/BaseProject.jks')
        storePassword 'android123456'
        keyPassword 'android123456'
        keyAlias 'BaseProject'
    }
}
```

## 🚀 使用示例

### 1. 创建 Compose Fragment

```kotlin
class MyComposeFragment : BaseComposeFragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                AppTheme {
                    MyComposeContent()
                }
            }
        }
    }
}
```

### 2. 使用 ViewModel

```kotlin
class MyViewModel : FlowBaseViewModel() {
    private val repository = Repository()
    
    val uiState = flow {
        val data = repository.getData()
        emit(UiState.Success(data))
    }.catch { error ->
        emit(UiState.Error(error))
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = UiState.Loading
    )
}
```

### 3. 网络请求

```kotlin
class Repository {
    suspend fun getBannerList(): Result<List<BannerData>> {
        return try {
            val response = apiService.getBannerList()
            if (response.isSuccess) {
                Result.success(response.data ?: emptyList())
            } else {
                Result.failure(Exception(response.errorMsg))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
```

## 📱 演示功能

### 1. 基础功能演示
- Activity/Fragment 生命周期
- ViewModel 状态管理
- 网络请求处理
- 错误处理

### 2. Compose 演示
- 声明式 UI 编程
- 状态管理
- 导航系统
- 自定义组件

### 3. 插件化演示
- 动态插件加载
- 插件通信
- 插件管理

### 4. 第三方集成演示
- 支付功能
- 社交分享
- 小程序集成
- 文件上传下载

## 🔍 调试信息

### 日志查看
应用使用 XLog 进行日志记录，可以通过以下方式查看：
```kotlin
XLog.d("Debug message")
XLog.e("Error message")
```

### 调试模式
在 `build.gradle` 中配置调试模式：
```gradle
debug {
    debuggable true
    minifyEnabled false
}
```

## 📚 相关文档

### 基础架构
- [base-api 网络请求](../../libs/project/base/api/README.md)
- [base-arch 架构核心](../../libs/project/base/arch/README.md)
- [base-ui UI 基础](../../libs/project/base/ui/README.md)
- [base-rv-adapter 适配器](../../libs/project/base/rv-adapter/README.md)
- [utils 工具类](../../libs/project/utils/README.md)

### 组件库
- [UIKit 组件库](../../libs/uikit/README.md)
- [uikit-toast Toast](../../libs/uikit/uikit-toast/README.md)

### SDK 集成
- [Pay 支付](../../libs/project/SDK/Pay/README.md)
- [Umeng 友盟](../../libs/project/SDK/Umeng/README.md)
- [TencentCloud 腾讯云](../../libs/project/SDK/TencentCloud/README.md)
- [UniMP 小程序](../../libs/project/SDK/UniMP/README.md)
- [Shadow 插件化](../../libs/project/SDK/Shadow/README.md)
- [Web WebView](../../libs/project/SDK/Web/README.md)
- [Navigation 导航](../../libs/project/SDK/Navigation/README.md)
- [SDK 完整列表](../../libs/project/SDK/README.md)