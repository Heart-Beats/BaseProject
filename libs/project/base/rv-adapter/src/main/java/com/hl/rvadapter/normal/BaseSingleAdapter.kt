package com.hl.rvadapter.normal

import android.view.View
import com.hl.rvadapter.IDataType
import com.hl.rvadapter.ItemViewType
import com.hl.rvadapter.normal.itemprovider.BaseItemProvider
import com.hl.rvadapter.normal.itemprovider.impl.DataItemProvider
import com.hl.rvadapter.normal.itemprovider.impl.FooterItemProvider
import com.hl.rvadapter.normal.itemprovider.impl.HeaderItemProvider
import com.hl.rvadapter.normal.viewholder.BaseViewHolder

/**
 * @author  张磊  on  2023/06/08 at 11:23
 * Email: 913305160@qq.com
 */
abstract class BaseSingleAdapter<T: IDataType>(private val adapterData: MutableList<T>) : BaseMultiAdapter<T>(adapterData) {

	/**
	 * 头部布局
	 */
	open var headerView: View? = null

	/**
	 * 尾部布局
	 */
	open var footerView: View? = null

	/**
	 * 缓存头部视图数据实例，避免重复创建
	 */
	private val cachedHeaderItemData: T by lazy {
		createDefaultItemData().also {
            it.itemViewType = ItemViewType.HEADER.ordinal
        }
	}

	/**
	 * 缓存尾部视图数据实例，避免重复创建
	 */
	private val cachedFooterItemData: T by lazy {
		createDefaultItemData().also {
            it.itemViewType = ItemViewType.FOOTER.ordinal
        }
	}

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
			isDisplayHeader(position) -> HeaderItemProvider(headerView)
			isDisplayFooter(position) -> FooterItemProvider(footerView)
			else -> DataItemProvider(itemLayout, this)
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


	/**
	 * 获取对应位置的真实数据，头尾时返回默认的虚拟数据
	 */
	override fun getRealData(position: Int): T {
		return when {
			isDisplayHeader(position) -> cachedHeaderItemData
			isDisplayFooter(position) -> cachedFooterItemData
			else -> getData()[getRealDataPosition(position)]
		}
	}

	private fun getRealDataPosition(position: Int): Int {
		return when {
			//  当有头部时，显示正常数据的索引需要减 1
			isDisplayData(position) -> if (isHaveHeader()) position - 1 else position
			else -> position
		}
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
}