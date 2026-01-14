package com.hl.rvadapter.normal

import android.util.SparseArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.util.containsKey
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.hl.rvadapter.IDataOperate
import com.hl.rvadapter.IDataType
import com.hl.rvadapter.ItemViewType
import com.hl.rvadapter.diffcallback.MyDiffCallback
import com.hl.rvadapter.normal.itemprovider.BaseItemProvider
import com.hl.rvadapter.normal.itemprovider.impl.EmptyItemProvider
import com.hl.rvadapter.normal.viewholder.BaseViewHolder
import com.hl.rvadapter.normal.viewholder.MultiViewHolder
import java.lang.reflect.ParameterizedType

/**
 * @author  张磊  on  2022/09/22 at 11:31
 * Email: 913305160@qq.com
 *
 * 目前使用 ItemDragCallBack 拖拽排序有问题 @see[com.hl.rvadapter.drag.ItemDragCallBack]
 */
abstract class BaseMultiAdapter<T : IDataType>(private val adapterData: MutableList<T>) :
    RecyclerView.Adapter<BaseViewHolder<T>>(),
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
     * 缓存空视图数据实例，避免重复创建
     */
    private val cachedEmptyItemData: T by lazy {
        createDefaultItemData().also {
            it.itemViewType = ItemViewType.EMPTY.ordinal
        }
    }

	/**
	 * 向 Adapter 注册 BaseItemProvider
	 */
	abstract fun registerItemProvider(position: Int, itemData: T): BaseItemProvider<out T>

	override fun getItemViewType(position: Int): Int {
        val itemData = getItemData(position)

        val viewTypeFromData = itemData.itemViewType

        // 检查是否已缓存该 viewType 的 ItemProvider
        if (!itemProviders.containsKey(viewTypeFromData)) {
            val provider = if (viewTypeFromData == ItemViewType.EMPTY.ordinal) {
                EmptyItemProvider(emptyView)
            } else {
                // 首次遇到该 viewType，调用 registerItemProvider 创建并缓存
                registerItemProvider(position, itemData)
            }
            itemProviders[viewTypeFromData] = provider
        }

        return viewTypeFromData
	}

	/**
	 * 是否显示空态
	 */
    protected fun isDisplayEmpty(): Boolean = getData().isEmpty() && emptyView != null

	/**
	 * 是否没有数据
	 */
    protected fun isNoData(): Boolean = getData().isEmpty()

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<T> {
        // 从缓存中获取 ItemProvider
        val itemProvider =
            itemProviders[viewType] ?: error("No ItemProvider found for viewType: $viewType")

		val itemView =
			itemProvider.layoutView ?: LayoutInflater.from(parent.context).inflate(itemProvider.layoutId, parent, false)
		return MultiViewHolder(itemProvider as BaseItemProvider<T>, this, itemView)
	}

	override fun onBindViewHolder(holder: BaseViewHolder<T>, position: Int) {
		holder.onBindView(getItemData(position))
	}

	override fun onBindViewHolder(holder: BaseViewHolder<T>, position: Int, payloads: MutableList<Any>) {
		// RecyclerView 默认使用此方法 bind 视图， 重写时需要主要根据 payloads 参数来保持原有的 bind 逻辑
        val itemData = getItemData(position)
		if (payloads.isEmpty()) {
            holder.onBindView(itemData)
        } else {
            holder.onBindView(itemData, payloads)
		}
	}

	/**
	 * 获取对应位置的数据
	 */
    protected fun getItemData(position: Int): T {
        return if (isDisplayEmpty()) {
            getEmptyItemData()
        } else {
            getRealData(position)
        }
	}

    // 空态：返回缓存的默认数据，采用懒加载
    private fun getEmptyItemData(): T = cachedEmptyItemData

    /**
     * 获取对应位置的真实数据
     */
    protected open fun getRealData(position: Int): T = getData()[position]

    /**
     * 根据泛型参数创建默认的 ItemData
     */
	protected fun createDefaultItemData(): T {
		return try {
            val genericSuperclass = this.javaClass.genericSuperclass
			if (genericSuperclass is ParameterizedType) {
				val type = genericSuperclass.actualTypeArguments[0]
                val declaredConstructor = (type as Class<T>).getDeclaredConstructor()
                declaredConstructor.isAccessible = true
                val instance = declaredConstructor.newInstance()
                declaredConstructor.isAccessible = false
                instance
			} else {
				error("获取传入数据类型失败！")
			}
        } catch (_: Exception) {
			error("请给数据类型添加默认构造器！")
		}
	}

    override fun getItemCount(): Int = if (isDisplayEmpty()) 1 else adapterData.size

	/**
	 * 向列表尾部插入数据
	 */
	override fun insertData(vararg addData: T) {
		if (addData.isEmpty()) return

        val wasEmpty = isDisplayEmpty()
        val lastDataSize = adapterData.size
        adapterData.addAll(addData)

        if (wasEmpty) {
            // 从空态到有数据，先移除空态，再插入新数据
            notifyItemRemoved(0)
            notifyItemRangeInserted(0, addData.size)
		} else {
			notifyItemRangeInserted(lastDataSize, addData.size)
		}
	}

	/**
     * 删除数据
	 */
	override fun removeData(vararg removeData: T) {
        if (removeData.isEmpty()) return

        removeData.forEach { item ->
            val removeIndex = adapterData.indexOf(item)
            if (removeIndex != -1 && adapterData.remove(item)) {
				if (isDisplayEmpty()) {
                    // 删除后变为空态，插入空态视图
                    notifyItemRemoved(removeIndex)
                    notifyItemInserted(0)
				} else {
					notifyItemRemoved(removeIndex)
				}
			}
		}
	}

	/**
	 * 更新当前的数据
	 */
	override fun updateData(newData: List<T>) {
        val wasEmpty = isDisplayEmpty()
        val willBeEmpty = newData.isEmpty() && emptyView != null

        if (wasEmpty && !willBeEmpty) {
            // 从空态到有数据
            adapterData.clear()
            adapterData.addAll(newData)
            notifyItemRemoved(0)
            notifyItemRangeInserted(0, newData.size)
        } else if (!wasEmpty && willBeEmpty) {
            // 从有数据到空态
            val oldSize = adapterData.size
            adapterData.clear()
            notifyItemRangeRemoved(0, oldSize)
            notifyItemInserted(0)
        } else {
            // 使用 DiffUtil 进行精确更新
            val myDiffCallback = MyDiffCallback(adapterData, newData)
            val diffResult = DiffUtil.calculateDiff(myDiffCallback, true)

            adapterData.clear()
            adapterData.addAll(newData)

            diffResult.dispatchUpdatesTo(this)
        }
	}

	/**
	 * 获取当前的数据
	 */
    override fun getData(): MutableList<T> = adapterData

	/**
	 * 修改符合条件的所有数据
	 */
    inline fun modifyDataByCondition(
        crossinline condition: (T) -> Boolean,
        crossinline modifyAction: T.() -> Unit
    ) {
        getData().forEachIndexed { index, item ->
			if (condition(item)) {
				item.modifyAction()
				notifyItemChanged(index)
			}
		}
	}

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        super.onDetachedFromRecyclerView(recyclerView)
        release()
    }

    /**
     * 释放资源
     */
    private fun release() {
        itemProviders.clear()
    }
}