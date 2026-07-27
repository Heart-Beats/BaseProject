package com.hl.avprocessor.video

import android.media.MediaCodec
import android.media.MediaCodecInfo
import android.media.MediaCodecList
import android.media.MediaExtractor
import android.media.MediaFormat
import android.media.MediaMuxer
import android.os.Build
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.isActive
import kotlinx.coroutines.withContext
import java.io.File
import java.nio.ByteBuffer

/**
 * 视频压缩工具 - 基于 Android 原生 MediaCodec 硬件编码
 * 零外部依赖
 *
 * 转码管线：MediaExtractor -> MediaCodec解码 -> YUV缩放 -> MediaCodec H.264编码 -> MediaMuxer封装MP4
 * 音频轨道直接透传，不做重编码
 *
 * @since 2026/7/13
 */
class VideoCompressor {

    companion object {
        private const val TAG = "VideoCompressor"
        private const val TIMEOUT_US = 10_000L
        private const val DEFAULT_FRAME_RATE = 30
        private const val I_FRAME_INTERVAL = 2
        private const val ALIGNMENT = 16
        private const val BYTES_PER_PIXEL = 1.5
    }

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

    suspend fun compress(
        inputFile: File,
        outputFile: File,
        settings: CompressSettings
    ): CompressResult = withContext(Dispatchers.IO) {
        var extractor: MediaExtractor? = null
        var decoder: MediaCodec? = null
        var encoder: MediaCodec? = null
        var muxer: MediaMuxer? = null

        try {
            if (!inputFile.exists()) {
                return@withContext CompressResult.Error("Input file not found: ${inputFile.absolutePath}")
            }

            val outputDir = outputFile.parentFile
            if (outputDir != null && !outputDir.exists()) outputDir.mkdirs()

            extractor = MediaExtractor().apply { setDataSource(inputFile.absolutePath) }
            val videoTrackIndex = findTrackByType(extractor, "video/")
            if (videoTrackIndex == -1) return@withContext CompressResult.Error("No video track found")

            val audioTrackIndices = findTracksByType(extractor, "audio/")

            extractor.selectTrack(videoTrackIndex)
            // 音频轨道不在这里 select，转码完成后通过 copyAudioTracks 单独处理

            val inputFormat = extractor.getTrackFormat(videoTrackIndex)
            val srcWidth = inputFormat.getInteger(MediaFormat.KEY_WIDTH)
            val srcHeight = inputFormat.getInteger(MediaFormat.KEY_HEIGHT)

            // 读取源视频旋转信息，压缩后保留原始方向
            val rotation = if (inputFormat.containsKey(MediaFormat.KEY_ROTATION)) {
                inputFormat.getInteger(MediaFormat.KEY_ROTATION)
            } else 0
            if (rotation != 0) {
                Log.d(TAG,  "VideoCompressor source rotation: $rotation")
            }

            // 读取源视频帧率，保留原始帧率避免变速
            val srcFrameRate = if (inputFormat.containsKey(MediaFormat.KEY_FRAME_RATE)) {
                inputFormat.getInteger(MediaFormat.KEY_FRAME_RATE)
            } else DEFAULT_FRAME_RATE

            // 输出分辨率对齐到 16
            val alignedW = (settings.outputWidth / ALIGNMENT) * ALIGNMENT
            val alignedH = (settings.outputHeight / ALIGNMENT) * ALIGNMENT
            val actualOutputW = if (alignedW > 0) alignedW else ALIGNMENT
            val actualOutputH = if (alignedH > 0) alignedH else ALIGNMENT

            // 自动计算码率：目标像素数 * 每像素字节数 * 帧率 * 压缩系数
            val effectiveBitrate = if (settings.bitrate > 0) {
                settings.bitrate
            } else {
                val pixelCount = actualOutputW * actualOutputH
                val rawBytesPerFrame = (pixelCount * BYTES_PER_PIXEL).toLong()
                val targetBytesPerFrame = (rawBytesPerFrame * 0.08).toLong() // ~12.5:1 压缩比
                (targetBytesPerFrame * DEFAULT_FRAME_RATE).toInt().coerceIn(500_000, 8_000_000)
            }

            Log.d(TAG, "VideoCompressor source: ${srcWidth}x${srcHeight} -> target: ${actualOutputW}x${actualOutputH}, bitrate: $effectiveBitrate, audio tracks: ${audioTrackIndices.size}")

            // 解码器
            val decoderMime = inputFormat.getString(MediaFormat.KEY_MIME) ?: MediaFormat.MIMETYPE_VIDEO_AVC
            decoder = MediaCodec.createDecoderByType(decoderMime)
            decoder.configure(inputFormat, null, null, 0)
            decoder.start()

            // 编码器：使用硬件 H.264 编码
            val encoderFormat = MediaFormat.createVideoFormat(
                MediaFormat.MIMETYPE_VIDEO_AVC,
                actualOutputW,
                actualOutputH
            ).apply {
                setInteger(MediaFormat.KEY_BIT_RATE, effectiveBitrate)
                setInteger(MediaFormat.KEY_FRAME_RATE, srcFrameRate)
                setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, I_FRAME_INTERVAL)
                setInteger(MediaFormat.KEY_COLOR_FORMAT, MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420Flexible)
                // 保留源视频色彩属性（HDR/广色域内容色彩不失真）
                copyColorMetadata(inputFormat, this)
            }

            val encoderName = findEncoderForType(MediaFormat.MIMETYPE_VIDEO_AVC)
                ?: return@withContext CompressResult.Error("No H.264 encoder available")
            encoder = MediaCodec.createByCodecName(encoderName)
            encoder.configure(encoderFormat, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE)
            encoder.start()

            muxer = MediaMuxer(outputFile.absolutePath, MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4)
            if (rotation != 0) {
                muxer.setOrientationHint(rotation)
            }

            // 转码（含音频透传）
            val transcodeResult = transcode(
                extractor, decoder, encoder, muxer,
                srcWidth, srcHeight, actualOutputW, actualOutputH,
                videoTrackIndex, audioTrackIndices
            )
            if (transcodeResult != null) return@withContext transcodeResult

            if (!isActive) return@withContext CompressResult.Cancelled
            if (!outputFile.exists() || outputFile.length() == 0L) {
                return@withContext CompressResult.Error("Output file is empty")
            }

            CompressResult.Success(outputFile)

        } catch (e: kotlinx.coroutines.CancellationException) {
            throw e
        } catch (e: Exception) {
            Log.d(TAG, "VideoCompressor exception: ${e.message}")
            CompressResult.Error("Compression failed: ${e.message}", e)
        } finally {
            try { muxer?.stop() } catch (_: Exception) {}
            try { muxer?.release() } catch (_: Exception) {}
            try { encoder?.stop() } catch (_: Exception) {}
            try { encoder?.release() } catch (_: Exception) {}
            try { decoder?.stop() } catch (_: Exception) {}
            try { decoder?.release() } catch (_: Exception) {}
            try { extractor?.release() } catch (_: Exception) {}
        }
    }

    private fun transcode(
        extractor: MediaExtractor,
        decoder: MediaCodec,
        encoder: MediaCodec,
        muxer: MediaMuxer,
        srcWidth: Int,
        srcHeight: Int,
        outputWidth: Int,
        outputHeight: Int,
        videoTrackIndex: Int,
        audioTrackIndices: List<Int>
    ): CompressResult? {
        val decoderInfo = MediaCodec.BufferInfo()
        val encoderInfo = MediaCodec.BufferInfo()

        var inputDone = false
        var decoderDone = false
        var encoderDone = false
        var muxerStarted = false
        var videoMuxerTrackIndex = -1

        var decoderStride = srcWidth
        var decoderSliceHeight = srcHeight
        var decoderColorFormat = MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420SemiPlanar

        // 编码器可能调整实际分辨率，用 var 跟踪
        var actualFrameW = outputWidth
        var actualFrameH = outputHeight
        var frameSize = actualFrameW * actualFrameH * 3 / 2
        // 预分配足够大的缓冲区（取配置值和编码器可能的最大值）
        val maxFrameSize = maxOf(frameSize, srcWidth * srcHeight * 3 / 2)
        val workBuffer = ByteBuffer.allocateDirect(maxFrameSize)

        // 预分配缩放工作缓冲区，避免每帧分配
        // 数据量 = srcStride * srcSliceHeight * 3/2，stride 确认前先按 srcWidth*srcHeight 预估
        val srcBytes = ByteArray(srcWidth * srcHeight * 3 / 2)
        val dstBytes = ByteArray(maxFrameSize)

        // 缓存编码后的视频样本，muxer 启动后统一写入
        data class EncodedVideoSample(val buffer: ByteBuffer, val info: MediaCodec.BufferInfo)
        val videoSampleQueue = mutableListOf<EncodedVideoSample>()

        while (!encoderDone) {
            if (Thread.interrupted()) return CompressResult.Cancelled

            // === Phase 1: 批量填充解码器输入 ===
            while (!inputDone) {
                val inIndex = decoder.dequeueInputBuffer(0)
                if (inIndex < 0) break
                @Suppress("DEPRECATION")
                val buf = decoder.getInputBuffer(inIndex) ?: break
                val size = extractor.readSampleData(buf, 0)
                if (size >= 0) {
                    decoder.queueInputBuffer(inIndex, 0, size, extractor.getSampleTime(), 0)
                    extractor.advance()
                } else {
                    decoder.queueInputBuffer(inIndex, 0, 0, 0, MediaCodec.BUFFER_FLAG_END_OF_STREAM)
                    inputDone = true
                }
            }

            // === Phase 2: 批量取出解码输出 → 缩放 → 编码器输入队列 ===
            while (!decoderDone) {
                val outIndex = decoder.dequeueOutputBuffer(decoderInfo, 0)
                when {
                    outIndex == MediaCodec.INFO_TRY_AGAIN_LATER -> break
                    outIndex == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED -> {
                        val format = decoder.outputFormat
                        decoderStride = format.getInteger(MediaFormat.KEY_STRIDE)
                        decoderSliceHeight = if (format.containsKey(MediaFormat.KEY_SLICE_HEIGHT)) {
                            format.getInteger(MediaFormat.KEY_SLICE_HEIGHT)
                        } else { srcHeight }
                        decoderColorFormat = if (format.containsKey(MediaFormat.KEY_COLOR_FORMAT)) {
                            format.getInteger(MediaFormat.KEY_COLOR_FORMAT)
                        } else {
                            MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420SemiPlanar
                        }
                        Log.d(TAG, "VideoCompressor decoder stride=$decoderStride, sliceHeight=$decoderSliceHeight, colorFormat=$decoderColorFormat")
                    }
                    outIndex >= 0 -> {
                        if (decoderInfo.flags and MediaCodec.BUFFER_FLAG_END_OF_STREAM != 0) {
                            decoderDone = true
                            decoder.releaseOutputBuffer(outIndex, false)
                        } else if (decoderInfo.size > 0) {
                            @Suppress("DEPRECATION")
                            val decBuf = decoder.getOutputBuffer(outIndex)
                            if (decBuf != null) {
                                decBuf.position(decoderInfo.offset)
                                decBuf.limit(decoderInfo.offset + decoderInfo.size)
                                // 缩放
                                workBuffer.clear()
                                val isNV21 = decoderColorFormat ==
                                        MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420PackedSemiPlanar
                                scaleYUV(decBuf, workBuffer, srcWidth, srcHeight, outputWidth, outputHeight,
                                    decoderStride, decoderSliceHeight, isNV21, srcBytes, dstBytes)
                                workBuffer.rewind()
                                // 送编码器
                                val encInIndex = encoder.dequeueInputBuffer(0)
                                if (encInIndex >= 0) {
                                    @Suppress("DEPRECATION")
                                    val encBuf = encoder.getInputBuffer(encInIndex)
                                    if (encBuf != null) {
                                        encBuf.clear()
                                        // 用编码器实际 buffer 容量，避免越界
                                        val encCapacity = encBuf.remaining()
                                        val putSize = minOf(frameSize, encCapacity)
                                        if (putSize < frameSize) {
                                            Log.d(TAG, "VideoCompressor encoder buffer resized: frameSize=$frameSize -> putSize=$putSize (capacity=$encCapacity)")
                                        }
                                        workBuffer.position(0)
                                        workBuffer.limit(putSize)
                                        // 用 byte[] 中转，避免 DirectByteBuffer 间 put 的兼容性问题
                                        val tmp = ByteArray(putSize)
                                        workBuffer.get(tmp)
                                        encBuf.put(tmp)
                                        encoder.queueInputBuffer(encInIndex, 0, putSize, decoderInfo.presentationTimeUs, 0)
                                    }
                                }
                            }
                            decoder.releaseOutputBuffer(outIndex, false)
                        } else {
                            decoder.releaseOutputBuffer(outIndex, false)
                        }
                    }
                }
            }

            // === Phase 3: 批量取出编码输出 → 写入 muxer ===
            while (true) {
                val encOutIndex = encoder.dequeueOutputBuffer(encoderInfo, 0)
                when {
                    encOutIndex == MediaCodec.INFO_TRY_AGAIN_LATER -> break
                    encOutIndex == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED -> {
                        if (!muxerStarted) {
                            // 编码器可能调整了实际分辨率，用编码器输出格式中的真实值
                            val encoderFormat = encoder.outputFormat
                            actualFrameW = encoderFormat.getInteger(MediaFormat.KEY_WIDTH)
                            actualFrameH = encoderFormat.getInteger(MediaFormat.KEY_HEIGHT)
                            frameSize = actualFrameW * actualFrameH * 3 / 2
                            Log.d(TAG, "VideoCompressor encoder actual: ${actualFrameW}x${actualFrameH}, frameSize=$frameSize")

                            videoMuxerTrackIndex = muxer.addTrack(encoderFormat)
                            audioTrackIndices.forEach { idx ->
                                muxer.addTrack(extractor.getTrackFormat(idx))
                            }
                            muxer.start()
                            muxerStarted = true
                            videoSampleQueue.forEach { sample ->
                                muxer.writeSampleData(videoMuxerTrackIndex, sample.buffer, sample.info)
                            }
                            videoSampleQueue.clear()
                        }
                    }
                    encOutIndex >= 0 -> {
                        @Suppress("DEPRECATION")
                        val encBuf = encoder.getOutputBuffer(encOutIndex)
                        if (encBuf != null && encoderInfo.size > 0 &&
                            (encoderInfo.flags and MediaCodec.BUFFER_FLAG_CODEC_CONFIG) == 0
                        ) {
                            encBuf.position(encoderInfo.offset)
                            encBuf.limit(encoderInfo.offset + encoderInfo.size)
                            if (muxerStarted) {
                                muxer.writeSampleData(videoMuxerTrackIndex, encBuf, encoderInfo)
                            } else {
                                val copy = ByteBuffer.allocateDirect(encoderInfo.size)
                                copy.put(encBuf)
                                copy.rewind()
                                videoSampleQueue.add(EncodedVideoSample(copy, MediaCodec.BufferInfo().apply {
                                    set(0, encoderInfo.size, encoderInfo.presentationTimeUs, encoderInfo.flags)
                                }))
                            }
                        }
                        encoder.releaseOutputBuffer(encOutIndex, false)
                        if (encoderInfo.flags and MediaCodec.BUFFER_FLAG_END_OF_STREAM != 0) {
                            encoderDone = true
                            break
                        }
                    }
                }
            }

            // 4. 告知编码器输入结束
            if (decoderDone && !encoderDone) {
                val encInIndex = encoder.dequeueInputBuffer(0)
                if (encInIndex >= 0) {
                    encoder.queueInputBuffer(encInIndex, 0, 0, 0, MediaCodec.BUFFER_FLAG_END_OF_STREAM)
                }
            }
        }

        // 5. 透传音频轨道
        if (muxerStarted && audioTrackIndices.isNotEmpty()) {
            copyAudioTracks(extractor, muxer, audioTrackIndices)
        }

        return null
    }

    /**
     * 将音频轨道数据直接透传到 muxer（不重编码）
     */
    private fun copyAudioTracks(
        extractor: MediaExtractor,
        muxer: MediaMuxer,
        audioTrackIndices: List<Int>
    ) {
        val info = MediaCodec.BufferInfo()
        val buf = ByteBuffer.allocate(256 * 1024)

        for ((localIdx, extractorTrackIdx) in audioTrackIndices.withIndex()) {
            val muxerTrackIdx = 1 + localIdx
            for (i in 0 until extractor.trackCount) {
                extractor.unselectTrack(i)
            }
            extractor.selectTrack(extractorTrackIdx)
            extractor.seekTo(0, MediaExtractor.SEEK_TO_CLOSEST_SYNC)

            while (true) {
                buf.clear()
                val sampleSize = extractor.readSampleData(buf, 0)
                if (sampleSize < 0) break

                val codecFlags = extractorFlagsToCodecFlags(extractor.getSampleFlags())
                info.set(0, sampleSize.toInt(), extractor.getSampleTime(), codecFlags)
                buf.rewind()
                buf.limit(sampleSize.toInt())
                muxer.writeSampleData(muxerTrackIdx, buf, info)
                extractor.advance()
            }
        }
    }

    /**
     * YUV 缩放，支持 stride/padding 和 NV12/NV21 格式
     * Y 平面双线性插值（定点整数） + UV 平面最近邻
     * 使用 byte[] 中间缓冲，最后一次性 bulk 写入
     *
     * @param srcBytes 预分配的源数据缓冲区（复用，避免每帧分配）
     * @param dstBytes 预分配的目标数据缓冲区（复用，避免每帧分配）
     */
    private fun scaleYUV(
        src: ByteBuffer, dst: ByteBuffer,
        srcW: Int, srcH: Int, dstW: Int, dstH: Int,
        srcStride: Int, srcSliceHeight: Int,
        swapUV: Boolean = false,
        srcBytes: ByteArray = ByteArray(srcW * srcH * 3 / 2),
        dstBytes: ByteArray = ByteArray(dstW * dstH * 3 / 2)
    ) {
        // 实际数据量 = srcStride * srcSliceHeight * 3/2，可能大于预分配的 srcBytes
        val dataSize = srcStride * srcSliceHeight * 3 / 2
        val safeSrcBytes = if (dataSize <= srcBytes.size) srcBytes else ByteArray(dataSize)
        src.position(0)
        src.get(safeSrcBytes, 0, minOf(dataSize, src.remaining()))
        src.rewind()

        val dstTotalSize = dstW * dstH * 3 / 2
        val dstYPlaneSize = dstW * dstH

        // 当源分辨率 <= 目标分辨率时，直接拷贝不做缩放（最快路径）
        if (srcW == dstW && srcH == dstH && srcStride == srcW) {
            System.arraycopy(safeSrcBytes, 0, dstBytes, 0, srcW * srcH * 3 / 2)
            dst.position(0)
            dst.put(dstBytes, 0, dstTotalSize)
            return
        }

        // Y 平面：定点整数双线性插值（乘以 256 用 int 运算替代 double）
        val fixScaleX = ((srcW - 1) shl 8) / (dstW - 1).coerceAtLeast(1)
        val fixScaleY = ((srcH - 1) shl 8) / (dstH - 1).coerceAtLeast(1)

        for (y in 0 until dstH) {
            val srcYf = y * fixScaleY
            val srcY0 = (srcYf shr 8).coerceIn(0, srcSliceHeight - 2)
            val srcY1 = (srcY0 + 1).coerceAtMost(srcSliceHeight - 1)
            val fy = srcYf and 0xFF
            val fy1 = 256 - fy

            val row0 = srcY0 * srcStride
            val row1 = srcY1 * srcStride
            val dstOff = y * dstW

            for (x in 0 until dstW) {
                val srcXf = x * fixScaleX
                val srcX0 = (srcXf shr 8).coerceIn(0, srcW - 2)
                val srcX1 = (srcX0 + 1).coerceAtMost(srcW - 1)
                val fx = srcXf and 0xFF
                val fx1 = 256 - fx

                val y00 = safeSrcBytes[row0 + srcX0].toInt() and 0xFF
                val y01 = safeSrcBytes[row0 + srcX1].toInt() and 0xFF
                val y10 = safeSrcBytes[row1 + srcX0].toInt() and 0xFF
                val y11 = safeSrcBytes[row1 + srcX1].toInt() and 0xFF
                dstBytes[dstOff + x] = ((y00 * fx1 * fy1 + y01 * fx * fy1 +
                        y10 * fx1 * fy + y11 * fx * fy + 32768) shr 16).toByte()
            }
        }

        // UV 平面：最近邻
        val uvPlaneOffset = srcSliceHeight * srcStride
        val uvSliceHeight = (srcSliceHeight + 1) / 2
        val uvFixScaleX = (srcW shl 8) / dstW
        val uvFixScaleY = (srcH shl 8) / dstH

        for (y in 0 until dstH / 2) {
            val srcUVY = ((y * uvFixScaleY) shr 8).coerceIn(0, uvSliceHeight - 1)
            val srcRowOff = uvPlaneOffset + srcUVY * srcStride
            val dstRowOff = dstYPlaneSize + y * dstW

            for (x in 0 until dstW / 2) {
                val srcUVX = ((x * uvFixScaleX) shr 8).coerceIn(0, srcW / 2 - 1)
                val si = srcRowOff + srcUVX * 2
                val di = dstRowOff + x * 2
                if (si + 1 < safeSrcBytes.size && di + 1 < dstBytes.size) {
                    if (swapUV) {
                        dstBytes[di] = safeSrcBytes[si + 1]
                        dstBytes[di + 1] = safeSrcBytes[si]
                    } else {
                        dstBytes[di] = safeSrcBytes[si]
                        dstBytes[di + 1] = safeSrcBytes[si + 1]
                    }
                }
            }
        }

        // 一次性 bulk 写入 dst ByteBuffer
        dst.position(0)
        dst.put(dstBytes, 0, dstTotalSize)
    }

    /**
     * MediaExtractor 采样标志 → MediaCodec buffer 标志转换
     * SAMPLE_FLAG_SYNC(1) → BUFFER_FLAG_KEY_FRAME(1)
     */
    private fun extractorFlagsToCodecFlags(extractorFlags: Int): Int {
        var codecFlags = 0
        if (extractorFlags and MediaExtractor.SAMPLE_FLAG_SYNC != 0) {
            codecFlags = codecFlags or MediaCodec.BUFFER_FLAG_KEY_FRAME
        }
        return codecFlags
    }

    /**
     * 从源视频格式复制色彩元数据到编码器格式
     * 包括：色彩原色、传输特性、色彩标准（BT.601/BT.709/BT.2020）
     */
    private fun copyColorMetadata(src: MediaFormat, dst: MediaFormat) {
        val colorKeys = listOf(
            MediaFormat.KEY_COLOR_STANDARD,
            MediaFormat.KEY_COLOR_RANGE,
            MediaFormat.KEY_COLOR_TRANSFER
        )
        for (key in colorKeys) {
            if (src.containsKey(key)) {
                dst.setInteger(key, src.getInteger(key))
            }
        }
    }

    private fun findTrackByType(extractor: MediaExtractor, typePrefix: String): Int {
        for (i in 0 until extractor.trackCount) {
            val mime = extractor.getTrackFormat(i).getString(MediaFormat.KEY_MIME) ?: continue
            if (mime.startsWith(typePrefix)) return i
        }
        return -1
    }

    private fun findTracksByType(extractor: MediaExtractor, typePrefix: String): List<Int> {
        val tracks = mutableListOf<Int>()
        for (i in 0 until extractor.trackCount) {
            val mime = extractor.getTrackFormat(i).getString(MediaFormat.KEY_MIME) ?: continue
            if (mime.startsWith(typePrefix)) tracks.add(i)
        }
        return tracks
    }

    private fun findEncoderForType(mimeType: String): String? {
        val codecList = MediaCodecList(MediaCodecList.REGULAR_CODECS)
        for (info in codecList.codecInfos) {
            if (!info.isEncoder) continue
            for (type in info.supportedTypes) {
                if (type.equals(mimeType, ignoreCase = true)) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q && info.isHardwareAccelerated) {
                        return info.name
                    }
                    return info.name
                }
            }
        }
        return null
    }
}
