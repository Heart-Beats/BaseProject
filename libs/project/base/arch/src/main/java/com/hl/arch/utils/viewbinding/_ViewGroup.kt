package com.hl.arch.utils.viewbinding

/**
 * @author  张磊  on  2023/02/10 at 16:45
 * Email: 913305160@qq.com
 */
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding
import kotlin.LazyThreadSafetyMode.*

inline fun <reified VB : ViewBinding> ViewGroup.inflate() =
	inflateBinding<VB>(LayoutInflater.from(context), this, true)

inline fun <reified VB : ViewBinding> ViewGroup.binding(attachToParent: Boolean = false) = lazy(NONE) {
	inflateBinding<VB>(LayoutInflater.from(context), if (attachToParent) this else null, attachToParent)
}