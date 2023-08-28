package com.hl.rvadapter.viewholder

import android.view.View
import com.hl.rvadapter.BaseMultiAdapter
import com.hl.rvadapter.itemprovider.BaseItemProvider

/**
 * @author  张磊  on  2022/09/22 at 16:03
 * Email: 913305160@qq.com
 */
internal open class MultiViewHolder<T>(
	private val baseItemProvider: BaseItemProvider<T>,
	adapter: BaseMultiAdapter<T>, itemView: View
) : BaseViewHolder<T>(adapter, itemView) {

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

	override fun onBindView(itemData: T, payloads: MutableList<Any>) {
		baseItemProvider.onItemBind(this, itemData, payloads)
	}

	override fun onItemClick(itemView: View, position: Int, itemData: T) {
		baseItemProvider.onItemClick(itemView, position, itemData)
	}

	override fun onItemLongClick(itemView: View, position: Int, itemData: T) {
		baseItemProvider.onItemLongClick(itemView, position, itemData)
	}
}