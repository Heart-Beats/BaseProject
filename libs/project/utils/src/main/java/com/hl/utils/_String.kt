package com.hl.utils

import android.net.Uri
import kotlin.random.Random

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
 *  密码校验规则（复杂）
 * @param  minLength : 最小长度
 * @param  maxLength : 最大长度
 * @return 是否至少包含数字、大小写字母、特殊字符其中三者，同时符合长度限制
 */
fun String.isMatchPasswordRuleComplex(minLength: Int = 8, maxLength: Int = 20): Boolean {
    val isDigit = """\d"""
    val isLowercaseChar = """a-z"""
    val isUppercaseChar = """A-Z"""
    val isSpecialChar = """\!@#\${'$'}%\^&\*\(\)_+\-=\[\]{};':"\|,\.<>\\/\?"""

    val regexSuffix = """{$minLength,$maxLength}${'$'}"""

    // 数字、小写字母、大写字母
    val regexPrefix1 = """^(?=.*$isDigit)(?=.*[$isLowercaseChar])(?=.*[$isUppercaseChar])"""
    // 数字、小写字母、特殊字符
    val regexPrefix2 = """^(?=.*$isDigit)(?=.*[$isLowercaseChar])(?=.*[$isSpecialChar])"""
    // 数字、大写字母、特殊字符
    val regexPrefix3 = """^(?=.*$isDigit)(?=.*[$isUppercaseChar])(?=.*[$isSpecialChar])"""
    // 小写字母、大写字母、特殊字符
    val regexPrefix4 = """^(?=.*[$isLowercaseChar])(?=.*[$isUppercaseChar])(?=.*[$isSpecialChar])"""
    // 数字、小写字母、大写字母、特殊字符
    val regexPrefix5 = """^(?=.*$isDigit)(?=.*[$isLowercaseChar])(?=.*[$isUppercaseChar])(?=.*[$isSpecialChar])"""

    // 数字、小写字母、大写字母
    val combination1 = """[$isDigit$isLowercaseChar$isUppercaseChar]"""
    // 数字、小写字母、特殊字符
    val combination2 = """[$isDigit$isLowercaseChar$isSpecialChar]"""
    // 数字、大写字母、特殊字符
    val combination3 = """[$isDigit$isUppercaseChar$isSpecialChar]"""
    // 小写字母、大写字母、特殊字符
    val combination4 = """[$isLowercaseChar$isUppercaseChar$isSpecialChar]"""
    // 数字、小写字母、大写字母、特殊字符
    val combination5 = """[$isDigit$isLowercaseChar$isUppercaseChar$isSpecialChar]"""

    val regex1 = "$regexPrefix1$combination1$regexSuffix"
    val regex2 = "$regexPrefix2$combination2$regexSuffix"
    val regex3 = "$regexPrefix3$combination3$regexSuffix"
    val regex4 = "$regexPrefix4$combination4$regexSuffix"
    val regex5 = "$regexPrefix5$combination5$regexSuffix"

    return this.matches(regex1.toRegex()) || this.matches(regex2.toRegex())
            || this.matches(regex3.toRegex()) || this.matches(regex4.toRegex()) || this.matches(regex5.toRegex())
}


/**
 * 获取指定长度的随机字符串
 */
fun getRandomString(sizeOfRandomString: Int): String {
    val ALLOWED_CHARACTERS = "0123456789qwertyuiopasdfghjklzxcvbnmQWERTYUIOPASDFGHJKLZXCVBNM"
    val sb = StringBuilder(sizeOfRandomString)
    for (i in 0 until sizeOfRandomString)
        sb.append(ALLOWED_CHARACTERS[Random.nextInt(ALLOWED_CHARACTERS.length)])
    return sb.toString()
}