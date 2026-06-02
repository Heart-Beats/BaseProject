# Download — 文件下载模块

## 模块概述

`download` 基于 EasyHttp 封装文件下载功能，支持进度监听、生命周期绑定和 MD5 缓存跳过。

**模块坐标**: `com.hl.download`
**发布 GroupId**: `io.github.heart-beats.baseproject`

## 依赖关系

| 外部 | `com.squareup.okhttp3:okhttp`, `com.hjq.http:easy-http` |
| 内部 | `xlog-init`, `permission`, `uikit-toast` |

## 对外接口

```kotlin
object DownloadFileUtil {
    fun startDownLoad(lifecycleOwner: LifecycleOwner, fileUrl: String,
        fileName: String? = null, isSave2AppDir: Boolean = false,
        md5: String? = null, listener: OnDownloadListener)

    fun startDownLoad(lifecycleOwner: LifecycleOwner, fileUrl: String,
        savePath: String, md5: String? = null, listener: OnDownloadListener)

    fun stopDownload(fileUrl: String)
    fun stopAllDownload()
}
```

## 构建与测试

```bash
./gradlew :download:assemble
```

## 使用示例

```kotlin
DownloadFileUtil.startDownLoad(viewLifecycleOwner, "https://example.com/file.pdf",
    fileName = "report.pdf", isSave2AppDir = true,
    listener = object : OnDownloadListener {
        override fun onDownloadProgressChange(file: File?, progress: Int) { /* 0-100 */ }
        override fun onDownloadSuccess(file: File?) { toast("下载完成") }
        override fun onDownloadFail(file: File?, e: Exception?) { toast("下载失败") }
    })
```
