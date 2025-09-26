package com.hl.arch.mvvm.vmDelegate

import android.util.Log
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.coroutineScope
import com.elvishew.xlog.XLog
import com.hl.api.PublicResp
import com.hl.arch.loading.getLoadingPopup
import com.hl.arch.mvvm.api.event.UiEvent
import com.hl.arch.mvvm.vm.DispatcherVM
import com.hl.arch.mvvm.vm.FlowVM
import com.hl.arch.mvvm.vm.LiveDataVM
import com.hl.arch.utils.apiRespRepeatSafeCollect
import com.hl.arch.utils.repeatSafeCollect
import com.hl.utils.onceLastObserve
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

/**
 * @author  张磊  on  2023/02/10 at 18:25
 * Email: 913305160@qq.com
 */
interface ViewModelDelegate {

	var lifecycleOwnerName: String

	/**
	 * 注册 ViewModel 已创建的监听
	 *
	 * @param viewModelStoreOwner viewModel 仓库所有者， 一般为 FragmentActivity 或 Fragment 即可
	 */
	fun LifecycleOwner.registerOnViewModelCreated(viewModelStoreOwner: ViewModelStoreOwner)

	/**
	 * ViewModel 创建完成
	 */
	fun <VM> onViewModelCreated(vm: VM, lifecycleOwner: LifecycleOwner) {
		XLog.d("$lifecycleOwnerName onViewModelCreated: $vm")
	}

	/**
	 * 重写处理  api 请求相关事件, 可见 [com.hl.api.event.ApiEvent.Failed]
	 */
	fun onLiveDataVMCreated(liveDataVM: LiveDataVM) {
		XLog.d("$lifecycleOwnerName onLiveDataVMCreated --------->$liveDataVM")
	}


	/**
	 * 重写处理  api 请求相关事件, 可见 [com.hl.api.event.ApiEvent.Failed]
	 */
	fun onFlowVMCreated(flowVM: FlowVM) {
		XLog.d("$lifecycleOwnerName onFlowVMCreated --------->$flowVM")
	}

	/********************请求时产生的相关 UI 事件处理*****************************************/

	fun onShowLoading(msg: CharSequence) {
		XLog.d("$lifecycleOwnerName onShowLoading: $msg")
		getLoadingPopup()?.show()
	}

	fun onShowError(throwable: Throwable) {
		XLog.e("$lifecycleOwnerName onShowError: ", throwable)
	}

	fun onDismissLoading() {
		XLog.d("$lifecycleOwnerName onDismissLoading: ")

		// 这里需要使用 smartDismiss， 因为若在 show 后很短的时间内收到 dismiss 请求， 会导致弹窗无法正常关闭
		getLoadingPopup()?.smartDismiss()
	}
}


class BaseViewModelDelegate : ViewModelDelegate {
	private val TAG = "BaseViewModelDelegate"

	override var lifecycleOwnerName: String = ""

	override fun LifecycleOwner.registerOnViewModelCreated(viewModelStoreOwner: ViewModelStoreOwner) {
		val lifecycleOwner = this@registerOnViewModelCreated
		lifecycleOwnerName = lifecycleOwner.javaClass.simpleName

		Log.d(TAG, "$lifecycleOwnerName registerOnViewModelCreated: ")

		lifecycleOwner.lifecycle.coroutineScope.launch {
			DispatcherVM.viewModelOnCreateSharedFlow.collect { (storeLifecycleOwner, viewModel) ->
				if (storeLifecycleOwner != lifecycleOwner) {
					return@collect
				}

				// 通知 ViewModel 已被创建
				onViewModelCreated(viewModel, lifecycleOwner)
			}
		}
	}


	override fun <VM> onViewModelCreated(vm: VM, lifecycleOwner: LifecycleOwner) {
		super.onViewModelCreated(vm, lifecycleOwner)

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
					onShowLoading(it.showMsg)
				}
				is UiEvent.UiShowException -> {
					onShowError(it.throwable)
				}
				is UiEvent.UiDismissLoading -> {
					onDismissLoading()
				}
			}
		}

		onLiveDataVMCreated(this)
	}

	private fun FlowVM.stateEventFlowCollect(lifecycleOwner: LifecycleOwner) {
		this.uiEvent.safeCollect(lifecycleOwner) {
			when (it) {
				is UiEvent.UiShowLoading -> {
					onShowLoading(it.showMsg)
				}

				is UiEvent.UiShowException -> {
					onShowError(it.throwable)
				}

				is UiEvent.UiDismissLoading -> {
					onDismissLoading()
				}
			}
		}

		onFlowVMCreated(this)
	}

	/********************请求时产生的相关 UI 事件处理*****************************************/
	private inline fun <T> Flow<T>.safeCollect(lifecycleOwner: LifecycleOwner, crossinline action: suspend (value: T) -> Unit) {
		repeatSafeCollect(lifecycleOwner) {
			action(it)
		}
	}

	protected inline fun <T> Flow<PublicResp<T?>>.apiRespSafeCollect(
		lifecycleOwner: LifecycleOwner,
		crossinline isSuccess: PublicResp<T?>.() -> Boolean,
		crossinline onFail: (failCode: String, failReason: String) -> Unit = { _, _ -> },
		crossinline onSuccess: (value: T) -> Unit
	) {
		apiRespRepeatSafeCollect(lifecycleOwner, isSuccess, onFail, onSuccess)
	}
}