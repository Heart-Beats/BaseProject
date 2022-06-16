package com.hl.baseproject.fragments

import android.os.Bundle
import com.hl.arch.mvvm.fragment.ViewBindingMvvmBaseFragment
import com.hl.arch.mvvm.vm.FlowVM
import com.hl.arch.mvvm.vm.LiveDataVM
import com.hl.baseproject.databinding.FragmentTestBinding

class TestFragment : ViewBindingMvvmBaseFragment<FragmentTestBinding>() {

	override fun isActivityMainPage(): Boolean {
		return false
	}

	override fun onLiveDataVMCreated(liveDataVM: LiveDataVM) {
	}

	override fun onFlowVMCreated(flowVM: FlowVM) {
	}

	override fun FragmentTestBinding.onViewCreated(savedInstanceState: Bundle?) {
		toolbar?.title = "测试标题"
		toolbar?.subtitle="你好"
	}


}