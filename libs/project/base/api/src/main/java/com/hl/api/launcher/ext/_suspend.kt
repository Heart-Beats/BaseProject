package com.hl.api.launcher.ext

import com.hl.api.PublicResp
import com.hl.api.error.ExceptionHandler
import com.hl.api.launcher.ApiLauncher
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

/**
 * @author  张磊  on  2025/09/23 at 16:17
 * Email: 913305160@qq.com
 */

internal fun <BODY, Resp : PublicResp<BODY>> (suspend CoroutineScope.() -> Resp).apiLaunch(
	apiLauncher: ApiLauncher,
	onFail: ((failCode: String, failReason: String) -> Unit)? = null,
	onSuccess: (body: BODY?) -> Unit = {}
) {
	val block: suspend CoroutineScope.() -> Unit = {
		// 发起请求
		val publicResp = this@apiLaunch()

		with(apiLauncher){
			// 分发请求数据
			publicResp.dispatchApiEvent(onFail, onSuccess)
		}
	}

	apiLaunch(block, onFail)
}

private fun apiLaunch(
	block: suspend CoroutineScope.() -> Unit,
	onFail: ((failCode: String, failReason: String) -> Unit)? = null
): Job {
	return apiLaunch(onException = {
		val responseThrowable = ExceptionHandler.handleException(it)
		onFail?.invoke(responseThrowable.code.toString(), responseThrowable.message)
		true
	}, block = block)
}

private fun apiLaunch(
	onException: (Throwable) -> Boolean,
	block: suspend CoroutineScope.() -> Unit
): Job {
	val coroutineScope = CoroutineScope(Dispatchers.IO + CoroutineExceptionHandler { _, throwable ->
		if (onException(throwable)) {
			return@CoroutineExceptionHandler
		}
	})

	return coroutineScope.launch {
		block()
	}
}