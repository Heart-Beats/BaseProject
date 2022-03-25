package com.hl.uikit.demo.fragments

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.android.vlayout.DelegateAdapter
import com.alibaba.android.vlayout.VirtualLayoutManager
import kotlinx.android.synthetic.main.activity_grid.*
import com.hl.uikit.demo.R
import com.hl.uikit.demo.adapter.*

class GridFragment : BaseFragment() {

    override val layout: Int
        get() = R.layout.activity_grid

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val layoutManager = VirtualLayoutManager(requireContext())
        recyclerView.layoutManager = layoutManager
        recyclerView.setRecycledViewPool(RecyclerView.RecycledViewPool().apply {
            setMaxRecycledViews(0, 10)
        })
        val adapter = DelegateAdapter(layoutManager)
        adapter.setAdapters(getAdapters())
        recyclerView.adapter = adapter
    }

    val item = ItemBean().apply {
        itemName = "标题文字"
    }

    private fun getAdapters(): List<DelegateAdapter.Adapter<*>> {
        val context = requireContext()
        return listOf(
            ItemTitleAdapter("4列（首页金刚区）"),
            Item1Adapter(
                context,
                mutableListOf(item, item, item, item, item, item, item, item)
            ),
            ItemTitleAdapter("3列（商户中心）"),
            Item2Adapter(context, mutableListOf(item, item, item, item, item))
            ,
            ItemTitleAdapter("3列（展业）"),
            Item3Adapter(context, mutableListOf(item, item, item, item, item))
        )

    }

}