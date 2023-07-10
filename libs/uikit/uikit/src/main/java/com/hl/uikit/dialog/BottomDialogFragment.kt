package com.hl.uikit.dialog

import android.content.DialogInterface
import android.os.Bundle
import android.view.*
import android.widget.FrameLayout
import androidx.fragment.app.DialogFragment
import kotlinx.android.synthetic.main.uikit_bottom_base_dialog_fragment.*
import kotlinx.android.synthetic.main.uikit_bottom_base_dialog_fragment.frameLayout_title
import kotlinx.android.synthetic.main.uikit_bottom_base_dialog_fragment.tvClose
import kotlinx.android.synthetic.main.uikit_bottom_base_dialog_fragment.tvTitle
import com.hl.uikit.BasicDialogFragment
import com.hl.uikit.R
import com.hl.uikit.utils.onClick

open class BottomDialogFragment : BasicDialogFragment() {

    private var mCustomViewId: Int? = null
    private var mCustomView: View? = null
    private var mNegativeButtonListener: ((dialog: DialogFragment, which: Int) -> Unit)? = null
    private var mNegativeButtonText: CharSequence? = null
    private var mTitle: CharSequence = ""

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        dialog?.window?.attributes?.windowAnimations = theme
        return inflater.inflate(R.layout.uikit_bottom_base_dialog_fragment, container, false)
    }

    override fun getTheme(): Int {
        return R.style.UiKit_BottomDialog
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setTitle(mTitle)

        mNegativeButtonText?.let { negativeButtonText ->
            setNegativeButton(negativeButtonText, mNegativeButtonListener ?: { _, _ -> })
        }
        mCustomViewId?.let {
            setCustomView(it)
        }
        mCustomView?.let {
            setCustomView(it)
        }
        tvClose?.onClick {
            dismiss()
        }
        initLayout()
    }

    private fun initLayout() {
        val params = dialog?.window?.attributes
        params?.gravity = Gravity.BOTTOM
        params?.width = WindowManager.LayoutParams.MATCH_PARENT
        dialog?.window?.attributes = params
    }


    fun setTitle(title: CharSequence) {
        mTitle = title
        if (title.isEmpty()) {
            frameLayout_title?.visibility = View.GONE
        } else {
            frameLayout_title?.visibility = View.VISIBLE
            tvTitle?.text = title
        }
    }

    fun setCustomView(view: View) {
        mCustomView = view
        mCustomViewId = null
        val lp = FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        customLayout?.addView(view, lp)
    }

    fun setCustomView(resId: Int) {
        mCustomViewId = resId
        mCustomView = null
        customLayout?.let {
            LayoutInflater.from(it.context).inflate(resId, it, true)
        }
    }


    fun setNegativeButton(text: CharSequence, listener: (dialog: DialogFragment, which: Int) -> Unit) {
        mNegativeButtonText = text
        mNegativeButtonListener = listener
        tvClose?.text = text
        tvClose?.onClick {
            listener.invoke(this, DialogInterface.BUTTON_POSITIVE)
        }
    }

    var onDisMissListener:(()->Unit)?=null



    override fun onDismiss(dialog: DialogInterface) {
        onDisMissListener?.invoke()
        super.onDismiss(dialog)
    }


}