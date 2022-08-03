package com.hl.baseproject

import android.content.Intent
import android.os.Bundle
import com.hl.arch.mvvm.activity.ViewBindingBaseActivity
import com.hl.baseproject.databinding.ActivityTest2Binding

class TestActivity2 : ViewBindingBaseActivity<ActivityTest2Binding>() {

	override fun onBackPressed() {
		setResult(RESULT_OK, Intent().apply {
			this.putExtra("data", "我是测试2页面数据")
		})
		super.onBackPressed()
	}

	override fun ActivityTest2Binding.onViewCreated(savedInstanceState: Bundle?) {
	}
}