package com.hl.uikit.demo.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.hl.uikit.demo.R


 class ItemTitleViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    companion object {
        const val TYPE_CODE = 0x80
        fun create(parent: ViewGroup): ItemTitleViewHolder {
            val context = parent.context
            return ItemTitleViewHolder(
                LayoutInflater.from(context)
                    .inflate(R.layout.layout_gride_item_title, parent, false)
            )
        }
    }

    val tvTitle: TextView = itemView.findViewById(R.id.tvTitle)
    fun bind(title: CharSequence?) {
        tvTitle.text = title
    }
}

class ItemBean {
    var itemName: String? = null
    var imgId: Int = 0
}

internal class ItemViewHolder1(view: View) : RecyclerView.ViewHolder(view) {
    companion object {
        const val TYPE_CODE = 0x81
        fun create(parent: ViewGroup): ItemViewHolder1 {
            val context = parent.context
            return ItemViewHolder1(
                LayoutInflater.from(context)
                    .inflate(R.layout.layout_gride_item_1, parent, false)
            )
        }
    }

     val itemImg: ImageView = itemView.findViewById(R.id.img)
     val tvName: TextView = itemView.findViewById(R.id.tvName)

    fun bind(bean: ItemBean?) {
        bean?.apply {
            if (imgId != 0) {
                itemImg.setImageResource(imgId)
            }
            tvName.text = itemName ?: ""
        }

    }
}

internal class ItemViewHolder2(view: View) : RecyclerView.ViewHolder(view) {
    companion object {
        const val TYPE_CODE = 0x81
        fun create(parent: ViewGroup): ItemViewHolder2 {
            val context = parent.context
            return ItemViewHolder2(
                LayoutInflater.from(context)
                    .inflate(R.layout.layout_gride_item_2, parent, false)
            )
        }
    }

    val itemImg: ImageView = itemView.findViewById(R.id.img)
    val tvName: TextView = itemView.findViewById(R.id.tvName)

    fun bind(bean: ItemBean?) {
        bean?.apply {
            if (imgId != 0) {
                itemImg.setImageResource(imgId)
            }
            tvName.text = itemName ?: ""
        }

    }
}

internal class ItemViewHolder3(view: View) : RecyclerView.ViewHolder(view) {
    companion object {
        const val TYPE_CODE = 0x81
        fun create(parent: ViewGroup): ItemViewHolder3 {
            val context = parent.context
            return ItemViewHolder3(
                LayoutInflater.from(context)
                    .inflate(R.layout.layout_gride_item_3, parent, false)
            )
        }
    }

    val itemImg: ImageView = itemView.findViewById(R.id.img)
    val tvName: TextView = itemView.findViewById(R.id.tvName)

    fun bind(bean: ItemBean?) {
        bean?.apply {
            if (imgId != 0) {
                itemImg.setImageResource(imgId)
            }
            tvName.text = itemName ?: ""
        }

    }
}

