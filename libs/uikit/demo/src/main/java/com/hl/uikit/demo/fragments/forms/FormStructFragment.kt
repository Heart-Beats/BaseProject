package com.hl.uikit.demo.fragments.forms

import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_form_struct.*
import com.hl.uikit.actionsheet.ArrayListSheetDialogFragment
import com.hl.uikit.demo.R
import com.hl.uikit.demo.fragments.BaseFragment
import com.hl.uikit.form.UIKitFormItemView
import com.hl.ui.utils.onClick

class FormStructFragment : BaseFragment() {
    override val layout: Int
        get() = R.layout.activity_form_struct

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val selectedItems = listOf("测试数据1", "测试数据2", "测试数据3", "测试数据4")

        var selectedItemText = ""

        selectItem.onClick {
            ArrayListSheetDialogFragment<String>().apply {
                this.data = selectedItems
                this.itemSelectedPosition = selectedItems.indexOf(selectedItemText)

                this.itemClickListener = { dialog, position ->
                    dialog.dismiss()
                    selectedItemText = selectedItems[position]
                    this@FormStructFragment.selectItem.setText(selectedItemText)
                    this@FormStructFragment.selectItem.setTextColor(Color.parseColor("#333333"))
                }

                this.addNegativeButton()
            }.show(childFragmentManager, "")
        }

        val selectableItemMap = hashMapOf(selectableItem1 to true, selectableItem2 to false)

        selectableItemMap.keys.onEach { formItemText ->
            formItemText.onClick { clickView ->
                val clickItem = clickView as UIKitFormItemView
                selectableItemMap.keys.onEach {
                    selectableItemMap[it] = clickItem == it

                    if (selectableItemMap[it] == true) {

                        val drawable = ContextCompat.getDrawable(requireContext(), R.drawable.icon_form_radio_checked)
                        it.setRightIcon(drawable)
                    } else {
                        it.setRightIcon(null)
                    }
                }
            }
        }

    }
}