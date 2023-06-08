package com.hl.arch.adapters

import android.view.View
import com.hl.arch.adapters.itemprovider.BaseItemProvider
import com.hl.arch.adapters.viewholder.BaseViewHolder
import java.lang.reflect.ParameterizedType

/**
 * @author  张磊  on  2023/06/08 at 11:23
 * Email: 913305160@qq.com
 */
abstract class BaseAbstractAdapter2<T>(private var adapterData: MutableList<T>) : BaseMultiAdapter<T>(adapterData) {

	/**
	 * 头部布局
	 */
	open var headerView: View? = null

	/**
	 * 尾部布局
	 */
	open var footerView: View? = null

	/**
	 * 空态 view
	 */
	open var emptyView: View? = null

	/**
	 * 正常数据布局
	 */
	abstract val itemLayout: Int

	/**
	 * 创建 viewHolder 时的回调
	 */
	protected open fun onItemInit(viewHolder: BaseViewHolder<T>) {}


	/**
	 * 绑定 viewHolder 时的回调
	 */
	protected open fun onItemBind(viewHolder: BaseViewHolder<T>, itemData: T) {}

	override fun registerItemProvider(position: Int, itemData: T): BaseItemProvider<out T> {
		return when {
			isDisplayEmpty() -> EmptyItemProvider()
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

		if (isDisplayEmpty()) {
			itemCount = 1
		}

		return itemCount
	}

	override fun getItemData(position: Int): T {
		return getRealData(position)
	}

	// /**
	//  * 向列表尾部插入数据
	//  */
	// override fun insertData(vararg addData: T) {
	// 	val lastDataSize = adapterData.size
	// 	this.adapterData.addAll(addData)
	//
	// 	if (isHaveFooter()) {
	// 		this.adapterData.add(Unit)
	// 	}
	//
	// 	notifyItemRangeInserted(lastDataSize, addData.size)
	// }
	//
	// /**
	//  *  删除数据
	//  */
	// override fun removeData(vararg removeData: T) {
	// 	removeData.forEach {
	// 		val removeIndex = this.adapterData.indexOf(it)
	// 		this.adapterData.remove(it)
	//
	// 		// 通知指定位置数据移除
	// 		notifyItemRemoved(removeIndex)
	// 	}
	// }

	// /**
	//  * 更新当前的数据
	//  */
	// override fun updateData(newData: List<T>) {
	// 	val myDiffCallback = MyDiffCallback(adapterData, newData)
	// 	DiffUtil.calculateDiff(myDiffCallback, true).dispatchUpdatesTo(this)
	// 	this.adapterData = newData.toMutableList()
	// }


	// /**
	//  * 获取当前的数据
	//  */
	// override fun getData(): MutableList<T> {
	// 	return this.adapterData
	// }


	/**
	 * 是否没有数据
	 */
	private fun isNoData() = adapterData.size == 0

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
	 * 是否显示空态
	 */
	private fun isDisplayEmpty() = isNoData() && emptyView != null

	/**
	 * 是否显示正常数据
	 */
	private fun isDisplayData(position: Int) =
		!isDisplayEmpty() && !isDisplayHeader(position) && !isDisplayFooter(position)


	/**
	 * 获取对应位置的真实数据
	 */
	private fun getRealData(position: Int): T {
		return when {
			isDisplayEmpty() || isDisplayHeader(position) || isDisplayFooter(position) -> createDefaultItemData()
			isDisplayData(position) -> getData()[getRealPosition(position)]
			else -> getData()[getRealPosition(position)]
		}
	}

	private fun createDefaultItemData(): T {
		val genericSuperclass = this.javaClass.genericSuperclass
		return try {
			if (genericSuperclass is ParameterizedType) {
				val type = genericSuperclass.actualTypeArguments[0]
				(type as Class<T>).newInstance()
			} else {
				error("获取传入数据类型失败！")
			}
		} catch (e: Exception) {
			error("请给数据类型添加默认构造器！")
		}
	}

	private fun getRealPosition(position: Int): Int {
		return when {
			//  当有头部时，显示正常数据的索引需要减 1
			isDisplayData(position) -> if (isHaveHeader()) position - 1 else position
			else -> position
		}
	}

	private inner class EmptyItemProvider : BaseItemProvider<T>() {

		override var layoutView = emptyView

		override val layoutId: Int = 0

		override val itemViewType: Int = ItemViewType.EMPTY.ordinal

		override fun onItemBind(viewHolder: BaseViewHolder<T>, itemData: T) {}
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
			this@BaseAbstractAdapter2.onItemInit(viewHolder)
		}

		override fun onItemBind(viewHolder: BaseViewHolder<T>, itemData: T) {
			this@BaseAbstractAdapter2.onItemBind(viewHolder, itemData)
		}
	}
}