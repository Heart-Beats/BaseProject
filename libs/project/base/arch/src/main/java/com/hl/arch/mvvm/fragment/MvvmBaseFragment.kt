package com.hl.arch.mvvm.fragment

import android.os.Bundle
import com.hl.arch.base.BaseNavigationFragment
import com.hl.arch.mvvm.vmDelegate.BaseViewModelDelegate
import com.hl.arch.mvvm.vmDelegate.ViewModelDelegate

/**
 * @Author  张磊  on  2020/08/28 at 18:35
 * Email: 913305160@qq.com
 */
abstract class MvvmBaseFragment : BaseNavigationFragment(), ViewModelDelegate by BaseViewModelDelegate() {
	override fun onCreate(savedInstanceState: Bundle?) {
		registerOnViewModelCreated(this)
		super.onCreate(savedInstanceState)
	}
}