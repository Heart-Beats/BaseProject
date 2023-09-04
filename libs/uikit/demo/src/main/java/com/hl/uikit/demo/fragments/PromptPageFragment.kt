package com.hl.uikit.demo.fragments

import android.os.Bundle
import android.view.View
import kotlinx.android.synthetic.main.activity_commit_msg.*
import com.hl.uikit.demo.R
import com.hl.uikit.demo.startFragment
import com.hl.ui.utils.onClick

/**
 * @Author  张磊  on  2020/10/23 at 9:53
 * Email: 913305160@qq.com
 */
class PromptPageFragment : BaseFragment() {


    override val layout: Int
        get() = R.layout.activity_commit_msg


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        toolbar.setNavigationOnClickListener {
            activity?.onBackPressed()
        }

        commit_success.onClick {
            this.startFragment(CommitSuccessFragment::class.java)
        }

        commit_fail.onClick {
            this.startFragment(CommitFailFragment::class.java)
        }
    }
}