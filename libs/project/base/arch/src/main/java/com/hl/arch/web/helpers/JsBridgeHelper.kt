package com.hl.arch.web.helpers

import android.util.Log
import androidx.fragment.app.Fragment
import com.github.lzyzsd.jsbridge.BridgeHandler
import com.github.lzyzsd.jsbridge.BridgeWebView
import com.github.lzyzsd.jsbridge.CallBackFunction
import com.hl.arch.web.sdk.ISdk
import com.hl.arch.web.sdk.ISdkImplProvider
import com.hl.utils.MethodHook
import com.hl.utils.ProxyHandler
import com.hl.utils.ReflectHelper
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
		this.iSdkImplProvider = iSdkImplProvider
	}

	/**
	 * 注册 H5 相关的调用方法
	 */
	internal fun registerHandlers(currentFragment: Fragment, bridgeWebView: BridgeWebView) {
		iSdkImplProvider?.also { iSdkImplProvider ->
			val bridgeHandlerProxy = createBridgeHandlerProxy(bridgeWebView)

			val standSdkImpl =
				iSdkImplProvider.provideStandSdkImpl(currentFragment, bridgeWebView, bridgeHandlerProxy)
			val projectSdkImpl =
				iSdkImplProvider.provideProjectSdkImpl(currentFragment, bridgeWebView, bridgeHandlerProxy)

			listOf(standSdkImpl, projectSdkImpl).forEach { iSdkImpl ->
				iSdkImpl.javaClass.interfaces.forEach {
					// 判断  it 是否继承 ISdk 或者相同
					if (ISdk::class.java.isAssignableFrom(it)) {
						it.methods.forEach { umSdkMethod ->
							// 实现调用接口中的定义的所有方法
							umSdkMethod.invoke(iSdkImpl, umSdkMethod.name)
						}
					}
				}
			}
		} ?: logJs("registerHandlers", "未设置 h5 调用方法的提供者")
	}

	private fun createBridgeHandlerProxy(bridgeWebView: BridgeWebView): ProxyHandler<BridgeHandler> {
		// 循环获取 BridgeWebView 对应的 Class
		var bridgeWebViewClass: Class<in BridgeWebView> = bridgeWebView.javaClass
		while (bridgeWebViewClass.typeName != BridgeWebView::class.java.typeName) {
			bridgeWebViewClass = bridgeWebViewClass.superclass
		}

		// 动态代理创建时获取该属性，避免多次反射取值
		val messageHandlers by lazy {
			ReflectHelper(bridgeWebViewClass).getFiledValue<Map<String, BridgeHandler>>(
				bridgeWebView, "messageHandlers"
			)
		}

		var jsFunName = ""

		val callBackFunctionProxyHandler = ProxyHandler(object : MethodHook<CallBackFunction>() {

			override fun beforeHookedMethod(
				target: CallBackFunction,
				proxy: CallBackFunction,
				method: Method,
				args: Array<Any>
			) {
				if (method.name == "onCallBack") {
					logJs("${jsFunName}.${method.name}() 执行", "返回数据 == ${args.firstOrNull()}")
				}
			}
		})

		return ProxyHandler(methodHook = object : MethodHook<BridgeHandler>() {

			override fun beforeHookedMethod(
				target: BridgeHandler,
				proxy: BridgeHandler,
				method: Method,
				args: Array<Any>
			) {
				// 通过注册的动态代理 BridgeHandler， 取到 H5 调用的方法名，这里只能通过取址的方式
				jsFunName = messageHandlers?.entries?.find { it.value === proxy }?.key ?: ""

				if (method.name == "handler") {
					// JS 方法执行时加上日志
					logJs(jsFunName, args.firstOrNull() as? String)

					// 代理原有的 CallBackFunction的 onCallBack 方法， 加上执行时的返回数据日志
					args.getOrNull(1)?.run {
						args[1] = callBackFunctionProxyHandler.bind(this as CallBackFunction)
					}
				}
			}
		})
	}

}

fun logJs(jsFun: String, data: String?) {
	Log.e("logJs--->", "$jsFun: $data")
}