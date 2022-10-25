package com.hl.arch.mvvm.fragment

import android.util.Log
import com.hl.arch.base.BaseFragment
import com.hl.arch.loading.loadingPopupWeakReference
import com.hl.arch.mvvm.PublicResp
import com.hl.arch.mvvm.api.event.RequestStateEvent
import com.hl.arch.mvvm.api.event.UiEvent
import com.hl.arch.utils.apiRespRepeatSafeCollect
import com.hl.arch.utils.repeatSafeCollect
import com.hl.arch.mvvm.vm.FlowVM
import com.hl.arch.mvvm.vm.LiveDataVM
import com.hl.arch.utils.onceLastObserve
import kotlinx.coroutines.flow.Flow

/**
 * @Author  张磊  on  2020/08/28 at 18:35
 * Email: 913305160@qq.com
 */
abstract class MvvmBaseFragment : BaseFragment() {

    private companion object {
        const val TAG = "MvvmBaseFragment"
    }

    fun <VM> onViewModelCreated(vm: VM) {
        if (vm is LiveDataVM) {
            vm.eventObserve()
        } else if (vm is FlowVM) {
            vm.stateEventFlowCollect()
        }
    }

    private fun LiveDataVM.eventObserve() {
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

    protected open fun FlowVM.stateEventFlowCollect() {
        this.requestStateEventFlow.safeCollect {
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

    /**
     * 重写处理  api 请求相关事件
     */
    abstract fun onLiveDataVMCreated(liveDataVM: LiveDataVM)


    /**
     * 重写处理  api 请求相关事件
     */
    abstract fun onFlowVMCreated(flowVM: FlowVM)

    /********************请求时产生的相关 UI 事件处理*****************************************/

    protected open fun onShowLoading(msg: CharSequence) {
        loadingPopupWeakReference.get()?.show()
    }

    protected open fun onShowError(throwable: Throwable) {
    }

    protected open fun onDismissLoading() {
        loadingPopupWeakReference.get()?.dismiss()
    }

    private inline fun <T> Flow<T>.safeCollect(crossinline action: suspend (value: T) -> Unit) {
        repeatSafeCollect(this@MvvmBaseFragment, action)
    }

    protected inline fun <T> Flow<PublicResp<T>?>.apiRespSafeCollect(
        crossinline isSuccess: PublicResp<T>.() -> Boolean,
        crossinline onFail: (failCode: String, failReason: String) -> Unit = { _, _ -> },
        crossinline onSuccess: (value: T) -> Unit
    ) {
        apiRespRepeatSafeCollect(this@MvvmBaseFragment, isSuccess, onFail, onSuccess)
    }
}