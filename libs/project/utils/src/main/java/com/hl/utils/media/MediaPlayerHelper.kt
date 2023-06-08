package com.hl.utils.media

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Handler
import android.os.Looper
import android.widget.SeekBar
import com.elvishew.xlog.XLog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


/**
 * @author  张磊  on  2022/08/25 at 16:04
 * Email: 913305160@qq.com
 */
class MediaPlayerHelper(private val seekBar: SeekBar? = null, private val playListener: OnPlayListener? = null) :
	MediaPlayer.OnBufferingUpdateListener, MediaPlayer.OnCompletionListener, MediaPlayer.OnPreparedListener,
	MediaPlayer.OnVideoSizeChangedListener, MediaPlayer.OnErrorListener, MediaPlayer.OnSeekCompleteListener {

	private val mediaPlayer: MediaPlayer = MediaPlayer() // 媒体播放器

	private var isTiming = false

	private val timerJob = MainScope().launch(Dispatchers.IO) {
		while (true) {
			if (isTiming /*&& mediaPlayer.isPlaying*/) {
				handler.sendEmptyMessage(0) // 发送消息

				// 每一秒执行一次
				delay(500)
			}
		}
	}


	/**
	 * handler 切换到主线程更新页面
	 */
	private val handler = Handler(Looper.getMainLooper()) {
		val duration = getTotalDuration()
		if (duration > 0) {
			val currentPosition = getCurrentPosition()
			val currentProgress = (currentPosition * 100 / duration).toInt()

			// 计算进度（获取进度条最大刻度*当前音乐播放位置 / 当前音乐时长）
			seekBar?.run {
				if (!this.isPressed) {
					val pos = this.max * currentProgress / 100
					this.progress = pos

					// seekBar 按住时不更新进度
					updatePlayProgress(currentPosition, duration, currentProgress)
				}
			} ?: updatePlayProgress(currentPosition, duration, currentProgress)
		}
		true
	}

	private fun updatePlayProgress(currentPosition: Long, duration: Long, currentProgress: Int) {
		XLog.d(
			"正在播放中 --------------> currentPosition == ${currentPosition}, duration == ${duration}, currentProgress== $currentProgress"
		)
		playListener?.onProgress(currentPosition, duration, currentProgress)

		if (currentProgress == 100) {
			// 进度到 100 ，手动通知播放完成
			onCompletion(mediaPlayer)
		}

		if (currentProgress == 0 && !mediaPlayer.isPlaying) {
			// 进度为 0 且未进行播放通知播放完成  （拖动进度条到末尾需要处理）
			onCompletion(mediaPlayer)
		}
	}


	// 初始化播放器
	init {
		try {
			// mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC) // 设置媒体流类型
			val audioAttributes = AudioAttributes.Builder()
				.setContentType(AudioAttributes.CONTENT_TYPE_MOVIE)   // 设置媒体流类型
				.build()
			mediaPlayer.setAudioAttributes(audioAttributes)
			mediaPlayer.setOnBufferingUpdateListener(this)
			mediaPlayer.setOnPreparedListener(this)
			mediaPlayer.setOnVideoSizeChangedListener(this)
			mediaPlayer.setOnErrorListener(this)
			mediaPlayer.setOnSeekCompleteListener(this)

			seekBar?.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
				override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
					if (fromUser) {
						val percent = progress.toFloat() / seekBar.max
						mediaPlayer.seekTo((getTotalDuration() * percent).toInt())

						if (!mediaPlayer.isPlaying) {
							play()
						}
					}
				}

				override fun onStartTrackingTouch(seekBar: SeekBar?) {
				}

				override fun onStopTrackingTouch(seekBar: SeekBar?) {
				}
			})
		} catch (e: Exception) {
			XLog.e("初始化 mediaPlayer 异常", e)
		}
	}

	private fun starTiming() {
		isTiming = true
	}

	private fun stopTiming() {
		isTiming = false
	}

	/**
	 *  准备开始播放
	 *
	 * @param url 媒体文件的路径，或者想播放的流的 http/rtsp URL
	 */
	fun preparePlayUrl(url: String) {
		XLog.d("开始准备播放， url == $url")

		try {
			mediaPlayer.reset()
			mediaPlayer.setDataSource(url) // 设置数据源
			mediaPlayer.prepareAsync() // prepareAsync 对于流异步自动播放
		} catch (e: Exception) {
			XLog.e("mediaPlayer 准备播放异常", e)
		}
	}

	/**
	 * 准备开始播放
	 *
	 * @param rawId  raw 目录下的媒体文件
	 */
	fun preparePlayRes(context: Context, rawId: Int) {
		XLog.d("开始准备播放， resId == $rawId")

		try {
			val afd = context.resources.openRawResourceFd(rawId)
			if (afd == null) {
				XLog.d("获取对应的资源文件失败")
			}

			mediaPlayer.reset()
			mediaPlayer.setDataSource(afd.fileDescriptor, afd.startOffset, afd.length) // 设置数据源
			afd.close()
			mediaPlayer.prepare() // prepare 对于本地文件同步加载
		} catch (e: Exception) {
			XLog.e("mediaPlayer 准备播放异常", e)
		}
	}

	/**
	 * 开始播放， 对于 pause 或者 onCompletion 时有效
	 */
	fun play() {
		XLog.d("开始播放")

		try {
			mediaPlayer.start()

			// 开始计时任务
			starTiming()
		} catch (e: Exception) {
			XLog.e("开始播放 异常", e)
		}
	}

	/**
	 * 暂停播放
	 */
	fun pause() {
		XLog.d("暂停播放")

		try {
			mediaPlayer.pause()

			// 取消计时
			stopTiming()

			handler.post {
				playListener?.onPause()
			}
		} catch (e: Exception) {
			XLog.e("暂停播放", e)
		}
	}

	/**
	 * 停止播放， 停止后必须重新调用 prepare 才可以进行播放
	 */
	fun stop() {
		XLog.d("停止播放")
		try {
			mediaPlayer.stop()
			mediaPlayer.release()

			// 取消计时
			stopTiming()

			handler.post {
				playListener?.onStop()
			}
		} catch (e: Exception) {
			XLog.e("停止播放异常", e)
		}
	}


	/**
	 * 当前是否正在播放
	 */
	fun isPalying(): Boolean {
		val playing = try {
			mediaPlayer.isPlaying
		} catch (e: Exception) {
			XLog.e("获取当前播放状态出错", e)
			false
		}
		XLog.d("当前是否正在播放  == ${playing}")
		return playing
	}

	/**
	 * 获取当前播放时长，  单位： ms
	 */
	private fun getCurrentPosition(): Long {
		try {
			return mediaPlayer.currentPosition.toLong()
		} catch (e: Exception) {
			XLog.e("获取当前播放位置出错", e)
			return 0
		}
	}

	/**
	 * 获取当前播放媒体文件的总时长,  单位： ms
	 */
	private fun getTotalDuration(): Long {
		try {
			return mediaPlayer.duration.toLong()
		} catch (e: Exception) {
			XLog.e("获取当前播放总时长出错", e)
			return 0
		}
	}

	/**
	 * 获取当前播放进度
	 */
	private fun getCurrentPlayProgress(): Long {
		return getCurrentPosition() * 100 / getTotalDuration()
	}

	/**
	 * 获取 mediaPlayer  对象
	 */
	fun getMediaPlayer(): MediaPlayer {
		return mediaPlayer
	}


	// 播放准备
	override fun onPrepared(player: MediaPlayer) {
		XLog.d("MediaPlayer 准备完成， 开始自动播放")
		handler.post {
			playListener?.onPrepareStart(getCurrentPosition(), getTotalDuration())
		}

		// 不进行自动播放，由用户自己控制
		// play()
	}

	// 目前无法收到播放完成的回调，通过进度判断手动发出通知
	// 播放完成
	override fun onCompletion(player: MediaPlayer?) {
		XLog.d("MediaPlayer 播放完成")

		// 取消计时
		stopTiming()

		handler.post {
			playListener?.onStop()
		}
	}

	/**
	 * 缓冲更新
	 */
	override fun onBufferingUpdate(mp: MediaPlayer?, percent: Int) {
		seekBar?.secondaryProgress = percent
		XLog.d("MediaPlayer 缓冲播放中, 播放进度 == ${getCurrentPlayProgress()},   缓冲进度 == $percent")

		handler.post {
			playListener?.onBufferingUpdate(percent)
		}
	}

	override fun onVideoSizeChanged(mp: MediaPlayer?, width: Int, height: Int) {
		XLog.d("MediaPlayer 播放视频大小改变, width == ${width},   height == $height")
	}

	override fun onError(mp: MediaPlayer?, what: Int, extra: Int): Boolean {
		XLog.d("MediaPlayer 播放出错, 错误类型 == ${what},   错误 == $extra")
		return true
	}

	override fun onSeekComplete(mp: MediaPlayer?) {
		XLog.d("MediaPlayer 指定位置播放, 播放进度 == ${getCurrentPlayProgress()}")

		handler.post {
			playListener?.onSeekComplete(getCurrentPosition())
		}
	}
}


interface OnPlayListener {

	/**
	 * 准备开始播放
	 *
	 * @param currentPosition    当前播放时间
	 * @param totalDuration      总时间
	 */
	fun onPrepareStart(currentPosition: Long, totalDuration: Long) {}

	/**
	 * 暂停播放
	 */
	fun onPause() {}

	/**
	 * 停止播放
	 */
	fun onStop() {}

	/**
	 * 播放中
	 *
	 * @param currentPosition    当前播放时间
	 * @param totalDuration      总时间
	 * @param currentProgress    播放进度
	 */
	fun onProgress(currentPosition: Long, totalDuration: Long, currentProgress: Int) {}

	/**
	 * 准备开始播放
	 *
	 * @param currentPosition    当前播放时间
	 */
	fun onSeekComplete(currentPosition: Long) {}

	/**
	 * 缓冲中
	 *
	 * @param percent 缓冲进度
	 */
	fun onBufferingUpdate(percent: Int) {}
}