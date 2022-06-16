package com.hl.utils

import android.util.Log
import java.lang.reflect.Type

/**
 * @Author  张磊  on  2020/12/31 at 0:11
 * Email: 913305160@qq.com
 */
class ReflectHelper<T>(private val clazz: Class<T>) {

    private companion object {
        private const val TAG = "ReflectHelper"
    }

    fun <V> getFiledValue(obj: T, filedName: String): V? {
        return try {
            val field = clazz.getDeclaredField(filedName)
            field.isAccessible = true
            val get = field.get(obj)
            field.isAccessible = false
            get as? V
        } catch (e: Exception) {
            Log.e(TAG, "getFiledValue: ", e)
            null
        }
    }

    fun setFiledValue(obj: T, filedName: String, value: Any?) {
        try {
            val field = clazz.getDeclaredField(filedName)
            field.isAccessible = true
            field.set(obj, value)
            field.isAccessible = false
        } catch (e: Exception) {
            Log.e(TAG, "setFiledValue: ", e)
        }
    }

    fun callMethod(obj: T, methodName: String, vararg params: Any): Any? {
        return try {
            val method = clazz.getDeclaredMethod(methodName, *params.map { it::class.java }.toTypedArray())
            method.isAccessible = true
            val result = when {
                params.isEmpty() -> method.invoke(obj) //无参数时不可直接调用 params, 因为 kotlin 中这也是个对象
                else -> method.invoke(obj, params)
            }
            method.isAccessible = false
            result
        } catch (e: Exception) {
            Log.e(TAG, "callMethod: ", e)
            null
        }
    }

    /**
     * 获取 clazz  的泛型参数
     */
    fun getGenericsType(): List<Class<Type>>? {

        // 获取带有泛型的父类类型
        val genericSuperclass = clazz.genericSuperclass
        if (genericSuperclass != null) {
            if (genericSuperclass is ParameterizedTypeImpl) {
                // 获取父类的泛型参数的实际类型
                return genericSuperclass.actualTypeArguments.map { it.javaClass }
            }
        }
        return null
    }
}