# Share — 分享模块

## 模块概述

`share` 提供系统分享功能的封装，支持分享文本、文件以及打开文件的系统选单。

**模块坐标**: `com.hl.share`
**发布 GroupId**: `io.github.heart-beats.baseproject`

## 依赖关系

| 外部 | `androidx.core:core-ktx` |
| 内部 | `mime-type`, `uikit-toast` |

## 对外接口

```kotlin
object ShareUtil {
    fun shareText(context: Context, text: String)
    fun shareFileWithText(context: Context, text: String, vararg filePaths: String)
    fun shareFile(context: Context, vararg filePaths: String)
}

object OpenFileUtil {
    fun openFileShare(context: Context, path: String)   // 弹出"用其他应用打开"菜单
    fun openFileByPath(context: Context, path: String)   // 根据 MimeType 自动打开
}
```

## 构建与测试

```bash
./gradlew :share:assemble
```

## 使用示例

```kotlin
ShareUtil.shareText(this, "分享一段文字")
ShareUtil.shareFile(this, "/sdcard/download/report.pdf")
OpenFileUtil.openFileByPath(this, "/sdcard/download/image.jpg")
```
