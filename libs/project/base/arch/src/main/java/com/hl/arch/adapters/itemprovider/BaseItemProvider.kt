package com.hl.arch.adapters.itemprovider

import android.view.View
import androidx.annotation.IdRes
import androidx.annotation.LayoutRes
import com.hl.arch.adapters.viewholder.BaseViewHolder

/**
 * @author  张磊  on  2022/09/22 at 15:56
 * Email: 913305160@qq.com
 */

/**
 *  adapter 与 ViewHolder 之间的连接类， 其可向 ViewHolder 提供相关的视图以及数据
 */
abstract class BaseItemProvider<T> {

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
	protected fun BaseViewHolder<T>.setChildClick(
		@IdRes clickChildId: Int,
		onClick: (childView: View, position: Int, itemData: T) -> Unit
	) {
		this.setChildClick(clickChildId, onClick)
	}

	/**
	 * 设置 item 上的子 view 的长按事件,  可重写 onItemInit 方法在其中进行调用
	 */
	protected fun BaseViewHolder<T>.setChildLongClick(
		@IdRes clickChildId: Int,
		onLongClick: (childView: View, position: Int, itemData: T) -> Unit
	) {
		this.setChildLongClick(clickChildId, onLongClick)
	}
}