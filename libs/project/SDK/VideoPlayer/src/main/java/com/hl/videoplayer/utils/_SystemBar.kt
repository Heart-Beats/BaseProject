package com.hl.videoplayer.utils

import android.app.Activity
import android.view.View
import androidx.core.view.ViewCompat
import androidx.core.view.updatePadding
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LifecycleOwner

/**
 * @author  张磊  on  2022/01/13 at 16:15
 * Email: 913305160@qq.com
 */

/**
 * 根试图是否设置状态栏或者导航栏高度的 padding
 */
internal fun Activity.initInsetPadding(top: Boolean = true, bottom: Boolean = true) {
	val view = findViewById<View>(android.R.id.content)
	ViewCompat.setOnApplyWindowInsetsListener(view) { v, insets ->
		when {
			bottom && top -> v.updatePadding(
				bottom = insets.systemWindowInsetBottom,
				top = insets.systemWindowInsetTop
			)

			bottom -> v.updatePadding(bottom = insets.systemWindowInsetBottom, top = 0)
			top -> v.updatePadding(top = insets.systemWindowInsetTop, bottom = 0)
		}
		insets
	}
	ViewCompat.requestApplyInsets(view)
}

/**
 * 根试图是否设置状态栏或者导航栏高度的 padding
 */
internal fun Fragment.initInsetPadding(top: Boolean = true, bottom: Boolean = true) {
	requireActivity().initInsetPadding(top, bottom)
}


/**
 * 根试图是否设置状态栏或者导航栏高度的 padding
 */
internal fun LifecycleOwner.initInsetPaddingSmart(top: Boolean = true, bottom: Boolean = true) {
	when (this) {
		is FragmentActivity -> this.initInsetPadding(top, bottom)
		is Fragment -> this.initInsetPadding(top, bottom)
	}
}