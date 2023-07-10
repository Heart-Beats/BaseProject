package com.hl.uikit.adapter

import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.hl.uikit.R
import com.hl.uikit.utils.onClick


/**
 * @author chibb
 * @description:
 * @date :2020/5/13 15:49
 */

@Suppress("DEPRECATION")
class MenuItemAdapter(private val mData: ArrayList<CharSequence>) : RecyclerView.Adapter<MenuItemAdapter.ViewHolder>() {
	var itemClickListener: (position: Int) -> Unit = {}

    var itemTextColor: Int = -1

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
		val context = parent.context
		val itemView = LayoutInflater.from(context).inflate(R.layout.uikit_bottom_dialog_item, parent, false)
		return ViewHolder(itemView)
	}

	@RequiresApi(Build.VERSION_CODES.M)
	override fun onBindViewHolder(holder: ViewHolder, position: Int) {
		val context = holder.itemView.context
		val bean = mData[position]
		holder.tvType.text = bean
		if (itemTextColor == -1) {
			itemTextColor = context.getColor(R.color.uikit_bottom_text_color)
		}


		holder.tvType.setTextColor(itemTextColor)

		try {

			holder.itemView.onClick {
				itemClickListener(position)
			}

		} catch (e: Exception) {
			e.printStackTrace()
		}

	}

	override fun getItemCount(): Int {
		return mData.size
	}

	class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
		val tvType: TextView = itemView.findViewById(R.id.item_name)


	}

}