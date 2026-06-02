# Update — 应用更新模块

## 模块概述

`update` 封装了应用内下载 APK 并安装的功能，支持进度监听、自定义 UI 和 MD5 校验。

**模块坐标**: `com.hl.update`
**发布 GroupId**: `io.github.heart-beats.baseproject`

## 依赖关系

| 外部 | `com.teprinciple:updateapputilsx` |

## 对外接口

```kotlin
object UpdateHelper {
    @JvmOverloads fun downloadApk(
        context: Context, apkUrl: String, title: String = "发现新版本",
        content: String = "请及时更新", uiConfig: UiConfig.() -> Unit = {},
        updateConfig: UpdateConfig.() -> Unit = {},
        cancelClickListener: DialogInterface.OnClickListener? = null,
        updateClickListener: DialogInterface.OnClickListener? = null,
        observer: DownloadApkObserver? = null
    )
}

interface DownloadApkObserver {
    fun onMd5CheckResult(result: Boolean) {}
    fun onStart() {}
    fun onDownload(progress: Int) {}
    fun onFinish() {}
    fun onError(e: Throwable) {}
}
```

## 构建与测试

```bash
./gradlew :update:assemble
```

## 使用示例

```kotlin
UpdateHelper.downloadApk(
    context = this,
    apkUrl = "https://example.com/app-v2.0.apk",
    title = "发现新版本 v2.0",
    content = "修复了已知问题，优化性能",
    uiConfig = { /* UI 配置 */ },
    updateConfig = { isShowNotification = true },
    observer = object : DownloadApkObserver {
        override fun onDownload(progress: Int) { updateProgress(progress) }
        override fun onFinish() { toast("下载完成，开始安装") }
    }
)
```
