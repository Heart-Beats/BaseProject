package com.hl.baseproject.base

import android.os.Bundle
import com.hl.arch.base.ComposeBaseNavigationFragment
import com.hl.arch.mvvm.vmDelegate.BaseViewModelDelegate
import com.hl.arch.mvvm.vmDelegate.ViewModelDelegate

/**
 * @author  张磊  on  2023/07/07 at 14:13
 * Email: 913305160@qq.com
 */
abstract class BaseComposeFragment : ComposeBaseNavigationFragment(), ViewModelDelegate by BaseViewModelDelegate() {
	override fun onCreate(savedInstanceState: Bundle?) {
		registerOnViewModelCreated(this)
		super.onCreate(savedInstanceState)
	}
}