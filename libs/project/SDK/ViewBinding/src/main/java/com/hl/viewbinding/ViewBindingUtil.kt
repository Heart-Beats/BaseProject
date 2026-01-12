package com.hl.viewbinding

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ComponentActivity
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.viewbinding.ViewBinding
import java.lang.reflect.ParameterizedType

/**
 * @author  张磊  on  2022/01/12 at 15:31
 * Email: 913305160@qq.com
 *
 * 通过反射创建 ViewBinding
 */

/**
 * 从指定对象的泛型参数中创建 ViewBinding 对象
 */
object ViewBindingUtil {

    @JvmStatic
     fun <VB : ViewBinding> inflateWithGeneric(genericOwner: LifecycleOwner, layoutInflater: LayoutInflater): VB =
        withGenericBindingClass(genericOwner) { clazz ->
            inflateBinding<VB>(layoutInflater, clazz)
        }.also { binding ->
            if (genericOwner is ComponentActivity && binding is ViewDataBinding) {
                binding.lifecycleOwner = genericOwner
            }
        }

    @JvmStatic
    fun <VB : ViewBinding> inflateWithGeneric(genericOwner: LifecycleOwner, parent: ViewGroup): VB =
        inflateWithGeneric(genericOwner, LayoutInflater.from(parent.context), parent, false)

    @JvmStatic
    fun <VB : ViewBinding> inflateWithGeneric(
        genericOwner: LifecycleOwner,
        layoutInflater: LayoutInflater,
        parent: ViewGroup?,
        attachToParent: Boolean
    ): VB =
        withGenericBindingClass(genericOwner) { clazz ->
            inflateBinding<VB>(layoutInflater, parent, attachToParent, clazz)
        }.also { binding ->
            if (genericOwner is Fragment && binding is ViewDataBinding) {
                binding.lifecycleOwner = genericOwner.viewLifecycleOwner
            }
        }

    @JvmStatic
    fun < VB : ViewBinding> bindWithGeneric(genericOwner: LifecycleOwner, view: View): VB =
        withGenericBindingClass(genericOwner) { clazz ->
            view.getBinding<VB>(clazz)
        }.also { binding ->
            if (genericOwner is Fragment && binding is ViewDataBinding) {
                binding.lifecycleOwner = genericOwner.viewLifecycleOwner
            }
        }



    fun <VB : ViewBinding> withGenericBindingClass(any: Any,  block: (Class<VB>) -> VB): VB {
        return block.invoke(getGenericBindingClass(any))
    }

    /**
     * 从对象的泛型参数中获取 ViewBinding 的 Class 类型
     */
    @Suppress("UNCHECKED_CAST")
    fun <VB : ViewBinding> getGenericBindingClass(any: Any): Class<VB> {
        var genericSuperclass = any.javaClass.genericSuperclass
        var superclass = any.javaClass.superclass

        while (superclass != null) {
            if (genericSuperclass is ParameterizedType) {
                // 查找 ViewBinding 类型的泛型参数
                genericSuperclass.actualTypeArguments
                    .filterIsInstance<Class<*>>()
                    .firstOrNull { ViewBinding::class.java.isAssignableFrom(it) }
                    ?.let { type ->
                        return type as Class<VB>
                    }
            }
            genericSuperclass = superclass.genericSuperclass
            superclass = superclass.superclass
        }

        throw IllegalArgumentException(
            "No generic ViewBinding type found for ${any.javaClass.simpleName}. " +
                    "Check if the class has a ViewBinding generic parameter."
        )
    }

    /**
     * 从对象的泛型参数中获取指定的 ViewBinding 的类型
     */
    @Suppress("UNCHECKED_CAST")
    inline fun <reified VB : ViewBinding> withBindingClass(any: Any, crossinline block: (Class<VB>) -> VB): VB {
        // 获取 VB 的具体类（在调用时已确定）
        val vbClass = VB::class.java

        var genericSuperclass = any.javaClass.genericSuperclass
        var superclass = any.javaClass.superclass

        while (superclass != null) {
            if (genericSuperclass is ParameterizedType) {
                genericSuperclass.actualTypeArguments
                    .filterIsInstance<Class<*>>() // // 只遍历 actualTypeArguments 中的 Class 类型
                    .firstOrNull { type -> type == vbClass }  // 精确匹配 VB 的具体类（不是父类）
                    ?.let { type ->
                        return block.invoke(type as Class<VB>)
                    }
            }
            genericSuperclass = superclass.genericSuperclass
            superclass = superclass.superclass
        }

        throw IllegalArgumentException(
            "No generic ViewBinding type found for ${vbClass.simpleName}. " +
                    "Check if the class implements ViewBinding with the correct generic type."
        )
    }
}