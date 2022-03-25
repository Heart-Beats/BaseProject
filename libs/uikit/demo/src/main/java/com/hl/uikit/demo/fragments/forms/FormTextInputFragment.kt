package com.hl.uikit.demo.fragments.forms

import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.core.widget.addTextChangedListener
import kotlinx.android.synthetic.main.activity_form_text_input.*
import com.hl.uikit.demo.R
import com.hl.uikit.demo.fragments.BaseFragment
import com.hl.uikit.onClick

class FormTextInputFragment : BaseFragment() {
    override val layout: Int
        get() = R.layout.activity_form_text_input

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        etInput?.addTextChangedListener {
            tvCount?.text = "${it?.toString()?.length ?: 0}/200"
        }
        frameLayout?.onClick {
            etInput?.isFocusable = true
            etInput?.isFocusableInTouchMode = true
            etInput?.requestFocus()
            val manager = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            manager.showSoftInput(etInput, 0)
        }
    }
}