package com.hl.api.interceptor

import cn.netdiscovery.http.interceptor.LoggingInterceptor
import cn.netdiscovery.http.interceptor.log.LogManager

/**
 * @author  张磊  on  2023/10/13 at 11:11
 * Email: 913305160@qq.com
 */
object HttpLoggingInterceptor {


	/**
	 * 构建网络请求日志拦截器
	 *
	 * @param [logProxy] 日志代理
	 * @param [isPrintLog] 是否打印日志
	 * @param [hideVerticalLine] 是否隐藏垂直线
	 * @param [requestTag] 请求 Tag
	 * @param [responseTag] 响应 Tag
	 * @return [LoggingInterceptor]
	 */
	@JvmOverloads
	@JvmStatic
	fun build(
		logProxy: LogProxy,
		isPrintLog: Boolean = true,
		hideVerticalLine: Boolean = true,
		requestTag: String = "Request",
		responseTag: String = "Response"
	): LoggingInterceptor {

		// log 代理器，必须设置 ，否则无法打印网络请求的 request 、response
		LogManager.logProxy(object : cn.netdiscovery.http.interceptor.log.LogProxy {
			override fun d(tag: String, msg: String) {
				logProxy.d(tag, msg)
			}

			override fun e(tag: String, msg: String) {
				logProxy.e(tag, msg)
			}

			override fun i(tag: String, msg: String) {
				logProxy.i(tag, msg)
			}

			override fun w(tag: String, msg: String) {
				logProxy.w(tag, msg)
			}

		})

		return LoggingInterceptor.Builder()
			.loggable(isPrintLog)
			.androidPlatform()
			.request()
			.requestTag(requestTag)
			.response()
			.responseTag(responseTag)
			.apply {
				if (hideVerticalLine) {
					hideVerticalLine() // 隐藏竖线边框
				}
			}
			.build()
	}
}