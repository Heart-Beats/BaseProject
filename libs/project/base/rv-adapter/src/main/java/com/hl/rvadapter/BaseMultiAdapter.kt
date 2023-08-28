package com.hl.rvadapter

import android.util.SparseArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.util.containsKey
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.hl.rvadapter.diffcallback.MyDiffCallback
import com.hl.rvadapter.itemprovider.BaseItemProvider
import com.hl.rvadapter.viewholder.BaseViewHolder
import com.hl.rvadapter.viewholder.MultiViewHolder
import java.lang.reflect.ParameterizedType

/**
 * @author  张磊  on  2022/09/22 at 11:31
 * Email: 913305160@qq.com
 *
 * 目前使用 ItemDragCallBack 拖拽排序有问题 @see[com.hl.arch.adapters.drag.ItemDragCallBack]
 */
abstract class BaseMultiAdapter<T>(private val adapterData: MutableList<T>) : RecyclerView.Adapter<BaseViewHolder<T>>(),
	IDataOperate<T> {

	/**
	 * 空态 view
	 */
	open var emptyView: View? = null


	/**
	 * 保存 itemViewType 与 ItemProvider 的映射关系
	 */
	private val itemProviders = SparseArray<BaseItemProvider<out T>>()

	/**
	 * 向 Adapter 注册 BaseItemProvider
	 */
	abstract fun registerItemProvider(position: Int, itemData: T): BaseItemProvider<out T>

	override fun getItemViewType(position: Int): Int {
		val itemProvider =
			if (isDisplayEmpty()) EmptyItemProvider() else registerItemProvider(position, getItemData(position))

		val itemViewType = itemProvider.itemViewType

		if (!itemProviders.containsKey(itemViewType)) {
			itemProviders[itemViewType] = itemProvider
		}

		return itemViewType
	}

	/**
	 * 是否显示空态
	 */
	protected fun isDisplayEmpty() = isNoData() && emptyView != null

	/**
	 * 是否没有数据
	 */
	protected fun isNoData() = adapterData.size == 0

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<T> {
		val itemProvider = itemProviders[viewType]

		val itemView =
			itemProvider.layoutView ?: LayoutInflater.from(parent.context).inflate(itemProvider.layoutId, parent, false)
		return MultiViewHolder(itemProvider as BaseItemProvider<T>, this, itemView)
	}

	override fun onBindViewHolder(holder: BaseViewHolder<T>, position: Int) {
		holder.onBindView(getItemData(position))
	}

	override fun onBindViewHolder(holder: BaseViewHolder<T>, position: Int, payloads: MutableList<Any>) {
		// RecyclerView 默认使用此方法 bind 视图， 重写时需要主要根据 payloads 参数来保持原有的 bind 逻辑
		if (payloads.isEmpty()) {
			onBindViewHolder(holder, position)
			return
		}
		holder.onBindView(getItemData(position), payloads)
	}

	/**
	 * 获取对应位置的数据
	 */
	protected open fun getItemData(position: Int): T {
		return if (isDisplayEmpty()) createDefaultItemData() else adapterData[position]
	}

	protected fun createDefaultItemData(): T {
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

	override fun getItemCount() = if (isDisplayEmpty()) 1 else adapterData.size

	/**
	 * 向列表尾部插入数据
	 */
	override fun insertData(vararg addData: T) {
		if (addData.isEmpty()) return

		if (isDisplayEmpty()) {
			this.adapterData.addAll(addData)
			notifyItemRangeChanged(0, addData.size)
		} else {
			val lastDataSize = adapterData.size
			this.adapterData.addAll(addData)
			notifyItemRangeInserted(lastDataSize, addData.size)
		}
	}

	/**
	 *  删除数据
	 */
	override fun removeData(vararg removeData: T) {
		removeData.forEach {
			val removeIndex = this.adapterData.indexOf(it)
			val remove = this.adapterData.remove(it)

			if (remove) {
				if (isDisplayEmpty()) {
					notifyDataSetChanged()
				} else {
					// 通知指定位置数据移除
					notifyItemRemoved(removeIndex)
				}
			}
		}
	}

	/**
	 * 更新当前的数据
	 */
	override fun updateData(newData: List<T>) {
		val myDiffCallback = MyDiffCallback(adapterData, newData)
		val diffResult = DiffUtil.calculateDiff(myDiffCallback, true)

		if (isDisplayEmpty() && newData.isNotEmpty()) {
			// 当有数据更新时，空态时的 viewHolder 需要进行移除
			notifyItemRemoved(0)
		}

		// 更新数据集
		this.adapterData.clear()
		this.adapterData.addAll(newData)

		//  将更新事件分派给给定的适配器
		diffResult.dispatchUpdatesTo(this)
	}

	/**
	 * 获取当前的数据
	 */
	override fun getData(): MutableList<T> {
		return this.adapterData
	}

	/**
	 * 修改符合条件的所有数据
	 */
	fun modifyDataByCondition(condition: (T) -> Boolean, modifyAction: T.() -> Unit = {}) {
		adapterData.forEachIndexed { index, item ->
			if (condition(item)) {
				item.modifyAction()
				notifyItemChanged(index)
			}
		}
	}


	private inner class EmptyItemProvider : BaseItemProvider<T>() {

		override var layoutView = emptyView

		override val layoutId: Int = 0

		override val itemViewType: Int = ItemViewType.EMPTY.ordinal

		override fun onItemBind(viewHolder: BaseViewHolder<T>, itemData: T) {}
	}
}