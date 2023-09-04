package com.hl.uikit.actionsheet

import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import com.hl.uikit.databinding.UikitAlert2SheetDialogFragmentBinding
import com.hl.uikit.utils.onClick
import com.hl.viewbinding.inflate

/**
 * 带有取消以及确认按钮的底部弹出框
 */
open class Alert2SheetDialogFragment : ActionSheetDialogFragment() {

    private val viewBinding by inflate<UikitAlert2SheetDialogFragmentBinding>()

    private var mCustomViewId: Int? = null
    private var mCustomView: View? = null
    private var mNegativeButtonText: CharSequence? = "取消"

    var negativeButtonText: CharSequence? = null
        set(value) {
            field = value
            mNegativeButtonText = value

            if (!isRootViewCreated()) return

            viewBinding.btnNegative.text = value ?: ""
            val visibility = when {
                value.isNullOrEmpty() -> View.GONE
                else -> View.VISIBLE
            }
            viewBinding.btnNegative.visibility = visibility
        }
        get() {
            return viewBinding.btnNegative.text
        }

    var negativeClickListener: (dialog: ActionSheetDialogFragment) -> Unit = { dialog -> dialog.dismiss() }

    private var mPositiveButtonText: CharSequence? = "确定"

    var positiveButtonText: CharSequence? = null
        set(value) {
            field = value
            mPositiveButtonText = value

            if (!isRootViewCreated()) return

            viewBinding.btnPositive.text = value ?: ""
            val visibility = when {
                value.isNullOrEmpty() -> View.GONE
                else -> View.VISIBLE
            }
            viewBinding.btnPositive.visibility = visibility
        }
        get() {
            return viewBinding.btnPositive.text
        }

    var positiveClickListener: (dialog: ActionSheetDialogFragment) -> Unit = { dialog -> dialog.dismiss() }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setContentView(viewBinding.root)

        negativeButtonText = mNegativeButtonText
        positiveButtonText = mPositiveButtonText
        viewBinding.btnNegative.onClick {
            negativeClickListener(this)
        }
        viewBinding.btnPositive.onClick {
            positiveClickListener(this)
        }
    }


    protected fun setCustomView(layoutView: View) {
        mCustomViewId = null
        mCustomView = layoutView

        if (!isRootViewCreated()) return

        viewBinding.customLayout.let {
            val lp = FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            lp.gravity = Gravity.TOP
            it.addView(layoutView, 0, lp)
        }
    }

    protected fun setCustomView(layoutRes: Int) {
        mCustomViewId = layoutRes
        mCustomView = null
        val customLayout = viewBinding.customLayout
        if (customLayout != null) {
            val view = LayoutInflater.from(customLayout.context).inflate(layoutRes, customLayout, false)
            setCustomView(view)
        }
    }

}