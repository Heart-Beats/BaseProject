package com.hl.ui.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.viewbinding.ViewBinding
import com.hl.ui.bindingDelegate.ActivityBindingDelegate
import com.hl.ui.bindingDelegate.ViewBindingDelegate

/**
 * @Author  张磊  on  2021/03/02 at 15:36
 * Email: 913305160@qq.com
 */
abstract class ViewBindingBaseActivity<Binding : ViewBinding> : BaseActivity(),
    ViewBindingDelegate<Binding> by ActivityBindingDelegate() {

    override val layoutResId: Int = 0

    /**
     * 重写，确保页面填充 ViewBinding 的视图
     */
    override fun getPageInflateView(layoutInflater: LayoutInflater): View {
        return createViewWithBinding(layoutInflater)
    }

    override fun onViewCreated(savedInstanceState: Bundle?) {
        viewBinding.onViewCreated(savedInstanceState)
    }

    /**
     * 使用 ViewBinding 的页面视图创建完成
     */
    abstract fun Binding.onViewCreated(savedInstanceState: Bundle?)
}