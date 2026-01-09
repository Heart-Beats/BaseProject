package com.hl.rvadapter.binding.itemprovider.impl

import android.view.View
import androidx.viewbinding.ViewBinding
import com.hl.rvadapter.ItemViewType
import com.hl.rvadapter.binding.BaseBindingSingleAdapter
import com.hl.rvadapter.binding.itemprovider.BaseBindingItemProvider
import com.hl.rvadapter.binding.viewholder.BaseBindingViewHolder

internal class DataItemProvider<T, VB : ViewBinding>(val adapter: BaseBindingSingleAdapter<T, VB>) : BaseBindingItemProvider<T, VB>() {

    override val itemViewType: Int = ItemViewType.DATA.ordinal

    override fun onItemInit(viewHolder: BaseBindingViewHolder<T, VB>) {
        adapter.onItemInit(viewHolder)
    }

    override fun onItemBind(helper: BaseBindingViewHolder<T, VB>, itemData: T, payloads: List<Any?>) {
        adapter.onItemBind(helper, itemData, payloads)
    }

    override fun onItemClick(itemView: View, position: Int, itemData: T) {
        adapter.onItemClick(itemView, position, itemData)
    }

    override fun onItemLongClick(itemView: View, position: Int, itemData: T) {
        adapter.onItemLongClick(itemView, position, itemData)
    }

    override fun onItemBind(viewHolder: BaseBindingViewHolder<T, VB>, itemData: T) {
        adapter.onItemBind(viewHolder, itemData)
    }
}