package com.hl.utils

import com.hl.utils.date.LunarUtil
import com.hl.utils.date.srcPattern
import com.hl.utils.date.toDate
import com.hl.utils.date.toFormatString
import java.util.*

/**
 * @author  张磊  on  2022/09/01 at 17:24
 * Email: 913305160@qq.com
 */

fun Calendar.toFormatString(destPattern: String = srcPattern): String {
	return time.toFormatString(destPattern)
}

fun Calendar.toDate(): Date {
	return this.toFormatString(srcPattern).toDate(srcPattern)
}

fun Calendar?.copyNewCalendar(): Calendar? {
	if (this == null) return null
	return Calendar.getInstance().apply {
		timeInMillis = this@copyNewCalendar.timeInMillis
	}
}

fun String.toCalendar(srcPattern: String): Calendar? {
	val date = try {
		toDate(srcPattern)
	} catch (e: Exception) {
		e.printStackTrace()
		return null
	}
	return Calendar.getInstance().apply {
		time = date
	}
}

/**
 * 设置分钟为 0
 */
fun Calendar.toMinuteFirst(): Calendar {
	set(Calendar.SECOND, 0)
	return this
}

/**
 * 设置分钟为 59
 */
fun Calendar.toMinuteLast(): Calendar {
	set(Calendar.SECOND, 59)
	return this
}

/**
 * 设置为一天的最后时刻
 */
fun Calendar.toDayLast(): Calendar {
	set(Calendar.HOUR_OF_DAY, 23)
	toMinuteLast()
	set(Calendar.SECOND, 59)
	return this
}

/**
 * 设置为一天的开始时刻
 */
fun Calendar.toDayFirst(): Calendar {
	set(Calendar.HOUR_OF_DAY, 0)
	toMinuteFirst()
	set(Calendar.SECOND, 0)
	return this
}

/**
 * 设置为一个月的开始时刻
 */
fun Calendar.toMonthFirst(): Calendar {
	set(Calendar.DAY_OF_MONTH, 1)
	toDayFirst()
	return this
}

/**
 * 设置为一个月的最后时刻
 */
fun Calendar.toMonthLast(): Calendar {
	val max = getActualMaximum(Calendar.DAY_OF_MONTH)
	set(Calendar.DAY_OF_MONTH, max)
	toDayLast()
	return this
}


/**
 * 获取当前日期是星期几
 */
fun Calendar.getDayOfWeek(): String {
	val dayOfWeek = this[Calendar.DAY_OF_WEEK]
	return when (dayOfWeek) {
		Calendar.SUNDAY -> "星期日"
		Calendar.MONDAY -> "星期一"
		Calendar.TUESDAY -> "星期二"
		Calendar.WEDNESDAY -> "星期三"
		Calendar.THURSDAY -> "星期四"
		Calendar.FRIDAY -> "星期五"
		Calendar.SATURDAY -> "星期六"
		else -> "星期?"
	}
}

/**
 * 获取农历日期
 *
 *  @param isNeedYear 是否需要农历年份
 */
fun Calendar.toLunarString(isNeedYear: Boolean = false): String {
	return LunarUtil.getLunarString(this, isNeedYear)
}

/**
 * 获取今天是星期几
 */
fun nowDayOfWeekString() = Calendar.getInstance().getDayOfWeek()

/**
 * 获取今天对应的农历日期
 */
fun nowLunarDateString() = Calendar.getInstance().toLunarString(isNeedYear = true)