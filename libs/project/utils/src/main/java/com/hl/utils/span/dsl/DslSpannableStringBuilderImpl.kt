package com.hl.utils.span.dsl

import android.text.SpannableStringBuilder
import com.hl.utils.span.*

/**
 * @author  张磊  on  2022/06/16 at 17:35
 * Email: 913305160@qq.com
 */

class DslSpannableStringBuilderImpl : DslSpannableStringBuilder {

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
				charSeq = charSeq.toSizeSpan(0..text.length, scaleSize)
			}
			if (isOnClick) {
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