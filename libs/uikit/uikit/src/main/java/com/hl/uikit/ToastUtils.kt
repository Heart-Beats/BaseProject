package com.hl.uikit

import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.LayoutRes
import com.hl.uikit.form.GravityFlag

object ToastUtils {

    private const val TOAST_DURATION_SHORT: Long = 2000
    private const val TOAST_DURATION_LONG: Long = 3500

    private var toast: Toast? = null
    private lateinit var context: Context

    val isInitialized: Boolean
        get() {
            return ::context.isInitialized
        }

    fun init(ctx: Context) {
        context = ctx
    }


    fun cancel() {
        toast?.cancel()
    }

    fun show(build: ToastBuilder.() -> Unit) {
        val toastBuilder = ToastBuilder().apply(build)

        val layout = toastBuilder.layout
        val iconRes = toastBuilder.iconRes
        val text = toastBuilder.text
        val gravity = toastBuilder.gravity
        val duration = toastBuilder.duration

        val layoutView = LayoutInflater.from(context).inflate(layout, null)
        val toastText = layoutView.findViewWithTag<TextView>("toast_text")
        val toastImageView = layoutView.findViewWithTag<ImageView>("toast_icon")

        toastText?.text = text
        if (iconRes != null && iconRes != 0) {
            toastImageView?.setImageResource(iconRes)
        }

        // toast 去重点击处理
        toast?.cancel()

        toast = Toast(context)
        toast?.setGravity(gravity, 0, 0)
        toast?.duration = duration
        toast?.view = layoutView
        toast?.show()

        // val durationTime = if (duration == Toast.LENGTH_LONG) TOAST_DURATION_LONG else TOAST_DURATION_SHORT
        //
        // layout.postDelayed(
        //     {
        //         onFinished()
        //     }, durationTime
        // )
    }
}

class ToastBuilder {
    /**
     * 布局文件
     *
     * 图标和文字需要设置两个 tag:
     *     1. icon : toast_icon
     *     2. text : toast_text
     */
    @LayoutRes
    var layout: Int = R.layout.uikit_layout_toast

    /**
     * 图标
     */
    var iconRes: Int? = null

    /**
     * 文字
     */
    var text: CharSequence? = null

    /**
     * 位置
     */
    @GravityFlag
    var gravity: Int = Gravity.CENTER

    /**
     * 时长
     */
    var duration: Int = Toast.LENGTH_SHORT
}