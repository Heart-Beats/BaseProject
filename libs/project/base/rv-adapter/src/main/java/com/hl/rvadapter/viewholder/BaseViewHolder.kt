package com.hl.rvadapter.viewholder

import android.view.View
import androidx.annotation.IdRes
import androidx.recyclerview.widget.RecyclerView
import com.hl.rvadapter.IDataOperate
import com.hl.rvadapter.utils.onClick

/**
 * @author  张磊  on  2022/09/22 at 14:10
 * Email: 913305160@qq.com
 */

abstract class BaseViewHolder<T>(val adapter: RecyclerView.Adapter<BaseViewHolder<T>>, itemView: View) : RecyclerView.ViewHolder(itemView) {

	init {
		// 给 viewHolder 的 item 设置点击以及长按事件
		itemView.apply {
			this.onClick {
				val (isLayoutEnd, position, itemData) = getPositionAndData()
				if (!isLayoutEnd) return@onClick

				onItemClick(this, position, itemData)
			}

			this.setOnLongClickListener {
				val (isLayoutEnd, position, itemData) = getPositionAndData()
				if (isLayoutEnd) return@setOnLongClickListener false

				onItemLongClick(this, position, itemData)
				true
			}
		}
	}

	/**
	 * 刷新 ViewHolder 的整体视图数据
	 */
	internal abstract fun onBindView(itemData: T)

	/**
	 * 刷新 ViewHolder 的视图上的局部数据，具体参见 @see [RecyclerView.Adapter.onBindViewHolder]  的三个参数方法
	 */
	internal open fun onBindView(itemData: T, payloads: MutableList<Any>) {}

	/**
	 * item 的点击事件
	 */
	protected open fun onItemClick(itemView: View, position: Int, itemData: T) {
	}

	/**
	 * item 的长按事件
	 */
	protected open fun onItemLongClick(itemView: View, position: Int, itemData: T) {
	}

	/**********************************  相关 API   *************************************************/
	/**
	 * 获取相应的 view
	 */
	protected fun <V : View> Int.findView(): V? {
		return itemView.findViewById(this)
	}

	/**
	 * 获取相应的 view
	 */
	fun <V : View> getView(@IdRes viewId: Int): V? {
		return viewId.findView()
	}

	/**
	 * 设置 item 上的子 view 的点击事件,  可重写 onItemInit 方法在其中进行调用
	 */
	fun setChildClick(
		@IdRes clickChildId: Int,
		onClick: (childView: View, position: Int, itemData: T) -> Unit
	) {
		this.getView<View>(clickChildId)?.onClick {
			val (isLayoutEnd, position, itemData) = getPositionAndData()
			if (!isLayoutEnd) return@onClick

			onClick(it, position, itemData)
		}
	}

	/**
	 * 设置 item 上的子 view 的长按事件,  可重写 onItemInit 方法在其中进行调用
	 */
	fun setChildLongClick(
		@IdRes clickChildId: Int,
		onLongClick: (childView: View, position: Int, itemData: T) -> Unit
	) {
		this.getView<View>(clickChildId)?.setOnLongClickListener {
			val (isLayoutEnd, position, itemData) = getPositionAndData()
			if (!isLayoutEnd) return@setOnLongClickListener false

			onLongClick(it, position, itemData)
			true
		}
	}

	private fun getPositionAndData(): Triple<Boolean, Int, T> {
		val position = this.bindingAdapterPosition
		val isLayoutEnd = position != RecyclerView.NO_POSITION // 布局是否已完成

		val iDataOperate = adapter as IDataOperate<T>
		val itemData = iDataOperate.getData()[position]
		return Triple(isLayoutEnd, position, itemData)
	}
}