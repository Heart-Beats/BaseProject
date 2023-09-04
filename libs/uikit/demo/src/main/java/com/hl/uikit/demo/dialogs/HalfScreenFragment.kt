package com.hl.uikit.demo.dialogs

import android.os.Bundle
import android.view.View
import kotlinx.android.synthetic.main.activity_half_screen.*
import com.hl.uikit.actionsheet.ArrayListSheetDialogFragment
import com.hl.uikit.demo.R
import com.hl.uikit.demo.fragments.BaseFragment
import com.hl.ui.utils.onClick

class HalfScreenFragment : BaseFragment() {
    override val layout: Int
        get() = R.layout.activity_half_screen

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        var sheet1SelectedPosition = 1
        itemSheet1?.onClick {
            ArrayListSheetDialogFragment<String>()
                .apply {
                    addNegativeButton()
                    data = listOf("选项一", "选项二", "选项三")
                    itemSelectedPosition = sheet1SelectedPosition
                    itemClickListener = { dialog, position ->
                        sheet1SelectedPosition = position
                        dialog.dismiss()
                    }
                }
                .show(childFragmentManager, "sheet")
        }

        var sheet2SelectedPosition = 0
        itemSheet2?.onClick {
            ArrayListSheetDialogFragment<PayType>()
                .apply {
                    addNegativeButton()
                    data = listOf(
                        PayType("全部"), PayType("消费"), PayType("退款"),
                        PayType("撤销"), PayType("预授权"), PayType("预授权撤销"),
                        PayType("撤销"), PayType("预授权"), PayType("预授权撤销"),
                        PayType("撤销"), PayType("预授权"), PayType("预授权撤销"),
                        PayType("预授权完成")
                    )
                    itemSelectedPosition = sheet2SelectedPosition
                    itemClickListener = { dialog, position ->
                        sheet2SelectedPosition = position
                        dialog.dismiss()
                    }
                    //通过自定义 Dialog 主题修改 Item 选中颜色
//                    sheetTheme = R.style.PayTypeListSheetItemStyle
                    itemDisplayConverter = { data: PayType ->
                        data.name
                    }
                }
                .show(childFragmentManager, "sheet")
        }

        var sheet3SelectedPosition = 0
        itemSheet3?.onClick {
            ArrayListSheetDialogFragment<String>()
                .apply {
                    title = "这里提供一行小标题或注释"
                    addNegativeButton()
                    data = listOf("选项一", "选项二", "选项三")
                    itemSelectedPosition = sheet3SelectedPosition
                    isCancelable = false
                    itemClickListener = { dialog, position ->
                        sheet3SelectedPosition = position
                        dialog.dismiss()
                    }
                }
                .show(childFragmentManager, "sheet")
        }
    }
}

data class PayType(
    val name: String
)