package com.hl.baseproject.base

import androidx.viewbinding.ViewBinding
import com.hl.api.event.ApiEvent
import com.hl.arch.mvvm.fragment.ViewBindingMvvmBaseFragment
import com.hl.arch.mvvm.vm.FlowVM
import com.hl.arch.mvvm.vm.LiveDataVM
import com.hl.arch.utils.repeatSafeCollect
import com.hl.utils.onceLastObserve

/**
 * @author  张磊  on  2022/09/30 at 18:00
 * Email: 913305160@qq.com
 */
abstract class BaseFragment<Binding : ViewBinding> : ViewBindingMvvmBaseFragment<Binding>() {

	override fun onLiveDataVMCreated(liveDataVM: LiveDataVM) {
		super.onLiveDataVMCreated(liveDataVM)

		val baseViewModel = liveDataVM as? BaseViewModel
		baseViewModel?.apiEventFailedLiveData?.onceLastObserve(viewLifecycleOwner) {
			dispatchApiEventFailed(it)
		}
	}

	override fun onFlowVMCreated(flowVM: FlowVM) {
		super.onFlowVMCreated(flowVM)

		val baseViewModel = flowVM as? FlowBaseViewModel
		baseViewModel?.apiEventFailedFlow?.repeatSafeCollect(viewLifecycleOwner) {
			dispatchApiEventFailed(it)
		}
	}

	private fun dispatchApiEventFailed(event: ApiEvent.Failed) {
		// todo 处理接口请求失败相关事件
		if (event is ApiEvent.Failed.UnknownError) {

			return
		}

		when (event as ApiEventFailed) {
			ApiEventFailed.Error -> {}
			else -> {}
		}
	}
}