package com.hl.utils.span.dsl

import android.graphics.Color
import android.graphics.Typeface

/**
 * @author  张磊  on  2022/06/16 at 17:32
 * Email: 913305160@qq.com
 */
class DslSpanBuilderImpl : DslSpanBuilder {
	var isSetColor = false
	var textColor: Int = Color.BLACK

	var isOnClick = false
	var isUseUnderLine = false
	var onClick: (() -> Unit)? = null

	var isSetScale = false
	var scaleSize = 1.0f

	var isSetSize = false
	var textSize = 14
	var isSp = true

	var isSetTypeface = false
	var typefaces: Typeface = Typeface.DEFAULT

	var isSetStrikethrough = false

	var isSetBackground = false
	var textBackgroundColor = 0

	override fun setColor(color: Int) {
		isSetColor = true
		textColor = color
	}

	override fun setClick(useUnderLine: Boolean, onClick: (() -> Unit)?) {
		isOnClick = true
		isUseUnderLine = useUnderLine
		this.onClick = onClick
	}

	override fun setScale(scale: Float) {
		isSetScale = true
		scaleSize = scale
	}

	override fun setSize(size: Int, isSp: Boolean) {
		this.isSetSize = true
		this.textSize = size
		this.isSp= isSp
	}

	override fun setTypeface(typeface: Typeface) {
		isSetTypeface = true
		typefaces = typeface
	}

	override fun setStrikethrough(isStrikethrough: Boolean) {
		isSetStrikethrough = isStrikethrough
	}

	override fun setBackground(color: Int) {
		isSetBackground = true
		textBackgroundColor = color
	}

}