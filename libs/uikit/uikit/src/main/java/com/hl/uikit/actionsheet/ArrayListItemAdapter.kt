package com.hl.uikit.actionsheet

import android.text.Spanned
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.hl.uikit.R
import com.hl.uikit.utils.onClick

class ArrayListItemAdapter<T> :
    RecyclerView.Adapter<ArrayListItemAdapter.ViewHolder>() {
    private val mData: MutableList<T?> = mutableListOf()
    var itemClickListener: (position: Int, data: T?) -> Unit = { _, _ -> }

    var itemDisplayConverter: (data: T) -> CharSequence = { data -> if (data is Spanned) data else data.toString() }

    var itemSelectedPosition: Int? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val context = parent.context
        val itemView =
            LayoutInflater.from(context).inflate(R.layout.uikit_bottom_dialog_item, parent, false)
        return ViewHolder(itemView)
    }

    fun submitList(list: List<T>) {
        mData.clear()
        mData.addAll(list)
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val displayName = getItemDisplay(position)
        holder.tvType.text = displayName
        holder.tvType.isSelected = position == itemSelectedPosition
        try {
            holder.itemView.onClick {
                itemClickListener(position, getItem(holder.adapterPosition))
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun getItem(position: Int): T? {
        return mData.getOrNull(position)
    }

    private fun getItemDisplay(position: Int): CharSequence {
        val bean = mData.getOrNull(position) ?: return ""
        return itemDisplayConverter(bean)
    }

    override fun getItemCount(): Int {
        return mData.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvType: TextView = itemView.findViewById(R.id.item_name)
    }

}