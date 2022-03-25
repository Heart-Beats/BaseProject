package com.hl.utils

import android.app.Activity
import android.content.Context
import android.graphics.Rect
import android.util.DisplayMetrics
import android.view.View
import android.view.Window
import android.view.WindowManager

/**
 * @author  张磊  on  2021/12/28 at 18:18
 * Email: 913305160@qq.com
 */

fun Activity.getNavigationBarRealHeight(): Int {
    val window: Window = this.getWindow()
    val decorView: View = window.getDecorView()
    val rect = Rect()
    decorView.getWindowVisibleDisplayFrame(rect)
    val outMetrics = DisplayMetrics()
    val windowManager: WindowManager = window.windowManager
    windowManager.defaultDisplay.getRealMetrics(outMetrics)

    val navigationBarHeight = getNavigationBarHeight(this)
    val heightDifference = outMetrics.heightPixels - rect.bottom - navigationBarHeight

    // 差值 >0 说明存在  NavigationBar（虚拟按键）, 反之不存在
    return if (heightDifference > 0) navigationBarHeight else 0
}

private fun getNavigationBarHeight(context: Context): Int {
    val resources = context.resources
    val resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android")
    return if (resourceId > 0) {
        resources.getDimensionPixelSize(resourceId)
    } else 0
}