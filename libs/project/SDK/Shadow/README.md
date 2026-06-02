# Shadow — 插件化框架模块

## 模块概述

`shadow` 基于腾讯 Shadow 框架，提供 Android 应用插件化宿主接入的完整解决方案。包括静态 PluginProcessService 和动态 APK 加载两种模式，支持单 Loader、多 Loader、动态 UUID 等场景。

### 子模块

| 模块 | 类型 | 说明 |
|------|------|------|
| `shadow-lib` | Android Library | 常量定义、组件名约定 |
| `shadow-init` | Android Library | 核心初始化、PluginManager 实现 |
| `plugin-manager` | Android Application（已注释） | 独立 PluginManager APK 进程 |
| `plugin-aidl` | Android Library（未激活） | AIDL 接口 |

**发布 GroupId**: `io.github.heart-beats.baseproject`

## 目录结构

```
Shadow/
├── shadow-lib/
│   └── src/main/java/com/hl/shadow/lib/
│       └── ShadowConstants.kt               # 全局常量（key 定义、组件名）
│
├── shadow-init/
│   └── src/main/java/com/hl/shadow/
│       ├── Shadow.kt                         # 主入口
│       ├── logger/
│       │   ├── LogLevel.kt                   # 日志级别
│       │   ├── AndroidLoggerFactory.kt       # 日志工厂
│       │   └── OnShadowLog.kt                # 日志回调
│       ├── pps/
│       │   ├── MainPluginProcessService.kt   # 单 Loader PPS
│       │   ├── MyMultiLoaderPluginProcessService.kt # 多 Loader PPS
│       │   ├── DynamicUuidPluginProcessService.kt   # 动态 UUID PPS
│       │   ├── DynamicUuidPpsBinder.kt
│       │   └── DynamicUuidPpsController.kt
│       ├── pluginmanager/
│       │   ├── MyPluginManager.kt
│       │   ├── MyDynamicUuidPluginManager.kt
│       │   ├── MyMultiLoaderPluginManager.kt
│       │   ├── ProcessPluginManager.kt
│       │   ├── ProcessPluginManager2.kt
│       │   └── base/
│       │       ├── BaseDynamicLoaderPluginManager.kt
│       │       └── BaseMultiLoaderPluginManager.kt
│       └── managerupdater/
│           └── MyPluginManagerUpdater.kt     # 动态 PluginManager APK 更新
│
├── plugin-manager/ (inactive)
└── plugin-aidl/ (inactive)
```

## 依赖关系

### 外部依赖
| 依赖 | 用途 |
|------|------|
| `com.tencent.shadow.dynamic:host` | Shadow 宿主层 |
| `com.tencent.shadow.dynamic:manager` | Shadow Manager 层 |
| `com.tencent.shadow.core:manager` | Shadow Core Manager |
| `com.tencent.shadow.core:common` | Shadow Common |
| `com.tencent.shadow.dynamic:loader` | Shadow Loader |
| `com.tencent.shadow.dynamic:host-multi-loader-ext` | Shadow 多 Loader 扩展 |

### 内部依赖
| 模块 | 关系 |
|------|------|
| `shadow-lib` ← `shadow-init` | `shadow-init` 依赖 `shadow-lib` |

## 对外接口

### Shadow（主入口）

```kotlin
object Shadow {
    // 初始化 Shadow 日志
    fun initShadowLog(logLevel: LogLevel, onShadowLog: OnShadowLog)

    // 获取 PluginManager
    fun getMyPluginManager(context: Context): PluginManager?

    // 多 Loader PluginManager
    fun getMultiPluginManager(context: Context, pluginName: String): PluginManager?

    // 进程级 PluginManager（两种构造方式）
    fun getProcessPluginManager(context: Context, ppsName: String): PluginManager?
    fun getProcessPluginManager2(context: Context, managerName: String, ppsName: String): PluginManager?

    // 动态 PluginManager APK 加载
    fun initDynamicPluginManager(pluginManagerApkPath: String)
    fun getDynamicPluginManager(): PluginManager?
}
```

