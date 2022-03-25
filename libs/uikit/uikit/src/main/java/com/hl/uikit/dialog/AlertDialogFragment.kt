package com.hl.uikit.dialog

import android.content.DialogInterface
import android.content.res.ColorStateList
import android.graphics.Point
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.fragment.app.DialogFragment
import kotlinx.android.synthetic.main.uikit_alert_dialog_fragment.*
import com.hl.uikit.BasicDialogFragment
import com.hl.uikit.R
import com.hl.uikit.onClick

open class AlertDialogFragment : BasicDialogFragment() {

    private var mMessageColor: Int? = null
    private var mCloseButtonVisibility: Int = View.GONE
    private var mCustomViewId: Int? = null
    private var mCustomView: View? = null
    private var mPositiveButtonListener: ((dialog: DialogFragment, which: Int) -> Unit)? = null
    private var mPositiveButtonText: CharSequence? = null
    private var mNegativeButtonListener: ((dialog: DialogFragment, which: Int) -> Unit)? = null
    private var mNegativeButtonText: CharSequence? = null
    private var mTitle: CharSequence = ""
    private var mMessage: CharSequence = ""

    var customViewInitListener: (View?) -> Unit = {}

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.uikit_alert_dialog_fragment, container, false)
    }

    override fun getTheme(): Int {
        return R.style.UiKit_AlertDialog
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setTitle(mTitle)
        setMessage(mMessage)
        val color = mMessageColor
        if (color != null) {
            setMessageColor(color)
        }
        isCancelable = false
        setCloseButtonVisibility(mCloseButtonVisibility)
        btnTopRightClose?.onClick {
            dismiss()
        }
        mPositiveButtonText?.let { positiveButtonText ->
            setPositiveButton(positiveButtonText, mPositiveButtonListener ?: { _, _ -> })
        }
        mNegativeButtonText?.let { negativeButtonText ->
            setNegativeButton(negativeButtonText, mNegativeButtonListener ?: { _, _ -> })
        }
        mCustomViewId?.let {
            setCustomView(it)
        }
        mCustomView?.let {
            setCustomView(it)
        }

        customViewInitListener(getCustomView())
    }

    override fun onResume() {
        super.onResume()
        initLayout()
    }

    private fun initLayout() {
        val params = dialog?.window?.attributes
        val display = dialog?.window?.windowManager?.defaultDisplay
        val point = Point()
        display?.getSize(point)
        params?.width = (0.72 * point.x).toInt()
        dialog?.window?.attributes = params
    }

    fun setMessage(message: CharSequence) {
        mMessage = message
        if (message.isEmpty()) {
            tvMessage?.visibility = View.GONE
        } else {
            tvMessage?.visibility = View.VISIBLE
            tvMessage?.text = message
        }
    }

    fun setMessageColor(color: Int) {
        mMessageColor = color
        tvMessage?.setTextColor(color)
    }

    fun setTitle(title: CharSequence) {
        mTitle = title
        if (title.isEmpty()) {
            tvTitle?.visibility = View.GONE
        } else {
            tvTitle?.visibility = View.VISIBLE
            tvTitle?.text = title
        }
    }

    fun setCustomView(view: View) {
        mCustomView = view
        mCustomViewId = null
        val lp = FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        lp.gravity = Gravity.CENTER_HORIZONTAL
        customLayout?.addView(view, 0, lp)
    }

    fun setCustomView(resId: Int) {
        mCustomViewId = resId
        mCustomView = null
        customLayout?.let {
            val view = LayoutInflater.from(it.context).inflate(resId, it, false)
            val lp = FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            lp.gravity = Gravity.CENTER_HORIZONTAL
            customLayout?.addView(view, 0, lp)
        }
    }

    private fun getCustomView(): View? {
        return customLayout?.getChildAt(0)
    }

    fun setPositiveButton(text: CharSequence, listener: (dialog: DialogFragment, which: Int) -> Unit) {
        mPositiveButtonText = text
        mPositiveButtonListener = listener
        btnConfirm?.text = text
        btnConfirm?.onClick {
            listener(this, DialogInterface.BUTTON_POSITIVE)
        }
        updateButtons()
    }

    fun setNegativeButton(text: CharSequence, listener: (dialog: DialogFragment, which: Int) -> Unit) {
        mNegativeButtonText = text
        mNegativeButtonListener = listener
        btnClose?.text = text
        btnClose?.onClick {
            listener.invoke(this, DialogInterface.BUTTON_POSITIVE)
        }
        updateButtons()
    }

    protected fun setPositiveButtonText(text: CharSequence) {
        mPositiveButtonText = text
        btnConfirm?.text = text
    }

    protected fun setPositiveButtonTextColor(colors: ColorStateList) {
        btnConfirm?.setTextColor(colors)
    }

    protected fun setPositiveButtonTextColor(color: Int) {
        btnConfirm?.setTextColor(color)
    }

    protected fun setPositiveButtonEnabled(isEnabled: Boolean) {
        btnConfirm?.isEnabled = isEnabled
    }

    protected fun setPositiveButtonTextSize(textSize: Int) {
        btnConfirm?.textSize = textSize.toFloat()
    }

    fun setCloseButtonVisibility(visibility: Int) {
        mCloseButtonVisibility = visibility
        btnTopRightClose?.visibility = visibility
    }

    private fun updateButtons() {
        val positiveShown = if (mPositiveButtonText.isNullOrEmpty()) {
            btnConfirm?.visibility = View.GONE
            false
        } else {
            btnConfirm?.visibility = View.VISIBLE
            true
        }
        val negativeShown = if (mNegativeButtonText.isNullOrEmpty()) {
            btnClose?.visibility = View.GONE
            false
        } else {
            btnClose?.visibility = View.VISIBLE
            true
        }
        if (positiveShown || negativeShown) {
            lineButtons?.visibility = View.VISIBLE
        } else {
            lineButtons?.visibility = View.GONE
        }
        if (!positiveShown || !negativeShown) {
            btnDivider?.visibility = View.GONE
        } else {
            btnDivider?.visibility = View.VISIBLE
        }

    }
}