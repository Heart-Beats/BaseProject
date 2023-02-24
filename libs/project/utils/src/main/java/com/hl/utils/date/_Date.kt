package com.hl.utils.date

import com.hl.utils.toFormatString
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.temporal.ChronoUnit
import java.util.*

fun Date.toFormatString(@DatePattern destPattern: String = DatePattern.YMD_HMS): String {
    val format = SimpleDateFormat(destPattern, Locale.ENGLISH)
    return format.format(this)
}

fun Date.toCalendar(): Calendar {
    val date = this
    return Calendar.getInstance().apply {
        this.time = date
    }
}


fun String.toDate(@DatePattern srcPattern: String): Date {
    val format = SimpleDateFormat(srcPattern, Locale.ENGLISH)
    return format.parse(this)
}


val nowDateTimeString = ::getFormattedNowDateTime

/**
 * 获取当前日期时间
 * @param
 * @return 格式化后的日期时间字符串
 */
fun getFormattedNowDateTime(): String {
    return Calendar.getInstance().toFormatString(DatePattern.YMD_HMS)
}

/**
 * 当前日期与指定日期的相差小时数
 */
infix fun Date.differHours(other: Date): Float {
    return (this.time - other.time) / (60 * 60 * 1000f)
}

/**
 * 比较当前日期与指定日期时间差 > 指定值
 */
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