package com.hl.utils

/**
 * @author  张磊  on  2021/06/04 at 17:01
 * Email: 913305160@qq.com
 */

/**
 * 根据传入的毫秒数计算出 时分
 */
fun calculateMills2HoursAndMinutes(mills: Long): Pair<Int, Int> {
	val hours = (mills / (60 * 60 * 1000)).toInt()
	val minutes = (mills % (60 * 60 * 1000) / (60 * 1000)).toInt() //整除小时后剩余的毫秒换算分钟
	return Pair(hours, minutes)
}

/**
 * 根据传入的毫秒数计算出 时分秒
 */
fun calculateMills2HMS(mills: Long): Triple<Int, Int, Int> {
	val (hours, minutes) = calculateMills2HoursAndMinutes(mills)
	val seconds = (mills % (60 * 1000) / 1000).toInt() //对分钟取余再换算成秒
	return Triple(hours, minutes, seconds)
}


/**
 * 根据小时和分钟计算总毫秒数
 */
fun calculateTotalMillsTime(inputHours: Int, inputMinutes: Int): Long {
	return inputHours * 60 * 60 * 1000L + inputMinutes * 60 * 1000L
}