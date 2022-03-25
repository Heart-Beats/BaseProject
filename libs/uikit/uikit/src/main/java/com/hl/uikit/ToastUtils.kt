package com.hl.uikit

import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import kotlinx.android.synthetic.main.uikit_layout_toast.view.textView
import kotlinx.android.synthetic.main.uikit_layout_toast_with_icon.view.*

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

    fun show(text: CharSequence, duration: Int = Toast.LENGTH_SHORT) {
        show(iconRes = null, text = text, duration = duration)
    }

    fun show(
        iconRes: Int? = null,
        textRes: Int,
        duration: Int = Toast.LENGTH_SHORT,
        onFinished: () -> Unit = {}
    ) {
        show(iconRes, context?.getString(textRes) ?: "", duration, onFinished)
    }

    fun show(
        iconRes: Int? = null,
        text: CharSequence,
        duration: Int = Toast.LENGTH_SHORT,
        onFinished: () -> Unit = {}
    ) {
        val inflater: LayoutInflater = LayoutInflater.from(context)
        val layout = inflater.inflate(
            if (iconRes != null && iconRes != 0) {
                R.layout.uikit_layout_toast_with_icon
            } else {
                R.layout.uikit_layout_toast
            }, null
        )
        val textView: TextView = layout.textView
        textView.text = text
        val imageView: ImageView? = layout.imageView
        if (toast != null) {
            toast = null
        }
        if (iconRes != null && iconRes != 0) {
            imageView?.setImageResource(iconRes)
        }
        toast = Toast(context)
        toast?.setGravity(Gravity.CENTER, 0, 0)
        toast?.duration = duration
        toast?.view = layout
        toast?.show()
        layout.postDelayed(
            {
                onFinished()
            }, when (duration) {
                Toast.LENGTH_LONG -> TOAST_DURATION_LONG
                else -> TOAST_DURATION_SHORT
            }
        )
    }
}