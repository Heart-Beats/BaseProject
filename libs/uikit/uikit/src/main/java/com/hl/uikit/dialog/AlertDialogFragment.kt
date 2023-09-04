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
import com.hl.uikit.BasicDialogFragment
import com.hl.uikit.R
import com.hl.uikit.databinding.UikitAlertDialogFragmentBinding
import com.hl.uikit.utils.onClick
import com.hl.viewbinding.inflate

open class AlertDialogFragment : BasicDialogFragment() {

    private val viewBind by inflate<UikitAlertDialogFragmentBinding>()

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

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return viewBind.root
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
        viewBind.btnTopRightClose.onClick {
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

        if (!isRootViewCreated()) return

        if (message.isEmpty()) {
            viewBind.tvMessage.visibility = View.GONE
        } else {
            viewBind.tvMessage.visibility = View.VISIBLE
            viewBind.tvMessage.text = message
        }
    }

    fun setMessageColor(color: Int) {
        mMessageColor = color

        if (!isRootViewCreated()) return
        viewBind.tvMessage.setTextColor(color)
    }

    fun setTitle(title: CharSequence) {
        mTitle = title

        if (!isRootViewCreated()) return

        if (title.isEmpty()) {
            viewBind.tvTitle.visibility = View.GONE
        } else {
            viewBind.tvTitle.visibility = View.VISIBLE
            viewBind.tvTitle.text = title
        }
    }

    fun setCustomView(view: View) {
        mCustomView = view
        mCustomViewId = null

        if (!isRootViewCreated()) return

        val lp = FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        lp.gravity = Gravity.CENTER_HORIZONTAL
        viewBind.customLayout.addView(view, 0, lp)
    }

    fun setCustomView(resId: Int) {
        mCustomViewId = resId
        mCustomView = null

        if (!isRootViewCreated()) return

        viewBind.customLayout.let {
            val view = LayoutInflater.from(it.context).inflate(resId, it, false)
            val lp = FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            lp.gravity = Gravity.CENTER_HORIZONTAL
            viewBind.customLayout.addView(view, 0, lp)
        }
    }

    private fun getCustomView(): View? {
        return viewBind.customLayout.getChildAt(0)
    }

    fun setPositiveButton(text: CharSequence, listener: (dialog: DialogFragment, which: Int) -> Unit) {
        mPositiveButtonText = text
        mPositiveButtonListener = listener

        if (!isRootViewCreated()) return

        viewBind.btnConfirm.text = text
        viewBind.btnConfirm.onClick {
            listener(this, DialogInterface.BUTTON_POSITIVE)
        }
        updateButtons()
    }

    fun setNegativeButton(text: CharSequence, listener: (dialog: DialogFragment, which: Int) -> Unit) {
        mNegativeButtonText = text
        mNegativeButtonListener = listener

        if (!isRootViewCreated()) return

        viewBind.btnClose.text = text
        viewBind.btnClose.onClick {
            listener.invoke(this, DialogInterface.BUTTON_POSITIVE)
        }
        updateButtons()
    }

    protected fun setPositiveButtonText(text: CharSequence) {
        mPositiveButtonText = text

        if (!isRootViewCreated()) return

        viewBind.btnConfirm.text = text
    }

    protected fun setPositiveButtonTextColor(colors: ColorStateList) {
        if (!isRootViewCreated()) return
        viewBind.btnConfirm.setTextColor(colors)
    }

    protected fun setPositiveButtonTextColor(color: Int) {
        if (!isRootViewCreated()) return
        viewBind.btnConfirm.setTextColor(color)
    }

    protected fun setPositiveButtonEnabled(isEnabled: Boolean) {
        if (!isRootViewCreated()) return
        viewBind.btnConfirm.isEnabled = isEnabled
    }

    protected fun setPositiveButtonTextSize(textSize: Int) {
        if (!isRootViewCreated()) return
        viewBind.btnConfirm.textSize = textSize.toFloat()
    }

    fun setCloseButtonVisibility(visibility: Int) {
        mCloseButtonVisibility = visibility
        if (!isRootViewCreated()) return
        viewBind.btnTopRightClose.visibility = visibility
    }

    private fun updateButtons() {
        val positiveShown = if (mPositiveButtonText.isNullOrEmpty()) {
            viewBind.btnConfirm.visibility = View.GONE
            false
        } else {
            viewBind.btnConfirm.visibility = View.VISIBLE
            true
        }
        val negativeShown = if (mNegativeButtonText.isNullOrEmpty()) {
            viewBind.btnClose.visibility = View.GONE
            false
        } else {
            viewBind.btnClose.visibility = View.VISIBLE
            true
        }
        if (positiveShown || negativeShown) {
            viewBind.lineButtons.visibility = View.VISIBLE
        } else {
            viewBind.lineButtons.visibility = View.GONE
        }
        if (!positiveShown || !negativeShown) {
            viewBind.btnDivider.visibility = View.GONE
        } else {
            viewBind.btnDivider.visibility = View.VISIBLE
        }

    }
}