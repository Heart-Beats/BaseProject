package com.hl.ui.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.viewbinding.ViewBinding
import com.hl.ui.bindingDelegate.FragmentBindingDelegate
import com.hl.ui.bindingDelegate.ViewBindingDelegate

/**
 * @author  张磊  on  2023/02/10 at 17:48
 * Email: 913305160@qq.com
 */
abstract class ViewBindingBaseFragment<Binding : ViewBinding> : BaseFragment(),
	ViewBindingDelegate<Binding> by FragmentBindingDelegate() {

	override val layoutResId: Int = 0

	/**
	 * 重写，确保页面填充 ViewBinding 的视图
	 */
	override fun getPageInflateView(layoutInflater: LayoutInflater): View {
		return createViewWithBinding(layoutInflater)
	}


	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		viewBinding.onViewCreated(savedInstanceState)
	}


	/**
	 * 使用 ViewBinding 的页面视图创建完成
	 */
	abstract fun Binding.onViewCreated(savedInstanceState: Bundle?)
}