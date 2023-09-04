package com.hl.viewbinding

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding

/**
 * @author  张磊  on  2023/02/10 at 16:06
 * Email: 913305160@qq.com
 *
 * 创建 ViewBinding 对象
 */

/**
 * 通过 layoutInflater 获取 VB
 */
inline fun <reified VB : ViewBinding> inflateBinding(layoutInflater: LayoutInflater) = inflateBinding(layoutInflater, VB::class.java)

/**
 * 通过 ViewGroup 获取 VB
 *
 * @param parent 父容器
 * @param attachToParent 是否将 ViewBinding 的布局添加到父容器中
 */
inline fun <reified VB : ViewBinding> inflateBinding(parent: ViewGroup, attachToParent: Boolean = false) =
	inflateBinding<VB>(LayoutInflater.from(parent.context), parent, attachToParent)

inline fun <reified VB : ViewBinding> inflateBinding(layoutInflater: LayoutInflater, parent: ViewGroup?, attachToParent: Boolean) =
	inflateBinding(layoutInflater, parent, attachToParent, VB::class.java)

inline fun <reified VB : ViewBinding> inflateBinding(parent: ViewGroup) =
	inflateBinding(LayoutInflater.from(parent.context), parent,  VB::class.java)


fun <VB : ViewBinding> inflateBinding(layoutInflater: LayoutInflater, clazz: Class<VB>) =
	clazz.getMethod("inflate", LayoutInflater::class.java).invoke(null, layoutInflater) as VB

fun <VB : ViewBinding> inflateBinding(layoutInflater: LayoutInflater, parent: ViewGroup?, clazz: Class<VB>) =
	clazz.getMethod("inflate", LayoutInflater::class.java, ViewGroup::class.java)
		.invoke(null, layoutInflater, parent) as VB

fun <VB : ViewBinding> inflateBinding(layoutInflater: LayoutInflater, parent: ViewGroup?, attachToParent: Boolean, clazz: Class<VB>) =
	clazz.getMethod("inflate", LayoutInflater::class.java, ViewGroup::class.java, Boolean::class.java)
		.invoke(null, layoutInflater, parent, attachToParent) as VB