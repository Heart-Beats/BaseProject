# ImageLoad — 图片加载模块

## 模块概述

`image-load` 基于 Glide 进行封装，提供简化的图片加载 API，支持占位图、圆角、视频首帧、Bitmap 转换等。

**模块坐标**: `com.hl.imageload`
**发布 GroupId**: `io.github.heart-beats.baseproject`

## 目录结构

```
ImageLoad/
├── build.gradle.kts
└── src/main/java/com/hl/imageload/
    ├── GlideUtil.kt          # Glide 加载工具
    └── _ImageView.kt         # ImageView 扩展（视频首帧）
```

## 依赖关系

| 外部 | `com.github.bumptech.glide:glide` |

## 对外接口

```kotlin
object GlideUtil {
    @JvmStatic fun loadHead(ctx: Context, url: String?, iv: ImageView, isCircle: Boolean? = true)
    @JvmStatic fun load(ctx: Context, url: String?, iv: ImageView,
        @DrawableRes placeholderResId: Int = 0, roundPx: Int = 0,
        optionsBlock: RequestOptions.() -> Unit = {})
    @JvmStatic fun load(ctx: Context, drawableId: Int, iv: ImageView, roundPx: Int = 0, ...)
    @JvmStatic fun loadUrl2Bitmap(ctx: Context, url: String, callback: (Bitmap) -> Unit)
}
// 加载视频首帧封面
fun ImageView.loadFirstFrameCover(url: String?, errorResId: Int? = null, placeholderResId: Int? = null)
```

## 构建与测试

```bash
./gradlew :image-load:assemble
./gradlew :image-load:publishToMavenLocal
```

## 使用示例

```kotlin
// 基础加载
GlideUtil.load(context, "https://example.com/image.jpg", imageView)

// 圆形头像
GlideUtil.loadHead(context, avatarUrl, imageView, isCircle = true)

// 圆角 + 占位图
GlideUtil.load(context, url, imageView, placeholderResId = R.drawable.placeholder, roundPx = 8)

// URL 转 Bitmap
GlideUtil.loadUrl2Bitmap(context, url) { bitmap -> /* 使用 bitmap */ }
```
