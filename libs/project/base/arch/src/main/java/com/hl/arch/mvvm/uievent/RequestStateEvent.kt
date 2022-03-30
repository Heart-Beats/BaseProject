package com.hl.arch.mvvm.api.event

/**
 * @author  张磊  on  2021/11/06 at 18:59
 * Email: 913305160@qq.com
 */

sealed class RequestStateEvent {

	companion object {
		fun createLoadingEvent(loadingMsg: String = "") = LoadingEvent(loadingMsg)
		fun createCompletedEvent() = CompletedEvent
		fun createErrorEvent(throwable: Throwable) = ErrorEvent(throwable)
	}

	data class LoadingEvent(var showMsg: CharSequence) : RequestStateEvent()

	object CompletedEvent : RequestStateEvent()

	data class ErrorEvent(var throwable: Throwable) : RequestStateEvent()
}
