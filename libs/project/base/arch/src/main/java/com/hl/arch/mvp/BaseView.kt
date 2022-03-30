package com.hl.arch.mvp

import android.content.Context

/**
 * BaseView 是应用中所有UiView的顶级抽象类，适合抽取UiView的公共方法和属性
 */
interface BaseView {
    /**
     * showLoading 方法主要用于页面请求数据时显示加载状态
     */
    fun showLoading()

    /**
     * showLoading 方法主要用于页面请求数据时取消加载状态
     */
    fun dismissLoading()

    /**
     * showEmpty 方法用于请求的数据为空的状态
     */
    fun showEmpty(vararg str: String)

    /**
     * showError 方法用于请求数据出错
     */
    fun showError(vararg str: String)

    fun dealNetError(code: Int, e: Throwable)

    fun showMsg(msg: String)

    fun getContext(): Context
}