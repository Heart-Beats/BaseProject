package com.hl.rvadapter

import android.view.View
import com.hl.rvadapter.itemprovider.BaseItemProvider
import com.hl.rvadapter.viewholder.BaseViewHolder

/**
 * @author  张磊  on  2023/06/08 at 11:23
 * Email: 913305160@qq.com
 */
abstract class BaseSingleAdapter<T>(private val adapterData: MutableList<T>) : BaseMultiAdapter<T>(adapterData) {

	/**
	 * 头部布局
	 */
	open var headerView: View? = null

	/**
	 * 尾部布局
	 */
	open var footerView: View? = null

	/**
	 * 正常数据布局
	 */
	abstract val itemLayout: Int

	/**
	 *创建 viewHolder 时的回调
	 */
	open fun onItemInit(viewHolder: BaseViewHolder<T>) {
	}

	/**
	 * 绑定 viewHolder 时的回调
	 */
	abstract fun onItemBind(viewHolder: BaseViewHolder<T>, itemData: T)

	/**
	 * 刷新 ViewHolder 的视图上的局部数据，
	 */
	open fun onItemBind(helper: BaseViewHolder<T>, itemData: T, payloads: List<Any>) {}

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


	override fun registerItemProvider(position: Int, itemData: T): BaseItemProvider<out T> {
		return when {
			isDisplayHeader(position) -> HeaderItemProvider()
			isDisplayFooter(position) -> FooterItemProvider()
			isDisplayData(position) -> DataItemProvider()
			else -> DataItemProvider()
		}
	}

	override fun getItemCount(): Int {
		var itemCount = super.getItemCount()

		if (isHaveHeader()) {
			itemCount++
		}

		if (isHaveFooter()) {
			itemCount++
		}

		return itemCount
	}


	override fun getItemData(position: Int): T {
		return getRealData(position)
	}

	override fun insertData(vararg addData: T) {
		if (addData.isEmpty()) return

		if (isDisplayEmpty()) {
			super.insertData(*addData)
		} else {
			val lastDataSize = adapterData.size
			this.adapterData.addAll(addData)
			// 有头部插入索引位置为 数据大小加上头部
			val insertIndex = if (isHaveHeader()) lastDataSize + 1 else lastDataSize
			notifyItemRangeInserted(insertIndex, addData.size)
		}
	}


	/**
	 *  删除数据
	 */
	override fun removeData(vararg removeData: T) {
		removeData.forEach {
			var removeIndex = this.adapterData.indexOf(it)
			val remove = this.adapterData.remove(it)

			if (remove) {
				// 有头部移除索引位置为 数据移除位置加上头部偏移
				removeIndex = if (isHaveHeader()) removeIndex + 1 else removeIndex

				if (isDisplayEmpty()) {
					// 空态时改变显示视图
					notifyDataSetChanged()
				} else {
					// 非空态时通知指定位置数据移除
					notifyItemRemoved(removeIndex)
				}
			}
		}
	}

	/**
	 * 更新当前的数据
	 */
	override fun updateData(newData: List<T>) {
		if (isHaveHeader() || isHaveFooter()) {
			// 更新数据集
			this.adapterData.clear()
			this.adapterData.addAll(newData)

			notifyDataSetChanged()
		} else {
			super.updateData(newData)
		}
	}

	/**
	 * 是否有头部
	 */
	private fun isHaveHeader() = !isNoData() && headerView != null

	/**
	 * 是否有尾部
	 */
	private fun isHaveFooter() = !isNoData() && footerView != null

	/**
	 * 是否显示 Header
	 */
	private fun isDisplayHeader(position: Int) = isHaveHeader() && position == 0

	/**
	 * 是否显示 Footer
	 */
	private fun isDisplayFooter(position: Int) = isHaveFooter() && position == itemCount - 1

	/**
	 * 是否显示正常数据
	 */
	private fun isDisplayData(position: Int) = !isDisplayHeader(position) && !isDisplayFooter(position)

	/**
	 * 获取对应位置的真实数据，头尾以及空态时返回默认的虚拟数据
	 */
	private fun getRealData(position: Int): T {
		return when {
			isDisplayEmpty() || isDisplayHeader(position) || isDisplayFooter(position) -> createDefaultItemData()
			isDisplayData(position) -> getData()[getRealPosition(position)]
			else -> getData()[getRealPosition(position)]
		}
	}

	private fun getRealPosition(position: Int): Int {
		return when {
			//  当有头部时，显示正常数据的索引需要减 1
			isDisplayData(position) -> if (isHaveHeader()) position - 1 else position
			else -> position
		}
	}

	private inner class HeaderItemProvider : BaseItemProvider<T>() {

		override var layoutView = headerView

		override val layoutId: Int = 0

		override val itemViewType: Int = ItemViewType.HEADER.ordinal

		override fun onItemBind(viewHolder: BaseViewHolder<T>, itemData: T) {}
	}

	private inner class FooterItemProvider : BaseItemProvider<T>() {

		override var layoutView = footerView

		override val layoutId: Int = 0

		override val itemViewType: Int = ItemViewType.FOOTER.ordinal

		override fun onItemBind(viewHolder: BaseViewHolder<T>, itemData: T) {}
	}

	private inner class DataItemProvider : BaseItemProvider<T>() {

		override val layoutId: Int = itemLayout

		override val itemViewType: Int = ItemViewType.DATA.ordinal

		override fun onItemInit(viewHolder: BaseViewHolder<T>) {
			this@BaseSingleAdapter.onItemInit(viewHolder)
		}

		override fun onItemBind(helper: BaseViewHolder<T>, itemData: T, payloads: List<Any>) {
			this@BaseSingleAdapter.onItemBind(helper, itemData, payloads)
		}

		override fun onItemClick(itemView: View, position: Int, itemData: T) {
			this@BaseSingleAdapter.onItemClick(itemView, position, itemData)
		}

		override fun onItemLongClick(itemView: View, position: Int, itemData: T) {
			this@BaseSingleAdapter.onItemLongClick(itemView, position, itemData)
		}

		override fun onItemBind(viewHolder: BaseViewHolder<T>, itemData: T) {
			this@BaseSingleAdapter.onItemBind(viewHolder, itemData)
		}
	}
}