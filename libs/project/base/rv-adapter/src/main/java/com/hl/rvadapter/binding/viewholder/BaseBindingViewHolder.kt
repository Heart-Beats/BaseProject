package com.hl.rvadapter.binding.viewholder

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.hl.rvadapter.IDataOperate
import com.hl.rvadapter.utils.onClick

/**
 * @author  张磊  on  2025/11/03 at 15:58
 * Email: 913305160@qq.com
 */
abstract class BaseBindingViewHolder<T, VB : ViewBinding>(
    val adapter: RecyclerView.Adapter<BaseBindingViewHolder<T, ViewBinding>>,
    val binding: VB
) : RecyclerView.ViewHolder(binding.root) {

    init {
        // 给 viewHolder 的 item 设置点击以及长按事件
        itemView.apply {
            this.onClick {
                val (isLayoutEnd, position, itemData) = getPositionAndData()
                if (!isLayoutEnd) return@onClick

                onItemClick(itemView, position, itemData)
            }

            this.setOnLongClickListener {
                val (isLayoutEnd, position, itemData) = getPositionAndData()
                if (isLayoutEnd) return@setOnLongClickListener false

                onItemLongClick(itemView, position, itemData)
                true
            }
        }
    }

    /**
     * 刷新 ViewHolder 的整体视图数据
     */
    internal abstract fun onBindView(itemData: T)

    /**
     * 刷新 ViewHolder 的视图上的局部数据，具体参见 @see [RecyclerView.Adapter.onBindViewHolder]  的三个参数方法
     */
    internal open fun onBindView(itemData: T, payloads: List<Any?>) {}

    /**
     * item 的点击事件
     */
    protected open fun onItemClick(itemView: View, position: Int, itemData: T) {
    }

    /**
     * item 的长按事件
     */
    protected open fun onItemLongClick(itemView: View, position: Int, itemData: T) {
    }


    /**
     * 设置 item 上的子 view 的点击事件,  可重写 onItemInit 方法在其中进行调用
     */
    fun setChildClick(
        childView: VB.() -> View,
        onClick: (childView: View, position: Int, itemData: T) -> Unit
    ) {
        binding.childView().onClick {
            val (isLayoutEnd, position, itemData) = getPositionAndData()
            if (!isLayoutEnd) return@onClick

            onClick(it, position, itemData)
        }
    }

    /**
     * 设置 item 上的子 view 的长按事件,  可重写 onItemInit 方法在其中进行调用
     */
    fun setChildLongClick(
        childView: VB.() -> View,
        onLongClick: (childView: View, position: Int, itemData: T) -> Unit
    ) {
        binding.childView().setOnLongClickListener {
            val (isLayoutEnd, position, itemData) = getPositionAndData()
            if (!isLayoutEnd) return@setOnLongClickListener false

            onLongClick(it, position, itemData)
            true
        }
    }

    /**
     * 获取当前 ViewHolder 对应的 Item 数据
     *  @return Triple<Boolean, Int, T> 其中 Boolean 表示布局是否已完成，Int 表示位置，T 表示数据
     */
    private fun getPositionAndData(): Triple<Boolean, Int, T> {
        val position = this.bindingAdapterPosition
        val isLayoutEnd = position != RecyclerView.NO_POSITION // 布局是否已完成

        val iDataOperate = adapter as IDataOperate<T>
        val itemData = iDataOperate.getData()[position]
        return Triple(isLayoutEnd, position, itemData)
    }
}