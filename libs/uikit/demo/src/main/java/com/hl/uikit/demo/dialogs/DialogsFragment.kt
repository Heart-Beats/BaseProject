package com.hl.uikit.demo.dialogs

import android.os.Bundle
import android.view.View
import kotlinx.android.synthetic.main.activity_dialogs.*
import com.hl.uikit.demo.R
import com.hl.uikit.demo.fragments.BaseFragment
import com.hl.uikit.dialog.AlertDialogFragment
import com.hl.uikit.dialog.InputDialogFragment
import com.hl.uikit.onClick
import com.hl.uikit.toast

class DialogsFragment : BaseFragment() {
    override val layout: Int
        get() = R.layout.activity_dialogs

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        itemDialog1?.onClick {
            AlertDialogFragment()
                .apply {
                    setTitle("标题文字")
                    setMessage("弹窗内容，告知当前状态、信息和解决方法，描述文字尽量控制在三行内")
                    onDismissListener = { dialog ->
                    }
                    onCancelListener = { dialog ->
                    }
                    setPositiveButton("按钮") { dialog, _ ->
                        dialog.dismiss()
                    }
                }
                .show(childFragmentManager, "dialog")
        }
        itemDialog2?.onClick {
            AlertDialogFragment()
                .apply {
                    setTitle("弹窗内容，告知当前状态、信息和解决方法，描述文字尽量控制在三行内")
                    setPositiveButton("按钮") { dialog, _ ->
                        dialog.dismiss()
                    }
                }
                .show(childFragmentManager, "dialog")
        }
        itemDialog3?.onClick {
            AlertDialogFragment()
                .apply {
                    setTitle("弹窗内容，告知当前状态、信息和解决方法，描述文字尽量控制在三行内")
                    setNegativeButton("取消") { dialog, _ ->
                        dialog.dismiss()
                    }
                    setPositiveButton("确定") { dialog, _ ->
                        dialog.dismiss()
                    }
                }
                .show(childFragmentManager, "dialog")
        }
        itemDialog4?.onClick {
            InputDialogFragment()
                .apply {
                    setTitle("标题文字")
                    inputHint = "请输入内容"
                    inputText = "详细信息"
                    hintText = "这是一句提示信息"
                    inputChangedListener = { dialog, editable ->
                        toast(editable.toString())
                    }
                    setPositiveButton("按钮二") { dialog, _ ->
                        toast(inputText.toString())
                        dialog.dismiss()
                    }
                    setNegativeButton("按钮一") { dialog, _ ->
                        dialog.dismiss()
                    }
                }
                .show(childFragmentManager, "dialog")
        }
    }
}