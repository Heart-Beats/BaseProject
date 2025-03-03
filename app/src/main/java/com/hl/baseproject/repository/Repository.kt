package com.hl.baseproject.repository

import com.blankj.utilcode.util.DeviceUtils
import com.elvishew.xlog.XLog
import com.hl.api.RetrofitManager
import com.hl.api.interceptor.LogProxy
import com.hl.baseproject.configs.AppConfig
import com.hl.baseproject.user.UserManager
import com.hl.utils.toJsonString

/**
 * @author  张磊  on  2021/11/03 at 15:25
 * Email: 913305160@qq.com
 */
object Repository {
	var baseUrl = AppConfig.BASE_URL
	var mockUrl = ""

	inline fun <reified T> buildApi(): T {
		if (mockUrl.isNotBlank()) baseUrl = mockUrl

		val logProxy = object : LogProxy {
			override fun d(tag: String, msg: String) {
				XLog.tag(tag).disableStackTrace().d(msg)
			}

			override fun e(tag: String, msg: String) {
				XLog.tag(tag).disableStackTrace().d(msg)
			}

			override fun i(tag: String, msg: String) {
				XLog.tag(tag).disableStackTrace().i(msg)
			}

			override fun w(tag: String, msg: String) {
				XLog.tag(tag).disableStackTrace().w(msg)
			}
		}

		return RetrofitManager.buildRetrofit(
			baseUrl, logProxy,
			publicHeaderOrParamsBlock = {
				val map: MutableMap<String, Any> = HashMap()
				map["deviceId"] = DeviceUtils.getUniqueDeviceId()
				val deviceInfo: String = map.toJsonString()

				this.addHeaderParam("Content-type", "application/json;charset=UTF-8")
					.addHeaderParam("User-Agent", "Android")
					.addHeaderParam("deviceInfo", deviceInfo)
					.addDynamicHeaderOrParams  { headerInterceptorBuilder, request ->
						headerInterceptorBuilder.addHeaderParam("Authorization", UserManager.getUser().token ?: "")
						headerInterceptorBuilder.addHeaderParam("traceId", createRandomString())
					}
			})
	}

	fun createRandomString(): String {
		val randomString: String = ((Math.random() * 9 + 1) * 16).toString() + ""
		val splitString = randomString.split("\\.").toTypedArray()
		val sb = StringBuilder(splitString.size)
		for (s in splitString) {
			sb.append(s)
		}
		return sb.toString()
	}

	fun getH5Url(route: String) {
		"$baseUrl${route}"
	}
}