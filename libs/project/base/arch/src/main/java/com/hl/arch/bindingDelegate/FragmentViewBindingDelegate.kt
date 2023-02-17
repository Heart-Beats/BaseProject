package com.hl.arch.bindingDelegate

import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.viewbinding.ViewBinding
import com.hl.utils.viewbinding.ViewBindingUtil

/**
 * @author  张磊  on  2023/02/10 at 17:23
 * Email: 913305160@qq.com
 */

class FragmentBindingDelegate<VB : ViewBinding> : ViewBindingDelegate<VB> {
	private var _binding: VB? = null
	private val handler by lazy { Handler(Looper.getMainLooper()) }

	override val viewBinding: VB
		get() = requireNotNull(_binding) { "The property of binding has been destroyed." }

	override fun LifecycleOwner.createViewWithBinding(inflater: LayoutInflater): View {
		if (_binding == null) {
			_binding = ViewBindingUtil.inflateWithGeneric(this, inflater, null, false)

			lifecycle.addObserver(object : DefaultLifecycleObserver {
				override fun onDestroy(owner: LifecycleOwner) {
					handler.post {
						//Fragment 的存在时间比其视图长。请务必在 Fragment 的 onDestroyView() 方法中清除对绑定类实例的所有引用。
						_binding = null
					}
				}
			})
		}
		return _binding!!.root
	}
}