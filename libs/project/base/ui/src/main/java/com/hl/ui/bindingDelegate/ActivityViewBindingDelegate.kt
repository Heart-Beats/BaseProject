package com.hl.ui.bindingDelegate

import android.view.LayoutInflater
import android.view.View
import androidx.lifecycle.LifecycleOwner
import androidx.viewbinding.ViewBinding
import com.hl.ui.utils.ViewBindingUtil

/**
 * @author  张磊  on  2023/02/10 at 17:22
 * Email: 913305160@qq.com
 */

open class ActivityBindingDelegate<VB : ViewBinding> : ViewBindingDelegate<VB> {
	private lateinit var _binding: VB

	override val viewBinding: VB
		get() = _binding

	override fun LifecycleOwner.createViewWithBinding(inflater: LayoutInflater): View {
		_binding = ViewBindingUtil.inflateWithGeneric(this, inflater)
		return _binding.root
	}
}