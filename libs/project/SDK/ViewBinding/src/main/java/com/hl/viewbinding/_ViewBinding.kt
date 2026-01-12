package com.hl.viewbinding

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding

/**
 * @author  张磊  on  2023/02/10 at 16:06
 * Email: 913305160@qq.com
 *
 * 通过反射创建 ViewBinding 对象
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

inline fun <VB : ViewBinding> inflateBinding(parent: ViewGroup, clazz: Class<VB>, attachToParent: Boolean = false) =
    inflateBinding(LayoutInflater.from(parent.context), parent, attachToParent, clazz)

inline fun <reified VB : ViewBinding> inflateMergeBinding(parent: ViewGroup) =
    inflateMergeBinding(LayoutInflater.from(parent.context), parent,  VB::class.java)


/**
 * 正常的布局文件生成的 ViewBind，存在以下两个方法
 *   1. inflate(LayoutInflater inflater)
 *   2. inflate(LayoutInflater inflater, ViewGroup parent, boolean attachToParent)
 */
fun <VB : ViewBinding> inflateBinding(layoutInflater: LayoutInflater, clazz: Class<VB>) =
	clazz.getMethod("inflate", LayoutInflater::class.java).invoke(null, layoutInflater) as VB

fun <VB : ViewBinding> inflateBinding(layoutInflater: LayoutInflater, parent: ViewGroup?, attachToParent: Boolean, clazz: Class<VB>) =
	clazz.getMethod("inflate", LayoutInflater::class.java, ViewGroup::class.java, Boolean::class.java)
		.invoke(null, layoutInflater, parent, attachToParent) as VB

/**
 * Merge 的布局文件生成的 ViewBind，仅存在以下方法
 *   1. inflate(LayoutInflater inflater, ViewGroup parent)
 */
fun <VB : ViewBinding> inflateMergeBinding(layoutInflater: LayoutInflater, parent: ViewGroup?, clazz: Class<VB>) =

    clazz.getMethod("inflate", LayoutInflater::class.java, ViewGroup::class.java)
        .invoke(null, layoutInflater, parent) as VB
