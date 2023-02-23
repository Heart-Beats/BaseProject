package com.hl.utils

import android.graphics.Color
import androidx.annotation.ColorInt
import androidx.annotation.FloatRange
import kotlin.math.roundToInt

/**
 * @author  张磊  on  2023/02/23 at 15:05
 * Email: 913305160@qq.com
 */

/**
 * 将指定的颜色转换透明度
 *
 * @param percent   透明度
 * @return          最终设置过透明度的颜色值
 */
@ColorInt
fun @receiver:ColorInt Int.convertAlpha(@FloatRange(from = 0.0, to = 1.0) percent: Float): Int {
	val blue = Color.blue(this)
	val green = Color.green(this)
	val red = Color.red(this)
	var alpha = Color.alpha(this)
	alpha = (alpha * percent).roundToInt()

	return Color.argb(alpha, red, green, blue)
}
