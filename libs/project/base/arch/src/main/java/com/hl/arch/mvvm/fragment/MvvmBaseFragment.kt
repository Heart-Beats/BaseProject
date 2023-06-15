package com.hl.arch.mvvm.fragment

import com.hl.arch.base.BaseFragment

import com.hl.arch.mvvm.vmDelegate.BaseViewModelDelegate
import com.hl.arch.mvvm.vmDelegate.ViewModelDelegate

/**
 * @Author  张磊  on  2020/08/28 at 18:35
 * Email: 913305160@qq.com
 */
abstract class MvvmBaseFragment : BaseFragment(), ViewModelDelegate by BaseViewModelDelegate() {

}