package com.hl.baseproject.base

import androidx.viewbinding.ViewBinding
import com.hl.arch.mvvm.fragment.ViewBindingMvvmBaseFragment
import com.hl.arch.mvvm.vm.BaseLiveDataVM
import com.hl.arch.mvvm.vm.LiveDataVM
import com.hl.utils.onceLastObserve

/**
 * @author  张磊  on  2022/09/30 at 18:00
 * Email: 913305160@qq.com
 */
abstract class BaseFragment<Binding : ViewBinding> : ViewBindingMvvmBaseFragment<Binding>() {
	protected val TAG = this.javaClass.simpleName

	override fun onLiveDataVMCreated(liveDataVM: LiveDataVM) {
		super.onLiveDataVMCreated(liveDataVM)
		if (liveDataVM is BaseLiveDataVM) {
			liveDataVM.apiEventFailedLiveData.onceLastObserve(viewLifecycleOwner) {
				if (it is ApiEventFailed) {
					dispatchApiEventFailed(it)
				}
			}
		}
	}

	private fun dispatchApiEventFailed(event: ApiEventFailed) {
		// todo 处理接口请求失败相关时间
	}
}