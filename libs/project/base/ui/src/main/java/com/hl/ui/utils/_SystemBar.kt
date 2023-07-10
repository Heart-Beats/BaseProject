package com.hl.ui.utils

import android.app.Activity
import android.os.Build
import android.view.View
import android.view.WindowManager
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
fun Activity.initInsetPadding(top: Boolean = true, bottom: Boolean = true) {
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
fun Fragment.initInsetPadding(top: Boolean = true, bottom: Boolean = true) {
    requireActivity().initInsetPadding(top, bottom)
}

/**
 * 根试图是否设置状态栏或者导航栏高度的 padding
 */
fun LifecycleOwner.initInsetPaddingSmart(top: Boolean = true, bottom: Boolean = true) {
    when (this) {
        is FragmentActivity -> this.initInsetPadding(top, bottom)
        is Fragment -> this.initInsetPadding(top, bottom)
    }
}


/**
 * 原始系统栏的状态 Flag, 在设置沉浸式之前获取用于后续取消沉浸式时恢复原始系统栏状态
 */
private var originSystemFlag: Int? = null

/**
 * 设置沉浸式状态栏
 *
 * @param isImmersive： 是否沉浸式，沉浸式时为全屏，只有用户上下拉状态栏或者导航栏时，系统栏才会显示并一段时间自动隐藏
 *
 */
fun Activity.setImmersiveSystemBar(isImmersive: Boolean) {
    /*
     *  SystemUiVisibility 中的 Flag:
     *       不带 Layout：
     *               1. View.SYSTEM_UI_FLAG_FULLSCREEN： 隐藏状态栏不显示
     *               2. View.SYSTEM_UI_FLAG_HIDE_NAVIGATION： 隐藏导航栏不显示
     *
     *       带 Layout :
     *              1. View.SYSTEM_UI_FLAG_LAYOUT_STABLE：    稳定布局，不会随系统栏显示隐藏变化
     *              2. View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN： 扩展布局到状态栏下
     *              3. View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION: 扩展布局到导航栏下
     *
     *
     *      沉浸模式：
     *          1. View.SYSTEM_UI_FLAG_IMMERSIVE： 全屏模式，如果没有设置这个标志，设置全屏时，我们点击屏幕的任意位置，就会恢复为正常模式
     *          2. View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY：全屏模式下，用户上下拉状态栏或者导航栏时，系统栏只是以半透明的状态显示并在一定时间后会自动消失
     *
     * */
    val decorView: View = window.decorView
    if (isImmersive) {
        originSystemFlag = decorView.systemUiVisibility

        if (Build.VERSION.SDK_INT >= 19) {
            decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
        }

        // 加入此标志位去除 fitsSystemWindows 属性设置对全屏模式下应用视图关于状态栏的保护
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
    } else {
        decorView.systemUiVisibility =
            originSystemFlag ?: View.SYSTEM_UI_FLAG_VISIBLE or View.SYSTEM_UI_FLAG_LOW_PROFILE

        // 退出全屏模式下清除该标志位，恢复原有的状态栏
        window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)

        originSystemFlag = null
    }
}

fun Fragment.setImmersiveSystemBar(isImmersive: Boolean) =
    requireActivity().setImmersiveSystemBar(isImmersive)