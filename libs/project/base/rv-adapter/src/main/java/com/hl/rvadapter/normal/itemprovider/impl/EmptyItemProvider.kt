package com.hl.rvadapter.normal.itemprovider.impl

import android.view.View
import com.hl.rvadapter.ItemViewType
import com.hl.rvadapter.normal.itemprovider.BaseItemProvider
import com.hl.rvadapter.normal.viewholder.BaseViewHolder

internal class EmptyItemProvider<T>(emptyView: View?) : BaseItemProvider<T>() {

    override var layoutView = emptyView

    override val layoutId: Int = 0

    override val itemViewType: Int = ItemViewType.EMPTY.ordinal

    override fun onItemBind(viewHolder: BaseViewHolder<T>, itemData: T) {}
}