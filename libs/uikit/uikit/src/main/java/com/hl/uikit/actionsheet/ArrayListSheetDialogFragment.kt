package com.hl.uikit.actionsheet

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.hl.uikit.R
import com.hl.uikit.databinding.UikitListSheetDialogFragmentBinding
import com.hl.viewbinding.inflate

/**
 * 内容为 list 可设置取消按钮的底部弹出框
 */
class ArrayListSheetDialogFragment<T> : Alert1SheetDialogFragment() {

    private val viewBinding by inflate<UikitListSheetDialogFragmentBinding>()

    private val mAdapter: ArrayListItemAdapter<T> = ArrayListItemAdapter()

    var data: List<T>? = null
        set(value) {
            field = value
            if (value?.isNotEmpty() == true) {
                mAdapter.submitList(value)
            }
        }
    var itemClickListener: (dialog: ActionSheetDialogFragment, position: Int) -> Unit = { _, _ -> }
        set(value) {
            field = value
            mAdapter.itemClickListener = { position, _ ->
                field.invoke(this, position)
            }
        }
    var itemDisplayConverter: (data: T) -> String = { data -> data.toString() }
        set(value) {
            field = value
            mAdapter.itemDisplayConverter = value
        }
    var itemSelectedPosition: Int? = null
        set(value) {
            field = value
            mAdapter.itemSelectedPosition = value
        }
    override var sheetTheme = R.style.UiKit_ArrayListSheetDialogTheme

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setCustomView(viewBinding.root)

        val recyclerView = viewBinding.recyclerView
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = mAdapter
        recyclerView.scrollToPosition(itemSelectedPosition ?: 0)
    }
}