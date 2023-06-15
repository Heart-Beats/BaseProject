package com.hl.ui.utils

import android.app.Activity
import android.os.Build
import android.view.View
import android.view.WindowManager
import androidx.core.view.ViewCompat
import androidx.core.view.updatePadding
import androidx.fragment.app.Fragment

/**
 * @author  张磊  on  2022/01/13 at 16:15
 * Email: 913305160@qq.com
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

internal fun Fragment.initInsetPadding(top: Boolean = true, bottom: Boolean = true) {
    requireActivity().initInsetPadding(top, bottom)
}