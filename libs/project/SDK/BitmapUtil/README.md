# BitmapUtil — 位图工具模块

## 模块概述

`bitmap-util` 提供 Bitmap 与 Base64 互转、View 截图、从 URL 获取 Bitmap、图片保存到相册/文件等功能。

**模块坐标**: `com.hl.bitmaputil`
**发布 GroupId**: `io.github.heart-beats.baseproject`

## 依赖关系

| 外部 | `com.blankj:utilcodex` |
| 内部 | `permission`, `mime-type` |

## 对外接口

```kotlin
object BitmapUtil {
    suspend fun getBitmapFromUrl(url: String): Bitmap?
    fun saveBitmap(ctx: Context, bitmap: Bitmap, saveName: String, ...)
}
object Base64BitmapUtil {
    fun bitmapToBase64(bitmap: Bitmap?): String?
    fun base64ToBitmap(base64Str: String?): Bitmap
}
fun View.toBitmap(): Bitmap?
```

## 构建与测试

```bash
./gradlew :bitmap-util:assemble
```

## 使用示例

```kotlin
// URL → Bitmap
val bitmap = BitmapUtil.getBitmapFromUrl("https://example.com/img.jpg")

// Bitmap → Base64
val base64 = Base64BitmapUtil.bitmapToBase64(bitmap)

// View 截图
val screenshot = someView.toBitmap()
```
