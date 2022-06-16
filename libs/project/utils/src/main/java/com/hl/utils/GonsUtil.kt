package com.hl.utils

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

/**
 * @author  张磊  on  2022/06/16 at 10:47
 * Email: 913305160@qq.com
 */
object GsonUtil {

	val gson = Gson()


	inline fun <reified T> fromJson(json: String): T {
		val type = TypeToken.get(T::class.java).type
		return gson.fromJson(json, type)
	}

	fun toJson(any: Any): String {
		return gson.toJson(any)
	}
}