# shadow-lib

Shadow 插件框架的常量定义库，为 `shadow-init`、`plugin-manager` 及插件模块提供统一的标识符和配置常量。

## Maven 坐标

| 属性 | 值 |
|------|-----|
| **GroupId** | `io.github.heart-beats.baseproject` |
| **ArtifactId** | `shadow-lib` |
| **当前版本** | `0.0.4-SNAPSHOT` |

```groovy
implementation 'io.github.heart-beats.baseproject:shadow-lib:0.0.4-SNAPSHOT'
```

## 模块概览

`shadow-lib` 是一个轻量级 Android Library，仅包含 `ShadowConstants` 单例对象，是所有 Shadow 相关模块的公共依赖基座。它定义了一套标准化的 Key 和 Name 常量，用于：

- **PluginManager 标识**：区分不同 PluginManager 实例对应的数据存储路径
- **PluginProcessService (PPS) 类名注册**：宿主中注册的三种 PPS 实现类名
- **Intent 参数传递**：插件启动时通过 Bundle → Intent 传递参数的 Key 约定
- **入口路由分发**：通过 `fromId` 区分启动 Activity 还是绑定 Service

## 源码结构

```
shadow-lib/src/main/java/com/hl/shadow/lib/
└── ShadowConstants.kt           # 全局常量定义（单例 object）
```

## ShadowConstants 常量清单

### PluginManager 标识

| 常量 | 值 | 说明 |
|------|-----|------|
| `PLUGIN_MANAGER_NAME` | `"my-plugin-manager"` | PluginManager 别名，用于区分不同 PluginManager 的数据存储路径 |
| `PLUGIN_NAME_KEY` | `"PLUGIN_NAME_KEY"` | 多 Loader PPS 加载插件包时，区分不同插件的标识 |

### PluginProcessService 类名

| 常量 | 值 | 对应 PPS 实现 |
|------|-----|-------------|
| `MAIN_PLUGIN_PROCESS_SERVICE_NAME` | `"com.hl.shadow.pps.MainPluginProcessService"` | 单 Loader 基础 PPS（不可动态修改 UUID） |
| `MULTI_LOADER_PLUGIN_PROCESS_SERVICE_NAME` | `"com.hl.shadow.pps.MyMultiLoaderPluginProcessService"` | 多 Loader PPS（一个 PPS 加载多个插件包） |
| `DYNAMIC_UUID_PLUGIN_PROCESS_SERVICE_NAME` | `"com.hl.shadow.pps.DynamicUuidPluginProcessService"` | 支持动态 UUID 切换的自定义 PPS |

### Intent/Bundle 参数 Key

| 常量 | 说明 |
|------|------|
| `KEY_PLUGIN_ZIP_PATH` | 插件 apk/zip 文件路径 |
| `KEY_PLUGIN_PART_KEY` | partKey，区分插件中不同业务的加载入口 |
| `KEY_CLASSNAME` | 要启动的插件 Activity 或 Service 完整类名 |
| `KEY_EXTRAS` | 传递给插件的额外 Bundle 参数 |
| `KEY_FROM_ID` | 启动类型标识 |
| `KEY_PLUGIN_PROCESS_SERVICE_NAME_KEY` | 动态传递宿主中注册的 PPS 类名（实现 Manager 管理多 PPS） |
| `KEY_INTENT_ACTION` | 启动 Intent 的 action |

### 路由分发 fromId

| 常量 | 值 | 说明 |
|------|-----|------|
| `FROM_ID_START_ACTIVITY` | `1001L` | 启动插件 Activity |
| `FROM_ID_CALL_SERVICE` | `1002L` | 绑定插件 Service |

### 其他

| 常量 | 说明 |
|------|------|
| `ABI` | 插件 SO 的 ABI 架构（当前为空字符串） |

## 使用示例

```kotlin
// 在 shadow-init 模块中引用
val bundle = Bundle().apply {
    putString(ShadowConstants.KEY_PLUGIN_ZIP_PATH, "/data/plugin.zip")
    putString(ShadowConstants.KEY_CLASSNAME, "com.example.plugin.MainActivity")
    putString(ShadowConstants.KEY_PLUGIN_PART_KEY, "main")
}

// 通过 PluginManager 进入插件
shadowPluginManager.enter(context, ShadowConstants.FROM_ID_START_ACTIVITY, bundle, callback)
```

## 依赖关系

```
shadow-lib (此模块)
    ↑ (api)
shadow-init          # 依赖所有常量
plugin-manager       # 依赖所有常量
```

`shadow-lib` 无外部依赖，仅依赖 Kotlin 标准库。
