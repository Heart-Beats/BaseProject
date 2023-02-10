package com.hl.arch.mvvm.vmDelegate

import android.util.Log
import androidx.lifecycle.LifecycleOwner
import com.hl.arch.loading.loadingPopupWeakReference
import com.hl.arch.mvvm.PublicResp
import com.hl.arch.mvvm.api.event.RequestStateEvent
import com.hl.arch.mvvm.api.event.UiEvent
import com.hl.arch.mvvm.vm.FlowVM
import com.hl.arch.mvvm.vm.LiveDataVM
import com.hl.arch.utils.apiRespRepeatSafeCollect
import com.hl.arch.utils.onceLastObserve
import com.hl.arch.utils.repeatSafeCollect
import kotlinx.coroutines.flow.Flow

/**
 * @author  张磊  on  2023/02/10 at 18:25
 * Email: 913305160@qq.com
 */
interface ViewModelDelegate {
	/**
	 * ViewModel 创建完成
	 */
	fun <VM> onViewModelCreated(vm: VM, viewLifecycleOwner: LifecycleOwner)

	/**
	 * 重写处理  api 请求相关事件
	 */
	fun onLiveDataVMCreated(liveDataVM: LiveDataVM) {}


	/**
	 * 重写处理  api 请求相关事件
	 */
	fun onFlowVMCreated(flowVM: FlowVM) {}

	/********************请求时产生的相关 UI 事件处理*****************************************/

	fun onShowLoading(msg: CharSequence) {
		loadingPopupWeakReference.get()?.show()
	}

	fun onShowError(throwable: Throwable) {
	}

	fun onDismissLoading() {
		loadingPopupWeakReference.get()?.dismiss()
	}
}


class BaseViewModelDelegate : ViewModelDelegate {
	private val TAG = "ViewModelDelegate"

	override fun <VM> onViewModelCreated(vm: VM, lifecycleOwner: LifecycleOwner) {
		if (vm is LiveDataVM) {
			vm.eventObserve(lifecycleOwner)
		} else if (vm is FlowVM) {
			vm.stateEventFlowCollect(lifecycleOwner)
		}
	}

	private fun LiveDataVM.eventObserve(viewLifecycleOwner: LifecycleOwner) {
		// 使用 onceLastObserve, 保证只有最后添加的页面才会收到通知
		this.uiEvent.onceLastObserve(viewLifecycleOwner) {
			when (it) {
				is UiEvent.UiShowLoading -> {
					Log.d(TAG, "UiShowLoading ---->$it")
					onShowLoading(it.showMsg)
				}
				is UiEvent.UiShowException -> {
					Log.d(TAG, "UiShowException  ---->$it")
					onShowError(it.throwable)
				}
				is UiEvent.UiDismissLoading -> {
					Log.d(TAG, "UiDismissLoading  ---->$it")
					onDismissLoading()
				}
			}
		}

		onLiveDataVMCreated(this)
	}

	protected open fun FlowVM.stateEventFlowCollect(lifecycleOwner: LifecycleOwner) {
		this.requestStateEventFlow.safeCollect(lifecycleOwner) {
			when (it) {
				is RequestStateEvent.LoadingEvent -> {
					Log.d(TAG, "LoadingEvent: ")
					onShowLoading(it.showMsg)
				}
				is RequestStateEvent.ErrorEvent -> {
					Log.d(TAG, "ErrorEvent: ")
					onShowError(it.throwable)
				}
				is RequestStateEvent.CompletedEvent -> {
					Log.d(TAG, "CompletedEvent: ")
					onDismissLoading()
				}
			}
		}

		onFlowVMCreated(this)
	}

	/********************请求时产生的相关 UI 事件处理*****************************************/
	private inline fun <T> Flow<T>.safeCollect(
		lifecycleOwner: LifecycleOwner,
		crossinline action: suspend (value: T) -> Unit
	) {
		repeatSafeCollect(lifecycleOwner, action)
	}

	protected inline fun <T> Flow<PublicResp<T>?>.apiRespSafeCollect(
		lifecycleOwner: LifecycleOwner,
		crossinline isSuccess: PublicResp<T>.() -> Boolean,
		crossinline onFail: (failCode: String, failReason: String) -> Unit = { _, _ -> },
		crossinline onSuccess: (value: T) -> Unit
	) {
		apiRespRepeatSafeCollect(lifecycleOwner, isSuccess, onFail, onSuccess)
	}
}