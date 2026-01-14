package com.hl.rvadapter.normal.itemprovider.impl

import android.view.View
import com.hl.rvadapter.IDataType
import com.hl.rvadapter.normal.BaseSingleAdapter
import com.hl.rvadapter.normal.itemprovider.BaseItemProvider
import com.hl.rvadapter.normal.viewholder.BaseViewHolder

internal class DataItemProvider<T: IDataType>(itemLayout: Int, private val adapter: BaseSingleAdapter<T>) : BaseItemProvider<T>() {

    override val layoutId: Int = itemLayout

    override fun onItemInit(viewHolder: BaseViewHolder<T>) {
        adapter.onItemInit(viewHolder)
    }

    override fun onItemBind(helper: BaseViewHolder<T>, itemData: T, payloads: List<Any>) {
        adapter.onItemBind(helper, itemData, payloads)
    }

    override fun onItemClick(itemView: View, position: Int, itemData: T) {
        adapter.onItemClick(itemView, position, itemData)
    }

    override fun onItemLongClick(itemView: View, position: Int, itemData: T) {
        adapter.onItemLongClick(itemView, position, itemData)
    }

    override fun onItemBind(viewHolder: BaseViewHolder<T>, itemData: T) {
        adapter.onItemBind(viewHolder, itemData)
    }
}