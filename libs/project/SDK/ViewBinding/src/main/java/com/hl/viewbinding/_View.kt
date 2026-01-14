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
fun <VB : ViewBinding> View.getBinding(clazz: Class<VB>): VB {
    // 尝试从 Tag 中获取已缓存的 ViewBinding
    val cachedBinding = getTag(R.id.hl_view_binding_tag) as? VB
    if (cachedBinding != null) {
        return cachedBinding
    }

    // 如果传入的是 ViewBinding 接口本身，创建一个简单的包装
    if (clazz == ViewBinding::class.java) {
        val simpleBinding = ViewBinding { this@getBinding }
        setTag(R.id.hl_view_binding_tag, simpleBinding)
        return simpleBinding as VB
    }

    // 否则通过反射调用具体 Binding 类的 bind 方法
    return try {
        val binding = clazz.getMethod("bind", View::class.java).invoke(null, this) as VB
        setTag(R.id.hl_view_binding_tag, binding)
        binding
    } catch (_: NoSuchMethodException) {
        // 如果找不到 bind 方法，也创建简单包装
        val simpleBinding = ViewBinding { this@getBinding }
        setTag(R.id.hl_view_binding_tag, simpleBinding)
        simpleBinding as VB
    }
}