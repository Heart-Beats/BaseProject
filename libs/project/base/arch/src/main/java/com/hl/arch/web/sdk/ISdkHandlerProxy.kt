package com.hl.arch.web.sdk

import com.github.lzyzsd.jsbridge.BridgeHandler
import com.github.lzyzsd.jsbridge.BridgeWebView
import com.github.lzyzsd.jsbridge.CallBackFunction
import com.hl.arch.web.helpers.logJs
import com.hl.utils.BuildVersionUtil
import com.hl.utils.MethodHook
import com.hl.utils.ProxyHandler
import com.hl.utils.ReflectHelper
import java.lang.reflect.Method

/**
 * @author  张磊  on  2023/01/20 at 20:31
 * Email: 913305160@qq.com
 *
 * 注册并代理 ISdk 相关实现类中声明的 JsBridge 方法
 */
open class ISdkHandlerProxy(private val bridgeWebView: BridgeWebView) : ISdkRegister {

	private val bridgeHandlerProxyLazy by lazy {
		createBridgeHandlerProxy(bridgeWebView)
	}

	override fun commonRegisterHandler(handlerName: String, bridgeHandler: BridgeHandler) {
		commonRegisterHandler(handlerName, bridgeHandlerProxyLazy, bridgeHandler)
	}

	/**
	 * BridgeWebView 通用注册 H5 调用事件方法
	 *
	 * @param handlerName         事件方法名
	 * @param bridgeHandlerProxy  BridgeHandler 的代理对象
	 * @param bridgeHandler       BridgeHandler，即 H5 调用方法时的默认处理
	 */
	private fun commonRegisterHandler(
		handlerName: String,
		bridgeHandlerProxy: ProxyHandler<BridgeHandler> = bridgeHandlerProxyLazy,
		bridgeHandler: BridgeHandler,
	) {
		logJs("BridgeWebView 注册 H5 方法", handlerName)

		// 注册的 Handler 实际为代理对象
		bridgeWebView.registerHandler(handlerName, bridgeHandlerProxy.bind(bridgeHandler))
	}


	/**
	 * 创建 BridgeHandler 的代理对象
	 *
	 * 这里默认 BridgeHandler.handler(...) 方法调用时加上参数日志，处理完成时加上返回数据日志
	 */
	private fun createBridgeHandlerProxy(bridgeWebView: BridgeWebView): ProxyHandler<BridgeHandler> {

		// 循环获取 BridgeWebView 对应的 Class
		var bridgeWebViewClass: Class<in BridgeWebView> = bridgeWebView.javaClass
		if (BuildVersionUtil.isOver8()) {
			while (bridgeWebViewClass.typeName != BridgeWebView::class.java.typeName) {
				bridgeWebViewClass = bridgeWebViewClass.superclass
			}
		} else {
			while (bridgeWebViewClass.simpleName != BridgeWebView::class.java.simpleName) {
				bridgeWebViewClass = bridgeWebViewClass.superclass
			}
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
				if (isExecOnCallBackMethod(method)) {
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
				// 通过注册的代理 BridgeHandler， 取到 H5 调用的方法名，这里只能通过取址的方式
				jsFunName = messageHandlers?.entries?.find { it.value === proxy }?.key ?: ""

				if (isExecHandlerMethod(method)) {
					// JS 方法执行时加上日志
					logJs(jsFunName, args.firstOrNull() as? String)

					// 代理原有的 CallBackFunction 的 onCallBack 方法， 加上执行时的返回数据日志
					args.getOrNull(1)?.run {
						// 修改原有的 CallBackFunction 为代理对象
						args[1] = callBackFunctionProxyHandler.bind(this as CallBackFunction)
					}
				}
			}
		})
	}

	/**
	 * 是否为执行 CallBackFunction 的 onCallBack 方法, 采用此方式可避免混淆时方法名改变的影响
	 * @see [CallBackFunction.onCallBack]
	 */
	private fun isExecOnCallBackMethod(method: Method): Boolean {
		val parameterTypes = method.parameterTypes
		return parameterTypes.size == 1
				&& parameterTypes[0] == String::class.java // 参数 String 类型
				&& method.returnType == Void.TYPE // 返回值 void 类型, 不可用 Void.class 判断
	}

	/**
	 * 是否为执行 BridgeHandler 的 Handler 方法， 采用此方式可避免混淆时方法名改变的影响
	 * @see [BridgeHandler.handler]
	 */
	private fun isExecHandlerMethod(method: Method): Boolean {
		val parameterTypes = method.parameterTypes
		return parameterTypes.size == 2
				&& parameterTypes[0] == String::class.java // 参数 String 类型
				&& parameterTypes[1] == CallBackFunction::class.java // 参数 CallBackFunction 类型
				&& method.returnType == Void.TYPE // 返回值 void 类型, 不可用 Void.class 判断
	}
}