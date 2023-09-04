package com.hl.uikit.actionsheet

import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.FrameLayout
import com.hl.uikit.BasicDialogFragment
import com.hl.uikit.R
import com.hl.uikit.maxheight.UIKitMaxHeightFrameLayout

open class ActionSheetDialogFragment : BasicDialogFragment() {

    private var mContentView: View? = null
    private var mContentViewId: Int? = null
    private var actionGroup: UIKitMaxHeightFrameLayout? = null

    open var sheetTheme: Int = R.style.UiKit_ActionSheetDialogTheme

    open var rootLayoutId: Int = R.layout.uikit_action_sheet_dialog_fragment

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dialog?.window?.attributes?.windowAnimations = sheetTheme
        return inflater.inflate(rootLayoutId, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        layoutBottom()
    }

    protected fun setContentView(layoutView: View) {
        mContentViewId = null
        mContentView = layoutView
        actionGroup = requireView().findViewById(R.id.actionGroup)
        actionGroup?.let {
            val lp = FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            lp.gravity = Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL
            it.addView(layoutView, 0, lp)
        }
    }

    protected fun setContentView(layoutRes: Int) {
        mContentViewId = layoutRes
        mContentView = null
        if (actionGroup != null) {
            val view =
                LayoutInflater.from(actionGroup!!.context).inflate(layoutRes, actionGroup, false)
            setContentView(view)
        }
    }

    override fun getTheme(): Int {
        return sheetTheme
    }

    private fun layoutBottom() {
        val params = dialog?.window?.attributes
        params?.gravity = Gravity.BOTTOM
        params?.width = WindowManager.LayoutParams.MATCH_PARENT
        dialog?.window?.attributes = params
    }
}