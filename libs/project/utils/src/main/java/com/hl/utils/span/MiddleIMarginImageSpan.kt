package com.hl.utils.span

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.drawable.Drawable


/**
 * @author  张磊  on  2022/06/16 at 17:45
 * Email: 913305160@qq.com
 */


/**
 * 支持设置图片左右间距的 ImageSpan
 * 继承与居中的imageSpan
 * 我一般用这个，功能更多  已经用于扩展Span中
 */
class MiddleIMarginImageSpan @JvmOverloads constructor(
	d: Drawable?,
	verticalAlignment: Int,
	marginLeft: Int,
	marginRight: Int,
	offsetY: Int = 0
) :
	AlignMiddleImageSpan(d!!, verticalAlignment) {
	private var mSpanMarginLeft = 0
	private var mSpanMarginRight = 0
	private var mOffsetY = 0

	override fun getSize(paint: Paint, text: CharSequence?, start: Int, end: Int, fm: Paint.FontMetricsInt?): Int {
		return if (mSpanMarginLeft != 0 || mSpanMarginRight != 0) {
			super.getSize(paint, text, start, end, fm)
			val d = drawable
			d.intrinsicWidth + mSpanMarginLeft + mSpanMarginRight
		} else {
			super.getSize(paint, text, start, end, fm)
		}
	}

	override fun draw(
		canvas: Canvas,
		text: CharSequence?,
		start: Int,
		end: Int,
		x: Float,
		top: Int,
		y: Int,
		bottom: Int,
		paint: Paint
	) {
		canvas.save()
		canvas.translate(0F, mOffsetY.toFloat())
		// marginRight不用专门处理，只靠getSize()中改变即可
		super.draw(canvas, text, start, end, x + mSpanMarginLeft, top, y, bottom, paint)
		canvas.restore()
	}

	init {
		mSpanMarginLeft = marginLeft
		mSpanMarginRight = marginRight
		mOffsetY = offsetY
	}
}