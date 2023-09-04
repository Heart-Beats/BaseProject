package com.hl.viewbinding

/**
 * @author  张磊  on  2023/02/10 at 16:16
 * Email: 913305160@qq.com
 */
import android.view.View
import androidx.viewbinding.ViewBinding

/**
 * 从已填充布局的 View 获取对应的 ViewBinding
 */
inline fun <reified VB : ViewBinding> View.getBinding() = getBinding(VB::class.java)

/**
 * 从已填充布局的 View 获取对应的 ViewBinding， 若 View 未与 ViewBinding 绑定，则进行绑定返回
 */
@Suppress("UNCHECKED_CAST")
fun <VB : ViewBinding> View.getBinding(clazz: Class<VB>) =
	getTag(R.id.hl_view_binding_tag ) as? VB ?: (clazz.getMethod("bind", View::class.java)
		.invoke(null, this) as VB)
		.also { setTag(R.id.hl_view_binding_tag, it) }