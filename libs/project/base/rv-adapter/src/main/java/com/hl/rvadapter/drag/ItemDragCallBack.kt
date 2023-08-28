package com.hl.rvadapter.drag

import android.content.res.ColorStateList
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.ColorStateListDrawable
import android.graphics.drawable.Drawable
import android.os.Build
import android.util.Log
import androidx.annotation.ColorInt
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.hl.rvadapter.R
import com.hl.rvadapter.utils.alpha
import java.util.Collections

/**
 * @author  张磊  on  2022/11/17 at 14:10
 * Email: 913305160@qq.com
 *
 * @param adapterData  RecyclerView 的 adapter 对应的数据
 * @param isCanSwipe  是否可以滑动
 */

class ItemDragCallBack<T>(private val adapterData: MutableList<T>, private val isCanSwipe: Boolean = true) :
	ItemTouchHelper.Callback() {

	private companion object {
		private const val TAG = "ItemDragCallBack"
	}

	/**
	 * 长按时的背景选中色
	 */
	@ColorInt
	var longPressColor: Int? = null

	private var recyclerView: RecyclerView? = null
		set(value) {
			field = value
			if (longPressColor == null && value != null) {
				longPressColor = ContextCompat.getColor(value.context, R.color.main_color).alpha(0.3F)
			}
		}

	private var lastItemViewBackground: Drawable? = null

	/**
	 * 拖拽或滑动是否结束
	 */
	private var isClearViewFlag = true

	/**
	 * 创建交互方式，交互方式分为两种：
	 *
	 *  1. 拖拽，网格布局支持上下左右，列表只支持上下（LEFT、UP、RIGHT、DOWN）
	 *  2. 滑动，只支持前后（START、END）
	 */
	override fun getMovementFlags(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder): Int {
		Log.i(TAG, "getMovementFlags: 开始获取交互标识")
		this.recyclerView = recyclerView

		val dragFlags: Int
		var swipeFlags = 0
		when (recyclerView.layoutManager) {
			is GridLayoutManager -> {
				// 网格布局
				dragFlags = ItemTouchHelper.LEFT or ItemTouchHelper.UP or ItemTouchHelper.RIGHT or ItemTouchHelper.DOWN
				return makeMovementFlags(dragFlags, swipeFlags)
			}
			is LinearLayoutManager -> {
				// 线性布局
				dragFlags = ItemTouchHelper.UP or ItemTouchHelper.DOWN
				swipeFlags = ItemTouchHelper.START or ItemTouchHelper.END
				return makeMovementFlags(dragFlags, swipeFlags)
			}
			else -> {
				// 其他情况可自行处理
				return 0
			}
		}
	}

	/**
	 * 拖拽时回调, 主要对起始位置和目标位置的item做一个数据交换，然后刷新视图显示
	 */
	override fun onMove(
		recyclerView: RecyclerView,
		viewHolder: RecyclerView.ViewHolder,
		target: RecyclerView.ViewHolder
	): Boolean {
		// 起始位置
		val fromPosition = viewHolder.bindingAdapterPosition
		// 结束位置
		val toPosition = target.bindingAdapterPosition

		Log.i(TAG, "onMove: 正在拖拽中------> fromPosition == $fromPosition, toPosition == $toPosition")

		if (fromPosition > adapterData.size || toPosition > adapterData.size) {
			throw ArrayIndexOutOfBoundsException("设置的 data 有误，请确认设置正确的 data")
		}

		// 根据滑动方向 交换数据
		if (fromPosition < toPosition) {
			// 含头不含尾
			for (index in fromPosition until toPosition) {
				Collections.swap(adapterData, index, index + 1)
			}
		} else {
			// 含头不含尾
			for (index in fromPosition downTo toPosition + 1) {
				Collections.swap(adapterData, index, index - 1)
			}
		}
		// 刷新布局
		recyclerView.adapter?.notifyItemMoved(fromPosition, toPosition)
		return true
	}

	/**
	 * 滑动时回调，这个回调方法里主要是做数据和视图的更新操作
	 */
	override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
		if (direction == ItemTouchHelper.START) {
			Log.i(TAG, "START--->向左滑")
		} else {
			Log.i(TAG, "END--->向右滑")
		}
		val position = viewHolder.bindingAdapterPosition
		adapterData.removeAt(position)
		recyclerView!!.adapter?.notifyItemRemoved(position)
	}

	/**
	 * 拖拽或滑动 发生改变时回调，这时可以修改 item 的视图
	 *
	 *   @see  [ItemTouchHelper.ACTION_STATE_IDLE]: 空闲状态
	 *   @see  [ItemTouchHelper.ACTION_STATE_SWIPE]: 滑动状态
	 *   @see  [ItemTouchHelper.ACTION_STATE_DRAG]: 拖拽状态
	 */
	override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {
		Log.d(TAG, "onSelectedChanged:   actionState == $actionState")

		if (actionState != ItemTouchHelper.ACTION_STATE_IDLE) {

			if (!isClearViewFlag) {
				Log.d(TAG, "onSelectedChanged:   拖拽或滑动是否结束 == $isClearViewFlag")
				return
			}

			viewHolder?.let {
				val itemView = it.itemView

				when (recyclerView!!.layoutManager) {
					is GridLayoutManager -> {
						// 网格布局 设置选中放大
						ViewCompat.animate(itemView).setDuration(200).scaleX(1.3F).scaleY(1.3F).start()
					}

					is LinearLayoutManager -> {

						// 长按时修改背景色
						longPressColor?.run {

							// 保存原有背景效果
							lastItemViewBackground = itemView.background

							val drawable = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
								// Android 10 以上
								val states = arrayOf(intArrayOf(android.R.attr.state_pressed))
								val colors = intArrayOf(this)
								ColorStateListDrawable(ColorStateList(states, colors))
							} else {
								ColorDrawable(this)
							}

							itemView.background = drawable
						}
					}

					else -> {}
				}
			}

			isClearViewFlag = false
		}
		super.onSelectedChanged(viewHolder, actionState)
	}

	/**
	 * 拖拽或滑动 结束时回调，这时要把改变后的 item 视图恢复到初始状态
	 */
	override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
		Log.d(TAG, "clearView: ")

		// 恢复显示
		when (recyclerView.layoutManager) {
			is GridLayoutManager -> {
				// 网格布局 设置选中大小
				ViewCompat.animate(viewHolder.itemView).setDuration(200).scaleX(1F).scaleY(1F).start()
			}

			is LinearLayoutManager -> {
				// 线性布局设置背景为原有背景

				viewHolder.itemView.background = lastItemViewBackground
			}
		}
		super.clearView(recyclerView, viewHolder)

		isClearViewFlag = true
	}


	/**
	 * 是否可以滑动
	 */
	override fun isItemViewSwipeEnabled(): Boolean {
		Log.d(TAG, "isItemViewSwipeEnabled: $isCanSwipe")
		return isCanSwipe
	}

	/**
	 * 是否可以长按拖拽,  禁用后需要手动调用 startDrag 开启
	 */
	override fun isLongPressDragEnabled(): Boolean {
		val longPressDragEnabled = super.isLongPressDragEnabled()
		Log.d(TAG, "isLongPressDragEnabled: $longPressDragEnabled")

		return longPressDragEnabled
	}
}