package com.hl.utils.views

import android.view.MotionEvent
import android.view.View
import androidx.core.widget.NestedScrollView

/**
 * @author  张磊  on  2023/10/24 at 10:30
 * Email: 913305160@qq.com
 */

/**
 * 设置滚动条可以被触摸
 */
fun NestedScrollView.setSupportTouchOnScrollBar() {
	this.setOnTouchListener { v, event ->
		handleTouch(event)
		false
	}
}

private fun NestedScrollView.handleTouch(event: MotionEvent) {
	// if (event.action != MotionEvent.ACTION_DOWN) return

	// 获取滚动条的滚动总范围
	val scrollRange = this.computeVerticalScrollRange()

	// 获取触摸事件的位置
	val x = event.x
	val y = event.y

	if (this.isTouchOnScrollBar(x, y)) {
		// 计算滚动位置
		val scrollTo = (y / this.height * scrollRange).toInt()

		// 滚动到计算的位置
		this.smoothScrollTo(0, scrollTo)
	}
}

/**
 * 是否按在滚动条上
 * @param [x] x 横坐标
 * @param [y] y 纵坐标
 * @return [Boolean]
 */
private fun NestedScrollView.isTouchOnScrollBar(x: Float, y: Float): Boolean {
	// this.verticalScrollbarThumbDrawable?.bounds 可通过这个来获取滚动条的范围

	// NestedScrollView 的宽度
	val width = this.width

	// 是否绘制竖直滚动条
	val verticalScrollBarEnabled = this.isVerticalScrollBarEnabled

	val scrollBarStyle = this.scrollBarStyle

	// 滚动条是否在内部
	val isInside =
		scrollBarStyle == View.SCROLLBARS_INSIDE_INSET || scrollBarStyle == View.SCROLLBARS_INSIDE_OVERLAY

	val verticalScrollbarPosition = this.verticalScrollbarPosition
	// 滚动条是否在右侧
	val isPositionRight =
		verticalScrollbarPosition == View.SCROLLBAR_POSITION_RIGHT || verticalScrollbarPosition == View.SCROLLBAR_POSITION_DEFAULT

	var isTouchOnScrollBar = false
	if (verticalScrollBarEnabled) {
		val verticalScrollbarWidth = this.verticalScrollbarWidth
		when {
			isInside -> {
				isTouchOnScrollBar = if (isPositionRight) {
					// 右侧内部
					width - verticalScrollbarWidth <= x && x <= width
				} else {
					// 左侧内部
					0 <= x && x <= verticalScrollbarWidth
				}
			}

			else -> {
				isTouchOnScrollBar = if (isPositionRight) {
					// 右侧外部
					width <= x && x <= width + verticalScrollbarWidth
				} else {
					// 左侧外部
					-verticalScrollbarWidth <= x && x <= 0
				}
			}
		}
	}
	return isTouchOnScrollBar
}