package com.hl.uikit.utils

import android.content.Context
import android.graphics.Point
import android.os.Build
import android.view.View
import android.view.WindowManager
import androidx.fragment.app.Fragment

/**
 * @author  张磊  on  2023/01/12 at 16:17
 * Email: 913305160@qq.com
 */

/**
 * 获取屏幕宽度
 */
fun Context.getScreenWidth(): Int {
	val wm = this.getSystemService(Context.WINDOW_SERVICE) as? WindowManager
	val point = Point()
	if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
		wm?.defaultDisplay?.getRealSize(point)
	} else {
		wm?.defaultDisplay?.getSize(point)
	}
	return point.x
}

/**
 * 获取屏幕宽度
 */
fun Fragment.getScreenWidth(): Int {
	return requireContext().getScreenWidth()
}

/**
 * 获取屏幕宽度
 */
fun View.getScreenWidth(): Int {
	return context.getScreenWidth()
}

/**
 * 获取屏幕高度
 */
fun Context.getScreenHeight(): Int {
	val wm = this.getSystemService(Context.WINDOW_SERVICE) as? WindowManager
	val point = Point()
	if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
		wm?.defaultDisplay?.getRealSize(point)
	} else {
		wm?.defaultDisplay?.getSize(point)
	}
	return point.y
}

/**
 * 获取屏幕高度
 */
fun Fragment.getScreenHeight(): Int {
	return requireContext().getScreenHeight()
}

/**
 * 获取屏幕高度
 */
fun View.getScreenHeight(): Int {
	return context.getScreenHeight()
}