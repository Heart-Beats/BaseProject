package com.hl.utils.span.dsl

import android.graphics.Color
import android.text.SpannableStringBuilder
import android.text.method.LinkMovementMethod
import android.widget.TextView
import com.hl.utils.span.toBackgroundColorSpan
import com.hl.utils.span.toClickSpan
import com.hl.utils.span.toColorSpan
import com.hl.utils.span.toCustomTypeFaceSpan
import com.hl.utils.span.toImageSpan
import com.hl.utils.span.toScaleSpan
import com.hl.utils.span.toSizeSpan
import com.hl.utils.span.toStrikeThroughSpan

/**
 * @author  张磊  on  2022/06/16 at 17:35
 * Email: 913305160@qq.com
 */

class DslSpannableStringBuilderImpl(private val textView: TextView) : DslSpannableStringBuilder {

	private val builder = SpannableStringBuilder()

	//添加文本
	override fun addText(text: String, method: (DslSpanBuilder.() -> Unit)?) {

		val spanBuilder = DslSpanBuilderImpl()
		method?.let { spanBuilder.it() }

		var charSeq: CharSequence = text

		spanBuilder.apply {
			if (isSetColor) {
				charSeq = charSeq.toColorSpan(0..text.length, textColor)
			}
			if (isSetBackground) {
				charSeq = charSeq.toBackgroundColorSpan(0..text.length, textBackgroundColor)
			}
			if (isSetScale) {
				charSeq = charSeq.toScaleSpan(0..text.length, scaleSize)
			}
			if (isSetSize) {
				charSeq = charSeq.toSizeSpan(0..text.length, textSize, isSp)
			}
			if (isOnClick) {
				textView.movementMethod = LinkMovementMethod.getInstance()
				// 点击时移除点击背景高亮色
				textView.highlightColor = Color.TRANSPARENT
				charSeq = charSeq.toClickSpan(0..text.length, textColor, isUseUnderLine, onClick)
			}
			if (isSetTypeface) {
				charSeq = charSeq.toCustomTypeFaceSpan(typefaces, 0..text.length)
			}
			if (isSetStrikethrough) {
				charSeq = charSeq.toStrikeThroughSpan(0..text.length)
			}

			builder.append(charSeq)
		}
	}

	//添加图标
	override fun addImage(imageRes: Int, verticalAlignment: Int, maginLeft: Int, marginRight: Int, width: Int, height: Int) {
		var charSeq: CharSequence = "1"
		charSeq = charSeq.toImageSpan(imageRes, 0..1, verticalAlignment, maginLeft, marginRight, width, height)
		builder.append(charSeq)
	}

	fun build(): SpannableStringBuilder {
		return builder
	}

}