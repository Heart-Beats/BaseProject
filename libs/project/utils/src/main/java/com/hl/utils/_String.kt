package com.hl.utils

import android.net.Uri
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

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


/**
 * 按 3-4-4 格式化手机号
 *
 * @param separator  分隔符
 * @return           格式化后的电话号码
 */
fun String.formatPhoneBy344(separator: Char = '-'): String {
    val formattedNumber: String
    val formattingNumber = StringBuilder()
    if (this.isNotEmpty()) {
        //插入分隔符
        for (i in 0 until this.length) {
            // 手机号的格式化为344(xxx xxxx xxxx),所以只在第 4，9 的位置插入分隔符
            if (i == 3 || i == 8 || this[i] != separator) {
                formattingNumber.append(this[i])
                //是否是包含空格的长度
                val isSpaceIndex = formattingNumber.length == 4 || formattingNumber.length == 9
                //最后一个字符是否为空格，
                val isLastSpace = formattingNumber[formattingNumber.length - 1] != separator
                //最后一个字符不为空格
                if (isSpaceIndex && isLastSpace) {
                    //在最后位置插入空格
                    formattingNumber.insert(formattingNumber.length - 1, separator)
                }
            }
        }
    }
    formattedNumber = formattingNumber.toString()
    return formattedNumber
}

fun String?.getLastPathSegment(): String {
    if (this == null) {
        return ""
    }
    val uri = Uri.parse(this)
    return uri.lastPathSegment ?: ""
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

/**
 * 是否为 Json 字符串
 */
fun String?.isJson(): Boolean {
    val content = this ?: ""
    return try {
        if (content.contains("[") && content.contains("]")) {
            JSONArray(content)
            true
        } else {
            JSONObject(content)
            true
        }
    } catch (e: JSONException) {
        false
    }
}