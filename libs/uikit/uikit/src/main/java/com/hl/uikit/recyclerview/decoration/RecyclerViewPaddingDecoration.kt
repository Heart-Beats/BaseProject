package com.hl.uikit.recyclerview.decoration

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.hl.uikit.utils.dp

/**
 * 设置左右 Padding 和第一个的上 padding 以及最后一个的下 padding
 */
class RecyclerViewPaddingDecoration(val dpLeft: Float = 0f, val dpTop: Float = 0f, val dpRight: Float = 0f, val dpBottom: Float = 0f) : RecyclerView.ItemDecoration() {
    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        val position = parent.getChildAdapterPosition(view)
        val itemCount = parent.adapter?.itemCount ?: 0
        val left = dpLeft.dp.toInt()
        val top: Int = dpTop.dp.toInt()
        val right: Int = dpRight.dp.toInt()
        val bottom: Int = dpBottom.dp.toInt()
        when (position) {
            0 -> {
                outRect.set(left, top, right, 0)
            }
            itemCount - 1 -> {
                outRect.set(left, 0, right, bottom)
            }
            else -> {
                outRect.set(left, 0, right, 0)
            }
        }
    }
}