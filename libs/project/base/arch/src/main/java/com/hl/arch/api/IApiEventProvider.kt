package com.hl.arch.api

/**
 * @author  张磊  on  2023/02/17 at 16:37
 * Email: 913305160@qq.com
 */

/**
 * ApiEvent 提供器， 即接口请求事件提供接口
 */
interface IApiEventProvider {

	/**
	 * ApiEvent 成功时的定义
	 */
	fun provideSuccessApiEvent(): ApiEvent.Success

	/**
	 * ApiEvent 失败时的定义
	 */
	fun provideFailedApiEvents(): List<ApiEvent.Failed>

	/**
	 * 根据 code 与 msg 创建相应的 ApiEvent
	 */
	fun createApiEvent(code: Int, msg: String): ApiEvent {
		val successApiEvent = provideSuccessApiEvent()
		val failedApiEvent = provideFailedApiEvents()

		// 定义的所有 ApiEvent 事件
		val apiEvents = failedApiEvent + successApiEvent

		val apiEvent = apiEvents.find { it.code == code } ?: ApiEvent.Failed.UnknownError(code)

		apiEvent.msg = msg.ifBlank { "未知错误" }

		return apiEvent
	}
}