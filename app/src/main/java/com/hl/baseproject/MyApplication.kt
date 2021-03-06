package com.hl.baseproject

import android.app.Application
import android.content.Context
import android.util.Log
import com.elvishew.xlog.LogConfiguration
import com.elvishew.xlog.LogLevel
import com.elvishew.xlog.XLog
import com.hjq.http.EasyConfig
import com.hjq.http.config.IRequestHandler
import com.hjq.http.request.HttpRequest
import com.hjq.http.ssl.HttpSslFactory
import com.tencent.smtt.export.external.TbsCoreSettings
import com.tencent.smtt.sdk.QbSdk
import com.tencent.smtt.sdk.TbsListener
import com.tencent.smtt.utils.TbsLogClient
import okhttp3.OkHttpClient
import okhttp3.Response
import java.lang.reflect.Type

/**
 * @author  张磊  on  2022/06/10 at 16:13
 * Email: 913305160@qq.com
 */
class MyApplication : Application() {

	override fun onCreate() {
		super.onCreate()

		val debug = BuildConfig.DEBUG

		initXlog(debug)

		initEasyHttp("https://www.baidu.com/", debug)

		initX5(this, debug)

		SDKInitHelper.preInitSdk(this)
	}

	private fun initXlog(isPrintLog: Boolean) {
		val logConfig = LogConfiguration.Builder()
			.logLevel(if (isPrintLog) LogLevel.ALL else LogLevel.INFO)
			.enableStackTrace(1)
			.addInterceptor {
				//......
				it
			}
			.build()
		XLog.init(logConfig)
	}

	private fun initEasyHttp(baseUrl: String, isPrintLog: Boolean) {
		val sslConfig = HttpSslFactory.generateSslConfig()
		val okHttpClient: OkHttpClient = OkHttpClient.Builder()
			.sslSocketFactory(sslConfig.sslSocketFactory, sslConfig.trustManager)
			.hostnameVerifier(HttpSslFactory.generateUnSafeHostnameVerifier())
			.build()
		EasyConfig.with(okHttpClient) // 是否打印日志
			.setLogEnabled(isPrintLog)
			.setLogTag("EasyHttp") // 设置服务器配置
			.setServer(baseUrl) // 设置请求处理策略
			.setHandler(object : IRequestHandler {

				override fun requestSucceed(httpRequest: HttpRequest<*>, response: Response, type: Type): Any {
					//定义成功时的返回数据，请求时的接收类型需要与其保持一致
					return response
				}

				override fun requestFail(httpRequest: HttpRequest<*>, e: java.lang.Exception): java.lang.Exception {
					return e
				}
			}) // 设置请求重试次数
			.setRetryCount(3) // 添加全局请求参数
			//.addParam("token", "6666666")
			// 添加全局请求头
			//.addHeader("time", "20191030")
			// 启用配置
			.into()
	}

	private fun initX5(context: Context, isPrintLog: Boolean) {
		// 首次初始化冷启动优化, TBS内核首次使用和加载时，ART虚拟机会将Dex文件转为Oat，该过程由系统底层触发且耗时较长，
		// 很容易引起anr问题，解决方法是使用TBS的 ”dex2oat优化方案“。

		// 在调用TBS初始化、创建WebView之前进行如下配置
		val map: HashMap<String, Any> = HashMap()
		map[TbsCoreSettings.TBS_SETTINGS_USE_SPEEDY_CLASSLOADER] = true
		map[TbsCoreSettings.TBS_SETTINGS_USE_DEXLOADER_SERVICE] = true
		QbSdk.initTbsSettings(map)
		// 支持移动数据进行初始化下载
		QbSdk.setDownloadWithoutWifi(true)

		if (isPrintLog) {
			QbSdk.setTbsLogClient(object : TbsLogClient(this) {
				override fun i(tag: String?, msg: String?) {
					Log.i(tag, msg ?: "")
				}

				override fun e(tag: String?, msg: String?) {
					Log.e(tag, msg ?: "")
				}

				override fun w(tag: String?, msg: String?) {
					Log.w(tag, msg ?: "")
				}

				override fun d(tag: String?, msg: String?) {
					Log.d(tag, msg ?: "")
				}

				override fun v(tag: String?, msg: String?) {
					Log.v(tag, msg ?: "")
				}
			})
		}

		QbSdk.setTbsListener(object : TbsListener {
			override fun onDownloadFinish(i: Int) {
				//tbs内核下载完成回调
				XLog.d("X5core tbs内核下载完成")
			}

			override fun onInstallFinish(i: Int) {
				//内核安装完成回调，
				XLog.d("X5core tbs内核安装完成")
			}

			override fun onDownloadProgress(i: Int) {
				//下载进度监听
				XLog.d("X5core tbs内核正在下载中----->$i")
			}
		})

		QbSdk.initX5Environment(context, object : QbSdk.PreInitCallback {
			override fun onCoreInitFinished() {
				XLog.d("X5core x5加载结束")
			}

			override fun onViewInitFinished(b: Boolean) {
				XLog.d("X5core x5加载结束$b")
			}
		})
	}
}