package com.hl.utils

import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.hl.utils.banner.AdsDetail
import com.hl.utils.banner.AdsIndicator
import com.youth.banner.Banner
import com.youth.banner.adapter.BannerAdapter
import com.youth.banner.config.IndicatorConfig
import com.youth.banner.indicator.Indicator
import com.youth.banner.listener.OnBannerListener

/**
 * @Author  张磊  on  2020/09/02 at 10:49
 * Email: 913305160@qq.com
 */

typealias OnItemClickListener<T> = (itemData: T, position: Int) -> Unit

fun Banner<AdsDetail, AdAdapter>.initAdvertBanner(
    localFragment: Fragment, @LayoutRes adLayoutId: Int,
    bannerIndicator: Indicator? = null,
    onBannerClickListener: OnItemClickListener<AdsDetail>? = null,
    onBannerPageChangeListener: com.youth.banner.listener.OnPageChangeListener? = null
) {

    val adAdapter = AdAdapter(listOf(), adLayoutId)

    val onItemClickListener = onBannerClickListener ?: { data: AdsDetail, position: Int ->
        if (!data.adsFlowUrl.isNullOrEmpty()) {
            val url = data.adsFlowUrl ?: ""

        }
    }

    val listener = OnBannerListener<AdsDetail> { data, position -> onItemClickListener(data, position) }

    val banner = this
    val onPageChangeListener = onBannerPageChangeListener ?: object : com.youth.banner.listener.OnPageChangeListener {
        var isDragFlag = false

        override fun onPageScrollStateChanged(state: Int) {
            if (state == ViewPager2.SCROLL_STATE_DRAGGING) {
                isDragFlag = true
            } else if (state == ViewPager2.SCROLL_STATE_IDLE) {
                isDragFlag = false
            }
        }

        override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
        }

        override fun onPageSelected(position: Int) {
            val bannerAdapter = banner.adapter as BannerAdapter<AdsDetail, AdAdapter.ViewHolder>

            val data = bannerAdapter.getData(position)
            val autoDescription = if (!isDragFlag) "自动轮播" else "手动轮播"

            Log.d("AdvertBannerUtil ", "$autoDescription , 广告时长---->${data.adsDuration}")
            banner.setLoopTime(data.adsDuration?.toLong() ?: Long.MAX_VALUE)


        }
    }

    initBanner(adAdapter, localFragment, listener = listener, onPageChangeListener = onPageChangeListener)

    initBannerIndicator(bannerIndicator)
}

private fun Banner<AdsDetail, AdAdapter>.initBanner(
    adAdapter: AdAdapter, owner: LifecycleOwner, listener:
    OnBannerListener<AdsDetail>, onPageChangeListener: com.youth.banner.listener.OnPageChangeListener
) {
    val bannerRadius = 8F.dp

    this.setAdapter(adAdapter)
        .addBannerLifecycleObserver(owner)
        .setLoopTime(5000)
        .setBannerRound(bannerRadius)
        .setOnBannerListener(listener)
        .addOnPageChangeListener(onPageChangeListener)

    // banner.setPageTransformer(DepthPageTransformer())
}

class AdAdapter(adDetailList: List<AdsDetail>, @LayoutRes val adLayoutId: Int) : BannerAdapter<AdsDetail,
        AdAdapter.ViewHolder>(adDetailList) {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val adImage: ImageView? = itemView.findViewWithTag(itemView.context.getString(R.string.hl_utils_ad_banner_tag))

    }

    override fun onCreateHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(adLayoutId, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindView(holder: ViewHolder, data: AdsDetail, position: Int, size: Int) {
        if (data.localImageRes != null) {
            holder.adImage?.also {
                Glide.with(holder.itemView.context).load(data.localImageRes).into(it)
            } ?: error("请确认轮播背景图已设置Tag（R.string.ad_banner_tag）")
        } else {
            holder.adImage?.also {
                Glide.with(holder.itemView.context).load(data.adsImgUrl).into(it)
            } ?: error("请确认轮播背景图已设置Tag（R.string.ad_banner_tag）")
        }
    }
}


fun Banner<AdsDetail, AdAdapter>.initBannerIndicator(indicator: Indicator?) {
    val indicatorHeight = 3.dpInt
    val indicatorNormalWidth = 3.dpInt
    val indicatorSelectWidth = 10.dpInt
    val indicatorMarginTop = 68.dpInt
    val indicatorMarginBottom = 9.dpInt

    this.setIndicator(indicator ?: AdsIndicator(context), true)
        .setIndicatorRadius(0)
        .setIndicatorGravity(IndicatorConfig.Direction.CENTER)
        .setIndicatorNormalColor(Color.parseColor("#E9E9E9"))
        .setIndicatorSelectedColor(Color.parseColor("#5E60C7"))
        .setIndicatorHeight(indicatorHeight)
        .setIndicatorWidth(indicatorNormalWidth, indicatorSelectWidth)
        .setIndicatorSpace(indicatorNormalWidth)
        .setIndicatorMargins(IndicatorConfig.Margins(0, indicatorMarginTop, 0, indicatorMarginBottom))
}