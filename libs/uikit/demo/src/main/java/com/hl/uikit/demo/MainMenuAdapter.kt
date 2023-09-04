package com.hl.uikit.demo

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.hl.ui.utils.onClick

class MainMenuAdapter(private var menus: List<FirstMenu>) :
    RecyclerView.Adapter<MenuGroupHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MenuGroupHolder {
        val inflater = LayoutInflater.from(parent.context)
        return MenuGroupHolder(inflater.inflate(R.layout.adapter_main_menu, parent, false))
    }

    override fun getItemCount(): Int {
        return menus.size
    }

    override fun onBindViewHolder(holder: MenuGroupHolder, position: Int) {
        val context = holder.itemView.context
        val inflater = LayoutInflater.from(context)
        val menu = menus[position]
        holder.tvName.text = menu.name
        holder.ivIcon.setImageResource(menu.icon)

        val isExpand = menu.isExpand
        holder.hideChildMenus()
        when {
            isExpand -> {
                if (holder.childMenuCount() == 0 && menu.childCount() > 0) {
                    for (childMenu in menu.nextMenus!!) {
                        val childView = inflater.inflate(
                            R.layout.adapter_main_second_menu,
                            holder.menuGroup,
                            false
                        )
                        val childHolder = MenuChildHolder(childView)
                        childView.onClick {
                            childMenu.menuClickListener.invoke(it)
                        }
                        childHolder.tvName.text = childMenu.name
                        holder.addView(childView)
                    }
                }
                holder.showChildMenus()
            }
        }
        holder.itemView.onClick {
            val adapterPosition = holder.adapterPosition
            menus.changePosition(adapterPosition)
            notifyItemChanged(adapterPosition)
        }
    }

    private fun List<FirstMenu>.changePosition(position: Int) {
        this.forEachIndexed { index, firstMenu ->
            if (index == position) {
                firstMenu.isExpand = !firstMenu.isExpand
            }
        }
    }
}

class MenuGroupHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val tvName: TextView = itemView.findViewById(R.id.tvName)
    val ivIcon: ImageView = itemView.findViewById(R.id.ivIcon)
    val menuGroup: LinearLayout = itemView.findViewById(R.id.llMenuGroup)

    fun addView(child: View) {
        menuGroup.addView(child)
    }

    fun childMenuCount(): Int {
        return menuGroup.childCount
    }

    fun hideChildMenus() {
        menuGroup.visibility = View.GONE
        menuGroup.removeAllViews()
    }

    fun showChildMenus() {
        menuGroup.visibility = View.VISIBLE
    }
}

class MenuChildHolder(itemView: View) {
    val tvName: TextView = itemView.findViewById(R.id.tvName)
}

data class FirstMenu(
    var name: String,
    var icon: Int = -1,
    var nextMenus: List<SecondMenu>? = null
) {

    var isExpand = false

    fun hasNext(): Boolean {
        return nextMenus?.isNullOrEmpty() != true
    }

    fun childCount(): Int {
        return nextMenus?.size ?: 0
    }
}

data class SecondMenu(
    var name: String,
    var menuClickListener: (view: View) -> Unit = {}
)