package com.hl.utils

import android.content.Context
import android.content.res.TypedArray

/**
 * @author  张磊  on  2022/08/15 at 14:15
 * Email: 913305160@qq.com
 */

fun Context.getActionBarSize(): Int {
	val styledAttributes: TypedArray = this.theme.obtainStyledAttributes(intArrayOf(android.R.attr.actionBarSize))
	val actionBarSize = styledAttributes.getDimension(0, 0f).toInt()
	styledAttributes.recycle()

	return actionBarSize
}