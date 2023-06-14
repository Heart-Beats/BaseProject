package com.hl.utils

import android.content.Context
import android.graphics.drawable.Drawable
import android.view.View
import androidx.annotation.ColorRes
import androidx.annotation.DimenRes
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
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

fun View.getColorByRes(@ColorRes colorRes: Int): Int {
	return context.getColorByRes(colorRes)
}

fun getColorByRes(@ColorRes colorRes: Int): Int {
	return BaseUtil.app.getColorByRes(colorRes)
}

fun Context.getPxByRes(@DimenRes dimenRes: Int): Int {
	return resources.getDimensionPixelOffset(dimenRes)
}

fun Fragment.getPxByRes(@DimenRes dimenRes: Int): Int {
	return requireContext().getPxByRes(dimenRes)
}

fun View.getPxByRes(@DimenRes dimenRes: Int): Int {
	return context.getPxByRes(dimenRes)
}

fun getPxByRes(@DimenRes dimenRes: Int): Int {
	return BaseUtil.app.getPxByRes(dimenRes)
}

fun Context.getTextByRes(@StringRes stringRes: Int): CharSequence {
	return resources.getText(stringRes)
}

fun Fragment.geTextByRes(@StringRes stringRes: Int): CharSequence {
	return requireContext().getTextByRes(stringRes)
}

fun View.geTextByRes(@StringRes stringRes: Int): CharSequence {
	return context.getTextByRes(stringRes)
}

fun geTextByRes(@StringRes stringRes: Int): CharSequence {
	return BaseUtil.app.getTextByRes(stringRes)
}

fun Context.getDrawableByRes(@DrawableRes drawableRes: Int): Drawable? {
	return ContextCompat.getDrawable(this, drawableRes)
}

fun Fragment.getDrawableByRes(@DrawableRes drawableRes: Int): Drawable? {
	return requireContext().getDrawableByRes(drawableRes)
}

fun View.getDrawableByRes(@DrawableRes drawableRes: Int): Drawable? {
	return context.getDrawableByRes(drawableRes)
}

fun getDrawableByRes(@DrawableRes drawableRes: Int): Drawable? {
	return BaseUtil.app.getDrawableByRes(drawableRes)
}