package com.hl.navigatioin.utils

import android.util.Log
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

/**
 * @Author  张磊  on  2020/12/31 at 0:11
 * Email: 913305160@qq.com
 */
internal class ReflectHelper<T>(private val clazz: Class<T>) {

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
     *   获取 clazz  的泛型参数
     *
     *  java 中有几种特殊情况，能够获取泛型对象的参数类型：
     *       1、类中有 class 信息（类中有一个 Class 字段）
     *       2、父类中有 class 信息（父类是泛型类，并指定了参数类型）
     *       3、持有者中有 class 信息（是一个类的 Field、Method）
     */
    fun getGenericsType(): List<Class<Type>>? {

        // 获取带有泛型的父类类型
        val genericSuperclass = clazz.genericSuperclass
        if (genericSuperclass != null) {
            if (genericSuperclass is ParameterizedType) {
                // 获取父类的泛型参数的实际类型
                return genericSuperclass.actualTypeArguments.map { it.javaClass }
            }
        }
        return null
    }
}