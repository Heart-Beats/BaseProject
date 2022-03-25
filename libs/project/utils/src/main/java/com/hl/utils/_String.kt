package com.hl.utils

import android.net.Uri
import java.text.SimpleDateFormat
import java.util.*

/**
 * 身份证号脱敏
 */
fun String.coverIDCard(): String {
    return if (length <= 10) {
        this
    } else {
        val startIndex = 6
        val endIndex = length - 4
        val starSize = endIndex - startIndex
        replaceRange(startIndex, endIndex, String(CharArray(starSize) { '*' }))
    }
}

/**
 * 银行卡脱敏
 */
fun String.coverCardNo(startIndex: Int = 4): String {
    return if (length <= 8) {
        this
    } else {
        val endIndex = length - 4
        replaceRange(startIndex, endIndex, "****")
    }
}

/**
 * 手机号脱敏
 */
fun String.coverPhoneNo(): String {
    return if (length <= 7) {
        this
    } else {
        val startIndex = 3
        val endIndex = length - 4
        val starSize = endIndex - startIndex
        val stars = CharArray(starSize) {
            '*'
        }
        replaceRange(startIndex, endIndex, String(stars))
    }
}

fun String?.getLastPathSegment(): String {
    if (this == null) {
        return ""
    }
    val uri = Uri.parse(this)
    return uri.lastPathSegment ?: ""
}

fun String?.formatDate(srcPattern: String): Date? {
    if (this.isNullOrEmpty()) {
        return null
    }
    val patternLength = srcPattern.length
    val srcFormat = SimpleDateFormat(srcPattern, Locale.ENGLISH)
    val source = if (patternLength < length) {
        substring(0, patternLength)
    } else {
        this
    }
    return try {
        srcFormat.parse(source)
    } catch (e: Exception) {
        null
    }
}

fun String.formatDateString(srcPattern: String, destPattern: String): String {
    val destFormat = SimpleDateFormat(destPattern, Locale.ENGLISH)
    return try {
        val date = formatDate(srcPattern)
        destFormat.format(date)
    } catch (e: Exception) {
        ""
    }
}

/**
 * 统计汉字个数
 */
fun String.countHan(): Int {
    val reg = "^[\u4e00-\u9fa5]{1}$"
    var result = 0
    this.forEach { char ->
        char.toString().matches(Regex(reg)).let { match ->
            if (match) {
                result++
            }
        }
    }
    return result
}

fun Int.left0(length: Int = 4): String {
    return String.format("%0${length}d", this)
}

/**
 *  密码校验规则
 * @param  minLength : 最小长度
 * @param  maxLength : 最大长度
 * @return 是否仅包含字母和数字，同时符合长度限制
 */
fun String.isMatchPasswordRule(minLength: Int = 6, maxLength: Int = 20): Boolean {
    val regex = """^(?=.*[A-Z])(?=.*[a-z])(?=.*\d).{$minLength,$maxLength}$"""
    return this.matches(regex.toRegex())
}