package com.hl.utils.media

import android.content.Context
import android.media.AudioManager


/**
 * @author  张磊  on  2022/06/01 at 17:41
 * Email: 913305160@qq.com
 */

class AudioHelper(context: Context) {

	private val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager

	private var currVolume = 0

	/**
	 * 扬声器是否打开
	 */
	fun isSpeakerphoneOn() = audioManager.isSpeakerphoneOn

	fun switchSpeaker(isOpen: Boolean): Boolean {
		return if (isOpen) openSpeaker() else closeSpeaker()
	}

	/**
	 * 开始扬声器
	 */
	private fun openSpeaker(): Boolean {
		try {
			audioManager.mode = AudioManager.MODE_IN_COMMUNICATION
			// 获取当前通话音量
			currVolume = audioManager.getStreamVolume(AudioManager.STREAM_VOICE_CALL)
			if (!audioManager.isSpeakerphoneOn) {
				audioManager.isSpeakerphoneOn = true
				audioManager.setStreamVolume(
					AudioManager.STREAM_VOICE_CALL,
					audioManager.getStreamMaxVolume(AudioManager.STREAM_VOICE_CALL),
					AudioManager.STREAM_VOICE_CALL
				)
			}

			return true
		} catch (e: Exception) {
			e.printStackTrace()
			return false
		}
	}

	/**
	 * 关闭扬声器
	 */
	private fun closeSpeaker(): Boolean {
		return try {
			if (audioManager.isSpeakerphoneOn) {
				audioManager.isSpeakerphoneOn = false
				audioManager.setStreamVolume(
					AudioManager.STREAM_VOICE_CALL,
					currVolume,
					AudioManager.STREAM_VOICE_CALL
				)
			}
			true
		} catch (e: Exception) {
			e.printStackTrace()
			false
		}
	}

}