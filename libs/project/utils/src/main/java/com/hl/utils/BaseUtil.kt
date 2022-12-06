package com.hl.utils

import android.app.Application
import com.elvishew.xlog.LogConfiguration
import com.elvishew.xlog.LogLevel
import com.elvishew.xlog.XLog
import com.hjq.http.EasyConfig
import com.hjq.http.config.IRequestHandler
import com.hjq.http.request.HttpRequest
import com.hjq.http.ssl.HttpSslFactory
import okhttp3.OkHttpClient
import okhttp3.Response
import java.lang.reflect.Type
import kotlin.concurrent.thread

/**
 * @author  张磊  on  2022/08/05 at 17:10
 * Email: 913305160@qq.com
 */
object BaseUtil {

	lateinit var app: Application

	fun init(app: Application) {
		this.app = app
		val debug = BuildConfig.DEBUG

		initXlog(debug)

		// 异步初始化相关库
		thread {
			initEasyHttp("https://www.baidu.com/", debug)
		}
	}


	private fun initXlog(isPrintLog: Boolean) {
		val logConfig = LogConfiguration.Builder()
			.tag("X-LOG")
			.logLevel(if (isPrintLog) LogLevel.ALL else LogLevel.INFO)
			.enableStackTrace(1)
			.build()
		XLog.init(logConfig)
	}


	private fun initEasyHttp(baseUrl: String, isPrintLog: Boolean) {
		val sslConfig = HttpSslFactory.generateSslConfig()
		val okHttpClient: OkHttpClient = OkHttpClient.Builder()
			.sslSocketFactory(sslConfig.sslSocketFactory, sslConfig.trustManager)
			.hostnameVerifier(HttpSslFactory.generateUnSafeHostnameVerifier())
			.build()
		EasyConfig.with(okHttpClient)
			// 是否打印日志
			.setLogEnabled(isPrintLog)
			.setLogTag("EasyHttp")
			// 设置服务器配置
			.setServer(baseUrl)
			// 设置请求处理策略
			.setHandler(object : IRequestHandler {

				override fun requestSucceed(httpRequest: HttpRequest<*>, response: Response, type: Type): Any {
					//定义成功时的返回数据，请求时的接收类型需要与其保持一致
					return response
				}

				override fun requestFail(httpRequest: HttpRequest<*>, e: Exception): Exception {
					return e
				}
			})
			// 设置请求重试次数
			.setRetryCount(3)
			// 添加全局请求参数
			//.addParam("token", "6666666")
			// 添加全局请求头
			//.addHeader("time", "20191030")
			// 启用配置
			.into()
	}
}