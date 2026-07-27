package com.hl.avprocessor.video

import android.content.Context
import android.media.MediaMetadataRetriever
import android.os.FileUtils
import android.util.Log
import com.primaverahq.videocompressor.CompressionResult
import com.primaverahq.videocompressor.VideoCompressor
import com.primaverahq.videocompressor.settings.CompressionSettings
import com.primaverahq.videocompressor.settings.EncoderSelectionMode
import java.io.File
import com.hl.avprocessor.video.VideoCompressor as NativeVideoCompressor

/**
 * 视频压缩引擎 — 动态选择压缩实现
 *
 * - 已集成 io.github.heart-beats:video-compressor 时，使用第三方库 compressVideo
 * - 未集成时，自动降级为自研 NativeVideoCompressor（纯 MediaCodec，零外部依赖）
 *
 * 调用方式：
 *   val result = VideoFileCompressEngine.compress(context, srcPath)
 */
object VideoFileCompressEngine {

    private const val TAG = "VideoFileCompressEngine"
    private const val MIN_EDGE = 128

    // 第三方库是否可用（一次性检测，结果缓存）
    private val hasThirdPartyLibrary: Boolean by lazy {
        try {
            Class.forName("com.primaverahq.videocompressor.VideoCompressor")
            true
        } catch (_: ClassNotFoundException) {
            false
        }
    }

    /**
     * 统一压缩入口 — 自动选择最优实现
     */
    suspend fun compress(context: Context, srcPath: String): String? {
        return if (hasThirdPartyLibrary) {
            Log.d(TAG, "使用第三方库 compressVideo")
            compressVideo(context, srcPath)
        } else {
            Log.d(TAG, "使用自研 compressVideoNative")
            compressVideoNative(context, srcPath)
        }
    }

    // ==================== 第三方库实现 ====================

    /**
     * 视频压缩 — pachca/VideoCompressor 库 (io.github.heart-beats:video-compressor:0.0.2)
     *
     * 分辨率策略：源宽高各取一半，任一边 < MIN_EDGE(128) 时以较小边为基准等比放大。
     * 码率策略：不超过源视频码率，上限 2 Mbps。
     * 兜底保护：输出不大于源文件，失败时清理临时文件。
     *
     */
    private suspend fun compressVideo(context: Context, srcPath: String): String? {
        val outputPath = getOutputPath(context, srcPath)

        return try {
            val inputFile = File(srcPath)
            val outputFile = File(outputPath)

            val len = FileUtils.getLength(srcPath)
            val sourceBitrate = getVideoBitrate(srcPath)
            val targetBitrate = if (sourceBitrate > 0) minOf(2_000_000, sourceBitrate) else 2_000_000
            Log.d(TAG, "compressVideo[第三方] 压缩前: $len bytes, 源码率: $sourceBitrate")

            val (srcW, srcH) = getVideoResolution(srcPath)
            val (outputW, outputH) = calculateTargetSize(srcW, srcH)
            Log.d(TAG, "compressVideo[第三方] 源: ${srcW}x${srcH} -> 目标: ${outputW}x${outputH}")

            val result = VideoCompressor.compress(
                context = context,
                input = inputFile,
                output = outputFile,
                onMetadataDecoded = { _, _ ->
                    CompressionSettings.Builder()
                        .setTargetSize(width = outputW, height = outputH)
                        .setBitrate(targetBitrate)
                        .setStreamable(true)
                        .allowSizeAdjustments(true)
                        .setEncoderSelectionMode(EncoderSelectionMode.TRY_ALL)
                        .build()
                }
            )

            when (result) {
                is CompressionResult.Success -> {
                    val compressedLen = outputFile.length()
                    if (outputFile.exists() && compressedLen > 0 && compressedLen < len) {
                        Log.d(TAG, "compressVideo[第三方] 成功, 压缩后: $compressedLen bytes")
                        outputPath
                    } else {
                        outputFile.delete()
                        Log.d(TAG, "compressVideo[第三方] 体积未减小, 已放弃")
                        null
                    }
                }
                is CompressionResult.Cancelled -> {
                    outputFile.delete()
                    Log.d(TAG, "compressVideo[第三方] 取消")
                    null
                }
                is CompressionResult.Error -> {
                    outputFile.delete()
                    val causeMsg = result.error.cause?.message?.let { ", cause: $it" } ?: ""
                    Log.e(TAG, "compressVideo[第三方] 错误: ${result.error.message}$causeMsg")
                    result.error.printStackTrace()
                    null
                }
            }
        } catch (e: Exception) {
            try { File(outputPath).delete() } catch (_: Exception) {}
            Log.e(TAG, "compressVideo[第三方] 异常: ${e.message}")
            e.printStackTrace()
            null
        }
    }

    // ==================== 自研实现 ====================

