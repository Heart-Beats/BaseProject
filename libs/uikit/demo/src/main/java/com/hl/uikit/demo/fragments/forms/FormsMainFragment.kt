package com.hl.uikit.demo.fragments.forms

import android.os.Bundle
import android.view.View
import kotlinx.android.synthetic.main.fragment_forms.*
import com.hl.uikit.demo.R
import com.hl.uikit.demo.fragments.BaseFragment
import com.hl.uikit.demo.startFragment
import com.hl.uikit.onClick

class FormsMainFragment:BaseFragment() {
    override val layout: Int
        get() = R.layout.fragment_forms

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        item1?.onClick {
            startFragment(FormStructFragment::class.java)
        }
        item2?.onClick {
            startFragment(FormRegisterFragment::class.java)
        }
        item3?.onClick {
            startFragment(FormTextInputFragment::class.java)
        }
        item4?.onClick {
            startFragment(FormToggleButtonFragment::class.java)
        }
        item5?.onClick {
            startFragment(FormImageFragment::class.java)
        }
        item6?.onClick {
            startFragment(FormStepperFragment::class.java)
        }
    }
}