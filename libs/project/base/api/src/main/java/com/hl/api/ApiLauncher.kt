package com.hl.api

import com.hl.api.error.ExceptionHandler
import com.hl.api.event.ApiEvent
import com.hl.api.event.IApiEventProvider
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

/**
 * @author  张磊  on  2025/03/03 at 14:11
 * Email: 913305160@qq.com
 */
abstract class ApiLauncher : IApiEventProvider {


	/**
	 * 发起网络请求,  这里的调用方式可以借助 suspend 关键字
	 *
	 *    示例： suspend {httpApi.xxx()}.launch{}
	 *
	 * @param  action                   请求成功与失败时的处理
	 */
	protected fun <BODY, Resp : PublicResp<BODY>> (suspend () -> Resp).launchAction(action: ApiLauncherAction<BODY>.() -> Unit) {
		val apiLauncherAction = ApiLauncherAction<BODY>().apply(action)

		this.launch(apiLauncherAction.onFailAction, apiLauncherAction.onSuccessAction)
	}

	/**
	 * 发起网络请求,  这里的调用方式可以借助 suspend 关键字
	 *
	 *     示例： suspend {httpApi.xxx()}.launch{}
	 *
	 * @param  onFail                   失败时的处理
	 * @param  onSuccess                请求成功时的处理
	 */
	protected fun <BODY, Resp : PublicResp<BODY>> (suspend () -> Resp).launch(
		onFail: ((failCode: String, failReason: String) -> Unit)? = null,
		onSuccess: (body: BODY?) -> Unit = {}
	) {
		apiLaunch({ this@launch() }, onFail, onSuccess)
	}

	private fun <BODY, Resp : PublicResp<BODY>> apiLaunch(
		reqBlock: suspend CoroutineScope.() -> Resp,
		onFail: ((failCode: String, failReason: String) -> Unit)? = null,
		onSuccess: (body: BODY?) -> Unit = {}
	) {
		apiLaunch({
			// 发起请求
			val publicResp = this.reqBlock()

			// 分发请求数据
			publicResp.dispatchApiEvent(onFail, onSuccess)
		}, onFail)
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


	/**
	 * 该方法用来分发请求完成后对应的事件
	 *
	 * @param  onFail                   失败时的处理
	 * @param  onSuccess                请求成功时的处理
	 *
	 */
	protected open fun <BODY> PublicResp<BODY>.dispatchApiEvent(
		onFail: ((failCode: String, failReason: String) -> Unit)? = null,
		onSuccess: (body: BODY?) -> Unit = {}
	) {
		val apiEvent = createApiEvent(this.code().toInt(), this.message())

		when (apiEvent) {
			is ApiEvent.Success -> onSuccess(this.respBody)
			is ApiEvent.Failed -> {
				onFail?.invoke(code(), message())
			}
		}
	}
}

class ApiLauncherAction<BODY> {

	var onSuccessAction: ((body: BODY?) -> Unit) = {}
	var onFailAction: ((failCode: String, failReason: String) -> Unit)? = null

	fun onSuccess(action: (body: BODY?) -> Unit) {
		this.onSuccessAction = action
	}

	fun onFail(action: (failCode: String, failReason: String) -> Unit) {
		this.onFailAction = action
	}
}