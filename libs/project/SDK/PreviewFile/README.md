# PreviewFile — 文件预览模块

## 模块概述

`preview-file` 基于腾讯 X5 WebView 内核，提供常见办公文档（PDF/Word/PPT/Excel）、图片、视频等文件的在线预览能力。

**模块坐标**: `com.hl.previewfile`
**发布 GroupId**: `io.github.heart-beats.baseproject`

## 依赖关系

| 外部 | `com.tencent.tbs:tbssdk`（腾讯 X5 内核） |
| 内部 | `base-ui`, `image-load`, `download`, `share`, `popup`, `video-player`, `mime-type`, `permission`, `xlog-init` |

## 对外接口

```kotlin
class PreviewFileActivity : Activity() {
    companion object {
        @JvmStatic fun start(context: Context, fileName: String, url: String?)
    }
}

object X5Helper {
    fun initX5(context: Context, isPrintLog: Boolean = true)
}

class DocView(context: Context) : View(context) {
    var openFailedAction: (() -> Unit)?
    fun displayFile(file: File)
    fun onStopDisplay()
}
```

## 构建与测试

```bash
./gradlew :preview-file:assemble
```

## 使用示例

```kotlin
// 初始化 X5 内核（在 Application 中）
X5Helper.initX5(context, isPrintLog = BuildConfig.DEBUG)

// 预览文件
PreviewFileActivity.start(context, fileName = "report.docx", url = "https://example.com/report.docx")
```
