package com.hl.uikit.demo.fragments.colors

import android.os.Bundle
import android.view.View
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.fragment_h_l_d_color.*
import com.hl.uikit.demo.R
import com.hl.uikit.demo.fragments.BaseFragment

class HLDColorFragment : BaseFragment() {
    override val layout: Int
        get() = R.layout.fragment_h_l_d_color

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        imageView.post {
            val width = imageView.width
            val height = width * 2610 / 1035
            Glide.with(view)
                .load(R.drawable.hld_color_template)
                .override(width, height)
                .into(imageView)
        }
    }
}