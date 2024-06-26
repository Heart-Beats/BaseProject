package com.hl.viewbinding

/**
 * @author  张磊  on  2023/02/10 at 16:15
 * Email: 913305160@qq.com
 */

import android.os.Handler
import android.os.Looper
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.viewbinding.ViewBinding
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

enum class Method { BIND, INFLATE }


inline fun <reified VB : ViewBinding> Fragment.inflate() = binding<VB>(Method.INFLATE)
inline fun <reified VB : ViewBinding> Fragment.binding() = binding<VB>(Method.BIND)

/**
 * 从 Fragment 获取 ViewBinding 的代理方法，支持两种方式：
 *          1. binding   -------->  fragment 需要在 onCreateView 中自己填充布局
 *          2. inflate   -------->  fragment 需要在 onCreateView 中返回 ViewBinding 填充的布局，即 viewBinding.root
 *
 *  更推荐使用第二种方式，因为 ViewBinding 填充布局代码更少，更方便维护
 */
inline fun <reified VB : ViewBinding> Fragment.binding(method: Method) = when (method) {
	Method.BIND -> FragmentBindingProperty(VB::class.java)
	Method.INFLATE -> FragmentInflateBindingProperty(VB::class.java)
}

class FragmentBindingProperty<VB : ViewBinding>(private val clazz: Class<VB>) : ReadOnlyProperty<Fragment, VB> {

	override fun getValue(thisRef: Fragment, property: KProperty<*>): VB =
		requireNotNull(thisRef.view) { "The constructor missing layout id or the property of ${property.name} has been destroyed." }
			.getBinding(clazz).also { binding ->
				if (binding is ViewDataBinding) binding.lifecycleOwner = thisRef.viewLifecycleOwner
			}
}

class FragmentInflateBindingProperty<VB : ViewBinding>(private val clazz: Class<VB>) : ReadOnlyProperty<Fragment, VB> {
	private var binding: VB? = null
	private val handler by lazy { Handler(Looper.getMainLooper()) }

	override fun getValue(thisRef: Fragment, property: KProperty<*>): VB {
		if (binding == null) {
			try {
				@Suppress("UNCHECKED_CAST")
				binding = inflateBinding(thisRef.layoutInflater, clazz)
					.also { binding ->
						if (binding is ViewDataBinding) binding.lifecycleOwner = thisRef.viewLifecycleOwner
					}


			} catch (e: IllegalStateException) {
				throw IllegalStateException("The property of ${property.name} can't access when before onCreateView() or after onDestroyView()")
			}
			thisRef.viewLifecycleOwner.lifecycle.addObserver(object : DefaultLifecycleObserver {
				override fun onDestroy(owner: LifecycleOwner) {
					handler.post { binding = null }
				}
			})
		}
		return binding!!
	}
}