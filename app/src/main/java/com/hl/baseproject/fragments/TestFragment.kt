package com.hl.baseproject.fragments

import com.hl.arch.mvvm.fragment.MvvmBaseFragment
import com.hl.arch.mvvm.vm.FlowVM
import com.hl.arch.mvvm.vm.LiveDataVM
import com.hl.baseproject.R

class TestFragment : MvvmBaseFragment() {

	override fun isActivityMainPage(): Boolean {
		return false
	}

	override fun onLiveDataVMCreated(liveDataVM: LiveDataVM) {
	}

	override fun onFlowVMCreated(flowVM: FlowVM) {
	}

	override val layoutResId: Int?
		get() = R.layout.fragment_test


}