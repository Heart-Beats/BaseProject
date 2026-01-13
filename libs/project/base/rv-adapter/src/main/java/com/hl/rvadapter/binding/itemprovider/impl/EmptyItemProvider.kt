package com.hl.rvadapter.binding.itemprovider.impl

import android.view.View
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding
import com.hl.rvadapter.IDataType
import com.hl.rvadapter.binding.itemprovider.BaseBindingItemProvider
import com.hl.rvadapter.binding.viewholder.BaseBindingViewHolder
import com.hl.viewbinding.getBinding

internal class EmptyItemProvider<T : IDataType>(val emptyView: View?) :
    BaseBindingItemProvider<T, ViewBinding>() {

    override fun createBinding(parent: ViewGroup): ViewBinding {
        return emptyView?.getBinding() ?: error("emptyView 未设置")
    }

    override fun onItemBind(viewHolder: BaseBindingViewHolder<T, ViewBinding>, itemData: T) {}
}