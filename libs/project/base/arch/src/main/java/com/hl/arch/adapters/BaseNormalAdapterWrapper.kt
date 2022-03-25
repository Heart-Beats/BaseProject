package com.youma.arch.adapters

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

/**
 * Adapter的包装类，用于添加 RecyclerView 的头尾布局
 *
 * @author 张磊
 */
abstract class BaseNormalAdapterWrapper<VH : RecyclerView.ViewHolder>(private val mAdapter: RecyclerView.Adapter<VH>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private enum class ItemType {
        HEADER, FOOTER, NORMAL
    }

    override fun getItemViewType(position: Int): Int {
        return when (position) {
            0 -> {
                ItemType.HEADER.ordinal
            }
            mAdapter.itemCount + 1 -> {
                ItemType.FOOTER.ordinal
            }
            else -> {
                ItemType.NORMAL.ordinal
            }
        }
    }

    override fun getItemCount(): Int {
        return mAdapter.itemCount + 2
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (0 < position && position < mAdapter.itemCount + 1) {
            mAdapter.onBindViewHolder(holder as VH, position - 1)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            ItemType.HEADER.ordinal -> {
                object : RecyclerView.ViewHolder(headerView) {}
            }
            ItemType.FOOTER.ordinal -> {
                object : RecyclerView.ViewHolder(footerView) {}
            }
            else -> {
                mAdapter.onCreateViewHolder(parent, viewType)
            }
        }
    }

    /**
     * 给包装的 adapter 添加头布局
     *
     * @return 添加的头部view
     */
    protected abstract val headerView: View

    /**
     * 给包装的 adapter 添加尾布局
     *
     * @return 添加的尾部view
     */
    protected abstract val footerView: View
}