package com.hl.utils

import android.util.Base64

/**
 * @author 张磊  on  2025/08/21 at 20:40
 * Email: 913305160@qq.com
 */
object Base64Util {

	fun encode(input: ByteArray?): ByteArray {
		val bytes = input ?: byteArrayOf()
		if (bytes.isEmpty()) return ByteArray(0)
		return Base64.encode(bytes, Base64.NO_WRAP)
	}


	fun encode2String(input: ByteArray?): String {
		val bytes = input ?: byteArrayOf()
		if (bytes.isEmpty()) return ""
		return Base64.encodeToString(bytes, Base64.NO_WRAP)
	}

	fun decode(input: String?): ByteArray {
		val str = input ?: ""
		if (str.isEmpty()) return ByteArray(0)
		return Base64.decode(str, Base64.NO_WRAP)
	}


	fun decode(input: ByteArray?): ByteArray {
		val bytes = input ?: byteArrayOf()
		if (bytes.isEmpty()) return ByteArray(0)
		return Base64.decode(bytes, Base64.NO_WRAP)
	}

	fun decode2String(input: String?): String {
		return String(decode(input))
	}

	fun decode2String(input: ByteArray?): String {
		return String(decode(input))
	}

	/**
	 * 正则 + 解码 校验判断是否为 base64 字符串
	 */
	fun isBase64(input: String?): Boolean {
		val str = input ?: ""

		if (str.isEmpty()) return false

		if (str.length % 4 != 0 || !str.matches(Regex("^[A-Za-z0-9+/]+={0,2}$"))) return false

		return try {
			encode2String(decode(str)) == str
		} catch (_: Exception) {
			false
		}
	}
}

fun ByteArray.toBase64String(): String {
	return Base64Util.encode2String(this)
}

fun String.toBase64String(): String {
	return this.toByteArray().toBase64String()
}