    /**
     * 视频压缩 — 使用自研 NativeVideoCompressor（零外部依赖，纯 MediaCodec 实现）
     *
     * 分辨率策略：与 compressVideo 一致，源宽高各取一半，任一边 < MIN_EDGE 时等比兜底
     * 码率策略：0 = 自动根据目标分辨率计算（NativeVideoCompressor 内置公式）
     * 兜底保护：输出不大于源文件时放弃，失败时清理临时文件
     */
    private suspend fun compressVideoNative(context: Context, srcPath: String): String? {
        val inputFile = File(srcPath)
        val outputFile = File(getOutputPath(context, srcPath))

        return try {
            val len = FileUtils.getLength(srcPath)
            val sourceBitrate = getVideoBitrate(srcPath)
            Log.d(TAG, "compressVideoNative 压缩前: $len bytes, 源码率: $sourceBitrate")

            val (srcW, srcH) = getVideoResolution(srcPath)
            val (outputW, outputH) = calculateTargetSize(srcW, srcH)
            Log.d(TAG, "compressVideoNative 源: ${srcW}x${srcH} -> 目标: ${outputW}x${outputH}")

            val result = NativeVideoCompressor().compress(
                inputFile = inputFile,
                outputFile = outputFile,
                settings = NativeVideoCompressor.CompressSettings(
                    outputWidth = outputW,
                    outputHeight = outputH,
                    bitrate = 0 // 自动计算
                )
            )

            when (result) {
                is NativeVideoCompressor.CompressResult.Success -> {
                    val compressedLen = outputFile.length()
                    if (compressedLen > 0 && compressedLen < len) {
                        Log.d(TAG, "compressVideoNative 成功, 压缩后: $compressedLen bytes")
                        outputFile.absolutePath
                    } else {
                        outputFile.delete()
                        Log.d(TAG, "compressVideoNative 体积未减小, 已放弃")
                        null
                    }
                }
                is NativeVideoCompressor.CompressResult.Cancelled -> {
                    outputFile.delete()
                    Log.d(TAG, "compressVideoNative 取消")
                    null
                }
                is NativeVideoCompressor.CompressResult.Error -> {
                    outputFile.delete()
                    Log.d(TAG, "compressVideoNative 错误: ${result.message}")
                    result.cause?.printStackTrace()
                    null
                }
            }
        } catch (e: Exception) {
            try { outputFile.delete() } catch (_: Exception) {}
            Log.d(TAG, "compressVideoNative 异常: ${e.message}")
            e.printStackTrace()
            null
        }
    }

    // ==================== 工具方法 ====================

    private fun getOutputPath(context: Context, srcPath: String): String {
        val fileName = FileUtil.getFileName(srcPath)
        val cacheDir = context.externalCacheDir ?: return ""
        val dir = File(cacheDir, "video_disk_cache")
        if (!dir.exists()) dir.mkdirs()
        return File(dir, fileName).absolutePath
    }

    private fun getVideoResolution(srcPath: String): Pair<Int, Int> {
        val retriever = MediaMetadataRetriever()
        return try {
            retriever.setDataSource(srcPath)
            val w = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH)?.toIntOrNull() ?: 0
            val h = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT)?.toIntOrNull() ?: 0
            Pair(w, h)
        } finally {
            retriever.release()
        }
    }

    private fun getVideoBitrate(srcPath: String): Int {
        val retriever = MediaMetadataRetriever()
        return try {
            retriever.setDataSource(srcPath)
            retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_BITRATE)?.toIntOrNull() ?: 0
        } catch (e: Exception) { 0 }
        finally {
            try { retriever.release() } catch (_: Exception) {}
        }
    }

    /**
     * 分辨率策略：源宽高各取一半，任一边 < MIN_EDGE 时等比兜底
     *
     *   ┌───────────┬───────────┬────────┬────────────┬───────────────┬─────────┬─────────────────┐
     *   │  源视频    │ raw（/2） │ 较小边 │   触发？   │     scale     │  输出   │     宽高比      │
     *   ├───────────┼───────────┼────────┼────────────┼───────────────┼─────────┼─────────────────┤
     *   │ 592×1280  │ 296×640   │ 296    │ 否（≥128） │ —             │ 296×640 │ 0.463 ≈ 0.463 ✓ │
     *   ├───────────┼───────────┼────────┼────────────┼───────────────┼─────────┼─────────────────┤
     *   │ 1920×1080 │ 960×540   │ 540    │ 否（≥128） │ —             │ 960×540 │ 1.778 ≈ 1.778 ✓ │
     *   ├───────────┼───────────┼────────┼────────────┼───────────────┼─────────┼─────────────────┤
     *   │ 320×240   │ 160×120   │ 120    │ 是（<128） │ 128/120=1.067 │ 171×128 │ 1.336 ≈ 1.333 ✓ │
     *   ├───────────┼───────────┼────────┼────────────┼───────────────┼─────────┼─────────────────┤
     *   │ 240×160   │ 120×80    │ 80     │ 是（<128） │ 128/80=1.6    │ 192×128 │ 1.500 = 1.500 ✓ │
     *   ├───────────┼───────────┼────────┼────────────┼───────────────┼─────────┼─────────────────┤
     *   │ 128×128   │ 64×64     │ 64     │ 是（<128） │ 128/64=2.0    │ 128×128 │ 1.000 = 1.000 ✓ │
     *   └───────────┴───────────┴────────┴────────────┴───────────────┴─────────┴─────────────────┘
     *
     */
    private fun calculateTargetSize(srcW: Int, srcH: Int): Pair<Int, Int> {
        if (srcW <= 0 || srcH <= 0) return Pair(0, 0)
        val rawW = srcW / 2
        val rawH = srcH / 2
        return if (rawW < MIN_EDGE || rawH < MIN_EDGE) {
            val smaller = minOf(rawW, rawH)
            val scale = MIN_EDGE.toDouble() / smaller
            Pair((rawW * scale).toInt(), (rawH * scale).toInt())
        } else {
            Pair(rawW, rawH)
        }
    }
}
