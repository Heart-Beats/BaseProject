package com.hl.arch.adapters.itemprovider

import android.view.View
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.RecyclerView
import com.hl.arch.adapters.BaseMultiAdapter
import com.hl.arch.adapters.viewholder.BaseViewHolder
import com.hl.uikit.onClick

/**
 * @author  张磊  on  2022/09/22 at 15:56
 * Email: 913305160@qq.com
 */

/**
 *  adapter 与 ViewHolder 之间的连接类， 其可向 ViewHolder 提供相关的视图以及数据
 */
abstract class BaseItemProvider<T>(protected val multiAdapter: BaseMultiAdapter<T>) {

	/**
	 * 当前 ViewHolder 对应的布局文件
	 */
	abstract val layoutId: Int
		@LayoutRes
		get

	/**
	 * 获取当前 ViewHolder 对应的 Item 类型
	 */
	abstract val itemViewType: Int

	/**
	 * 刷新 ViewHolder 的整体视图数据，
	 */
	abstract fun onItemBind(holder: BaseViewHolder<T>, itemData: T)

	/**
	 * 刷新 ViewHolder 的视图上的局部数据，
	 */
	open fun onItemBind(helper: BaseViewHolder<T>, itemData: T, payloads: List<Any>) {}

	/**
	 * ViewHolder 已完成初始化
	 */
	open fun onItemInit(viewHolder: BaseViewHolder<T>) {
		// 给 viewHolder 的 item 设置点击以及长按事件
		viewHolder.itemView.apply {
			this.onClick {
				val (isLayoutEnd, position, itemData) = getDataForViewHolder(viewHolder)
				if (!isLayoutEnd) return@onClick

				onItemClick(this, position, itemData)
			}

			this.setOnLongClickListener {
				val (isLayoutEnd, position, itemData) = getDataForViewHolder(viewHolder)
				if (isLayoutEnd) return@setOnLongClickListener false

				onItemLongClick(this, position, itemData)
				true
			}
		}
	}


	private fun getDataForViewHolder(viewHolder: BaseViewHolder<T>): Triple<Boolean, Int, T> {
		val position = viewHolder.bindingAdapterPosition
		val isLayoutEnd = position != RecyclerView.NO_POSITION // 布局是否已完成
		val itemData = multiAdapter.getData()[position]
		return Triple(isLayoutEnd, position, itemData)
	}


	/**
	 * item 的点击事件
	 */
	open fun onItemClick(itemView: View, position: Int, itemData: T) {
	}

	/**
	 * item 的长按事件
	 */
	open fun onItemLongClick(itemView: View, position: Int, itemData: T) {
	}

	/**
	 * 设置 item 上的子 view 的点击事件,  可重写 onItemInit 方法在其中进行调用
	 */
	fun BaseViewHolder<T>.setChildClick(
		clickChildId: Int,
		onClick: (childView: View, position: Int, itemData: T) -> Unit
	) {
		this.getView<View>(clickChildId)?.onClick {
			val (isLayoutEnd, position, itemData) = getDataForViewHolder(this)
			if (!isLayoutEnd) return@onClick

			onClick(it, position, itemData)
		}
	}

	/**
	 * 设置 item 上的子 view 的长按事件,  可重写 onItemInit 方法在其中进行调用
	 */
	fun BaseViewHolder<T>.setChildLongClick(
		clickChildId: Int,
		onLongClick: (childView: View, position: Int, itemData: T) -> Unit
	) {
		this.getView<View>(clickChildId)?.setOnLongClickListener {
			val (isLayoutEnd, position, itemData) = getDataForViewHolder(this)
			if (!isLayoutEnd) return@setOnLongClickListener false

			onLongClick(it, position, itemData)
			true
		}
	}

}