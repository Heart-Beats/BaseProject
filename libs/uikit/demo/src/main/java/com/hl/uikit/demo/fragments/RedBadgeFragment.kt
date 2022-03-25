package com.hl.uikit.demo.fragments

import android.os.Bundle
import android.view.View
import kotlinx.android.synthetic.main.activity_red_badge.*
import com.hl.uikit.demo.R
import com.hl.uikit.onClick

class RedBadgeFragment : BaseFragment() {
    override val layout: Int
        get() = R.layout.activity_red_badge

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        item1?.onClick {
            when (ivBadge?.visibility) {
                View.VISIBLE -> ivBadge?.visibility = View.GONE
                else -> ivBadge?.visibility = View.VISIBLE
            }
        }
    }
}