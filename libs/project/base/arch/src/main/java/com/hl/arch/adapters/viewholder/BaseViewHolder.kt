package com.hl.arch.adapters.viewholder

import android.view.View
import androidx.recyclerview.widget.RecyclerView

/**
 * @author  张磊  on  2022/09/22 at 14:10
 * Email: 913305160@qq.com
 */

abstract class BaseViewHolder<T>(itemView: View) : RecyclerView.ViewHolder(itemView) {


	fun <V : View> getView(viewId: Int): V? {
		return itemView.findViewById(viewId)
	}

	/**
	 * 通知 ViewHolder 已完成创建
	 */
	abstract fun onCreateView()

	/**
	 * 刷新 ViewHolder 的整体视图数据
	 */
	abstract fun onBindView(itemData: T)

	/**
	 * 刷新 ViewHolder 的视图上的局部数据，具体参见 @see [RecyclerView.Adapter.onBindViewHolder]  的三个参数方法
	 */
	open fun onBindView(itemData: T, payloads: MutableList<Any>) {}
}