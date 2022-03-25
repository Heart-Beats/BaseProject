package com.hl.utils

import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.temporal.ChronoUnit
import java.util.*

fun Date.toFormatString(destPattern: String): String {
    val format = SimpleDateFormat(destPattern, Locale.ENGLISH)
    return format.format(this)
}

fun Calendar.toFormatString(destPattern: String): String {
    return time.toFormatString(destPattern)
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

fun String.toDate(srcPattern: String): Date {
    val format = SimpleDateFormat(srcPattern, Locale.ENGLISH)
    return format.parse(this)
}

fun Calendar.toMinuteFirst(): Calendar {
    set(Calendar.SECOND, 0)
    return this
}

fun Calendar.toMinuteLast(): Calendar {
    set(Calendar.SECOND, 59)
    return this
}

/**
 * 设置为一天的最后时刻
 */
fun Calendar.toDayLast(): Calendar {
    set(Calendar.HOUR_OF_DAY, 23)
    set(Calendar.MINUTE, 59)
    set(Calendar.SECOND, 59)
    return this
}

/**
 * 设置为一天的开始时刻
 */
fun Calendar.toDayFirst(): Calendar {
    set(Calendar.HOUR_OF_DAY, 0)
    set(Calendar.MINUTE, 0)
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

const val srcPattern = "yyyy-MM-dd HH:mm:ss.SSS"

val nowDateTime = ::getFormattedNowDateTime

/**
 * 获取当前日期时间
 * @param
 * @return 格式化后的日期时间字符串
 */
fun getFormattedNowDateTime(): String {
    return Calendar.getInstance().toFormatString(srcPattern)
}

fun Calendar.getTAFormatDateTime(): String {
    return toFormatString("yyyy-MM-dd HH:mm:ss.SSS")
}

fun Date.toCalendar(): Calendar {
    val date = this
    return Calendar.getInstance().apply {
        this.time = date
    }
}

fun Calendar.toDate(): Date {
    return this.toFormatString(srcPattern).toDate(srcPattern)
}

infix fun Date.differHours(other: Date): Float {
    return (this.time - other.time) / (60 * 60 * 1000f)
}

fun Date.compareValueTo(otherDate: Date, betweenValue: Long, unit: TimeUnit): Boolean {
    return if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
        val thisLocalDateTime = LocalDateTime.from(this.toInstant().atZone(ZoneId.systemDefault()))
        val otherLocalDateTime = LocalDateTime.from(otherDate.toInstant().atZone(ZoneId.systemDefault()))
        otherLocalDateTime.plus(betweenValue, unit.getChronoUnit()) < thisLocalDateTime
    } else {
        (this.time - otherDate.time) >= betweenValue * unit.baseValue
    }
}

enum class TimeUnit(val unitName: String, val baseValue: Long) {
    MILLIS("Millis", 1L),
    SECONDS("Seconds", MILLIS.baseValue * 1000),
    MINUTES("Minutes", SECONDS.baseValue * 60),
    HOURS("Hours", MINUTES.baseValue * 60),
    DAYS("Days", HOURS.baseValue * 24),
    MONTHS("Months", DAYS.baseValue * 30),
    YEARS("Years", MONTHS.baseValue * 12)
}

private fun TimeUnit.getChronoUnit(): ChronoUnit? {
    ChronoUnit.values().forEach {
        if (it.toString() == this.unitName) {
            return it
        }
    }
    return null
}