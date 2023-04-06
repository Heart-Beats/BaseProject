package com.hl.utils

import java.lang.reflect.InvocationHandler
import java.lang.reflect.Method
import java.lang.reflect.Proxy

/**
 * @author 张磊  on  2022/01/20 at 19:32
 * Email: 913305160@qq.com
 *
 * 动态代理工具类 , 仅可代理实现接口的对象
 */
class ProxyHandler<T : Any>(private val methodHook: MethodHook<T>? = null) : InvocationHandler {

    /**
     * 代理对象的地址与被代理对象的映射
     */
    private var proxy2TargetMap = hashMapOf<Int, T>()

    /**
     * 绑定委托对象(即被代理对象)，并返回代理对象
     */
    fun bind(target: T): T {
        //绑定该类实现的所有接口，取得代理类
        val proxy = Proxy.newProxyInstance(target.javaClass.classLoader, target.javaClass.interfaces, this) as T

        // 动态代理对象无法拿到 hashCode 值， 因此只能通过本存地址来映射 target
        val proxyMemoryAddress = System.identityHashCode(proxy)
        proxy2TargetMap[proxyMemoryAddress] = target
        return proxy
    }

    @Throws(Throwable::class)
    override fun invoke(proxy: Any, method: Method, args: Array<Any>): Any? {
        val proxyMemoryAddress = System.identityHashCode(proxy)
        val target = proxy2TargetMap[proxyMemoryAddress] ?: throw NullPointerException("未找到被代理对象")

        //这里就可以进行所谓的AOP编程了
        //在调用具体函数方法前，执行功能处理
        methodHook?.beforeHookedMethod(target, proxy as T, method, args)


        val result = if (methodHook != null) {
            methodHook.onHookedMethod(target, proxy as T, method, args)
        } else {
            method.invoke(target, *args)
        }

        //在调用具体函数方法后，执行功能处理
        methodHook?.afterHookedMethod(target, proxy as T, method, args)

        return result
    }
}

open class MethodHook<T : Any> {

    /**
     * 被代理对象的方法执行之前 的 Hook
     *
     * @param target  被代理对象
     * @param proxy   代理对象， 注意： 动态代理的对象判断与其他对象是否相等仅可使用 === （地址相等）
     * @param method  执行的方法
     * @param args    方法的参数
     */
    open fun beforeHookedMethod(target: T, proxy: T, method: Method, args: Array<Any>) {
    }

    /**
     * 被代理对象的方法执行时的 Hook
     *
     * @param target  被代理对象
     * @param proxy   代理对象， 注意： 动态代理的对象判断与其他对象是否相等仅可使用 === （地址相等）
     * @param method  执行的方法
     * @param args    方法的参数
     *
     * @return        方法执行后返回的结果
     */
    open fun onHookedMethod(target: T, proxy: T, method: Method, args: Array<Any>): Any? {
        return method.invoke(target, *args)
    }

    /**
     * 被代理对象的方法执行之后 的 Hook
     *
     * @param target  被代理对象
     * @param proxy   代理对象， 注意： 动态代理的对象判断与其他对象是否相等仅可使用 === （地址相等）
     * @param method  执行的方法
     * @param args    方法的参数
     */
    open fun afterHookedMethod(target: T, proxy: T, method: Method, args: Array<Any>) {
    }
}