package com.youma.arch.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView

/**
 * @Author  张磊  on  2020/11/16 at 12:33
 * Email: 913305160@qq.com
 */
abstract class BaseAbstractAdapter<T>(private var data: MutableList<T>) :
    RecyclerView.Adapter<BaseAbstractAdapter<T>.ViewHolder>() {

    open var headerView: View? = null
    open var footerView: View? = null

    abstract val itemLayout: Int

    open var onViewHolderInitListener: (viewHolder: ViewHolder, position: Int, itemData: T?) -> Unit = { _, _, _ -> }
    open var onBindItemListener: (viewHolder: ViewHolder, itemData: T?) -> Unit = { _, _ -> }

    inner class ViewHolder(itemView: View, private val viewType: Int) : RecyclerView.ViewHolder(itemView) {

        /**
         * 该属性需要在数据改变时重新设置 ViewHolder 对应的数据
         * onBindViewHolder（）中默认已实现，若想自己设置请确保数据正确，否则对应回调方法的参数也会不正确
         */
        var initData: T? = null
            set(value) {
                //重新设置 ViewHolder对应的数据时，同时更新监听方法的参数
                onViewHolderInitListener(this, viewType, value)
                field = value
            }

        init {
            initData = getDataForHeaderAndFooter(viewType)
        }

        fun <V : View> getView(viewId: Int): V? {
            return itemView.findViewById(viewId)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        var itemView = LayoutInflater.from(parent.context).inflate(itemLayout, parent, false)
        if (viewType == 0) {
            itemView = headerView ?: itemView
        } else if (viewType == itemCount - 1) {
            itemView = footerView ?: itemView
        }
        return ViewHolder(itemView, viewType)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = getDataForHeaderAndFooter(position)

        if (holder.initData != data) {
            holder.initData = data
        }
        onBindItemListener(holder, data)
    }

    /**
     * 获取头尾存在或不存在时对应位置的数据
     */
    private fun getDataForHeaderAndFooter(position: Int): T? {
        //头尾布局都存在时
        return if (headerView != null && footerView != null) {
            if (position == 0 || position == itemCount - 1) {
                //头尾ViewHolder处理
                null
            } else {
                //正常的ViewHolder处理
                data[position - 1]
            }
        } else {
            if (headerView == null && footerView == null) {
                //头尾布局都不存在时的处理
                data[position]
            } else if (headerView != null) {
                //仅头布局存在时
                if (position == 0) {
                    null
                } else {
                    data[position - 1]
                }
            } else if (footerView != null) {
                //仅尾布局存在时
                if (position < itemCount - 1) {
                    data[position]
                } else {
                    null
                }
            } else null
        }
    }

    override fun getItemCount(): Int {
        return data.size + if (headerView == null) 0 else 1 + if (footerView == null) 0 else 1
    }

    /**
     * 更新当前的数据
     */
    fun updateData(newData: MutableList<T>) {
        val myDiffCallback = MyDiffCallback(data, newData)
        DiffUtil.calculateDiff(myDiffCallback, true).dispatchUpdatesTo(this)
        this.data = newData
    }

    /**
     * 更新当前的数据
     */
    fun insertData(addData: MutableList<T>) {
        val lastDataSize = data.size
        this.data.addAll(addData)
        notifyItemRangeInserted(lastDataSize, addData.size)
    }

    /**
     * 获取当前的数据
     */
    fun getData(): MutableList<T> {
        return this.data
    }

    fun modifyFirstFind2NotifyItemChanged(findCondition: (T) -> Boolean, findCallAction: T.() -> Unit = {}) {
        data?.firstOrNull { findCondition(it) }?.run {
            this.findCallAction()
            notifyItemChanged(data.indexOf(this))
        }
    }

    private class MyDiffCallback<T>(val oldList: List<T>, val newList: List<T>) : DiffUtil.Callback() {

        /**
         * 旧数据的size
         */
        override fun getOldListSize() = oldList.size

        /**
         * 新数据的size
         */
        override fun getNewListSize() = newList.size


        /**
         * 这个方法自由定制 ，
         * 在对比数据的时候会被调用
         * 返回 true 被判断为同一个 item
         */
        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int) = true


        /**
         * areItemsTheSame 方法返回 true 时，
         * 这个方法才会被 diff 调用
         * 返回 true 就证明内容相同
         */
        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition] == newList[newItemPosition]
        }
    }

}