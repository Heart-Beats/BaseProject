package com.hl.uikit.demo.fragments

import android.os.Bundle
import android.view.View
import kotlinx.android.synthetic.main.fragment_forms.*
import com.hl.uikit.demo.R
import com.hl.ui.utils.onClick
import com.hl.uikit.toast
import com.hl.uikit.toastFailure
import com.hl.uikit.toastSuccess

class ToastFragment:BaseFragment() {
    override val layout: Int
        get() = R.layout.activity_toast

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        item1?.onClick {
           toast("提示信息")
        }
        item2?.onClick {
            toastSuccess("成功文案")
        }
        item3?.onClick {
            toastFailure("失败文案")
        }
    }
}