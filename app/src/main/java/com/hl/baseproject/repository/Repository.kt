package com.youma.yky.app.main

import com.blankj.utilcode.util.DeviceUtils
import com.hl.api.RetrofitManager
import com.hl.api.interceptor.RequestHeaderOrParamsInterceptor
import com.hl.baseproject.configs.AppConfig
import com.hl.utils.toJsonString
import com.youma.yky.merchant.user.UserManager

/**
 * @author  张磊  on  2021/11/03 at 15:25
 * Email: 913305160@qq.com
 */
object Repository {
	var baseUrl = AppConfig.BASE_URL
	var isPrintHttpLog = AppConfig.isDebug
	var mockUrl = ""

	inline fun <reified T> buildApi(): T {
		if (mockUrl.isNotBlank()) baseUrl = mockUrl

		return RetrofitManager.buildRetrofit(
			baseUrl, isPrintHttpLog,
			publicHeaderOrParamsBlock = {
				val map: MutableMap<String, Any> = HashMap()
				map["deviceId"] = DeviceUtils.getUniqueDeviceId()
				val deviceInfo: String = map.toJsonString()

				this.addHeaderParam("Content-type", "application/json;charset=UTF-8")
					.addHeaderParam("User-Agent", "Android")
					.addHeaderParam("deviceInfo", deviceInfo)
					.addDynamicHeaderOrParams(object : RequestHeaderOrParamsInterceptor.IDynamicHeaderOrParams {

						override fun onDynamic(headerInterceptorBuilder: RequestHeaderOrParamsInterceptor.Builder) {
							headerInterceptorBuilder
								.addHeaderParam("Authorization", UserManager.getUser().token ?: "")
								.addHeaderParam("traceId", createRandomString())
						}
					})
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
		"${baseUrl}${route}"
	}
}