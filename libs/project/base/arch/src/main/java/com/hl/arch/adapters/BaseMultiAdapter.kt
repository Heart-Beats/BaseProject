package com.hl.arch.adapters

import android.util.SparseArray
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.util.containsKey
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.hl.arch.adapters.diffcallback.MyDiffCallback
import com.hl.arch.adapters.itemprovider.BaseItemProvider
import com.hl.arch.adapters.viewholder.BaseViewHolder
import com.hl.arch.adapters.viewholder.MultiViewHolder

/**
 * @author  张磊  on  2022/09/22 at 11:31
 * Email: 913305160@qq.com
 */
abstract class BaseMultiAdapter<T>(private var adapterData: MutableList<T>) : RecyclerView.Adapter<BaseViewHolder<T>>(),
	IDataOperate<T> {

	/**
	 * 保存 itemViewType 与 ItemProvider 的映射关系
	 */
	private val itemProviders = SparseArray<BaseItemProvider<out T>>()

	/**
	 * 向 Adapter 注册 BaseItemProvider
	 */
	abstract fun registerItemProvider(position: Int, itemData: T): BaseItemProvider<out T>

	override fun getItemViewType(position: Int): Int {
		val itemProvider = registerItemProvider(position, adapterData[position])
		val itemViewType = itemProvider.itemViewType

		if (!itemProviders.containsKey(itemViewType)) {
			itemProviders[itemViewType] = itemProvider
		}

		return itemViewType
	}

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<T> {
		val itemProvider = itemProviders[viewType]

		val itemView = LayoutInflater.from(parent.context).inflate(itemProvider.layoutId, parent, false)
		return MultiViewHolder(itemProvider as BaseItemProvider<T>, this, itemView)
	}

	override fun onBindViewHolder(holder: BaseViewHolder<T>, position: Int) {
		holder.onBindView(adapterData[position])
	}

	override fun onBindViewHolder(holder: BaseViewHolder<T>, position: Int, payloads: MutableList<Any>) {
		// RecyclerView 默认使用此方法 bind 视图， 重写时需要主要根据 payloads 参数来保持原有的 bind 逻辑
		if (payloads.isEmpty()) {
			onBindViewHolder(holder, position)
			return
		}
		holder.onBindView(adapterData[position], payloads)
	}

	override fun getItemCount() = adapterData.size

	/**
	 * 向列表尾部插入数据
	 */
	override fun insertData(vararg addData: T) {
		val lastDataSize = adapterData.size
		this.adapterData.addAll(addData)
		notifyItemRangeInserted(lastDataSize, addData.size)
	}

	/**
	 *  删除数据
	 */
	override fun removeData(vararg removeData: T) {
		removeData.forEach {
			val removeIndex = this.adapterData.indexOf(it)
			this.adapterData.remove(it)

			// 通知指定位置数据移除
			notifyItemRemoved(removeIndex)
		}
	}

	/**
	 * 更新当前的数据
	 */
	override fun updateData(newData: List<T>) {
		val myDiffCallback = MyDiffCallback(adapterData, newData)
		DiffUtil.calculateDiff(myDiffCallback, true).dispatchUpdatesTo(this)
		this.adapterData = newData.toMutableList()
	}


	/**
	 * 获取当前的数据
	 */
	override fun getData(): MutableList<T> {
		return this.adapterData
	}
}