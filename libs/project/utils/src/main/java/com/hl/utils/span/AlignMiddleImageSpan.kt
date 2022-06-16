package com.hl.utils.span

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.drawable.Drawable

import android.text.style.ImageSpan
import androidx.annotation.NonNull


/**
 * @author  张磊  on  2022/06/16 at 17:43
 * Email: 913305160@qq.com
 */

/**
 * 支持垂直居中的ImageSpan
 */
open class AlignMiddleImageSpan @JvmOverloads constructor(
	@NonNull d: Drawable,
	verticalAlignment: Int,
	fontWidthMultiple: Float = 0f
) :
	ImageSpan(d.mutate(), verticalAlignment) {
	/**
	 * 规定这个Span占几个字的宽度
	 */
	private var mFontWidthMultiple = -1f

	/**
	 * 是否避免父类修改FontMetrics，如果为 false 则会走父类的逻辑, 会导致FontMetrics被更改
	 */
	private var mAvoidSuperChangeFontMetrics = false
	private var mWidth = 0
	private val mDrawable: Drawable
	private val mDrawableTintColorAttr = 0
	override fun getSize(paint: Paint, text: CharSequence?, start: Int, end: Int, fm: Paint.FontMetricsInt?): Int {
		mWidth = if (mAvoidSuperChangeFontMetrics) {
			val d = drawable
			val rect: Rect = d.bounds
			rect.right
		} else {
			super.getSize(paint, text, start, end, fm)
		}
		if (mFontWidthMultiple > 0) {
			mWidth = ((paint.measureText("子") * mFontWidthMultiple).toInt())
		}
		return mWidth
	}

	override fun draw(
		canvas: Canvas, text: CharSequence?, start: Int, end: Int,
		x: Float, top: Int, y: Int, bottom: Int, paint: Paint
	) {
		if (mVerticalAlignment == ALIGN_MIDDLE) {
			val d = mDrawable
			canvas.save()

			//            // 注意如果这样实现会有问题：TextView 有 lineSpacing 时，这里 bottom 偏大，导致偏下
			//            int transY = bottom - d.getBounds().bottom; // 底对齐
			//            transY -= (paint.getFontMetricsInt().bottom - paint.getFontMetricsInt().top) / 2 - d.getBounds().bottom / 2; // 居中对齐
			//            canvas.translate(x, transY);
			//            d.draw(canvas);
			//            canvas.restore();
			val fontMetricsInt: Paint.FontMetricsInt = paint.getFontMetricsInt()
			val fontTop: Int = y + fontMetricsInt.top
			val fontMetricsHeight: Int = fontMetricsInt.bottom - fontMetricsInt.top
			val iconHeight = d.bounds.bottom - d.bounds.top
			val iconTop = fontTop + (fontMetricsHeight - iconHeight) / 2
			canvas.translate(x, iconTop.toFloat())
			d.draw(canvas)
			canvas.restore()
		} else {
			super.draw(canvas, text, start, end, x, top, y, bottom, paint)
		}
	}

	/**
	 * 是否避免父类修改FontMetrics，如果为 false 则会走父类的逻辑, 会导致FontMetrics被更改
	 */
	fun setAvoidSuperChangeFontMetrics(avoidSuperChangeFontMetrics: Boolean) {
		mAvoidSuperChangeFontMetrics = avoidSuperChangeFontMetrics
	}

	companion object {
		const val ALIGN_MIDDLE = 4 // 默认垂直居中
	}
	/**
	 * @param d                 作为 span 的 Drawable
	 * @param verticalAlignment 垂直对齐方式, 如果要垂直居中, 则使用 [.ALIGN_MIDDLE]
	 * @param fontWidthMultiple 设置这个Span占几个中文字的宽度, 当该值 > 0 时, span 的宽度为该值*一个中文字的宽度; 当该值 <= 0 时, span 的宽度由 [.mAvoidSuperChangeFontMetrics] 决定
	 */
	/**
	 * @param d                 作为 span 的 Drawable
	 * @param verticalAlignment 垂直对齐方式, 如果要垂直居中, 则使用 [.ALIGN_MIDDLE]
	 */
	init {
		mDrawable = drawable
		if (fontWidthMultiple >= 0) {
			mFontWidthMultiple = fontWidthMultiple
		}
	}
}