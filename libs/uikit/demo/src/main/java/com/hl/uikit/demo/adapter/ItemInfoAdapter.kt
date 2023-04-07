package com.hl.uikit.demo.adapter

import android.content.Context
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import com.alibaba.android.vlayout.DelegateAdapter
import com.alibaba.android.vlayout.LayoutHelper
import com.alibaba.android.vlayout.layout.GridLayoutHelper
import com.alibaba.android.vlayout.layout.LinearLayoutHelper
import com.hl.uikit.demo.R
import com.hl.uikit.demo.util.dpInt
import com.hl.uikit.onClick


@Suppress("DEPRECATION")
internal class Item1Adapter<T : ItemBean?>(
    val context: Context,
    data: MutableList<T>,val spanCount:Int = 4
) : DelegateAdapter.Adapter<ItemViewHolder1>() {


    val mData = mutableListOf<T?>()

    init {
        mData.addAll(data)
    }

    override fun getItemViewType(position: Int): Int {
        return ItemViewHolder1.TYPE_CODE
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder1 {
        return ItemViewHolder1.create(parent)
    }

    override fun getItemCount(): Int {
        return mData.size
    }

    var onItemClick: (itemData: T?) -> Unit = {}

    fun setOnItemClickListener(onItemClick: (itemData: T?) -> Unit) {
        this.onItemClick = onItemClick
    }


    override fun onCreateLayoutHelper(): GridLayoutHelper {
        val dp10 = 10.dpInt
        val dp19 = 19.dpInt
        return GridLayoutHelper(spanCount, itemCount, dp19,dp19).apply {
            paddingTop = dp19
            paddingBottom = dp19
            paddingLeft = dp10
            paddingRight = dp10
            setAutoExpand(false)
            bgColor=context.resources.getColor(R.color.white)
        }
    }

    override fun onBindViewHolder(holder: ItemViewHolder1, position: Int) {
        val itemData = mData.getOrNull(position)


        if (itemData != null) {
            holder.bind(itemData)
            holder.itemView.onClick {
                onItemClick(itemData)
            }
            holder.itemImg.visibility = View.VISIBLE
            holder.tvName.visibility = View.VISIBLE
        } else {
            holder.itemImg.visibility = View.GONE
            holder.tvName.visibility = View.GONE

        }
    }

}


@Suppress("DEPRECATION")
internal class Item2Adapter<T : ItemBean?>(
    val context: Context,
    data: MutableList<T>, private val spanCount:Int = 3
) : DelegateAdapter.Adapter<ItemViewHolder2>() {


    val mData = mutableListOf<T?>()

    init {
        mData.addAll(data)
    }


    override fun getItemViewType(position: Int): Int {
        return ItemViewHolder1.TYPE_CODE
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder2 {
        return ItemViewHolder2.create(parent)
    }

    override fun getItemCount(): Int {
        return mData.size
    }

    var onItemClick: (itemData: T?) -> Unit = {}

    fun setOnItemClickListener(onItemClick: (itemData: T?) -> Unit) {
        this.onItemClick = onItemClick
    }

    override fun onCreateLayoutHelper(): GridLayoutHelper {
        val dp22 = 22.dpInt
        val dp25 = 25.dpInt
        return GridLayoutHelper(spanCount, itemCount, dp22, 0).apply {
            marginLeft = dp25
            marginRight = dp25
            paddingTop= dp22
            paddingBottom=dp22
            setAutoExpand(false)
            bgColor=context.resources.getColor(R.color.white)
        }
    }

    override fun onBindViewHolder(holder: ItemViewHolder2, position: Int) {
        val itemData = mData.getOrNull(position)


        if (itemData != null) {
            holder.bind(itemData)
            holder.itemView.onClick {
                onItemClick(itemData)
            }
            holder.itemImg.visibility = View.VISIBLE
            holder.tvName.visibility = View.VISIBLE
        } else {
            holder.itemImg.visibility = View.GONE
            holder.tvName.visibility = View.GONE

        }
    }

}

@Suppress("DEPRECATION")
internal class Item3Adapter<T : ItemBean?>(
    val context: Context,
    data: MutableList<T>, private val spanCount:Int = 3
) : DelegateAdapter.Adapter<ItemViewHolder3>() {


    val mData = mutableListOf<T?>()

    init {
        mData.addAll(data)
    }


    override fun getItemViewType(position: Int): Int {
        return ItemViewHolder1.TYPE_CODE
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder3 {
        return ItemViewHolder3.create(parent)
    }

    override fun getItemCount(): Int {
        return mData.size
    }

    var onItemClick: (itemData: T?) -> Unit = {}

    fun setOnItemClickListener(onItemClick: (itemData: T?) -> Unit) {
        this.onItemClick = onItemClick
    }

    val dp12 = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        12f,
        context.resources.displayMetrics
    ).toInt()
    val dp15 = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        15f,
        context.resources.displayMetrics
    ).toInt()


    override fun onCreateLayoutHelper(): GridLayoutHelper {

        return GridLayoutHelper(spanCount, itemCount, dp12, dp12).apply {
            paddingLeft = dp15
            paddingRight=dp15
            paddingTop = dp15
            paddingBottom=dp15
            setAutoExpand(false)
            bgColor=context.resources.getColor(R.color.white)
        }
    }

    override fun onBindViewHolder(holder: ItemViewHolder3, position: Int) {
        val itemData = mData.getOrNull(position)
        if (itemData != null) {
            holder.bind(itemData)
            holder.itemView.onClick {
                onItemClick(itemData)
            }
            holder.itemImg.visibility = View.VISIBLE
            holder.tvName.visibility = View.VISIBLE
        } else {
            holder.itemImg.visibility = View.GONE
            holder.tvName.visibility = View.GONE

        }
    }

}

class ItemTitleAdapter(private val title: String?) :
    DelegateAdapter.Adapter<ItemTitleViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemTitleViewHolder {

        return ItemTitleViewHolder.create(parent)
    }

    override fun onBindViewHolder(viewHolder: ItemTitleViewHolder, position: Int) {
        viewHolder.bind(title)
        //横屏情况处理
    }


    override fun getItemCount(): Int {
        return 1
    }

    override fun onCreateLayoutHelper(): LayoutHelper {
        return LinearLayoutHelper()
    }


}

/**
 * dp转px
 * @param dp
 * @return
 */
fun Dp2Px(context: Context, dp: Float): Int {
    return (dp * context.resources.displayMetrics.density + 0.5f).toInt()
}
