# XLogInit — 日志初始化模块

## 模块概述

`xlog-init` 基于 XLog（Logger）提供应用的日志系统初始化和配置能力，支持日志文件写入、按大小滚动、自动上传等。

**模块坐标**: `com.hl.xloginit`
**发布 GroupId**: `io.github.heart-beats.baseproject`

## 目录结构

```
XLogInit/
├── build.gradle.kts
└── src/main/java/com/hl/xloginit/
    ├── XLogInitUtil.kt            # XLog 初始化入口
    ├── XLogUtil.kt                # XLog 工具（文件打印机）
    ├── XLogInitializer.kt         # AndroidX Startup 初始化器
    ├── XLogInitConfig.kt          # 初始化配置
    └── _XLog.kt                   # 全局日志函数（xlogV/D/W/I/E）
```

## 依赖关系

| 外部 | `com.elvishew:xlog` |
| 内部 | `mmkv-sp`（依赖） |

## 对外接口

```kotlin
object XLogInitUtil {
    fun init(config: XLogInitConfig.() -> Unit)
    fun initWithWriteFile(tagName: String, isPrintLog: Boolean, logFileMinUploadMB: Long,
                          onUploadFiles: (List<File>) -> Unit)
}

data class XLogInitConfig(
    var tagName: String = "XLog",
    var isPrintLog: Boolean = false,
    var filePrinter: FilePrinter? = null,
    var logConfiguration: LogConfiguration.Builder.() -> Unit = {}
)

// 全局日志函数
fun xlogV(tag: String, msg: String, tr: Throwable? = null)
fun xlogD(tag: String, msg: String, tr: Throwable? = null)
fun xlogI(tag: String, msg: String, tr: Throwable? = null)
fun xlogW(tag: String, msg: String, tr: Throwable? = null)
fun xlogE(tag: String, msg: String, tr: Throwable? = null)
```

## 构建与测试

```bash
./gradlew :xlog-init:assemble
./gradlew :xlog-init:publishToMavenLocal
```

## 使用示例

```kotlin
XLogInitUtil.init {
    tagName = "MyApp"
    isPrintLog = BuildConfig.DEBUG
    filePrinter = XLogUtil.getFilePrinter(
        logFolderPath = XLogUtil.defaultLogFolderPath,
        fileNameGenerator = DateFileNameGenerator()
    )
}
xlogD("Main", "页面加载完成")
xlogE("Network", "请求失败", exception)
```
