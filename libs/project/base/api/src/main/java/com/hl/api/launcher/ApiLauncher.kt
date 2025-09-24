package com.hl.api.launcher

import com.hl.api.PublicResp
import com.hl.api.error.ExceptionHandler
import com.hl.api.event.ApiEvent
import com.hl.api.event.IApiEventProvider
import com.hl.api.launcher.ext.apiLaunch
import kotlinx.coroutines.CoroutineScope
import retrofit2.Call

/**
 * @author  张磊  on  2025/03/03 at 14:11
 * Email: 913305160@qq.com
 */
interface ApiLauncher : IApiEventProvider {


	/******************************** suspend 的 PublicResp<BODY> 相关调用     *********************************/

	/**
	 * 发起网络请求,  这里的调用方式可以借助 suspend 关键字
	 *
	 *    示例： suspend {httpApi.xxx()}.launch{}
	 *
	 * @param  action                   请求成功与失败时的处理
	 */
	fun <BODY, Resp : PublicResp<BODY>> (suspend CoroutineScope.() -> Resp).launchAction(action: ApiLauncherAction<BODY>.() -> Unit) {
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
	fun <BODY, Resp : PublicResp<BODY>> (suspend CoroutineScope.() -> Resp).launch(
		onFail: ((failCode: String, failReason: String) -> Unit)? = null,
		onSuccess: (body: BODY?) -> Unit = {}
	) {
		apiLaunch(this@ApiLauncher, onFail, onSuccess)
	}


	/******************************** 原始的 Call<PublicResp<BODY>> 相关调用     *********************************/

	/**
	 * 发起网络请求
	 *
	 *   示例：  {httpApi.xxx()}.launch{}
	 *
	 * @param  action                   请求成功与失败时的处理
	 */
	fun <BODY, Resp : Call<PublicResp<BODY>>> (() -> Resp).launchAction(action: ApiLauncherAction<BODY>.() -> Unit) {
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
	fun <BODY, Resp : Call<PublicResp<BODY>>> (() -> Resp).launch(
		onFail: ((failCode: String, failReason: String) -> Unit)? = null,
		onSuccess: (body: BODY?) -> Unit = {}
	) {
		apiLaunch(this@ApiLauncher, onFail, onSuccess)
	}


	/************************************  PublicResp<BODY> 相关调用  **************************************/

	/**
	 * 该方法用来分发请求完成后对应的事件
	 *
	 * @param  onFail                   失败时的处理
	 * @param  onSuccess                请求成功时的处理
	 *
	 */
	fun <BODY> PublicResp<BODY>.dispatchApiEvent(
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


	/**************************************** 错误码相关 ******************************************/

	/**
	 * 是否是请求异常的错误码
	 */
	fun isRequestExceptionByCode(errorCode: Int): Boolean {
		return ExceptionHandler.isRequestExceptionByCode(errorCode)
	}
}