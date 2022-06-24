package com.hl.baseproject.fragments

import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import com.hl.arch.mvvm.fragment.ViewBindingMvvmBaseFragment
import com.hl.arch.mvvm.vm.FlowVM
import com.hl.arch.mvvm.vm.LiveDataVM
import com.hl.baseproject.R
import com.hl.baseproject.databinding.FragmentTestBinding
import com.hl.utils.span.dsl.buildSpannableString

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

		testSpan.buildSpannableString {

			this.addText("开始时间："){
				this.setColor(Color.parseColor("#FF31415F"))
				this.setTypeface(Typeface.DEFAULT_BOLD)
				this.setSize(18,true)
			}

			this.addText("2022/04/23 09:00"){
				this.setColor(Color.RED)
				this.setSize(14)
			}

			this.addText("\n")

			this.addImage(com.cjt2325.cameralibrary.R.drawable.ic_camera, marginLeft = 10)
		}
	}


}