# shadow-init

Shadow 插件框架的宿主端核心初始化模块，封装了多种 PluginManager 实现、PluginProcessService（PPS）、日志系统，并提供统一的入口类 `Shadow` 供外部调用。

## Maven 坐标

| 属性 | 值 |
|------|-----|
| **GroupId** | `io.github.heart-beats.baseproject` |
| **ArtifactId** | `shadow-init` |
| **当前版本** | `0.0.4-SNAPSHOT` |

```groovy
implementation 'io.github.heart-beats.baseproject:shadow-init:0.0.4-SNAPSHOT'
```

## 模块概览

`shadow-init` 是宿主接入 Shadow 插件框架的唯一入口模块，向上层提供多种 PluginManager 实例的获取能力，内部封装了：

- **3 种 PluginProcessService (PPS)**：基础、多 Loader、动态 UUID
- **5 种 PluginManager**：静态、多 Loader、多进程、增强多进程、动态 UUID
- **2 个 PluginManager 基类**：抽象安装/加载流程的模板方法
- **日志系统**：Shadow 内部日志 → Android Logger 桥接
- **动态更新器**：PluginManager 热更新支持

## 源码结构

```
shadow-init/src/main/java/com/hl/shadow/
├── Shadow.kt                                    # 单例入口，管理所有 PluginManager 实例
├── logger/
│   ├── AndroidLoggerFactory.kt                   # ILoggerFactory 实现（ConcurrentHashMap 缓存）
│   └── LogLevel.kt                               # 日志等级枚举（TRACE/DEBUG/INFO/WARN/ERROR）
├── pps/
│   ├── MainPluginProcessService.kt               # 基础 PPS（不可动态修改 UUID）
│   ├── MyMultiLoaderPluginProcessService.kt       # 多 Loader PPS
│   ├── DynamicUuidPluginProcessService.kt         # 动态 UUID PPS
│   ├── DynamicUuidPpsBinder.kt                    # Binder 服务端：UUID 跨进程读写
│   └── DynamicUuidPpsController.kt                # Binder 客户端：UUID 跨进程代理
├── pluginmanager/
│   ├── MyPluginManager.kt                         # 基础静态 PluginManager
│   ├── MyMultiLoaderPluginManager.kt              # 多 Loader PluginManager
│   ├── MyDynamicUuidPluginManager.kt              # 动态 UUID PluginManager
│   ├── ProcessPluginManager.kt                    # 多进程 PluginManager
│   ├── ProcessPluginManager2.kt                   # 增强多进程（反射自定义 name）
│   └── base/
│       ├── BaseDynamicLoaderPluginManager.kt      # 动态加载 PluginManager 基类（396 行）
│       └── BaseMultiLoaderPluginManager.kt        # 多 Loader PluginManager 基类（374 行）
└── managerupdater/
    └── MyPluginManagerUpdater.kt                  # PluginManager 动态更新器
```

## 核心入口：`Shadow`

```kotlin
object Shadow {
    // === 静态 PluginManager ===
    fun getMyPluginManager(context: Context): MyPluginManager
    fun getMultiPluginManager(context: Context, pluginName: String): MyMultiLoaderPluginManager
    fun getProcessPluginManager(context: Context, ppsName: String): ProcessPluginManager
    fun getProcessPluginManager2(context: Context, managerName: String, ppsName: String): ProcessPluginManager2

    // === 动态 PluginManager ===
    fun initDynamicPluginManager(pluginManagerApkPath: String)
    fun getDynamicPluginManager(): PluginManagerThatUseDynamicLoader?

    // === 日志初始化 ===
    fun initShadowLog(logPrint: OnShadowLog)
}
```

## PluginManager 变体说明

### 1. MyPluginManager — 基础静态

- 继承自 `BaseDynamicLoaderPluginManager`
- 绑定单一 `MainPluginProcessService`（不可修改 UUID）
- 适用场景：最简单的插件加载

### 2. MyMultiLoaderPluginManager — 多 Loader

- 继承自 `BaseMultiLoaderPluginManager`
- 通过 `pluginName` 区分不同插件包
- 一个 PPS 可加载多个不同插件包

### 3. ProcessPluginManager — 多进程

