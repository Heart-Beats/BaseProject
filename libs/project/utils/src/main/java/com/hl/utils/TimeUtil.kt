package com.hl.utils

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.*

/**
 * @author  张磊  on  2021/06/04 at 17:01
 * Email: 913305160@qq.com
 */

object TimeUtil {

	/**
	 * 根据传入的毫秒数计算出 时分
	 */
	@JvmStatic
	fun calculateMills2HM(mills: Long): Pair<Int, Int> {
		val hours = (mills / (60 * 60 * 1000)).toInt()
		val minutes = (mills % (60 * 60 * 1000) / (60 * 1000)).toInt() //整除小时后剩余的毫秒换算分钟
		return Pair(hours, minutes)
	}

	/**
	 * 根据传入的毫秒数计算出 时分秒
	 */
	@JvmStatic
	fun calculateMills2HMS(mills: Long): Triple<Int, Int, Int> {
		val (hours, minutes) = calculateMills2HM(mills)
		val seconds = (mills % (60 * 1000) / 1000).toInt() //对分钟取余再换算成秒
		return Triple(hours, minutes, seconds)
	}

	/**
	 * 根据传入的毫秒数计算出 时分秒，并返回格式化后的总计时时间
	 *
	 * @param countTime 需要转换的毫秒数
	 *
	 */
	@JvmStatic
	fun calculateCountTime(countTime: Long): String {
		val (hours, minutes, seconds) = calculateMills2HMS(countTime)
		return if (hours > 0) {
			String.format("%02d:%02d:%02d", hours, minutes, seconds)
		} else {
			String.format("%02d:%02d", minutes, seconds)
		}
	}


	/**
	 * 根据小时和分钟计算总毫秒数
	 */
	@JvmStatic
	fun calculateTotalMills(inputHours: Int, inputMinutes: Int): Long {
		return inputHours * 60 * 60 * 1000L + inputMinutes * 60 * 1000L
	}

	/**
	 * 开始正向计时
	 *
	 * @param  lifecycleOwner 生命周期所属者
	 * @param  startTime 计时的开始时间毫秒数，默认从 0 开始
	 * @param  interval 计时的间隔毫秒数， 默认 1000ms
	 * @param  action 计时时完成的操作
	 *
	 * @return 计时任务， 正常会在 lifecycleOwner 生命周期结束时自动停止计时，若需要生命周期内停止，
	 *
	 */
	@JvmStatic
	fun startTiming(
		lifecycleOwner: LifecycleOwner, startTime: Long = 0, interval: Int = 1 * 1000,
		action: (currentCountTime: Long, formatTime: String) -> Unit
	): Job {
		return lifecycleOwner.lifecycleScope.launch {
			var countTime = startTime
			while (true) {
				withContext(Dispatchers.IO) {
					delay(1 * 1000)
					countTime += interval
				}
				action(countTime, calculateCountTime(countTime))
			}
		}
	}
}