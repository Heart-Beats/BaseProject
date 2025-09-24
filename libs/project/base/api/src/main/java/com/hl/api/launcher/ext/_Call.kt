package com.hl.api.launcher.ext

import com.hl.api.PublicResp
import com.hl.api.error.ExceptionHandler
import com.hl.api.launcher.ApiLauncher
import retrofit2.Call
import kotlin.concurrent.thread

/**
 * @author  张磊  on  2025/09/23 at 16:36
 * Email: 913305160@qq.com
 */


internal fun <BODY, Resp : Call<PublicResp<BODY>>> (() -> Resp).apiLaunch(
	apiLauncher: ApiLauncher,
	onFail: ((failCode: String, failReason: String) -> Unit)? = null,
	onSuccess: (body: BODY?) -> Unit = {}
) {
	val block: () -> Unit = {
		// 发起请求
		val publicResp = this().execute().body()

		with(apiLauncher){
			// 分发请求数据
			publicResp?.dispatchApiEvent(onFail, onSuccess)
		}
	}

	apiLaunch(block, onFail)
}

private fun apiLaunch(block: () -> Unit, onFail: ((failCode: String, failReason: String) -> Unit)? = null) {
	apiLaunch(onException = {
		val responseThrowable = ExceptionHandler.handleException(it)
		onFail?.invoke(responseThrowable.code.toString(), responseThrowable.message)
		true
	}, block = block)
}

private fun apiLaunch(onException: (Throwable) -> Boolean, block: () -> Unit) {
	thread {
		runCatching(block).onFailure {
			if (onException(it)) {
				return@onFailure
			}
		}
	}
}