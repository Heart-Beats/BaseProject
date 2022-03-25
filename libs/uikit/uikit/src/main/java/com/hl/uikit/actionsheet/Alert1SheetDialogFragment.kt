package com.hl.uikit.actionsheet

import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import kotlinx.android.synthetic.main.uikit_alert1_sheet_dialog_fragment.*
import com.hl.uikit.R
import com.hl.uikit.onClick

/**
 * 带有取消按钮以及标题的底部弹出框
 */
open class Alert1SheetDialogFragment : ActionSheetDialogFragment() {
    private var mCustomViewId: Int? = null
    private var mCustomView: View? = null
    private var mTitle: CharSequence? = null
    var title: CharSequence? = null
        set(value) {
            field = value
            mTitle = value
            tvTitle?.text = value ?: ""
            val visibility = when {
                value.isNullOrEmpty() -> View.GONE
                else -> View.VISIBLE
            }
            tvTitle?.visibility = visibility
            dividerView?.visibility = visibility
        }
        get() {
            return tvTitle?.text
        }

    private var mNegativeButtonText: CharSequence? = null

    private var negativeClickListener: () -> Unit = {}

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setContentView(R.layout.uikit_alert1_sheet_dialog_fragment)
        title = mTitle
        initBtnNegative(mNegativeButtonText)

    }

    private fun initBtnNegative(text: CharSequence?) {
        btnNegative?.text = text ?: ""
        val visibility = when {
            text.isNullOrEmpty() -> View.GONE
            else -> View.VISIBLE
        }
        btnNegative?.visibility = visibility
        viewBgSpace?.visibility = visibility

        btnNegative?.onClick {
            dismiss()
            negativeClickListener()
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

    fun addNegativeButton(negativeText: CharSequence = "取消", clickListener: () -> Unit = {}) {
        mNegativeButtonText = negativeText
        negativeClickListener = clickListener
    }
}