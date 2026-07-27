package com.hl.avprocessor.audio


import android.media.MediaCodec
import android.media.MediaCodecInfo
import android.media.MediaCodecList
import android.media.MediaExtractor
import android.media.MediaFormat
import android.media.MediaMuxer
import android.os.Handler
import android.os.HandlerThread
import android.os.Looper
import android.util.Log
import java.io.File
import java.nio.ByteBuffer
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicReference

/**
 * 音频格式转换工具
 * 基于 Android 原生 MediaCodec API（异步模式），不依赖 ffmpeg 等外部库
 *
 * 异步模式优势：
 * - 无轮询开销，回调立即响应
 * - 解码器和编码器可以真正并行工作
 * - 减少线程切换和等待
 *
 * @author
 * @since 2026/7/6
 */
object AudioConverter {

    private const val TAG = "AudioConverter"

    /** 是否输出详细的每帧日志（生产环境应设为 false 以提升性能） */
    private const val VERBOSE_LOG = false

    /** 转码超时时间（秒） */
    private const val TRANSCODE_TIMEOUT_SECONDS = 60L

    /**
     * 将 WMA 格式音频转为 M4A (AAC-LC) 格式
     *
     * @param inputFile  源文件路径
     * @param outputFile 目标文件路径
     * @param callback   转换结果回调，运行在主线程
     */
    fun convertWmaToM4a(inputFile: String, outputFile: String, callback: (Boolean) -> Unit) {
        convertAsync(inputFile, outputFile, callback)
    }

    /**
     * 将 WAV 格式音频转为 M4A (AAC-LC) 格式
     *
     * @param inputFile  源文件路径（.wav）
     * @param outputFile 目标文件路径（.m4a）
     * @param callback   转换结果回调，运行在主线程
     */
    fun convertWavToM4a(inputFile: String, outputFile: String, callback: (Boolean) -> Unit) {
        convertAsync(inputFile, outputFile, callback)
    }

    private fun convertAsync(inputFile: String, outputFile: String, callback: (Boolean) -> Unit) {
        Thread {
            val startTime = System.currentTimeMillis()
            try {
                val success = executeConversion(inputFile, outputFile)
                val elapsed = System.currentTimeMillis() - startTime
                Log.i(TAG, "转码结果: success=$success, elapsed=${elapsed}ms")
                Handler(Looper.getMainLooper()).post { callback.invoke(success) }
            } catch (e: Exception) {
                Log.e(TAG, "转码异常", e)
                Handler(Looper.getMainLooper()).post { callback.invoke(false) }
            }
        }.start()
    }

