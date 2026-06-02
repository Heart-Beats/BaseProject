# BaseProject - Android 项目基础框架

![Android](https://img.shields.io/badge/Android-3DDC84?style=for-the-badge&logo=android&logoColor=white)
![Kotlin](https://img.shields.io/badge/Kotlin-0095D5?style=for-the-badge&logo=kotlin&logoColor=white)
![Jetpack Compose](https://img.shields.io/badge/Jetpack%20Compose-4285F4?style=for-the-badge&logo=jetpack-compose&logoColor=white)

BaseProject 是一个功能完整的 Android 项目基础框架，集成了现代化的开发架构和丰富的功能模块，旨在帮助开发者快速构建高质量的 Android 应用程序。

## 🌟 项目特色

### 🏗️ 现代化架构设计
- **MVVM + MVP 双架构**：支持 MVVM (LiveData/Flow) 和 MVP 两种架构模式
- **模块化设计**：清晰的模块分层，便于维护和扩展
- **自定义 ViewModel 代理**：通过覆写 `viewModels()` 扩展实现 ViewModel 创建通知机制
- **响应式编程**：支持 Flow、LiveData 等响应式数据流

### 🎨 双 UI 框架支持
- **Jetpack Compose**：现代化的声明式 UI 框架
- **传统 View 系统**：兼容传统 XML 布局开发
- **主题系统**：完整的设计系统和主题切换

### 🔌 丰富的功能模块
- **网络请求**：基于 Retrofit 的封装，支持多域名、拦截器
- **插件化支持**：集成 Shadow 框架实现动态插件加载
- **小程序支持**：集成 UniMP 支持小程序功能
- **第三方服务**：支付、分享、推送、统计等完整解决方案

### 🚀 开发工具集成
- **性能优化**：ProfileInstaller、基准配置文件
- **多渠道打包**：VasDolly 集成支持
- **日志系统**：XLog 日志框架
- **MMKV 存储**：高性能键值存储

## 📋 项目结构

```
BaseProject/
├── app/                           # 主应用模块
│   ├── src/main/java/com/hl/baseproject/
│   │   ├── base/                  # 基础类库
│   │   ├── compose/               # Compose UI 组件
│   │   ├── fragments/             # Fragment 页面
│   │   ├── repository/            # 数据仓库
│   │   ├── viewmodels/             # ViewModel 层
│   │   └── configs/               # 配置文件
│   └── build.gradle
│
├── libs/
│   ├── project/                   # 项目基础框架
│   │   ├── base/                  # 基础模块
│   │   │   ├── api/                # 网络请求模块
│   │   │   ├── arch/               # 架构模块
│   │   │   ├── app-res/            # 公共资源
│   │   │   └── utils/              # 工具类
│   │   │
│   │   ├── SDK/                  # 第三方 SDK 集成
│   │   │   ├── ActivityResult/    # Activity 结果处理
│   │   │   ├── Banner/            # 轮播图组件
│   │   │   ├── Camera/            # 相机功能
│   │   │   ├── DateUtil/          # 日期工具
│   │   │   ├── Download/          # 下载功能
│   │   │   ├── ImageLoad/         # 图片加载
│   │   │   ├── JsonUtil/          # JSON 处理
│   │   │   ├── MMKVSharedPreferences/ # 存储
│   │   │   ├── MimeType/          # 文件类型
│   │   │   ├── Navigation/        # 导航组件
│   │   │   ├── Pay/               # 支付模块
│   │   │   ├── Shadow/            # 插件化框架
│   │   │   ├── TencentCloud/      # 腾讯云存储
│   │   │   ├── Umeng/             # 友盟服务
│   │   │   └── UniMP/             # 小程序支持
│   │   │
│   │   └── build.gradle
│   │
│   └── uikit/                     # UI 组件库
│       ├── uikit/                # 主 UI 库
│       ├── uikit-res/            # UI 资源
│       └── demo/                 # UI 演示应用
│
├── benchmark/                     # 性能测试模块
├── commonGradle/                  # 公共 Gradle 配置
└── build.gradle                   # 根项目配置
```

## 📦 核心功能模块

### 🔧 基础架构模块 (`libs/project/base/`)

#### `base-arch` - 架构核心
- MVVM 架构基础类
- ViewModel、Repository 模式实现
- 生命周期管理
- 状态管理

#### `base-api` - 网络请求
- Retrofit 封装
- 多域名支持
- 公共请求头/参数
- 拦截器链
- 错误处理

#### `base-app-res` - 公共资源
- 颜色、字体、尺寸系统
- 动画资源
- Shape 样式
- 主题配置

### 🔌 SDK 集成模块 (`libs/project/SDK/`)

#### 支付服务 (`Pay/`)
- 微信支付集成
- 支付宝支付集成
- 统一支付接口

#### 插件化框架 (`Shadow/`)
- Shadow 插件化宿主支持
- 动态插件加载
- 插件进程管理
- 4 个子模块：`shadow-lib`（常量定义）、`shadow-init`（核心初始化与插件管理器）、`plugin-aidl`（AIDL 接口定义）、`plugin-manager`（插件管理器 APK 构建）

#### 云服务 (`TencentCloud/`)
- 腾讯云对象存储
- 文件上传/下载
- 传输进度监听

#### 社交服务 (`Umeng/`)
- 三方登录（微信、QQ、微博）
- 社交分享
- 推送服务
- 应用统计

#### 小程序支持 (`UniMP/`)
- Uni 小程序集成
- 小程序生命周期管理
- 原生与小程序通信

### 🎨 UI 组件库 (`libs/uikit/`)

提供丰富的 UI 组件和工具：
- 通用列表适配器
- 轮播图组件
- 图片加载组件
- 自定义控件
- 动画效果

## 📦 模块发布与依赖配置

`libs/` 目录下的所有模块均通过 [vanniktech/gradle-maven-publish-plugin](https://github.com/vanniktech/gradle-maven-publish-plugin) 发布至 **Maven Central**。

### 统一坐标

| 属性 | 值 |
|------|-----|
| **GroupId** | `io.github.heart-beats.baseproject` |
| **当前版本** | `0.0.4-SNAPSHOT` |

### 完整模块坐标表

| 分类 | 模块 | ArtifactId | 坐标字符串 |
|------|------|-----------|-----------|
| 基础架构 | base-api | `base-api` | `io.github.heart-beats.baseproject:base-api:0.0.4-SNAPSHOT` |
| | base-arch | `base-arch` | `io.github.heart-beats.baseproject:base-arch:0.0.4-SNAPSHOT` |
| | base-ui | `base-ui` | `io.github.heart-beats.baseproject:base-ui:0.0.4-SNAPSHOT` |
| | base-rv-adapter | `base-rv-adapter` | `io.github.heart-beats.baseproject:base-rv-adapter:0.0.4-SNAPSHOT` |
| | base-app-res | `base-app-res` | `io.github.heart-beats.baseproject:base-app-res:0.0.4-SNAPSHOT` |
| | utils | `utils` | `io.github.heart-beats.baseproject:utils:0.0.4-SNAPSHOT` |
| UI 组件 | uikit | `uikit` | `io.github.heart-beats.baseproject:uikit:0.0.4-SNAPSHOT` |
| | uikit-res | `uikit-res` | `io.github.heart-beats.baseproject:uikit-res:0.0.4-SNAPSHOT` |
| | uikit-toast | `uikit-toast` | `io.github.heart-beats.baseproject:uikit-toast:0.0.4-SNAPSHOT` |
| SDK | navigation | `navigation` | `io.github.heart-beats.baseproject:navigation:0.0.4-SNAPSHOT` |
| | pay | `pay` | `io.github.heart-beats.baseproject:pay:0.0.4-SNAPSHOT` |
| | umeng | `umeng` | `io.github.heart-beats.baseproject:umeng:0.0.4-SNAPSHOT` |
| | ... | ... | (共 40+ 个模块，完整列表见 [libs/project/README.md](libs/project/README.md)) |

### Gradle 依赖配置示例

```groovy
// settings.gradle — 添加 Maven Central 仓库
dependencyResolutionManagement {
    repositories {
        mavenCentral()
    }
}

// build.gradle — 直接依赖
dependencies {
    // 基础架构（按需选择）
    implementation 'io.github.heart-beats.baseproject:base-arch:0.0.4-SNAPSHOT'
    implementation 'io.github.heart-beats.baseproject:base-api:0.0.4-SNAPSHOT'

    // UI 组件库
    implementation 'io.github.heart-beats.baseproject:uikit:0.0.4-SNAPSHOT'

    // SDK 集成（按需）
    implementation 'io.github.heart-beats.baseproject:pay:0.0.4-SNAPSHOT'
    implementation 'io.github.heart-beats.baseproject:navigation:0.0.4-SNAPSHOT'
}
```

> **注意**：本项目在内部开发时使用 Composite Build 模式，外部集成时通过 Maven Central 拉取。版本号请以 [publish_config.gradle](commonGradle/maven-publish/publish_config.gradle) 中的实际值为准。

## 🚀 快速开始

### 1. 环境要求

```gradle
// build.gradle 配置
compileSdk 34
minSdk 26
targetSdk 34

// 依赖版本
kotlinVersion = '1.9.20'
composeVersion = '1.5.4'
```

### 2. 基本配置

```kotlin
// Application 初始化
class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        
        if (isMainProcess()) {
            SDKPrepareHelper.preInitSdk(this)
            SDKInitHelper.initSdk(this)
        }
    }
}
```

### 3. 网络请求示例

```kotlin
// API 接口定义
interface ApiService {
    @GET("banner/json")
    suspend fun getBannerList(): WanAndroidPublicResp<List<BannerData>>
}

// 使用 Repository
class Repository {
    private val apiService = RetrofitManager.buildRetrofit<ApiService>(
        baseUrl = "https://www.wanandroid.com/",
        logProxy = object : LogProxy {
            override fun log(message: String) {
                XLog.d("HTTP", message)
            }
        }
    )
    
    suspend fun getBannerList() = apiService.getBannerList()
}
```

### 4. MVVM 示例（LiveData 模式）

```kotlin
// ViewModel 定义
class HomeViewModel : LiveDataVM() {
    private val repository = Repository()
    
    val bannerLiveData = createApiLaunchLiveData<List<BannerData>>(
        reqBlock = { repository.getBannerList() }
    )
}

// Fragment 中使用
class HomeFragment : ViewBindingMvvmBaseFragment<FragmentHomeBinding>() {
    private val viewModel by viewModels<HomeViewModel>()
    
    override fun Binding.onViewCreated(savedInstanceState: Bundle?) {
        viewModel.bannerLiveData.observe(viewLifecycleOwner) { data ->
            // 更新 UI，data 为 List<BannerData>?
        }
    }
}
```

### 5. Compose UI 示例

```kotlin
@Composable
fun HomeScreen(viewModel: HomeViewModel = viewModel()) {
    val uiState by viewModel.uiState.collectAsState()
    
    LazyColumn {
        items(uiState.bannerList) { banner ->
            BannerItem(banner = banner)
        }
    }
}
```

## 🔧 构建配置

### 多渠道打包

```bash
# 使用 VasDolly 进行多渠道打包
./gradlew assembleRelease
```

### 基准配置文件

```bash
# 生成基准配置文件
./gradlew assembleBenchmark
```

### 性能分析

```bash
# 构建性能分析版本
./gradlew assembleBenchmark
```

## 📱 主要功能演示

### 1. 主页面结构
- **MainActivity**: 主 Activity，集成导航组件
- **AppMainFragment**: 应用主 Fragment
- **HomeFragment**: 首页展示
- **TestFragment**: 功能测试页面

### 2. Compose 演示
- **ComposeDemoFragment**: Compose 基础演示
- **ComposeStateFragment**: 状态管理演示
- **ComposeNavigationFragment**: 导航演示
- **ComposeListFragment**: 列表演示

### 3. 插件化演示
- **ShadowPluginFragment**: 插件功能演示
- 支持动态加载插件 APK

### 4. WebView 集成
- **WebViewNavigationFragment**: WebView 封装
- JavaScript 桥接
- 导航控制

## 🏗️ 架构特点

### 1. 分层架构
```
┌─────────────────┐
│   UI Layer      │  ← Activity/Fragment/Compose
├─────────────────┤
│   ViewModel     │  ← 状态管理、业务逻辑
├─────────────────┤
│   Repository    │  ← 数据源管理
├─────────────────┤
│   Data Source   │  ← 网络、数据库、文件
└─────────────────┘
```

### 2. 依赖注入
- 通过自定义 `ViewModelLazy` + `ViewModelDelegate` 实现 ViewModel 创建通知
- 覆写 AndroidX 官方 `viewModels()` 扩展，注入自定义创建流程
- 无需 Hilt/Dagger 等第三方 DI 框架

### 3. 响应式编程
- Flow 数据流
- LiveData 状态管理
- Compose 状态观察

## 🔍 调试与测试

### 调试功能
- XLog 日志系统
- 调试模式配置
- 进程分离调试

### 测试支持
- 单元测试框架
- 仪器化测试
- 基准测试

## 📚 相关文档

### 项目模块
- [App 主应用模块](app/README.md) — 应用层架构、Compose 演示
- [项目基础框架](libs/project/README.md) — Base + SDK 模块总览
- [UI 组件库](libs/uikit/README.md) — UIKit 组件使用指南
- [基准测试](benchmark/README.md) — Baseline Profile 生成

### 基础架构
- [base-api 网络请求](libs/project/base/api/README.md) — Retrofit 封装
- [base-arch 架构核心](libs/project/base/arch/README.md) — MVVM/MVP 架构
- [base-app-res 公共资源](libs/project/base/app-res/README.md) — 资源规范
- [base-ui UI 基础](libs/project/base/ui/README.md) — Activity/Fragment 基类
- [base-rv-adapter 适配器](libs/project/base/rv-adapter/README.md) — RecyclerView 适配器
- [utils 工具类](libs/project/utils/README.md) — 综合工具

### SDK 集成
- [Pay 支付](libs/project/SDK/Pay/README.md)
- [Umeng 友盟](libs/project/SDK/Umeng/README.md)
- [TencentCloud 腾讯云](libs/project/SDK/TencentCloud/README.md)
- [UniMP 小程序](libs/project/SDK/UniMP/README.md)
- [Shadow 插件化](libs/project/SDK/Shadow/README.md)
- [Web WebView](libs/project/SDK/Web/README.md)
- [Navigation 导航](libs/project/SDK/Navigation/README.md)
- [SDK 完整列表](libs/project/SDK/README.md)

## 🤝 贡献指南

1. Fork 项目
2. 创建功能分支
3. 提交更改
4. 推送到分支
5. 创建 Pull Request

## 📄 开源协议

本项目基于 MIT 协议开源，详见 [LICENSE](LICENSE) 文件。

## 🔗 相关链接

- [Android 官方文档](https://developer.android.com/)
- [Jetpack Compose 文档](https://developer.android.com/jetpack/compose)
- [Shadow 插件化框架](https://github.com/Tencent/shadow)
- [UniMP 小程序 SDK](https://nativesupport.dcloud.net.cn/UniMPDocs/)

---

**BaseProject** - 让 Android 开发更简单、更高效！ 🚀