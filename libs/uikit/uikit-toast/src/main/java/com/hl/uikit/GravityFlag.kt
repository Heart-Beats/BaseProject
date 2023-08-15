package com.hl.uikit

import android.view.Gravity
import androidx.annotation.IntDef

/**
 * @author  张磊  on  2023/06/07 at 17:20
 * Email: 913305160@qq.com
 */


@Retention(AnnotationRetention.SOURCE)
@IntDef(
	Gravity.TOP,
	Gravity.BOTTOM,
	Gravity.START,
	Gravity.END,
	Gravity.CENTER_HORIZONTAL,
	Gravity.CENTER_VERTICAL,
	Gravity.CENTER
)
annotation class GravityFlag