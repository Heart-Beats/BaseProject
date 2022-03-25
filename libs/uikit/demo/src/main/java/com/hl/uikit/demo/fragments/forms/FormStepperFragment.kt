package com.hl.uikit.demo.fragments.forms

import android.os.Bundle
import android.view.View
import kotlinx.android.synthetic.main.activity_form_stepper.*
import com.hl.uikit.demo.R
import com.hl.uikit.demo.fragments.BaseFragment

class FormStepperFragment : BaseFragment() {
    override val layout: Int
        get() = R.layout.activity_form_stepper

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        item1?.setIntegerStepClickListener(0, 15)
        item2?.setDoubleOperatorClickListener(0.00, 100.00, "10.0")
        item3?.setStepperEnabled(false)
    }
}