package com.hl.arch.utils.viewbinding

import androidx.activity.ComponentActivity
import androidx.databinding.ViewDataBinding
import androidx.viewbinding.ViewBinding

/**
 * @author  张磊  on  2023/02/10 at 16:14
 * Email: 913305160@qq.com
 */

inline fun <reified VB : ViewBinding> ComponentActivity.binding(setContentView: Boolean = true) =
	lazy(LazyThreadSafetyMode.NONE) {
		inflateBinding<VB>(layoutInflater).also { binding ->
			if (setContentView) setContentView(binding.root)
			if (binding is ViewDataBinding) binding.lifecycleOwner = this
		}
	}