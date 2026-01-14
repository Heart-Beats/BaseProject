package com.hl.rvadapter.normal.itemprovider.impl

import android.view.View
import com.hl.rvadapter.IDataType
import com.hl.rvadapter.normal.itemprovider.BaseItemProvider
import com.hl.rvadapter.normal.viewholder.BaseViewHolder

internal class FooterItemProvider<T: IDataType>(footerView: View?) : BaseItemProvider<T>() {

    override var layoutView = footerView

    override val layoutId: Int = 0

    override fun onItemBind(viewHolder: BaseViewHolder<T>, itemData: T) {}
}