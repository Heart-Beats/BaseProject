package com.hl.uikit.actionsheet

import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import kotlinx.android.synthetic.main.uikit_alert2_sheet_dialog_fragment.*
import com.hl.uikit.R
import com.hl.uikit.onClick

/**
 * 带有取消以及确认按钮的底部弹出框
 */
open class Alert2SheetDialogFragment : ActionSheetDialogFragment() {
    private var mCustomViewId: Int? = null
    private var mCustomView: View? = null
    private var mNegativeButtonText: CharSequence? = "取消"
    var negativeButtonText: CharSequence? = null
        set(value) {
            field = value
            mNegativeButtonText = value
            btnNegative?.text = value ?: ""
            val visibility = when{
                value.isNullOrEmpty()->View.GONE
                else -> View.VISIBLE
            }
            btnNegative?.visibility = visibility
        }
        get() {
            return btnNegative?.text
        }
    var negativeClickListener: (dialog: ActionSheetDialogFragment) -> Unit =
        { dialog -> dialog.dismiss() }

    private var mPositiveButtonText: CharSequence? = "确定"
    var positiveButtonText: CharSequence? = null
        set(value) {
            field = value
            mPositiveButtonText = value
            btnPositive?.text = value ?: ""
            val visibility = when {
                value.isNullOrEmpty() -> View.GONE
                else -> View.VISIBLE
            }
            btnPositive?.visibility = visibility
        }
        get() {
            return btnPositive?.text
        }
    var positiveClickListener: (dialog: ActionSheetDialogFragment) -> Unit =
        { dialog -> dialog.dismiss() }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setContentView(R.layout.uikit_alert2_sheet_dialog_fragment)
        negativeButtonText = mNegativeButtonText
        positiveButtonText = mPositiveButtonText
        btnNegative?.onClick {
            negativeClickListener(this)
        }
        btnPositive?.onClick {
            positiveClickListener(this)
        }
    }


    protected fun setCustomView(layoutView: View) {
        mCustomViewId = null
        mCustomView = layoutView
        customLayout?.let {
            val lp = FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            lp.gravity = Gravity.TOP
            customLayout.addView(layoutView, 0, lp)
        }
    }

    protected fun setCustomView(layoutRes: Int) {
        mCustomViewId = layoutRes
        mCustomView = null
        if (customLayout != null) {
            val view =
                LayoutInflater.from(customLayout.context).inflate(layoutRes, customLayout, false)
            setCustomView(view)
        }
    }

}