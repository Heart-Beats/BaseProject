package com.youma.arch.utils

import android.content.Context
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment

/**
 * @author  张磊  on  2021/11/05 at 16:06
 * Email: 913305160@qq.com
 */

fun Context.getColorByRes(@ColorRes colorRes: Int): Int {
	return ContextCompat.getColor(this, colorRes)
}

fun Fragment.getColorByRes(@ColorRes colorRes: Int): Int {
	return requireContext().getColorByRes(colorRes)
}