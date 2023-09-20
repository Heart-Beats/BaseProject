package com.hl.uikit.utils

import android.text.InputFilter
import android.widget.TextView

/**
 * @author  张磊  on  2023/09/07 at 17:38
 * Email: 913305160@qq.com
 */

/**
 * 设置 TextView 最大长度
 */
fun TextView.setMaxLength(length: Int) {
	val filters = this.filters.toMutableList()
	//  移除所有长度过滤器
	filters.removeAll { it is InputFilter.LengthFilter }
	filters.add(InputFilter.LengthFilter(length))
	this.filters = filters.toTypedArray()
}

/**
 * 给 TextView 添加 inputFilter
 */
fun TextView.addInputFilter(inputFilter: InputFilter) {
	val filters = this.filters.toMutableList().apply {
		add(inputFilter)
	}
	this.filters = filters.toTypedArray()
}