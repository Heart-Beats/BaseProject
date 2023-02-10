package com.hl.arch.utils.viewbinding

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding

/**
 * @author  张磊  on  2023/02/10 at 16:06
 * Email: 913305160@qq.com
 *
 * 创建 ViewBinding 对象
 */

inline fun <reified VB : ViewBinding> inflateBinding(layoutInflater: LayoutInflater) =
	VB::class.java.getMethod("inflate", LayoutInflater::class.java).invoke(null, layoutInflater) as VB

inline fun <reified VB : ViewBinding> inflateBinding(parent: ViewGroup) =
	inflateBinding<VB>(LayoutInflater.from(parent.context), parent, false)

inline fun <reified VB : ViewBinding> inflateBinding(
	layoutInflater: LayoutInflater, parent: ViewGroup?, attachToParent: Boolean
) = VB::class.java.getMethod("inflate", LayoutInflater::class.java, ViewGroup::class.java, Boolean::class.java)
	.invoke(null, layoutInflater, parent, attachToParent) as VB