- 继承自 `BaseDynamicLoaderPluginManager`
- 构造函数接收 `ppsName`，每个实例绑定不同的 PPS
- 适用场景：多进程宿主，不同进程使用不同插件

### 4. ProcessPluginManager2 — 增强多进程

- 继承自 `ProcessPluginManager`
- 支持自定义 `managerName`（影响数据库存储路径）
- 通过反射修改父类 `BasePluginManager` 的 `mUnpackManager` 和 `mInstalledDao`

### 5. MyDynamicUuidPluginManager — 动态 UUID

- 继承自 `MyPluginManager`
- 在 `onPluginServiceConnected` 时动态设置 PPS 的 UUID
- 支持同一个 PPS 先后加载不同 UUID 的插件包

## PluginProcessService 变体说明

| PPS | 特点 | 绑定 PluginManager |
|-----|------|-------------------|
| `MainPluginProcessService` | 基础实现，UUID 不可更改 | `MyPluginManager` |
| `MyMultiLoaderPluginProcessService` | 继承 `MultiLoaderPluginProcessService` | `MyMultiLoaderPluginManager` |
| `DynamicUuidPluginProcessService` | 通过反射读写 `mUuid` 字段，可动态切换 UUID | `MyDynamicUuidPluginManager`, `ProcessPluginManager`, `ProcessPluginManager2` |

## 插件加载流程（BaseDynamicLoaderPluginManager）

```
enter(context, fromId, bundle, callback)
    │
    ├── fromId == FROM_ID_START_ACTIVITY (1001L)
    │       └── launchPluginActivity()
    │               ├── installPlugin(zipPath)        # 解压、odex、提取 SO、写数据库
    │               ├── loadPlugin()                   # 加载 loader/runtime、加载插件、Application.onCreate
    │               ├── startPluginActivity()          # 启动插件 Activity
    │               └── callback.onEnterComplete()
    │
    └── fromId == FROM_ID_CALL_SERVICE (1002L)
            └── launchPluginService()
                    ├── installPlugin(zipPath)
                    ├── loadPlugin()
                    └── bindPluginService()
```

### installPlugin 步骤

1. 从 ZIP 解压出 Runtime、Loader、Plugin APK
2. 并发执行 odex 优化（Runtime + Loader + 各 plugin partKey）
3. 并发提取各 plugin partKey 的 so 库
4. 调用 `onInstallCompleted` 写入数据库

## 日志系统

```kotlin
// 使用示例
Shadow.initShadowLog { logLevel, message, throwable ->
    when (logLevel) {
        LogLevel.DEBUG -> Log.d("Shadow", message, throwable)
        LogLevel.ERROR -> Log.e("Shadow", message, throwable)
        else -> {}
    }
}
```

- `AndroidLoggerFactory` 使用 `ConcurrentHashMap` 缓存 Logger 实例
- 支持 TRACE / DEBUG / INFO / WARN / ERROR 五个等级

## PluginManager 动态更新

```kotlin
// MyPluginManagerUpdater 用于动态更新 PluginManager APK
class MyPluginManagerUpdater(pluginManagerApk: File) : PluginManagerUpdater {
    override fun getLatest(): File? = pluginManagerApk
    override fun wasUpdating(): Boolean = false
    override fun update(): Future<*>? = null
    override fun isAvailable(): Boolean? = null
}
```

## 外部依赖

| 依赖 | 说明 |
|------|------|
| `:shadow-lib` | api 依赖，引入 `ShadowConstants` |
| `shadow.host` | Shadow 宿主 API |
| `shadow.manager` | Shadow Manager 核心 |
| `shadow.activity.container` | Shadow Activity 容器 |
| `shadow.host.multi.loader.ext` | 多 Loader 扩展（宿主端） |
| `shadow.manager.multi.loader.ext` | 多 Loader 扩展（Manager 端） |
| `shadow.common` | 通用库（compileOnly） |
| `shadow.loader` | Loader API（compileOnly） |
| `shadow.loader.impl` | Loader 实现（compileOnly） |
| `gson` | JSON 序列化 |

## 注意事项

- 所有 Shadow 核心依赖通过阿里云 Maven 镜像 + GitHub Packages 获取
- `shadow-loader` 和 `shadow-common` 为 `compileOnly`，不会打包进 APK
- 必须先调用 `initShadowLog()` 再获取 PluginManager 实例
