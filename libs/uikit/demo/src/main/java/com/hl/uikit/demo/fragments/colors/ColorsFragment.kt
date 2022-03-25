package com.hl.uikit.demo.fragments.colors

import android.os.Bundle
import android.view.View
import kotlinx.android.synthetic.main.activity_colors.*
import com.hl.uikit.demo.R
import com.hl.uikit.demo.fragments.BaseFragment
import com.hl.uikit.demo.startFragment
import com.hl.uikit.onClick

class ColorsFragment : BaseFragment() {
    override val layout: Int
        get() = R.layout.activity_colors

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        itemColor1?.onClick {
            startFragment(HLDColorFragment::class.java)
        }
        itemColor2?.onClick {
            startFragment(EMSColorFragment::class.java)
        }
    }
}