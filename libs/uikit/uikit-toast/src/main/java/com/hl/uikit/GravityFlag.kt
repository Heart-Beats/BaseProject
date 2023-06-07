package com.hl.uikit

import android.view.Gravity
import androidx.annotation.IntDef
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy

/**
 * @author  张磊  on  2023/06/07 at 17:20
 * Email: 913305160@qq.com
 */


@Retention(RetentionPolicy.SOURCE)
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