### ShadowConstants

```kotlin
object ShadowConstants {
    const val PLUGIN_MANAGER_NAME = "my-plugin-manager"
    const val PLUGIN_NAME_KEY = "PLUGIN_NAME_KEY"
    const val KEY_PLUGIN_ZIP_PATH = "pluginZipPath"
    const val KEY_PLUGIN_PART_KEY = "KEY_PLUGIN_PART_KEY"
    const val KEY_CLASSNAME = "KEY_CLASSNAME"
    const val KEY_EXTRAS = "KEY_EXTRAS"
    const val KEY_FROM_ID = "KEY_FROM_ID"
    const val FROM_ID_START_ACTIVITY = 1001L
    const val FROM_ID_CALL_SERVICE = 1002L
    const val MAIN_PLUGIN_PROCESS_SERVICE_NAME = ...
    const val MULTI_LOADER_PLUGIN_PROCESS_SERVICE_NAME = ...
    const val DYNAMIC_UUID_PLUGIN_PROCESS_SERVICE_NAME = ...
}
```

### PluginManager 子类

| 类 | 说明 |
|----|------|
| `MyPluginManager` | 单 Loader 静态管理 |
| `MyDynamicUuidPluginManager` | 动态 UUID 管理 |
| `MyMultiLoaderPluginManager` | 多 Loader 管理（传入 pluginName） |
| `ProcessPluginManager` | 进程级管理（传入 ppsName） |
| `ProcessPluginManager2` | 进程级管理（自定义 managerName） |

## 构建与测试

```bash
# 构建模块
./gradlew :shadow-lib:assemble
./gradlew :shadow-init:assemble

# 发布到本地 Maven
./gradlew :shadow-lib:publishToMavenLocal
./gradlew :shadow-init:publishToMavenLocal
```

## 使用示例

### 初始化

```kotlin
// 在 Application.onCreate() 中初始化
Shadow.initShadowLog(LogLevel.DEBUG) { level, message, t ->
    XLog.d("Shadow", "[$level] $message")
}
```

### 启动插件 Activity

```kotlin
// 方式 1：使用单 Loader PluginManager
val pluginManager = Shadow.getMyPluginManager(context)
pluginManager?.let { pm ->
    val intent = Intent().apply {
        setClassName(context.packageName, "com.example.plugin.MainActivity")
        putExtra(ShadowConstants.KEY_PLUGIN_ZIP_PATH, "/sdcard/plugin.zip")
        putExtra(ShadowConstants.KEY_PLUGIN_PART_KEY, "plugin-main")
        putExtra(ShadowConstants.KEY_CLASSNAME, "com.example.plugin.MainActivity")
        putExtra(ShadowConstants.KEY_FROM_ID, ShadowConstants.FROM_ID_START_ACTIVITY)
    }
    pm.enter(context, ShadowConstants.FROM_ID_START_ACTIVITY, intent, null)
}

// 方式 2：使用多 Loader PluginManager
val multiPm = Shadow.getMultiPluginManager(context, "plugin-name")
// ...

// 方式 3：使用动态 PluginManager APK
Shadow.initDynamicPluginManager("/sdcard/plugin-manager.apk")
val dynamicPm = Shadow.getDynamicPluginManager()
// ...
```

### AndroidManifest 配置

```xml
<!-- 在宿主的 AndroidManifest 中声明 PluginProcessService -->
<service
    android:name="com.hl.shadow.pps.MainPluginProcessService"
    android:process=":plugin" />

<service
    android:name="com.hl.shadow.pps.MyMultiLoaderPluginProcessService"
    android:process=":plugin_ml" />

<service
    android:name="com.hl.shadow.pps.DynamicUuidPluginProcessService"
    android:process=":plugin_uuid" />
```
