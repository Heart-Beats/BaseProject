# Plugin-Manager

> **状态**: ⚠️ 仅作示例参考，当前项目未激活使用（inactive）

PluginManager 的动态实现模块。该模块为 **Android Application 类型**，构建产出 APK 文件（非 AAR），可实现 PluginManager 的运行时动态更新。

## Maven 坐标

| 属性 | 值 |
|------|-----|
| **GroupId** | `io.github.heart-beats.baseproject` |
| **ArtifactId** | `plugin-manager` |
| **当前版本** | `0.0.4-SNAPSHOT` |
| **产物类型** | APK（非 AAR） |

## 模块概览

本模块是 Shadow 框架 PluginManager 的完整实现，编译为独立 APK，通过 `shadow-init` 中的 `Shadow.initDynamicPluginManager()` 动态加载。包含插件的安装、解压、odex 优化、加载、Activity 启动及 Service 绑定等完整流程。

## 源码结构

```
plugin-manager/src/main/
├── AndroidManifest.xml                                  # package: com.hl.shadow.pluginmanager
└── java/
    ├── com/hl/shadow/pluginmanager/
    │   └── MyPluginManager.kt                           # PluginManager 核心实现（353 行）
    └── com/tencent/shadow/dynamic/impl/                   # Shadow 框架要求的固定包名
        ├── ManagerFactoryImpl.java                       # PluginManager 工厂（固定类名）
        └── WhiteList.java                                # 类加载器白名单（固定类名/接口）
```

## 核心类

### MyPluginManager

继承自 `PluginManagerThatUseDynamicLoader`，实现完整的插件加载流程：

```kotlin
open class MyPluginManager(context: Context) : PluginManagerThatUseDynamicLoader(context) {
    // 两个线程池
    private val installPluginExecutorService  // 单线程，安装插件
    private val mFixedPool                    // 4 线程，并发解压/odex/提取 SO

    override fun getName(): String = ShadowConstants.PLUGIN_MANAGER_NAME
    protected open fun getPluginProcessServiceName(): String = ppsName

    // 入口分发
    override fun enter(context: Context, fromId: Long, bundle: Bundle, callback: EnterCallback?)

    // fromId == FROM_ID_START_ACTIVITY (1001L) → launchPluginActivity()
    // fromId == FROM_ID_CALL_SERVICE   (1002L) → launchPluginService()
}
```

### installPlugin 流程

```
installPlugin(zipPath, hash, odex)
    │
    ├── installPluginFromZip(zipFile, hash)     # 解压 ZIP，获取 PluginConfig
    ├── 并发 odex 优化 (mFixedPool)
    │   ├── oDexPluginLoaderOrRunTime(runtime)   # Runtime odex
    │   └── oDexPluginLoaderOrRunTime(loader)    # Loader odex
    ├── 并发提取 SO (mFixedPool)
    │   └── extractSo(uuid, partKey, apkFile)    # 各 plugin partKey 的 SO 提取
    ├── 并发 odex Plugin (mFixedPool)
    │   └── oDexPlugin(uuid, partKey, apkFile)   # 各 plugin partKey 的 odex
    ├── onInstallCompleted(pluginConfig, soDirMap) # 写入数据库
    └── getInstalledPlugins(1)[0]                  # 返回最新安装的插件
```

### launchPluginService 流程

```
launchPluginService(zipPath, partKey, className)
    │
    ├── installPlugin()                                  # 安装插件
    ├── loadPlugin(uuid, partKey)                        # 加载
    ├── Class.forName("ServiceConnectionIml")            # 反射获取 ServiceConnection
    └── mPluginLoader.bindPluginService(pluginIntent,     # 绑定插件 Service
            PluginServiceConnection { ... })
```

### ManagerFactoryImpl — 固定入口类

```java
// 包名 com.tencent.shadow.dynamic.impl，类名 ManagerFactoryImpl 均为 Shadow 框架强制要求
public final class ManagerFactoryImpl implements ManagerFactory {
    @Override
    public PluginManagerImpl buildManager(Context context) {
        return new MyPluginManager(context);
    }
}
```

### WhiteList — 类加载器白名单

```java
// 包名 com.tencent.shadow.dynamic.impl，接口名 WhiteList 均为 Shadow 框架强制要求
public interface WhiteList {
    String[] sWhiteList = new String[] {
        "com.hl.shadow.dynamic.impl"
    };
}
```

## 构建特点

### APK 打包与自动复制

```groovy
applicationVariants.all { variant ->
    variant.outputs.all {
        def fileName = "My-PluginManager_${buildType}.apk"
        outputFileName = fileName
        // Debug 输出到 build/debug/，Release 输出到 build/release/
        def outputDirectory = new File(buildDir, buildType)
        // 打包完成后自动复制至 app/src/main/assets/plugins/
        it.doLast {
            copy {
                from "${outputDirectory}/My-PluginManager_${buildType}.apk"
                into "../../../../../app/src/main/assets/plugins"
            }
        }
    }
}
```

### 依赖说明

| 依赖 | 类型 | 说明 |
|------|------|------|
| `:shadow-lib` | implementation | 引入 `ShadowConstants` |
| `shadow.host` | api | Shadow 宿主 API |
| `shadow.activity_container` | api | Activity 容器 |
| `shadow.manager` | api | Manager 核心 |
| `shadow.common` | compileOnly | 通用库（编译时检查，不打包） |
| `shadow.loader` | compileOnly | Loader API（编译时检查，不打包） |
| `shadow.loader_impl` | compileOnly | Loader 实现（编译时检查，不打包） |
| `kotlin-stdlib` | implementation | Kotlin 标准库 |

## 与 shadow-init 的关系

```
宿主 App
    └── Shadow.initDynamicPluginManager("plugins/My-PluginManager_Release.apk")
            └── Shadow.getDynamicPluginManager()
                    └── MyPluginManager (此模块的产物)
                            └── 在此模块内实现完整的 install/load/start 流程
```

`plugin-manager` 产出独立 APK，由 `shadow-init` 侧的 `MyPluginManagerUpdater` 管理路径和版本，运行时通过 `Shadow.getDynamicPluginManager()` 获取其实例。这种设计使得 PluginManager 本身可以作为插件被动态更新，而无需重新发布宿主 App。

## 使用示例

```kotlin
// 1. 将 plugin-manager 打包的 APK 放入 assets/plugins/
// 2. 初始化动态 PluginManager
val pluginManagerPath = copyAssetToFile("plugins/My-PluginManager_Release.apk")
Shadow.initDynamicPluginManager(pluginManagerPath.absolutePath)

// 3. 获取并启动插件
val manager = Shadow.getDynamicPluginManager()
val bundle = Bundle().apply {
    putString(ShadowConstants.KEY_PLUGIN_ZIP_PATH, "/sdcard/my_plugin.zip")
    putString(ShadowConstants.KEY_CLASSNAME, "com.example.plugin.MainActivity")
}
manager?.enter(context, ShadowConstants.FROM_ID_START_ACTIVITY, bundle, callback)
```
