package com.hl.viewbinding

/**
 * @author  张磊  on  2023/02/10 at 16:45
 * Email: 913305160@qq.com
 */
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding
import kotlin.LazyThreadSafetyMode.NONE

/**
 * 从 ViewGroup 中获取并绑定 ViewBinding， 此时 ViewBinding 对应的布局会被添加到 ViewGroup 上
 */
inline fun <reified VB : ViewBinding> ViewGroup.binding() = inflateBinding<VB>(this, true)

/**
 * 从 ViewGroup 中获取 ViewBinding， 此时 ViewBinding 对应的布局不会被添加到 ViewGroup 上， lazy 仅在实际使用时才执行
 */
inline fun <reified VB : ViewBinding> ViewGroup.inflate() = lazy(NONE) { inflateBinding<VB>(this, false) }

/**
 * 从 ViewGroup 中获取并绑定 ViewBinding， 此时 ViewBinding 对应的布局会被添加到 ViewGroup 上
 *
 * 注意此时布局的根节点需要为 Merge, 因为此种布局的 ViewBinding 中的 inflate 方法只有 LayoutInflater 与 ViewGroup 两个参数， 与正常的不同
 */
inline fun <reified VB : ViewBinding> ViewGroup.bindingMerge() = inflateBinding<VB>(this)