package com.hl.uikit.dialog

import android.content.DialogInterface
import android.os.Bundle
import android.view.*
import android.widget.FrameLayout
import androidx.fragment.app.DialogFragment
import kotlinx.android.synthetic.main.uikit_slide_x_dialog_fragment.*
import com.hl.uikit.BasicDialogFragment
import com.hl.uikit.R
import com.hl.uikit.utils.onClick

open class SlideXDialogFragment : BasicDialogFragment() {
    private var mTitle: CharSequence = ""
    protected var mGravity: Int = Gravity.END
    private var mCustomViewId: Int? = null
    private var mCustomView: View? = null
    private var mPositiveButtonListener: ((dialog: DialogFragment, which: Int) -> Unit)? = null
    private var mPositiveButtonText: CharSequence? = null
    private var mNegativeButtonListener: ((dialog: DialogFragment, which: Int) -> Unit)? = null
    private var mNegativeButtonText: CharSequence? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mGravity = arguments?.getInt("gravity", Gravity.END) ?: Gravity.END
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        dialog?.window?.attributes?.windowAnimations = theme
        return inflater.inflate(R.layout.uikit_slide_x_dialog_fragment, container, false)
    }

    override fun getTheme(): Int {
        return if (mGravity == Gravity.START) {
            R.style.UiKit_SlideXLeftDialog
        } else {
            R.style.UiKit_SlideXRightDialog
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setTitle(mTitle)
        mCustomViewId?.let {
            setCustomView(it)
        }
        mCustomView?.let {
            setCustomView(it)
        }
        mPositiveButtonText?.let { positiveButtonText ->
            setPositiveButton(positiveButtonText, mPositiveButtonListener ?: { _, _ -> })
        }
        mNegativeButtonText?.let { negativeButtonText ->
            setNegativeButton(negativeButtonText, mNegativeButtonListener ?: { _, _ -> })
        }
        btnCancel?.onClick {
            dismiss()
        }
    }

    override fun onResume() {
        super.onResume()
        initLayout()
    }

    private fun initLayout() {
        val params = dialog?.window?.attributes
        params?.gravity = mGravity
        params?.height = WindowManager.LayoutParams.MATCH_PARENT
        dialog?.window?.attributes = params
    }


    fun setCustomView(view: View) {
        mCustomView = view
        mCustomViewId = null
        val lp = FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        customLayout?.addView(view, 0, lp)
    }

    fun setCustomView(resId: Int) {
        mCustomViewId = resId
        mCustomView = null
        customLayout?.let {
            val view = LayoutInflater.from(it.context).inflate(resId, it, false)
            it.addView(view, 0)
        }
    }
    fun setTitle(title: CharSequence) {
        mTitle = title
        if (title.isEmpty()) {
            tvTitle?.visibility = View.INVISIBLE
        } else {
            tvTitle?.visibility = View.VISIBLE
            tvTitle?.text = title
        }
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
    }
}