    private fun executeConversion(inputFile: String, outputFile: String): Boolean {
        var extractor: MediaExtractor? = null
        var decoder: MediaCodec? = null
        var encoder: MediaCodec? = null
        var muxer: MediaMuxer? = null
        var codecThread: HandlerThread? = null
        var conversionSucceeded = false

        return try {
            Log.i(TAG, "开始转码: $inputFile → $outputFile")

            val sourceFile = File(inputFile)
            val targetFile = File(outputFile)
            if (!sourceFile.exists() || !sourceFile.isFile) {
                Log.e(TAG, "源文件不存在或不是普通文件: $inputFile")
                return false
            }
            if (sourceFile.absolutePath == targetFile.absolutePath) {
                Log.e(TAG, "输入输出路径不能相同: $inputFile")
                return false
            }
            targetFile.parentFile?.mkdirs()
            if (targetFile.exists() && !targetFile.delete()) {
                Log.e(TAG, "无法删除已存在的输出文件: $outputFile")
                return false
            }

            // 1. 解析源文件
            extractor = MediaExtractor()
            extractor.setDataSource(inputFile)
            Log.i(TAG, "设置数据源成功")

            val audioTrackIndex = findAudioTrackIndex(extractor)
                ?: run {
                    Log.e(TAG, "未找到音频轨道: $inputFile")
                    return false
                }
            Log.i(TAG, "找到音频轨道: $audioTrackIndex")

            extractor.selectTrack(audioTrackIndex)
            val inputFormat = extractor.getTrackFormat(audioTrackIndex)
            val inputMime = inputFormat.getString(MediaFormat.KEY_MIME) ?: return false
            val sampleRate = inputFormat.getInteger(MediaFormat.KEY_SAMPLE_RATE)
            val channelCount = inputFormat.getInteger(MediaFormat.KEY_CHANNEL_COUNT)
            Log.i(TAG, "输入格式: $inputMime, 采样率: $sampleRate, 通道数: $channelCount")

            val durationUs = try {
                inputFormat.getLong(MediaFormat.KEY_DURATION)
            } catch (_: Exception) { Long.MAX_VALUE }
            Log.i(TAG, "音频时长: ${durationUs / 1_000_000.0}s")

            // 2. 创建编码器输出格式
            val outputFormat = createAacOutputFormat(sampleRate, channelCount)

            // 3. 查找支持的编码器
            val encoderInfo = findEncoderForType(OUTPUT_MIME)
                ?: run {
                    Log.e(TAG, "未找到支持 $OUTPUT_MIME 的编码器")
                    return false
                }
            Log.i(TAG, "使用编码器: ${encoderInfo.name}")

            // 4. 创建 HandlerThread 用于回调
            codecThread = HandlerThread("AudioCodecThread")
            codecThread.start()
            val codecHandler = Handler(codecThread.looper)

            // 5. 创建解码器和编码器
            decoder = MediaCodec.createDecoderByType(inputMime)
            encoder = MediaCodec.createEncoderByType(OUTPUT_MIME)
            Log.i(TAG, "解码器/编码器创建成功")

            // 6. 使用 CountDownLatch 同步等待完成
            val completionLatch = CountDownLatch(1)
            val result = AtomicBoolean(false)
            val error = AtomicReference<Exception?>(null)

            // 7. 创建转码状态机
            val state = TranscodeState(
                extractor = extractor,
                decoder = decoder,
                encoder = encoder,
                muxer = MediaMuxer(outputFile, MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4),
                sampleRate = sampleRate,
                channelCount = channelCount,
                durationUs = durationUs,
                codecHandler = codecHandler,
                onComplete = { success ->
                    result.set(success)
                    completionLatch.countDown()
                },
                onError = { e ->
                    error.set(e)
                    result.set(false)
                    completionLatch.countDown()
                }
            )

            // 8. 配置解码器（异步模式）
            decoder.configure(inputFormat, null, null, 0)
            decoder.setCallback(state.decoderCallback, codecHandler)
            Log.i(TAG, "解码器配置完成（异步模式）")

            // 9. 配置编码器（异步模式）
            encoder.configure(outputFormat, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE)
            encoder.setCallback(state.encoderCallback, codecHandler)
            Log.i(TAG, "编码器配置完成（异步模式）")

            // 10. 启动编码器（先启动编码器，因为解码器输出需要喂给编码器）
            encoder.start()
            Log.i(TAG, "编码器已启动")

            // 11. 启动解码器
            decoder.start()
            Log.i(TAG, "解码器已启动")

            // 12. 初始喂入一些数据到解码器
            state.feedDecoderInput()

            // 13. 等待转码完成
            val completed = completionLatch.await(TRANSCODE_TIMEOUT_SECONDS, TimeUnit.SECONDS)
            if (!completed) {
                Log.e(TAG, "转码超时")
                return false
            }

            conversionSucceeded = result.get()
            if (conversionSucceeded) {
                Log.i(TAG, "转码完成")
            } else {
                Log.e(TAG, "转码未正常完成")
            }

            conversionSucceeded
        } catch (e: Exception) {
            Log.e(TAG, "音频转码失败", e)
            false
        } finally {
            Log.i(TAG, "开始释放资源")
            codecThread?.quitSafely()
            codecThread?.join(1000)
            muxer?.runSafely { stop(); Log.i(TAG, "Muxer 已停止") }
            muxer?.runSafely { release(); Log.i(TAG, "Muxer 已释放") }
            encoder?.runSafely { stop(); release(); Log.i(TAG, "编码器已停止并释放") }
            decoder?.runSafely { stop(); release(); Log.i(TAG, "解码器已停止并释放") }
            extractor?.release()
            if (!conversionSucceeded) {
                File(outputFile).delete()
            }
            Log.i(TAG, "资源释放完成")
        }
    }

    /**
     * 查找音频轨道索引
     */
    private fun findAudioTrackIndex(extractor: MediaExtractor): Int? {
        for (i in 0 until extractor.trackCount) {
            val format = extractor.getTrackFormat(i)
            val mime = format.getString(MediaFormat.KEY_MIME) ?: ""
            if (mime.startsWith("audio/")) {
                return i
            }
        }
        return null
    }

