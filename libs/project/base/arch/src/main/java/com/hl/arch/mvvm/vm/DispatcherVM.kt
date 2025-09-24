package com.hl.arch.mvvm.vm

import android.util.Log
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import com.hl.api.event.ApiEvent
import com.hl.api.launcher.ApiLauncher
import com.hl.arch.mvvm.liveData.EventLiveData
import kotlinx.coroutines.flow.MutableSharedFlow

/**
 * @author  张磊  on  2023/05/30 at 11:32
 * Email: 913305160@qq.com
 */
abstract class DispatcherVM : ViewModel(), ApiLauncher {

	private val tag = "DispatcherVM"

	internal companion object {
		val viewModelOnCreateSharedFlow = MutableSharedFlow<Pair<LifecycleOwner, ViewModel>>()
	}

	/**
	 * 接口请求事件 ------ LiveData
	 */
	val apiEventFailedLiveData by lazy { EventLiveData<ApiEvent.Failed>() }

	/**
	 * 接口请求事件 ------ Flow
	 */
	val apiEventFailedFlow by lazy { MutableSharedFlow<ApiEvent.Failed>() }

	override fun onCleared() {
		Log.d(tag, "onCleared:  ViewModel(${this.javaClass.simpleName}) 被清除")
		super.onCleared()
	}
}