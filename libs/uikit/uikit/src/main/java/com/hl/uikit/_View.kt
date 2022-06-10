package com.hl.uikit

import android.content.res.Resources
import android.text.InputFilter
import android.text.TextUtils
import android.util.TypedValue
import android.view.View
import android.widget.TextView

fun View.onClick(listener: (View) -> Unit) {
    setOnClickListener {
        val time = it.getTag(R.id.uikit_view_click_time)?.toString()?.toLongOrNull()
        val currentTime = System.currentTimeMillis()
        if (time == null || currentTime - time > 500) {
            it.setTag(R.id.uikit_view_click_time, currentTime)
            listener.invoke(it)
        }
    }
}

fun View.getStatusBarHeight(): Int {
    try {
        var result = 0
        val resourceId = context.resources.getIdentifier("status_bar_height", "dimen", "android")
        if (resourceId > 0) {
            result = context.resources.getDimensionPixelSize(resourceId)
        }
        return result
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return 0
}

fun TextView.setMaxLength(length: Int) {
    val filters = this.filters.toMutableList()
    filters.add(InputFilter.LengthFilter(length))
    this.filters = filters.toTypedArray()
    this.ellipsize = TextUtils.TruncateAt.END
}

private fun View.setVisible(visibility: Int) {
    if (this.visibility != visibility) {
        this.visibility = visibility
    }
}

fun View.visible() {
    setVisible(View.VISIBLE)
}

fun View.invisible() {
    setVisible(View.INVISIBLE)
}

fun View.gone() {
    setVisible(View.GONE)
}

fun View.visibleOrGone(show: Boolean) {
    if (show) visible() else gone()
}


val Int.dpInt
    get() = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, this.toFloat(), Resources.getSystem().displayMetrics)
        .toInt()

val Float.dp
    get() = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, this, Resources.getSystem().displayMetrics)

val Float.sp
    get() = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, this, Resources.getSystem().displayMetrics)

