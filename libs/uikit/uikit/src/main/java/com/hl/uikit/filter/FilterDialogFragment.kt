package com.hl.uikit.filter

import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import kotlinx.android.synthetic.main.uikit_bottom_base_dialog_fragment.*
import com.hl.uikit.R
import com.hl.uikit.flowlayout.FlowLayout
import com.hl.uikit.flowlayout.TagAdapter
import com.hl.uikit.flowlayout.TagFlowLayout
import com.hl.uikit.dialog.BottomDialogFragment
import com.hl.uikit.flowlayout.FilterTagAdapter


/**
 * 暂时不用 需要使用时再来做具体的定制
 * @author chibb
 * @description:
 * @date :2020/5/20 14:32
 */
class FilterDialogFragment : BottomDialogFragment {
    var optionsItems: FilterOptions? = null

    constructor(options: FilterOptions) : super() {
        optionsItems = options
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        addItems()
    }


    private fun setTagClick1(position: Int, flowLayout: FlowLayout) {
        val adapter = (flowLayout as TagFlowLayout).adapter
        var bean = adapter?.getItem(position) as FiltraceKindBean
        val check = ! bean.isCheck
        bean.isCheck = check
        if (check) {
            bean.apply {
                if (filterType == 2) {
                    if (value == null) {
                        setSelect(flowLayout, position)
//                        for (i in 0 until adapter.count) {
//                            (adapter.getItem(i) as FiltraceKindBean).isCheck = false
//                            setTagUnCheckColor(flowLayout, i)
//                        }
                    } else {
                        (adapter.getItem(0) as FiltraceKindBean).isCheck = false
                        setTagUnCheckColor(flowLayout, 0)
                    }

                }
            }
            setTagCheckColor(flowLayout, position)
        } else {
            setTagUnCheckColor(flowLayout, position)

        }

    }

    private fun setSelect(flowLayout: FlowLayout, position: Int) {
        var adapter = (flowLayout as TagFlowLayout).adapter !!
        for (i in 0 until adapter.count) {
            var bean = adapter.getItem(i) as FiltraceKindBean

            if (i == position) {
                bean.isCheck = true

                setTagCheckColor(flowLayout, i)

            } else {
                bean.isCheck = false

                setTagUnCheckColor(flowLayout, i)

            }

        }
    }

    private fun addItems() {
        customLayout?.removeAllViews()
        optionsItems?.apply {
            options?.apply {
                for (option in this) {
                    when (option.optionType) {
                        0, 2 -> {
                            var view = View.inflate(context, R.layout.uikit_view_filter_item, null)
                            var itemTitle = view.findViewById<TextView>(R.id.tv_title_select_item)
                            var flowLayout = view.findViewById<TagFlowLayout>(R.id.id_flowlayout)
                            flowLayout.adapter =
                                    FilterTagAdapter(requireContext(), option.filterBeans) as TagAdapter<Any>
                            if (option.isMultiSelect) {
                                flowLayout.setOnTagClickListener(onTagListener)
                            } else {
                                flowLayout.setOnTagClickListener(object :
                                        TagFlowLayout.OnTagClickListener {
                                    override fun onTagClick(
                                            view: View,
                                            position: Int,
                                            parent: FlowLayout
                                    ): Boolean {
                                        setSelect(parent, position)
                                        return true
                                    }
                                })
                            }
                            if(option.titleStr.isNullOrEmpty()){
                                itemTitle.visibility=View.GONE
                            }else{
                                itemTitle.visibility=View.VISIBLE
                                itemTitle.text = option.titleStr
                            }


                            customLayout?.addView(view)
                        }
                    }


                }
            }

        }


    }

    var onTagListener = object : TagFlowLayout.OnTagClickListener {
        override fun onTagClick(view: View, position: Int, parent: FlowLayout): Boolean {
            setTagClick1(position, parent)
            return true
        }
    }


    private fun setTagCheckColor(flowLayout: FlowLayout, position: Int) {
        //暂时不用图标
//                (flowLayout.getChildAt(i).findViewById(R.id.iv) as ImageView).setImageResource(
//                    bean.resourceIdSelect
//                )
        flowLayout.getChildAt(position).findViewById<LinearLayout>(R.id.ll_item)
                .setBackgroundResource(R.drawable.uikit_shape_bg_filtrate_selected)
        (flowLayout.getChildAt(position).findViewById(R.id.tv) as TextView).setTextColor(
                resources?.getColor(
                        R.color.uikit_main_color
                )
        )
    }

    private fun setTagUnCheckColor(flowLayout: FlowLayout, position: Int) {
        //暂时不用图标
//                (flowLayout.getChildAt(i).findViewById(R.id.iv) as ImageView).setImageResource(
//                    bean.resourceId
//                )
        flowLayout.getChildAt(position).findViewById<LinearLayout>(R.id.ll_item)
                .setBackgroundResource(R.drawable.uikit_shape_bg_filtrate_normal)
        (flowLayout.getChildAt(position).findViewById(R.id.tv) as TextView).setTextColor(
                requireContext().resources.getColor(
                        R.color.uikit_txt_color_normal
                )
        )
    }

}