package com.hl.uikit.demo.fragments

import android.view.View
import android.widget.Toast
import androidx.core.text.HtmlCompat

/**
 * @Author  张磊  on  2020/10/22 at 19:22
 * Email: 913305160@qq.com
 */
class CommitSuccessFragment : CommitMsgFragment() {

    override val title: CharSequence
        get() = "成功提示页"

    override val commitMsgType: CommitMsgType
        get() = CommitMsgType.COMMIT_SUCCESS

    override val operateMsg: CharSequence
        get() = "操作成功"

    override val operateDesc: CharSequence
        get() = HtmlCompat.fromHtml("内容详情，可根据实际需要安排，如果换行则不超过规定长度，居中展现", 0)

    override val completeClickListener: (View) -> Unit
        get() = {
            Toast.makeText(requireContext(), operateMsg, Toast.LENGTH_SHORT).show()
        }
}