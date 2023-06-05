package com.hl.utils

import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

/**
 * @author  张磊  on  2023/06/05 at 14:50
 * Email: 913305160@qq.com
 */

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


/**
 * 将任意数据转化为 Json 字符串
 */
fun Any?.toJson() = GsonUtil.toJson(this)

/**
 * 将 Json 字符串转换为数据类
 */
inline fun <reified T> String?.fromJson() = GsonUtil.fromJson<T>(this)
