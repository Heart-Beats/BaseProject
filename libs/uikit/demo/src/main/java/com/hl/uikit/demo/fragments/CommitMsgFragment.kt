package com.hl.uikit.demo.fragments

import android.os.Bundle
import android.view.View
import kotlinx.android.synthetic.main.fragment_commit_msg.*
import com.hl.uikit.demo.R
import com.hl.ui.utils.onClick

/**
 * @Author  张磊  on  2020/10/22 at 17:14
 * Email: 913305160@qq.com
 */
abstract class CommitMsgFragment : BaseFragment() {

    protected enum class CommitMsgType {
        COMMIT_SUCCESS,
        COMMIT_FAIL
    }

    protected abstract val title: CharSequence

    protected abstract val commitMsgType: CommitMsgType

    protected abstract val operateMsg: CharSequence

    protected abstract val operateDesc: CharSequence

    protected abstract val completeClickListener: (View) -> Unit

    override val layout: Int
        get() = R.layout.fragment_commit_msg

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        toolbar.setTitle(title)

        when (commitMsgType) {
            CommitMsgType.COMMIT_SUCCESS ->
                status_image.setImageResource(R.drawable.icon_commit_success)

            CommitMsgType.COMMIT_FAIL ->
                status_image.setImageResource(R.drawable.icon_commit_fail)

        }

        operate_msg.text = operateMsg
        operate_desc.text = operateDesc
        complete.onClick(completeClickListener)
    }


}