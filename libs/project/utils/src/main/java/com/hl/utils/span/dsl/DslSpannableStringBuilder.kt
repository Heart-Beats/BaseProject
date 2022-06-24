package com.hl.utils.span.dsl

/**
 * @author  张磊  on  2022/06/16 at 17:34
 * Email: 913305160@qq.com
 */

interface DslSpannableStringBuilder {
	//增加一段文字
	fun addText(text: String, method: (DslSpanBuilder.() -> Unit)? = null)

	//添加一个图标 ,  默认底部  4是垂直居中
	fun addImage(imageRes: Int, verticalAlignment: Int = 0, marginLeft: Int = 0, marginRight: Int = 0, width: Int = 0, height: Int = 0)
}