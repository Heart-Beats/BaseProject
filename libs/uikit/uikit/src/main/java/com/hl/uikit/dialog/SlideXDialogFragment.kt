package com.hl.uikit.dialog

import android.content.DialogInterface
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.FrameLayout
import androidx.fragment.app.DialogFragment
import com.hl.uikit.BasicDialogFragment
import com.hl.uikit.R
import com.hl.uikit.databinding.UikitSlideXDialogFragmentBinding
import com.hl.uikit.utils.onClick
import com.hl.viewbinding.inflate

open class SlideXDialogFragment : BasicDialogFragment() {

    private val viewBind by inflate<UikitSlideXDialogFragmentBinding>()

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
        return viewBind.root
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
        viewBind.btnCancel.onClick {
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

        if (!isRootViewCreated()) return

        val lp = FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        viewBind.customLayout.addView(view, 0, lp)
    }

    fun setCustomView(resId: Int) {
        mCustomViewId = resId
        mCustomView = null

        if (!isRootViewCreated()) return

        viewBind.customLayout.let {
            val view = LayoutInflater.from(it.context).inflate(resId, it, false)
            it.addView(view, 0)
        }
    }
    fun setTitle(title: CharSequence) {
        mTitle = title

        if (!isRootViewCreated()) return

        if (title.isEmpty()) {
            viewBind.tvTitle.visibility = View.INVISIBLE
        } else {
            viewBind.tvTitle.visibility = View.VISIBLE
            viewBind.tvTitle.text = title
        }
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
    }
}