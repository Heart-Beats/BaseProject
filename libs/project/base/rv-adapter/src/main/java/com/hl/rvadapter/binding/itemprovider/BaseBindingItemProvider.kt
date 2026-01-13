package com.hl.rvadapter.binding.itemprovider

import android.view.View
import android.view.ViewGroup
import androidx.annotation.IdRes
import androidx.viewbinding.ViewBinding
import com.hl.rvadapter.IDataType
import com.hl.rvadapter.binding.viewholder.BaseBindingViewHolder
import com.hl.viewbinding.ViewBindingUtil
import com.hl.viewbinding.inflateBinding

/**
 * @author  张磊  on  2022/09/22 at 15:56
 * Email: 913305160@qq.com
 */

/**
 *  adapter 与 ViewHolder 之间的连接类， 其可向 ViewHolder 提供相关的视图以及数据
 */
abstract class BaseBindingItemProvider<T : IDataType, VB : ViewBinding>(){

    /**
     * 缓存 ViewBinding 的 Class 对象，避免每次创建 ViewHolder 时都进行反射
     */
    private val bindingClass: Class<VB> by lazy {
        ViewBindingUtil.getGenericBindingClass(getBindingClassSource())
    }

    /**
     * 获取 ViewBinding 的 Class 类型，子类可重写以指定从哪个对象提取泛型信息
     */
    protected open fun getBindingClassSource(): Any = this

    /**
     * 获取当前 ViewHolder 所需的 ViewBinding
     */
    internal open fun createBinding(parent: ViewGroup): VB {
        return inflateBinding(parent, bindingClass)
    }

    /**
     * ViewHolder 已完成初始化
     */
    open fun onItemInit(viewHolder: BaseBindingViewHolder<T, VB>) {
    }

    /**
     * 刷新 ViewHolder 的整体视图数据，
     */
    abstract fun onItemBind(viewHolder: BaseBindingViewHolder<T, VB>, itemData: T)

    /**
     * 刷新 ViewHolder 的视图上的局部数据，
     */
    open fun onItemBind(helper: BaseBindingViewHolder<T, VB>, itemData: T, payloads: List<Any?>) {}

    /**
     * item 的点击事件
     */
    open fun onItemClick(itemView: View, position: Int, itemData: T) {
    }

    /**
     * item 的长按事件
     */
    open fun onItemLongClick(itemView: View, position: Int, itemData: T) {
    }

    /**
     * 设置 item 上的子 view 的点击事件,  可重写 onItemInit 方法在其中进行调用
     */
    protected fun BaseBindingViewHolder<T, VB>.setChildClick(
        @IdRes clickChildId: Int,
        onClick: (childView: View, position: Int, itemData: T) -> Unit
    ) {
        this.setChildClick(clickChildId, onClick)
    }

    /**
     * 设置 item 上的子 view 的长按事件,  可重写 onItemInit 方法在其中进行调用
     */
    protected fun BaseBindingViewHolder<T, VB>.setChildLongClick(
        @IdRes clickChildId: Int,
        onLongClick: (childView: View, position: Int, itemData: T) -> Unit
    ) {
        this.setChildLongClick(clickChildId, onLongClick)
    }
}