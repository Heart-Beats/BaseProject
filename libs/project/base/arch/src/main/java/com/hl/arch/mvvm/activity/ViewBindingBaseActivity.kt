package com.hl.arch.mvvm.activity

import android.os.Bundle
import android.view.View
import androidx.viewbinding.ViewBinding
import com.hl.arch.base.BaseActivity
import com.hl.arch.utils.ViewBindingUtil

/**
 * @Author  张磊  on  2021/03/02 at 15:36
 * Email: 913305160@qq.com
 */
abstract class ViewBindingBaseActivity<Binding : ViewBinding> : BaseActivity() {

    /**
     * ViewBinding 的初始化必须在 FragmentActivity 的 onCreate(savedInstanceState) 之后，
     *    之前获取到的 LayoutInflater 填充布局可能会出错
     */
    protected var viewBinding: Binding? = null
        private set

    abstract fun Binding.onViewCreated(savedInstanceState: Bundle?)

    /**
     * 重写设置为 null， 使用 ViewBinding 来进行布局填充
     */
    override val layoutResId: Int?
        get() = null

    override fun getViewBindingLayoutView(): View {
        val binding = ViewBindingUtil.inflateWithGeneric<Binding>(this,layoutInflater)
        binding.run {
            viewBinding = this
            return this.root
        }
    }

    override fun onViewCreated(savedInstanceState: Bundle?) {
        viewBinding?.onViewCreated(savedInstanceState)
    }
}