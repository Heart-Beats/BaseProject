package com.hl.uikit.demo.fragments

import android.os.Bundle
import android.view.View
import kotlinx.android.synthetic.main.fragment_search.*
import com.hl.uikit.demo.R
import com.hl.uikit.demo.startFragment
import com.hl.uikit.onClick

class SearchFragment : BaseFragment() {
    override val layout: Int
        get() = R.layout.fragment_search

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        item1.onClick {
            startFragment(SearchBarFragment::class.java)
        }
        item2.onClick {
            startFragment(SearchViewFragment::class.java)
        }
    }
}