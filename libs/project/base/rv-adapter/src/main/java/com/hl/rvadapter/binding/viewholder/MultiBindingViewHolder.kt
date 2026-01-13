package com.hl.rvadapter.binding.viewholder

import android.view.View
import androidx.viewbinding.ViewBinding
import com.hl.rvadapter.IDataType
import com.hl.rvadapter.binding.BaseBindingMultiAdapter
import com.hl.rvadapter.binding.itemprovider.BaseBindingItemProvider

/**
 * @author  张磊  on  2025/11/03 at 11:19
 * Email: 913305160@qq.com
 */
internal open class MultiBindingViewHolder<T: IDataType, VB : ViewBinding>(
    private val baseItemProvider: BaseBindingItemProvider<T,VB>,
    adapter: BaseBindingMultiAdapter<T>, binding: VB
) : BaseBindingViewHolder<T,VB>(adapter, binding) {

	init {
		onItemInit()
	}


	/**
	 * 通知 ViewHolder 已完成创建,  需要初始化完成后主动调用
	 */
	private fun onItemInit() {
		baseItemProvider.onItemInit(this)
	}


	override fun onBindView(itemData: T) {
		baseItemProvider.onItemBind(this, itemData)
	}

	override fun onBindView(itemData: T, payloads: List<Any?>) {
		baseItemProvider.onItemBind(this, itemData, payloads)
	}

	override fun onItemClick(itemView: View, position: Int, itemData: T) {
		baseItemProvider.onItemClick(itemView, position, itemData)
	}

	override fun onItemLongClick(itemView: View, position: Int, itemData: T) {
		baseItemProvider.onItemLongClick(itemView, position, itemData)
	}
}