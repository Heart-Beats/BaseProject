package com.hl.arch.adapters.viewholder

import android.view.View
import com.elvishew.xlog.XLog
import com.hl.arch.adapters.itemprovider.BaseItemProvider

/**
 * @author  张磊  on  2022/09/22 at 16:03
 * Email: 913305160@qq.com
 */
open class MultiViewHolder<T>(protected val baseItemProvider: BaseItemProvider<T>, itemView: View) :
	BaseViewHolder<T>(itemView) {


	override fun onCreateView() {
		baseItemProvider.onItemInit(this)
	}


	override fun onBindView(itemData: T) {
		baseItemProvider.onItemBind(this, itemData)
	}

	override fun onBindView(itemData: T, payloads: MutableList<Any>) {
		baseItemProvider.onItemBind(this, itemData, payloads)
	}
}