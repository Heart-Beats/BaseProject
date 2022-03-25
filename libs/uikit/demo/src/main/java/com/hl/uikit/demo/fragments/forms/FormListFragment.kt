package com.hl.uikit.demo.fragments.forms

import android.os.Bundle
import android.view.View
import kotlinx.android.synthetic.main.activity_form_list.*
import com.hl.uikit.demo.R
import com.hl.uikit.demo.fragments.BaseFragment
import com.hl.uikit.onClick

class FormListFragment : BaseFragment() {
    override val layout: Int
        get() = R.layout.activity_form_list


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        pressItem.onClick {
        }
    }
}