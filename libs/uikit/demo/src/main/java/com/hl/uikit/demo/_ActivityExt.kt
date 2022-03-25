package com.hl.uikit.demo

import android.app.Activity
import android.view.View
import androidx.core.view.ViewCompat
import androidx.core.view.updatePadding

fun Activity.initInsetPadding(bottom: Boolean = true, top: Boolean = true) {
    val view = findViewById<View>(android.R.id.content)
    ViewCompat.setOnApplyWindowInsetsListener(view) { v, insets ->
        when {
            bottom && top -> v.updatePadding(bottom = insets.systemWindowInsetBottom, top = insets.systemWindowInsetTop)
            bottom -> v.updatePadding(bottom = insets.systemWindowInsetBottom, top = 0)
            top -> v.updatePadding(top = insets.systemWindowInsetTop, bottom = 0)
        }
        insets
    }
    ViewCompat.requestApplyInsets(view)
}