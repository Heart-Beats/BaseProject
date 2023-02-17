package com.hl.baseproject.base

import androidx.viewbinding.ViewBinding
import com.hl.arch.mvvm.fragment.ViewBindingMvvmBaseFragment
import com.hl.arch.mvvm.vm.FlowVM
import com.hl.arch.mvvm.vm.LiveDataVM

/**
 * @author  张磊  on  2022/09/30 at 18:00
 * Email: 913305160@qq.com
 */
abstract class BaseFragment<Binding : ViewBinding> : ViewBindingMvvmBaseFragment<Binding>() {
	protected val TAG = this.javaClass.simpleName


}