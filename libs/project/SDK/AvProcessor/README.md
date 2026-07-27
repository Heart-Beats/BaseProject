# AvProcessor — 音视频处理模块

## 模块概述

`avprocessor` 基于 Android 原生 MediaCodec API 实现音视频处理，零外部依赖（ffmpeg 等），提供音频格式转换与视频压缩能力。

**模块坐标**: `com.hl.avprocessor`
**发布 GroupId**: `io.github.heart-beats.baseproject`

## 依赖关系

| 外部 | `io.github.heart-beats:video-compressor:0.0.2`（compileOnly，可选降级） |
|------|----------------------------------------------------------------------|
| 内部 | `kotlinx.coroutines.android` |

## 功能概览

```
AvProcessor/
├── audio/
│   └── AudioConverter        # 音频格式转换（WMA/WAV → M4A/AAC-LC）
└── video/
    ├── VideoCompressor       # 视频压缩（自研，纯 MediaCodec 硬件编码）
    └── VideoFileCompressEngine  # 视频压缩引擎（动态选择第三方库或自研实现）
```

## 对外接口

### AudioConverter — 音频格式转换

```kotlin
object AudioConverter {
    /** WMA → M4A (AAC-LC) */
    fun convertWmaToM4a(inputFile: String, outputFile: String, callback: (Boolean) -> Unit)

    /** WAV → M4A (AAC-LC) */
    fun convertWavToM4a(inputFile: String, outputFile: String, callback: (Boolean) -> Unit)
}
```

### VideoFileCompressEngine — 视频压缩引擎

```kotlin
object VideoFileCompressEngine {
    /**
     * 统一压缩入口，自动选择最优实现
     * @return 压缩后文件路径，失败返回 null
     */
    suspend fun compress(context: Context, srcPath: String): String?
}
```

### VideoCompressor — 自研视频压缩

```kotlin
class VideoCompressor {
    data class CompressSettings(
        val outputWidth: Int = 720,
        val outputHeight: Int = 1080,
        val bitrate: Int = 0  // 0 = 自动根据分辨率计算
    )

    sealed class CompressResult {
        data class Success(val outputFile: File) : CompressResult()
        data class Error(val message: String, val cause: Throwable? = null) : CompressResult()
        object Cancelled : CompressResult()
    }

    suspend fun compress(inputFile: File, outputFile: File, settings: CompressSettings): CompressResult
}
```

## 构建与测试

```bash
./gradlew :avprocessor:assemble
```

## 使用示例

### 音频格式转换

```kotlin
// WMA 转 M4A
AudioConverter.convertWmaToM4a(inputPath, outputPath) { success ->
    if (success) {
        Log.d("Audio", "转码成功: $outputPath")
    } else {
        Log.e("Audio", "转码失败")
    }
}

// WAV 转 M4A
AudioConverter.convertWavToM4a(wavPath, m4aPath) { success ->
    // 回调运行在主线程
}
```

### 视频压缩（推荐，自动选择实现）

```kotlin
viewModelScope.launch {
    val outputPath = VideoFileCompressEngine.compress(context, srcPath)
    if (outputPath != null) {
        Log.d("Video", "压缩成功: $outputPath")
    } else {
        Log.e("Video", "压缩失败或体积未减小")
    }
}
```

### 视频压缩（直接使用自研实现）

```kotlin
viewModelScope.launch {
    val compressor = VideoCompressor()
    val result = compressor.compress(
        inputFile = File(srcPath),
        outputFile = File(outputPath),
        settings = VideoCompressor.CompressSettings(
            outputWidth = 720,
            outputHeight = 1080,
            bitrate = 0  // 自动计算
        )
    )
    when (result) {
        is VideoCompressor.CompressResult.Success -> Log.d("Video", "压缩成功")
        is VideoCompressor.CompressResult.Error -> Log.e("Video", "压缩失败: ${result.message}")
        VideoCompressor.CompressResult.Cancelled -> Log.d("Video", "压缩已取消")
    }
}
```

## 实现说明

### AudioConverter

- 基于 MediaCodec **异步回调模式**（非轮询），解码器与编码器可真正并行工作
- 支持 WMA、WAV 等格式解码后统一编码为 AAC-LC（M4A 容器）
- 自动根据采样率和通道数选择码率（48/64/96/128 kbps）
- 内置 60s 超时保护，失败自动清理临时文件
- 结果回调自动派发至主线程，可直接更新 UI

### VideoFileCompressEngine

- **动态策略选择**：已集成 `io.github.heart-beats:video-compressor` 时使用第三方库，否则自动降级为自研 `VideoCompressor`
- 分辨率策略：源宽高各取一半，任一边 < 128px 时等比兜底
- 兜底保护：输出体积未减小时自动放弃并清理

### VideoCompressor（自研）

- 纯 MediaCodec 硬件编码（H.264），零外部依赖
- 转码管线：MediaExtractor → 解码 → YUV 缩放 → H.264 编码 → MediaMuxer 封装 MP4
- 音频轨道直接透传，不做重编码
- 支持旋转角度保留、色彩元数据保留（HDR/广色域不失真）
- YUV 缩放：Y 平面双线性插值（定点整数）+ UV 平面最近邻
- 支持协程取消（`isActive` 检测），可中途取消
