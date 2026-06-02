# MimeType — 文件类型识别模块

## 模块概述

`mime-type` 提供文件扩展名与 MIME 类型的双向映射，支持判断是否为图片/视频/音频/文档/压缩包等。

**模块坐标**: `com.hl.mimetype`
**发布 GroupId**: `io.github.heart-beats.baseproject`

## 依赖关系

无外部依赖（纯 Kotlin 枚举）。

## 对外接口

```kotlin
enum class MimeType {
    JPEG, PNG, GIF, BMP, WEBP,
    MP4, MPEG, QUICKTIME, AVI, WMV, FLV, MKV,
    MP3, WAV, M4A,
    PDF, WORD, PPT, PPTX, TXT, JSON, HTML,
    APK, ZIP, RAR, TAR, GZ,
    // ... 50+ 类型

    fun isImage(): Boolean
    fun isVideo(): Boolean
    fun isAudio(): Boolean
    fun isGif(): Boolean
    fun isDocument(): Boolean
    fun isArchiveFiles(): Boolean

    companion object {
        fun ofImage(onlyGif: Boolean? = null): List<MimeType>
        fun ofVideo(): List<MimeType>
        fun getByExtension(ext: String): MimeType
    }
}
```

## 构建与测试

```bash
./gradlew :mime-type:assemble
```

## 使用示例

```kotlin
val type = MimeType.getByExtension(".apk")  // MimeType.APK
MimeType.JPEG.isImage()                     // true
MimeType.MP4.isVideo()                      // true
```
