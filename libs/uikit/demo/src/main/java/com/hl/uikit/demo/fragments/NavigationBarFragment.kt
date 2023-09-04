package com.hl.uikit.demo.fragments

import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.fragment_navigation_bar.*
import com.hl.uikit.UIKitToolbar
import com.hl.uikit.demo.R
import com.hl.uikit.demo.util.StatusBarUtil
import com.hl.ui.utils.onClick

/**
 * @Author  张磊  on  2020/10/23 at 10:24
 * Email: 913305160@qq.com
 */
class NavigationBarFragment : BaseFragment() {


    override val layout: Int
        get() = R.layout.fragment_navigation_bar

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        itemSheet1?.onClick {
            toolbar?.updateFromDefault(isWhiteBg = true)
        }
        itemSheet2?.onClick {
            toolbar?.updateFromDefault(isWhiteBg = true, showNavigation = false)
        }
        itemSheet3?.onClick {
            toolbar?.updateFromDefault(isWhiteBg = true, rightText = "文字按钮")
        }
        itemSheet4?.onClick {
            toolbar?.updateFromDefault(isWhiteBg = true, rightIcon = R.drawable.icon_toolbar_search)
        }
        itemSheet5?.onClick {
            toolbar?.updateFromDefault(isWhiteBg = false)
        }
        itemSheet6?.onClick {
            toolbar?.updateFromDefault(isWhiteBg = false, showNavigation = false)
        }
        itemSheet7?.onClick {
            toolbar?.updateFromDefault(isWhiteBg = false, rightText = "文字按钮")
        }
        itemSheet8?.onClick {
            toolbar?.updateFromDefault(isWhiteBg = false, rightIcon = R.drawable.icon_search_white)
        }
        itemSheet9?.onClick {
            toolbar?.updateFromDefault(isWhiteBg = true, rightIcon = R.drawable.icon_toolbar_more)
        }
        itemSheet11?.onClick {
            toolbar?.updateFromDefault(
                isWhiteBg = false,
                rightIcon = R.drawable.icon_toolbar_more_white
            )
        }
    }

    private fun UIKitToolbar.updateFromDefault(
        isWhiteBg: Boolean = false,
        rightText: CharSequence? = null,
        rightIcon: Int? = null,
        rightTextColor: Int? = null,
        backgroundColor: Int? = null,
        navigationIcon: Int? = null,
        showNavigation: Boolean = true
    ) {
        val defNavigationIcon = com.hl.uikit.R.drawable.uikit_icon_arrow_left
        val defNavigationIconWhite = com.hl.uikit.R.drawable.uikit_icon_arrow_left_white
        val defBackgroundColorWhite = Color.WHITE
        val defBackgroundColor = ContextCompat.getColor(context, com.hl.uikitres.R.color.uikit_color_1)
        val defRightTextColor = ContextCompat.getColor(context, com.hl.uikitres.R.color.uikit_color_1)
        val defRightTextColorWhite = Color.WHITE
        if (rightIcon != null) {
            setRightActionIcon(rightIcon)
        } else {
            setRightActionIcon(null)
        }
        setRightActionText(rightText)

        val activity = requireActivity()

        if (isWhiteBg) {
            if (showNavigation) {
                setNavigationIcon(navigationIcon ?: defNavigationIcon)
            } else {
                setNavigationIcon(null)
            }
            setBackgroundColor(backgroundColor ?: defBackgroundColorWhite)
            setTitleTextColor(ContextCompat.getColor(context, com.hl.uikitres.R.color.uikit_color_3))
            setRightActionTextColor(rightTextColor ?: defRightTextColor)

            StatusBarUtil.transparencyBar(activity.window)
            StatusBarUtil.statusBarLightMode(activity.window)
            StatusBarUtil.setStatusBarColor(activity, Color.WHITE)
        } else {
            if (showNavigation) {
                setNavigationIcon(navigationIcon ?: defNavigationIconWhite)
            } else {
                setNavigationIcon(null)
            }
            setBackgroundColor(backgroundColor ?: defBackgroundColor)
            setTitleTextColor(Color.WHITE)
            setRightActionTextColor(rightTextColor ?: defRightTextColorWhite)
            StatusBarUtil.transparencyBar(activity.window)
            StatusBarUtil.statusBarDarkMode(activity.window)
            StatusBarUtil.setStatusBarColor(activity, backgroundColor ?: defBackgroundColor)
        }
    }
}