package com.hl.arch.mvvm.api.event

import com.hl.arch.mvvm.liveData.EventLiveData
import com.hl.utils.isMainThread

/**
 * @author  张磊  on  2021/11/06 at 18:57
 * Email: 913305160@qq.com
 */

fun EventLiveData<UiEvent>.showLoading(showMsg: CharSequence = "") {
	setSafeValue(UiEvent.UiShowLoading(showMsg))
}

fun EventLiveData<UiEvent>.dismissLoading() {
	setSafeValue(UiEvent.UiDismissLoading)
}

fun EventLiveData<UiEvent>.showException(throwable: Throwable) {
	setSafeValue(UiEvent.UiShowException(throwable))
}

fun <T> EventLiveData<T>.setSafeValue(value: T?) {
	if (isMainThread()) {
		this.value = value
	} else {
		this.postValue(value)
	}
}

sealed class UiEvent {
	data class UiShowLoading(var showMsg: CharSequence = "") : UiEvent()

	object UiDismissLoading : UiEvent()

	data class UiShowException(var throwable: Throwable) : UiEvent()
}