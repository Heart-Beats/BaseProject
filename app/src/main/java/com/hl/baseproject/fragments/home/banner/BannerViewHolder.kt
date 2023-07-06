package com.hl.baseproject.fragments.home.banner

import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.RecyclerView
import com.hl.utils.getColorByRes
import com.hl.baseproject.R
import com.hl.baseproject.repository.network.bean.BannerData
import com.hl.imageload.GlideUtil

/**
 * @author  张磊  on  2022/09/01 at 21:25
 * Email: 913305160@qq.com
 */

abstract class BannerViewHolder(parent: ViewGroup, @LayoutRes itemLayoutId: Int) :
	RecyclerView.ViewHolder(LayoutInflater.from(parent.context).inflate(itemLayoutId, parent, false)) {

	/**
	 * 绑定刷新视图
	 */
	abstract fun onBindView(holder: BannerViewHolder, data: BannerData)

	fun <V : View> getView(viewId: Int): V? {
		return itemView.findViewById(viewId)
	}
}


class BannerNormalViewHolder(parent: ViewGroup, itemLayoutId: Int) : BannerViewHolder(parent, itemLayoutId) {
	private val placeholderColorDrawable = ColorDrawable(parent.context.getColorByRes(com.hl.res.R.color._FFEFF2F7))

	override fun onBindView(holder: BannerViewHolder, data: BannerData) {
		getView<ImageView>(R.id.banner_image)?.apply {
			GlideUtil.load(context, data.imagePath, this, placeholderColorDrawable)
		}
	}
}