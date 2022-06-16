package com.hl.utils.span

import android.graphics.Paint
import android.graphics.Typeface

import android.text.TextPaint

import android.text.style.MetricAffectingSpan


/**
 * @author  张磊  on  2022/06/16 at 17:44
 * Email: 913305160@qq.com
 */


/**
 * 系统原生的TypefaceSpan只能使用原生的默认字体
 * 如果使用自定义的字体，通过这个来实现
 */
class CustomTypefaceSpan(private val typeface: Typeface) : MetricAffectingSpan() {
	override fun updateDrawState(drawState: TextPaint) {
		apply(drawState)
	}

	override fun updateMeasureState(paint: TextPaint) {
		apply(paint)
	}

	private fun apply(paint: Paint) {
		val oldTypeface: Typeface = paint.getTypeface()
		val oldStyle = oldTypeface?.style ?: 0
		val fakeStyle = oldStyle and typeface.style.inv()
		if (fakeStyle and Typeface.BOLD != 0) {
			paint.setFakeBoldText(true)
		}
		if (fakeStyle and Typeface.ITALIC != 0) {
			paint.setTextSkewX(-0.25f)
		}
		paint.setTypeface(typeface)
	}
}