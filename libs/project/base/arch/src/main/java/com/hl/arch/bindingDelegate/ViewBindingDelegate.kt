package com.hl.arch.bindingDelegate

import android.view.LayoutInflater
import android.view.View
import androidx.lifecycle.LifecycleOwner
import androidx.viewbinding.ViewBinding

/**
 * @author  张磊  on  2023/02/10 at 17:06
 * Email: 913305160@qq.com
 */


interface ViewBindingDelegate<VB : ViewBinding> {
	/**
	 * 泛型参数对应的 ViewBinding
	 */
	val viewBinding: VB

	/**
	 *  创建 ViewBinding 对应的视图
	 */
	fun LifecycleOwner.createViewWithBinding(inflater: LayoutInflater): View

}