    /**
     * 查找指定类型的编码器
     */
    private fun findEncoderForType(mime: String): MediaCodecInfo? {
        val codecList = MediaCodecList(MediaCodecList.ALL_CODECS)
        for (codecInfo in codecList.codecInfos) {
            if (codecInfo.isEncoder) {
                try {
                    val capabilities = codecInfo.getCapabilitiesForType(mime)
                    if (capabilities != null) {
                        return codecInfo
                    }
                } catch (_: Exception) {
                    // 该编码器不支持此类型，继续
                }
            }
        }
        return null
    }

    /**
     * 创建 AAC-LC 编码器输出格式
     */
    private fun createAacOutputFormat(sampleRate: Int, channelCount: Int): MediaFormat {
        val bitRate = when {
            sampleRate <= 22050 && channelCount == 1 -> 48_000
            sampleRate <= 22050 -> 64_000
            channelCount == 1 -> 96_000
            else -> 128_000
        }
        Log.i(TAG, "编码器配置: sampleRate=$sampleRate, channels=$channelCount, bitRate=$bitRate")

        return MediaFormat().apply {
            setString(MediaFormat.KEY_MIME, OUTPUT_MIME)
            setInteger(MediaFormat.KEY_SAMPLE_RATE, sampleRate)
            setInteger(MediaFormat.KEY_CHANNEL_COUNT, channelCount)
            setInteger(MediaFormat.KEY_BIT_RATE, bitRate)
            setInteger(MediaFormat.KEY_AAC_PROFILE, MediaCodecInfo.CodecProfileLevel.AACObjectLC)
            setInteger(MediaFormat.KEY_MAX_INPUT_SIZE, MAX_ENCODER_INPUT_SIZE)
        }
    }

