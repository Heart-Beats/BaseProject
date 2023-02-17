package com.youma.yky.app.main.main.newhome.banner

import android.view.ViewGroup
import com.hl.baseproject.R
import com.hl.baseproject.repository.network.bean.BannerData
import com.youth.banner.adapter.BannerAdapter

/**
 * @author  张磊  on  2022/09/01 at 20:19
 * Email: 913305160@qq.com
 */
class CommonBannerAdapter(datas: MutableList<BannerData>) : BannerAdapter<BannerData, BannerViewHolder>(datas) {


	override fun onCreateHolder(parent: ViewGroup, viewType: Int): BannerViewHolder {
		return when (viewType) {
			// BannerType.HOME_TOP_FIRST.ordinal -> {
			// 	BannerHomeTopFirstViewHolder(parent, R.layout.item_banner_home_top_first)
			// }
			BannerType.NORMAL.ordinal -> {
				BannerNormalViewHolder(parent, R.layout.item_banner_normal)
			}
			else -> {
				BannerNormalViewHolder(parent, R.layout.item_banner_normal)
			}
		}
	}

	override fun onBindView(holder: BannerViewHolder, data: BannerData?, position: Int, size: Int) {
		data ?: return

		holder.onBindView(holder, data)
	}

	override fun getItemViewType(position: Int): Int {
		return if (mDatas.isNotEmpty()) {
			// 因为有自动轮播首尾切换，所以 BannerAdapter 添加了占位的 item, 调用 getRealData() 才可获取真实的实体数据
			val bannerData = getRealData(position)

			// if (bannerData is HomeTopFirstBannerData) BannerType.HOME_TOP_FIRST.ordinal else BannerType.NORMAL.ordinal
			BannerType.NORMAL.ordinal
		} else {
			super.getItemViewType(position)
		}
	}

	/**
	 *获取当前的数据
	 */
	fun getDatas() = mDatas
}