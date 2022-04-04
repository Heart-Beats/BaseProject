package com.hl.uikit.recyclerview.decoration

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.hl.uikit.dpInt

/**
 * @Author  张磊  on  2020/07/20 at 14:44
 * Email: 913305160@qq.com
 */
class MarginStartDecoration(
	val marginPx: Int,
	dividerColor: Int = Color.parseColor("#F4F4F4"),
	val dividerHeightPx: Int = 1.dpInt
) : RecyclerView.ItemDecoration() {

	private val paint: Paint = Paint()

	init {
		paint.isAntiAlias = true
		paint.style = Paint.Style.FILL
		paint.color = dividerColor
	}

	override fun onDrawOver(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
		super.onDrawOver(c, parent, state)
		val linearLayoutManager = requireLinearLayoutManager(parent)
		if (linearLayoutManager.orientation == LinearLayoutManager.VERTICAL) {
			drawVerticalDivider(c, parent, linearLayoutManager)
		}
	}

	override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
		super.getItemOffsets(outRect, view, parent, state)
		outRect.set(0, 0, 0, 0)
	}

	private fun drawVerticalDivider(c: Canvas, parent: RecyclerView, layoutManager: RecyclerView.LayoutManager) {
		//最后一条不画分割线
		for (i in 0 until parent.childCount - 1) {
			val childView = parent.getChildAt(i)

			val leftDecorationWidth = layoutManager.getLeftDecorationWidth(childView).toFloat()
			// val topDecorationHeight = layoutManager.getTopDecorationHeight(childView).toFloat()
			// val rightDecorationWidth = layoutManager.getRightDecorationWidth(childView).toFloat()
			// val bottomDecorationHeight = layoutManager.getBottomDecorationHeight(childView).toFloat()

			c.drawRect(leftDecorationWidth + marginPx, childView.bottom - dividerHeightPx.toFloat(), childView.right.toFloat(), childView.bottom.toFloat(), paint)
		}
	}

	private fun requireLinearLayoutManager(parent: RecyclerView): LinearLayoutManager {
		val layoutManager = parent.layoutManager
		if (layoutManager is LinearLayoutManager) {
			return layoutManager
		}
		throw IllegalStateException("The layoutManager must be LinearLayoutManager")
	}

}