package com.hl.utils

import android.content.res.Resources
import android.util.TypedValue

internal val Int.dpInt
	get() = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, this.toFloat(), Resources.getSystem().displayMetrics)
		.toInt()

internal val Int.dp
	get() = this.dpInt

internal val Int.sp
	get() = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, this.toFloat(), Resources.getSystem().displayMetrics)

internal val Float.dp
	get() = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, this, Resources.getSystem().displayMetrics)

internal val Float.sp
	get() = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, this, Resources.getSystem().displayMetrics)