# Plugin-AIDL

> **状态**: ⚠️ 仅作示例参考，当前项目未激活使用（inactive）

定义宿主与插件之间跨进程通信的 AIDL 接口及数据传输对象（Parcelable）。本模块展示了如何在 Shadow 插件化架构中实现宿主与插件之间的双向 IPC 通信。

## Maven 坐标

| 属性 | 值 |
|------|-----|
| **GroupId** | `io.github.heart-beats.baseproject` |
| **ArtifactId** | `plugin-aidl` |
| **当前版本** | `0.0.4-SNAPSHOT` |

```groovy
implementation 'io.github.heart-beats.baseproject:plugin-aidl:0.0.4-SNAPSHOT'
```

## 源码结构

```
plugin-aidl/src/main/
├── AndroidManifest.xml                         # package: com.hl.pluginaidl
└── aidl/com/hl/pluginaidl/
    ├── PluginAidlInterface.aidl                 # 插件暴露给宿主的接口
    ├── PluginAidlListener.aidl                  # 宿主回调给插件的监听器
    └── Person.kt                                # Parcelable 数据传输对象
```

## AIDL 接口定义

### PluginAidlInterface — 插件侧接口

宿主通过 Binder 调用插件提供的能力：

```java
interface PluginAidlInterface {
    // 打开插件中的指定 Activity
    void openActivity(String activityClassName, in Bundle extras);

    // 设置宿主回调监听器（回调接口也必须是 AIDL 生成）
    void setPluginListener(PluginAidlListener listener);
}
```

### PluginAidlListener — 宿主回调接口

插件通过此接口回调宿主：

```java
interface PluginAidlListener {
    // 回调打开结果
    void onOpenActivity(boolean result, String openActivityName);

    // 跨进程传递 Parcelable 对象（演示 in/out/inout 定向 tag）
    /*
        in：  数据从客户端流向服务端，服务端修改不回传
        out： 数据从服务端流向客户端，服务端修改会同步
        inout：数据双向流通，双方共享同一对象
    */
    void onTransPerson(in Person person);
}
```

### Person — Parcelable 数据类

演示自定义对象跨进程传输：

```kotlin
@Parcelize
data class Person(
    var name: String? = null,
    var age: Int? = null,
    var sex: String? = null
) : Parcelable
```

## AIDL 定向 Tag 说明

| Tag | 数据流向 | 行为 |
|-----|---------|------|
| `in` | 客户端 → 服务端 | 服务端收到的对象独立拷贝，服务端修改不同步到客户端 |
| `out` | 服务端 → 客户端 | 服务端创建新空对象，服务端填充后同步到客户端 |
| `inout` | 双向 | 服务端和客户端共享同一对象，任一端修改都同步到对端 |

## 构建配置

`build.gradle` 通过 `sourceSets` 将 AIDL 目录纳入源码路径：

```groovy
sourceSets {
    main {
        java.srcDirs += ['src/main/aidl']  // AIDL 中的 Kotlin 文件可被识别
    }
}
```

模块启用了 `kotlin-parcelize` 插件以支持 `@Parcelize` 注解。

## 使用示例

```kotlin
// === 宿主侧 ===
// 通过 Binder 调用插件接口
val pluginInterface = PluginAidlInterface.Stub.asInterface(serviceBinder)
pluginInterface.openActivity("com.plugin.DetailActivity", Bundle().apply {
    putString("id", "123")
})

// 设置回调
pluginInterface.setPluginListener(object : PluginAidlListener.Stub() {
    override fun onOpenActivity(result: Boolean, name: String) {
        Log.d("Host", "open $name: $result")
    }
    override fun onTransPerson(person: Person) {
        Log.d("Host", "received: ${person.name}")
    }
})
```

## 依赖

| 依赖 | 说明 |
|------|------|
| `kotlin-stdlib` | Kotlin 标准库 |

无其他外部依赖，无 Shadow 依赖。

## 与 Shadow 框架的关系

此模块独立于 Shadow 核心框架运行，仅依赖 Kotlin 标准库。它演示了 Shadow 插件架构中的一种可选通信范式：宿主加载插件后，通过 AIDL 定义的接口进行双向调用，实现数据交换和逻辑协作。实际项目中可根据业务需求在 AIDL 文件中扩展自定义接口方法。
