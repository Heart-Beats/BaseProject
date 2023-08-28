package com.hl.utils.span.dsl

import android.widget.TextView

/**
 * @author  张磊  on  2022/06/16 at 17:34
 * Email: 913305160@qq.com
 */

/**
 * 为 TextView 创建扩展函数，其参数为接口的扩展函数
 */
fun TextView.buildSpannableString(init: DslSpannableStringBuilder.() -> Unit) {
	//具体实现类
	val spanStringBuilderImpl = DslSpannableStringBuilderImpl(this)
	spanStringBuilderImpl.init()
	//通过实现类返回SpannableStringBuilder
	text = spanStringBuilderImpl.build()
}