package com.hl.arch.mvvm.activity

import android.os.Bundle
import com.hl.arch.base.BaseActivity
import com.hl.arch.mvvm.vmDelegate.BaseViewModelDelegate
import com.hl.arch.mvvm.vmDelegate.ViewModelDelegate

/**
 * @author  张磊  on  2024/01/05 at 10:49
 * Email: 913305160@qq.com
 */
abstract class MvvmBaseActivity(
	private val viewModelDelegate: ViewModelDelegate = BaseViewModelDelegate()
) : BaseActivity(), ViewModelDelegate by viewModelDelegate {

	override fun onCreate(savedInstanceState: Bundle?) {
		registerOnViewModelCreated(this)
		super.onCreate(savedInstanceState)
	}

}