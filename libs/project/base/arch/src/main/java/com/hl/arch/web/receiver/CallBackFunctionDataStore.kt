package com.hl.arch.web.receiver

import com.github.lzyzsd.jsbridge.CallBackFunction

/**
 * @author  张磊  on  2022/06/17 at 20:06
 * Email: 913305160@qq.com
 */
internal object CallBackFunctionDataStore {

	private val callBackFunctionMap = hashMapOf<String, CallBackFunction>()

	fun putCallBackFunction(key: String, callBackFunction: CallBackFunction) {
		callBackFunctionMap.put(key, callBackFunction)
	}

	fun getCallBackFunction(key: String): CallBackFunction? {
		return callBackFunctionMap[key]
	}

	fun clearCallBackFunction() {
		callBackFunctionMap.clear()
	}

	fun removeCallBackFunction(key: String): CallBackFunction? {
		return callBackFunctionMap.remove(key)
	}

}