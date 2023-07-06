package com.hl.web.helpers

import android.util.Log
import androidx.fragment.app.Fragment
import com.github.lzyzsd.jsbridge.BridgeWebView
import com.hl.web.sdk.ISdk
import com.hl.web.sdk.ISdkImplProvider
import java.lang.reflect.Method

/**
 * @author  张磊  on  2022/02/19 at 23:21
 * Email: 913305160@qq.com
 */
object JsBridgeHelper {

	private var iSdkImplProvider: ISdkImplProvider? = null

	/**
	 * 设置  h5 调用方法的提供者
	 */
	@JvmStatic
	fun setISdkImplProvider(iSdkImplProvider: ISdkImplProvider) {
		JsBridgeHelper.iSdkImplProvider = iSdkImplProvider
	}

	internal fun getUserAgent() = iSdkImplProvider?.provideUserAgent()

	/**
	 * bridgeWebView 注册 H5 相关的调用方法
	 */
	internal fun registerHandlers(webViewFragment: Fragment, bridgeWebView: BridgeWebView) {
		iSdkImplProvider?.also { iSdkImplProvider ->

			val standSdkImpl = iSdkImplProvider.provideStandSdkImpl(webViewFragment, bridgeWebView)
			val projectSdkImpl = iSdkImplProvider.provideProjectSdkImpl(webViewFragment, bridgeWebView)

			listOf(standSdkImpl, projectSdkImpl).forEach { iSdkImpl ->
				iSdkImpl.javaClass.interfaces.forEach { `interface` ->
					// 判断  iSdkImpl 是否继承 ISdk 或者相同
					if (ISdk::class.java.isAssignableFrom(`interface`)) {
						`interface`.methods.asSequence()
							.filter {
								// 过滤出 bridgeWebView 需要注册的所有方法
								isRegisterHandlersMethod(it)
							}.forEach { umSdkMethod ->
								// 实现调用接口中的定义的所有方法
								umSdkMethod.invoke(iSdkImpl, umSdkMethod.name)
							}
					}
				}
			}
		} ?: logJs("registerHandlers", "未设置 h5 调用方法的提供者")
	}

	/**
	 * 是否为 JsBridge 需要注册的方法
	 * @see [ISdk.sdkFunTemplate]
	 */
	private fun isRegisterHandlersMethod(method: Method): Boolean {
		val parameterTypes = method.parameterTypes
		return parameterTypes.size == 1
				&& parameterTypes[0] == String::class.java // 参数 String 类型
				&& method.returnType == Void.TYPE // 返回值 void 类型, 不可用 Void.class 判断
	}
}

fun logJs(jsFun: String, data: String?) {
	Log.e("logJs--->", "$jsFun: $data")
}