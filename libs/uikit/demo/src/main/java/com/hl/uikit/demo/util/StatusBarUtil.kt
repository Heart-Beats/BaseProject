package com.hl.uikit.demo.util

import android.annotation.TargetApi
import android.app.Activity
import android.graphics.Color
import android.os.Build
import android.view.View
import android.view.Window
import android.view.WindowManager
import androidx.core.view.ViewCompat
import androidx.core.view.updateLayoutParams
import androidx.core.view.updatePadding

object StatusBarUtil {

    /**
     * 根据insetTop值设置view的paddingTop
     */
    fun viewPaddingInsetTop(view: View) {
        ViewCompat.setOnApplyWindowInsetsListener(view) { v, insets ->
            view.updatePadding(top = insets.systemWindowInsetTop)
            insets
        }
        view.requestApplyInsets()
    }

    /**
     * 根据insetTop值设置view的高度
     */
    fun viewHeightInsetTop(view: View) {
        ViewCompat.setOnApplyWindowInsetsListener(view) { v, insets ->
            view.updateLayoutParams {
                height = insets.systemWindowInsetTop
            }
            insets
        }
        view.requestApplyInsets()
    }

    /**
     * 修改状态栏为全透明
     */
    @TargetApi(19)
    fun transparencyBar(window: Window) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor = Color.TRANSPARENT
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            window.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        }
    }

    /**
     * 修改状态栏颜色，支持4.4以上版本
     * @param activity
     * @param colorId
     */
    fun setStatusBarColor(activity: Activity, color: Int) {
        val window = activity.window
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP) {
            window.statusBarColor = Color.parseColor("#4D000000")
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            window.statusBarColor = color
        }
    }

    /**
     * 状态栏亮色模式，设置状态栏黑色文字、图标，
     * 适配4.4以上版本MIUIV、Flyme和6.0以上版本其他Android
     * @param activity
     * @return 1:MIUUI 2:Flyme 3:android6.0
     */
    fun statusBarLightMode(window: Window): Int {
        return when {
            miuiSetStatusBarLightMode(
                window,
                true
            ) -> 1
            flymeSetStatusBarLightMode(
                window,
                true
            ) -> 2
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.M -> {
                window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                3
            }
            else -> 0
        }
    }

    fun statusBarDarkMode(window: Window): Int {
        return when {
            miuiSetStatusBarLightMode(
                window,
                false
            ) -> 1
            flymeSetStatusBarLightMode(
                window,
                false
            ) -> 2
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.M -> {
                window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                3
            }
            else -> 0
        }
//        if (!miuiSetStatusBarLightMode(window, false)) {
//            if (!flymeSetStatusBarLightMode(window, false)) {
//                window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_VISIBLE
//            }
//        }
    }

    /**
     * 已知系统类型时，设置状态栏黑色文字、图标。
     * 适配4.4以上版本MIUIV、Flyme和6.0以上版本其他Android
     * @param activity
     * @param type 1:MIUUI 2:Flyme 3:android6.0
     */
    fun statusBarLightMode(window: Window, type: Int) {
        when (type) {
            1 -> miuiSetStatusBarLightMode(
                window,
                true
            )
            2 -> flymeSetStatusBarLightMode(
                window,
                true
            )
            3 -> {
                val uiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    window.decorView.systemUiVisibility = uiVisibility or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                } else {
                    window.decorView.systemUiVisibility = uiVisibility
                }
            }
        }
    }

    /**
     * 状态栏暗色模式，清除MIUI、flyme或6.0以上版本状态栏黑色文字、图标
     */
    fun statusBarDarkMode(window: Window, type: Int) {
        when (type) {
            1 -> miuiSetStatusBarLightMode(
                window,
                false
            )
            2 -> flymeSetStatusBarLightMode(
                window,
                false
            )
            3 -> window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_VISIBLE
        }
    }


    /**
     * 设置状态栏图标为深色和魅族特定的文字风格
     * 可以用来判断是否为Flyme用户
     * @param window 需要设置的窗口
     * @param dark 是否把状态栏文字及图标颜色设置为深色
     * @return  boolean 成功执行返回true
     */
    private fun flymeSetStatusBarLightMode(window: Window, dark: Boolean): Boolean {
        var result = false
        try {
            StatusbarColorUtils.setStatusBarDarkIcon(window, dark)
            result = true
        } catch (e: Exception) {
        }
        return result
    }

    /**
     * 需要MIUIV6以上
     * @param activity
     * @param dark 是否把状态栏文字及图标颜色设置为深色
     * @return  boolean 成功执行返回true
     */
    private fun miuiSetStatusBarLightMode(window: Window, dark: Boolean): Boolean {
        var result = false
        val clazz = window::class.java
        try {
            var darkModeFlag = 0
            val layoutParams = Class.forName("android.view.MiuiWindowManager\$LayoutParams")
            val field = layoutParams.getField("EXTRA_FLAG_STATUS_BAR_DARK_MODE")
            darkModeFlag = field.getInt(layoutParams)
            val extraFlagField = clazz.getMethod("setExtraFlags", Int::class.javaPrimitiveType, Int::class.javaPrimitiveType)
            result = true
            if (!dark) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    val flag = window.decorView.systemUiVisibility and View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR.inv()
                    window.decorView.systemUiVisibility = flag
                }
            } else {
                if (dark) {
                    extraFlagField.invoke(window, darkModeFlag, darkModeFlag)//状态栏透明且黑色字体
                } else {
                    extraFlagField.invoke(window, 0, darkModeFlag)//清除黑色字体
                }

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    //开发版 7.7.13 及以后版本采用了系统API，旧方法无效但不会报错，所以两个方式都要加上
                    if (dark) {
                        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                    } else {
                        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_VISIBLE
                    }
                }
            }
        } catch (e: Exception) {
        }
        return result
    }
}
