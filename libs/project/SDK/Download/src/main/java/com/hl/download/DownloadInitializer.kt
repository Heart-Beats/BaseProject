package com.hl.download

import android.content.Context
import android.util.Log
import androidx.startup.Initializer
import com.hjq.http.EasyConfig
import com.hjq.http.config.IRequestHandler
import com.hjq.http.request.HttpRequest
import com.hjq.http.ssl.HttpSslFactory
import com.hl.xloginit.XLogInitializer
import okhttp3.OkHttpClient
import okhttp3.Response
import java.lang.reflect.Type
import kotlin.concurrent.thread

/**
 * @author  张磊  on  2022/12/06 at 16:47
 * Email: 913305160@qq.com
 */
class DownloadInitializer : Initializer<Unit> {

	private val TAG = this.javaClass.simpleName

	/**
	 * 初始化操作，返回的初始化结果将被缓存
	 */
	override fun create(context: Context) {
		Log.d(TAG, "create: 开始初始化")

		// 异步初始化相关库
		thread {
			initEasyHttp("https://www.baidu.com/", true)
		}
	}

	/**
	 * 依赖关系，返回值是一个依赖组件的列表， 依赖组件优先初始化
	 */
	override fun dependencies(): List<Class<out Initializer<*>>> {
		return listOf(XLogInitializer::class.java)
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

				override fun requestSuccess(httpRequest: HttpRequest<*>, response: Response, type: Type): Any {
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