    /**
     * 转码状态机
     *
     * 管理解码器和编码器之间的数据流：
     * - 解码器输入 ← extractor
     * - 解码器输出 → 编码器输入
     * - 编码器输出 → muxer
     */
    private class TranscodeState(
        private val extractor: MediaExtractor,
        private val decoder: MediaCodec,
        private val encoder: MediaCodec,
        private val muxer: MediaMuxer,
        private val sampleRate: Int,
        private val channelCount: Int,
        private val durationUs: Long,
        private val codecHandler: Handler,
        private val onComplete: (Boolean) -> Unit,
        private val onError: (Exception) -> Unit
    ) {
        private val startTime = System.currentTimeMillis()
        private val bytesPerFrame = channelCount * BYTES_PER_SAMPLE_16BIT
        private val lastSampleTimeUs = AtomicInteger(0)

        // 状态标志
        private val decoderInputDone = AtomicBoolean(false)
        private val decoderOutputDone = AtomicBoolean(false)
        private val encoderInputDone = AtomicBoolean(false)
        private val encoderOutputDone = AtomicBoolean(false)
        private val muxerStarted = AtomicBoolean(false)
        private val audioTrackIndex = AtomicInteger(-1)
        private val completionCalled = AtomicBoolean(false)

        // 解码器输出队列（等待喂给编码器）
        private val decoderOutputQueue = ArrayDeque<DecoderOutputBuffer>(16)

        // 编码器可用输入 buffer index 队列
        private val encoderInputQueue = ArrayDeque<Int>(8)

        /**
         * 解码器回调
         */
        val decoderCallback = object : MediaCodec.Callback() {
            override fun onInputBufferAvailable(codec: MediaCodec, index: Int) {
                if (decoderInputDone.get()) return
                feedDecoderInputBuffer(codec, index)
            }

            override fun onOutputBufferAvailable(codec: MediaCodec, index: Int, info: MediaCodec.BufferInfo) {
                val isEos = (info.flags and MediaCodec.BUFFER_FLAG_END_OF_STREAM) != 0

                if (info.size > 0) {
                    val buffer = codec.getOutputBuffer(index) ?: return
                    buffer.position(info.offset)
                    buffer.limit(info.offset + info.size)

                    if (VERBOSE_LOG) Log.d(TAG, "解码器输出: size=${info.size}, pts=${info.presentationTimeUs}")

                    // 放入队列等待喂给编码器
                    synchronized(decoderOutputQueue) {
                        decoderOutputQueue.add(
                            DecoderOutputBuffer(
                                buffer = buffer,
                                bufferIndex = index,
                                pts = info.presentationTimeUs,
                                size = info.size,
                                isEos = isEos
                            )
                        )
                    }

                    // 尝试喂给编码器
                    feedEncoderInputFromQueue()
                } else if (isEos) {
                    // size=0 的 EOS buffer
                    Log.i(TAG, "解码器输出 EOS (size=0)")
                    codec.releaseOutputBuffer(index, false)
                    decoderOutputDone.set(true)

                    // 确保所有剩余数据都喂给编码器，然后发送编码器 EOS
                    ensureEncoderEos()
                } else {
                    codec.releaseOutputBuffer(index, false)
                }
            }

            override fun onOutputFormatChanged(codec: MediaCodec, format: MediaFormat) {
                Log.i(TAG, "解码器输出格式变化: $format")
            }

            override fun onError(codec: MediaCodec, e: MediaCodec.CodecException) {
                Log.e(TAG, "解码器错误", e)
                callError(e)
            }
        }

        /**
         * 编码器回调
         */
        val encoderCallback = object : MediaCodec.Callback() {
            override fun onInputBufferAvailable(codec: MediaCodec, index: Int) {
                if (encoderInputDone.get()) return

                // 如果解码器已完成且队列为空，直接发送 EOS
                if (decoderOutputDone.get() && decoderOutputQueue.isEmpty()) {
                    encoderInputDone.set(true)
                    try {
                        encoder.queueInputBuffer(
                            index, 0, 0, 0,
                            MediaCodec.BUFFER_FLAG_END_OF_STREAM
                        )
                        Log.i(TAG, "编码器 EOS 发送成功（在 onInputBufferAvailable 中）")
                    } catch (e: Exception) {
                        Log.e(TAG, "发送编码器 EOS 失败", e)
                    }
                    return
                }

                // 存储可用的输入 buffer index，等待解码器输出数据
                synchronized(encoderInputQueue) {
                    encoderInputQueue.add(index)
                }
                // 尝试喂数据
                feedEncoderInputFromQueue()
            }

            override fun onOutputBufferAvailable(codec: MediaCodec, index: Int, info: MediaCodec.BufferInfo) {
                val isCodecConfig = info.flags and MediaCodec.BUFFER_FLAG_CODEC_CONFIG != 0
                val isEos = (info.flags and MediaCodec.BUFFER_FLAG_END_OF_STREAM) != 0

                if (info.size > 0 && !isCodecConfig) {
                    // 确保 Muxer 已启动
                    if (!muxerStarted.get()) {
                        val format = codec.outputFormat
                        val trackIndex = muxer.addTrack(format)
                        audioTrackIndex.set(trackIndex)
                        muxer.start()
                        muxerStarted.set(true)
                        Log.i(TAG, "Muxer 已启动，音频轨道索引: $trackIndex")
                    }

                    val encodedData = codec.getOutputBuffer(index)
                    if (encodedData != null) {
                        encodedData.position(info.offset)
                        encodedData.limit(info.offset + info.size)
                        muxer.writeSampleData(audioTrackIndex.get(), encodedData, info)
                    }
                } else if (isCodecConfig) {
                    if (VERBOSE_LOG) Log.d(TAG, "跳过编码器 codec config buffer")
                }

                codec.releaseOutputBuffer(index, false)

                if (isEos) {
                    Log.i(TAG, "编码器收到 EOS")
                    encoderOutputDone.set(true)
                    checkCompletion()
                }
            }

            override fun onOutputFormatChanged(codec: MediaCodec, format: MediaFormat) {
                Log.i(TAG, "编码器输出格式变化: $format")
                // 可能在此处启动 Muxer
            }

            override fun onError(codec: MediaCodec, e: MediaCodec.CodecException) {
                Log.e(TAG, "编码器错误", e)
                callError(e)
            }
        }

        /**
         * 从 extractor 喂数据到解码器
         */
        fun feedDecoderInput() {
            if (decoderInputDone.get()) return
            // 解码器会在有空闲输入缓冲区时回调 onInputBufferAvailable
        }

        private fun feedDecoderInputBuffer(codec: MediaCodec, index: Int) {
            try {
                val inputBuffer = codec.getInputBuffer(index) ?: return
                inputBuffer.clear()

                val sampleSize = extractor.readSampleData(inputBuffer, 0)
                val sampleTime = extractor.sampleTime

                // EOS 判断
                if (sampleSize <= 0 || sampleTime == -1L ||
                    (durationUs != Long.MAX_VALUE && sampleTime > durationUs + 1_000_000L)
                ) {
                    Log.i(TAG, "解码器输入完成，发送 EOS")
                    codec.queueInputBuffer(
                        index, 0, 0, 0,
                        MediaCodec.BUFFER_FLAG_END_OF_STREAM
                    )
                    decoderInputDone.set(true)
                } else {
                    codec.queueInputBuffer(index, 0, sampleSize, sampleTime, 0)
                    lastSampleTimeUs.set((sampleTime / 1000).toInt())
                    extractor.advance()
                }

                // 定期报告进度
                reportProgress()
            } catch (e: Exception) {
                Log.e(TAG, "喂解码器输入异常", e)
                callError(e)
            }
        }

        /**
         * 从解码器输出队列和编码器输入队列喂数据
         */
        private fun feedEncoderInputFromQueue() {
            while (true) {
                val output: DecoderOutputBuffer
                val encoderInputId: Int

                synchronized(decoderOutputQueue) {
                    synchronized(encoderInputQueue) {
                        if (decoderOutputQueue.isEmpty() || encoderInputQueue.isEmpty()) {
                            return
                        }
                        output = decoderOutputQueue.first()
                        encoderInputId = encoderInputQueue.first()
                    }
                }

                // 喂数据
                val encoderInputBuffer = encoder.getInputBuffer(encoderInputId) ?: return
                encoderInputBuffer.clear()

                val remaining = output.size - output.currentOffset
                val capacity = encoderInputBuffer.capacity()
                val chunkSize = minOf(remaining, capacity)

                output.buffer.position(output.currentOffset)
                output.buffer.limit(output.currentOffset + chunkSize)
                encoderInputBuffer.put(output.buffer)

                val pts = calculatePresentationTimeUs(
                    output.pts, output.currentOffset, bytesPerFrame, sampleRate
                )

                encoder.queueInputBuffer(encoderInputId, 0, chunkSize, pts, 0)
                output.currentOffset += chunkSize

                if (VERBOSE_LOG) Log.d(TAG, "喂编码器: chunkSize=$chunkSize, pts=$pts")

                // 移除已使用的 encoder input index
                synchronized(encoderInputQueue) {
                    encoderInputQueue.removeFirst()
                }

                // 如果这个 buffer 喂完了
                if (output.currentOffset >= output.size) {
                    synchronized(decoderOutputQueue) {
                        decoderOutputQueue.removeFirst()
                    }
                    // 释放解码器输出缓冲区
                    decoder.releaseOutputBuffer(output.bufferIndex, false)

                    // 如果这是 EOS buffer
                    if (output.isEos) {
                        // 如果解码器输出也完成，发送编码器 EOS
                        if (decoderOutputDone.get()) {
                            sendEncoderEos()
                        }
                        return
                    }
                } else {
                    // 还有剩余数据，等待更多编码器输入 buffer
                    return
                }
            }
        }

        /**
         * 发送编码器 EOS
         * 注意：async 模式下不能调用 dequeueInputBuffer，必须使用回调提供的 index
         */
        private fun sendEncoderEos() {
            if (encoderInputDone.get()) return  // 已发送过

            // 从队列获取可用的输入 buffer index
            val encoderInputId: Int
            synchronized(encoderInputQueue) {
                if (encoderInputQueue.isEmpty()) {
                    Log.w(TAG, "发送编码器 EOS: 暂无可用输入 buffer，等待回调...")
                    // 编码器会在有空闲输入时回调 onInputBufferAvailable
                    // 在那里会调用 feedEncoderInputFromQueue，最终会触发 EOS 发送
                    return
                }
                encoderInputId = encoderInputQueue.removeFirst()
            }

            encoderInputDone.set(true)
            try {
                encoder.queueInputBuffer(
                    encoderInputId, 0, 0, 0,
                    MediaCodec.BUFFER_FLAG_END_OF_STREAM
                )
                Log.i(TAG, "编码器 EOS 发送成功")
            } catch (e: Exception) {
                Log.e(TAG, "发送编码器 EOS 失败", e)
            }
        }

        /**
         * 确保编码器 EOS 被发送
         * 当解码器输出 EOS 时调用，确保所有剩余数据都喂给编码器后发送 EOS
         */
        private fun ensureEncoderEos() {
            Log.i(TAG, "ensureEncoderEos: decoderOutputQueue.size=${decoderOutputQueue.size}, encoderInputQueue.size=${encoderInputQueue.size}")

            // 先尝试喂完所有剩余数据
            feedEncoderInputFromQueue()

            // 尝试发送 EOS
            sendEncoderEos()

            // 如果 EOS 还没发送成功，启动重试
            if (!encoderInputDone.get()) {
                Log.i(TAG, "EOS 暂未发送，启动重试...")
                codecHandler.postDelayed({
                    ensureEncoderEosRetry()
                }, 50)
            }
        }

        /**
         * 重试确保编码器 EOS 被发送
         */
        private fun ensureEncoderEosRetry() {
            if (encoderInputDone.get()) return

            feedEncoderInputFromQueue()

            if (decoderOutputQueue.isNotEmpty()) {
                // 还有数据，继续等待编码器输入 buffer
                codecHandler.postDelayed({
                    ensureEncoderEosRetry()
                }, 50)
            } else {
                // 数据已喂完，尝试发送 EOS
                sendEncoderEos()
                // 如果 EOS 还没发送成功（没有可用 input buffer），继续重试
                if (!encoderInputDone.get()) {
                    codecHandler.postDelayed({
                        ensureEncoderEosRetry()
                    }, 50)
                }
            }
        }

        /**
         * 检查是否转码完成
         */
        private fun checkCompletion() {
            if (encoderOutputDone.get() && !completionCalled.get()) {
                val elapsed = System.currentTimeMillis() - startTime
                Log.i(TAG, "转码完成，总耗时: ${elapsed}ms")

                // 停止 Muxer
                try {
                    muxer.stop()
                    Log.i(TAG, "Muxer 已停止")
                } catch (e: Exception) {
                    Log.e(TAG, "停止 Muxer 异常", e)
                }

                completionCalled.set(true)
                onComplete(true)
            }
        }

        /**
         * 报告转码进度
         */
        private fun reportProgress() {
            val elapsed = System.currentTimeMillis() - startTime
            val processedMs = lastSampleTimeUs.get().toLong()
            val speed = if (elapsed > 0) processedMs.toFloat() / elapsed else 0f

            if (VERBOSE_LOG &&  elapsed % 5000 < 100) { // 大约每 5 秒报告一次
                Log.i(TAG, "转码进度: ${processedMs}ms/${durationUs / 1000}ms, " +
                    "速度=${String.format("%.1f", speed)}x, " +
                    "elapsed=${elapsed}ms, " +
                    "decoderInputDone=${decoderInputDone.get()}, " +
                    "encoderOutputDone=${encoderOutputDone.get()}")
            }
        }

        /**
         * 错误处理
         */
        private fun callError(e: Exception) {
            if (!completionCalled.get()) {
                completionCalled.set(true)
                onError(e)
            }
        }

        /**
         * 解码器输出缓冲区
         */
        private data class DecoderOutputBuffer(
            val buffer: ByteBuffer,
            val bufferIndex: Int,
            val pts: Long,
            val size: Int,
            val isEos: Boolean,
            var currentOffset: Int = 0
        )
    }

    /**
     * 计算 PTS
     */
    private fun calculatePresentationTimeUs(
        basePts: Long,
        byteOffset: Int,
        bytesPerFrame: Int,
        sampleRate: Int
    ): Long {
        if (byteOffset <= 0 || bytesPerFrame <= 0 || sampleRate <= 0) {
            return basePts
        }
        val sampleOffset = byteOffset / bytesPerFrame
        return basePts + sampleOffset * 1_000_000L / sampleRate
    }

    private inline fun <T> T.runSafely(block: T.() -> Unit) {
        try {
            block(this)
        } catch (e: Exception) {
            Log.e(TAG, "释放资源异常", e)
        }
    }

    // ==================== 常量 ====================

    /** AAC-LC 编码器 mime 类型 */
    private const val OUTPUT_MIME = "audio/mp4a-latm"

    private const val BYTES_PER_SAMPLE_16BIT = 2

    /** 编码器最大输入缓冲区大小 */
    private const val MAX_ENCODER_INPUT_SIZE = 16384
}
