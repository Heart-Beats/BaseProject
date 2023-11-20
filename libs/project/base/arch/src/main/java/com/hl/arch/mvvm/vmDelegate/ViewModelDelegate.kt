package com.hl.arch.mvvm.vmDelegate

import android.content.BroadcastReceiver
import android.content.Intent
import android.util.Log
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import com.elvishew.xlog.XLog
import com.hl.api.PublicResp
import com.hl.arch.loading.getLoadingPopup
import com.hl.arch.mvvm.api.event.RequestStateEvent
import com.hl.arch.mvvm.api.event.UiEvent
import com.hl.arch.mvvm.vm.DispatcherVM
import com.hl.arch.mvvm.vm.FlowVM
import com.hl.arch.mvvm.vm.LiveDataVM
import com.hl.arch.utils.apiRespRepeatSafeCollect
import com.hl.arch.utils.repeatSafeCollect
import com.hl.utils.ReflectHelper
import com.hl.utils.onceLastObserve
import com.hl.utils.registerLocalReceiver
import kotlinx.coroutines.flow.Flow

/**
 * @author  张磊  on  2023/02/10 at 18:25
 * Email: 913305160@qq.com
 */
interface ViewModelDelegate {

	/**
	 * 注册 ViewModel 创建的广播
	 *
	 * @param viewModelStoreOwner viewModel 仓库所有者， 一般为 FragmentActivity 或 Fragment 即可
	 */
	fun LifecycleOwner.registerOnViewModelCreated(viewModelStoreOwner: ViewModelStoreOwner)

	/**
	 * ViewModel 创建完成
	 */
	fun <VM> onViewModelCreated(vm: VM, lifecycleOwner: LifecycleOwner)

	/**
	 * 重写处理  api 请求相关事件, 比如： UiShowLoading， UiShowException， UiDismissLoading
	 */
	fun onLiveDataVMCreated(liveDataVM: LiveDataVM) {
		XLog.d("onLiveDataVMCreated --------->$liveDataVM")
	}


	/**
	 * 重写处理  api 请求相关事件, 比如： UiShowLoading， UiShowException， UiDismissLoading
	 */
	fun onFlowVMCreated(flowVM: FlowVM) {
		XLog.d("onFlowVMCreated --------->$flowVM")
	}

	/********************请求时产生的相关 UI 事件处理*****************************************/

	fun onShowLoading(msg: CharSequence) {
		XLog.d("onShowLoading: $msg")
		getLoadingPopup()?.show()
	}

	fun onShowError(throwable: Throwable) {
		XLog.e("onShowError: ", throwable)
	}

	fun onDismissLoading() {
		XLog.d("onDismissLoading: ")

		// 这里需要使用 smartDismiss， 因为若在 show 后很短的时间内收到 dismiss 请求， 会导致弹窗无法正常关闭
		getLoadingPopup()?.smartDismiss()
	}
}


class BaseViewModelDelegate : ViewModelDelegate {
	private val TAG = "BaseViewModelDelegate"


	override fun LifecycleOwner.registerOnViewModelCreated(viewModelStoreOwner: ViewModelStoreOwner) {
		Log.d(TAG, "${this.javaClass.simpleName} registerOnViewModelCreated: ")

		val viewModelStore = viewModelStoreOwner.viewModelStore

		val onReceive: (BroadcastReceiver, Intent) -> Unit = { _, intent ->
			if (intent.action == DispatcherVM.VIEW_MODEL_ON_CREATE) {
				val viewModelMap =
					ReflectHelper(ViewModelStore::class.java).getFiledValue<Map<String, ViewModel>>(
						viewModelStore,
						"map"
					)

				viewModelMap?.values
					?.filter {
						// 过滤出已存放中的当前创建的 viewModel
						it.javaClass.name == intent.getStringExtra(DispatcherVM.VIEW_MODEL_NAME)
					}
					?.forEach { viewModel ->
						onViewModelCreated(viewModel, this)
					}
			}
		}

		// 注册 ViewModel 创建的广播
		this.registerLocalReceiver(DispatcherVM.VIEW_MODEL_ON_CREATE, onReceive = onReceive)
	}


	override fun <VM> onViewModelCreated(vm: VM, lifecycleOwner: LifecycleOwner) {
		Log.d(TAG, "${lifecycleOwner.javaClass.simpleName} onViewModelCreated: $vm")

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

	private fun FlowVM.stateEventFlowCollect(lifecycleOwner: LifecycleOwner) {
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