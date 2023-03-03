package com.hl.arch.web.sdk

import com.github.lzyzsd.jsbridge.BridgeHandler

/**
 * @author  张磊  on  2023/03/03 at 16:12
 * Email: 913305160@qq.com
 */
interface ISdkRegister {

	/**
	 * BridgeWebView 通用注册 H5 调用事件方法
	 *
	 * @param handlerName         事件方法名
	 * @param bridgeHandler       BridgeHandler，即 H5 调用方法时的处理
	 */
	fun commonRegisterHandler(
		handlerName: String,
		bridgeHandler: BridgeHandler,
	)
}