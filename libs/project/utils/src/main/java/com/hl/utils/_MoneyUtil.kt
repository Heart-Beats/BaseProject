package com.hl.utils

import java.math.BigDecimal
import java.math.RoundingMode
import java.text.DecimalFormat


fun yuan2fen(yuan: String, length: Int = 12): String {
    val newYuan = yuan2doubleYuan(yuan)
    return yuan2fen(newYuan, length)
}

fun yuan2fen(yuan: Double, length: Int = 12): String {
    val bigYuan = BigDecimal.valueOf(yuan).setScale(2, RoundingMode.HALF_UP)
    val bigFen = bigYuan.times(BigDecimal.valueOf(100)).setScale(0, RoundingMode.HALF_UP)
    val fen = bigFen.toLong()
    return String.format("%0${length}d", fen)
}

fun yuan2formatYuan(yuan: Double, useComma: Boolean = true): String {
    val pattern = when (useComma) {
        true -> ",##0.00"
        else -> "##0.00"
    }
    val decimalFormat = DecimalFormat(pattern)
    return decimalFormat.format(yuan)
}

fun yuan2formatYuan(yuan: String?, useComma: Boolean = true): String {
    return yuan2formatYuan(yuan2doubleYuan(yuan ?: "0"), useComma)
}

fun fen2yuan(fen: String): String {
    val yuan: Double = fen.toLong().toDouble() / 100
    return String.format("%.2f", yuan)
}

fun yuan2doubleYuan(yuan: String): Double {
    val newYuan = yuan.replace(",", "").replace("，", "")
    return newYuan.toDoubleOrNull() ?: 0.0
}

fun String?.toDoubleYuan(defYuan: Double = 0.0): Double {
    return yuan2doubleYuan(this ?: defYuan.toString())
}

