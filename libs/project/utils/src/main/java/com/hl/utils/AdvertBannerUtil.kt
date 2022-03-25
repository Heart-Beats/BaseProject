package com.hl.utils

import android.content.Context
import android.graphics.Canvas
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
import com.google.gson.annotations.SerializedName
import com.youth.banner.Banner
import com.youth.banner.adapter.BannerAdapter
import com.youth.banner.config.IndicatorConfig
import com.youth.banner.indicator.BaseIndicator
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
        val adImage: ImageView? = itemView.findViewWithTag(itemView.context.getString(R.string.ad_banner_tag))

    }

    override fun onCreateHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(adLayoutId, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindView(holder: ViewHolder, data: AdsDetail, position: Int, size: Int) {
        if (data.localImageRes != null) {
            holder.adImage?.also {
                Glide.with(holder.itemView.context).load(data.localImageRes).into(it)
            } ?: holder.itemView.context.showShortError("请确认轮播背景图已设置Tag（R.string.ad_banner_tag）")
        } else {
            holder.adImage?.also {
                Glide.with(holder.itemView.context).load(data.adsImgUrl).into(it)
            } ?: holder.itemView.context.showShortError("请确认轮播背景图已设置Tag（R.string.ad_banner_tag）")
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

data class AdsDetail(
    /**
     * 广告类型名称
     */
    @SerializedName("adsTypeAlias")
    var adsTypeAlias: String? = null,

    /**
     * 广告标题
     */
    var adsTitle: String? = null,

    /**
     * 广告内容
     */
    var adsContent: String? = null,
    @SerializedName("adsFlowId")

    /**
     * 广告id
     */
    var adsId: String? = null,
    @SerializedName("adsPicUrl")

    /**
     * 广告图片url
     */
    var adsImgUrl: String? = null,
    @SerializedName("creTime")

    /**
     * 创建时间：格式为：yyyy-MM-dd HH:mm:ss
     */
    var createTime: String? = null,
    @SerializedName("webUrl")

    /**
     * 广告HTML的url
     */
    var adsFlowUrl: String? = null,

    /**
     * 广告时长
     */
    var adsDuration: String? = null,

    /**
     * 消息类型 01-首页轮播 02-自定义 03-欢迎界面
     */
    var adsType: String? = null,

    /**
     * 采用本地广告图片，不可与 adsImgUrl 同时使用
     */
    var localImageRes: Int? = null
)

class AdsIndicator(context: Context?) : BaseIndicator(context) {

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val count = config.indicatorSize
        if (count <= 1)
            return

        //间距*（总数-1）+ 默认宽度*(总数-1) + 选择的宽度
        val indicatorWidth =
            (count - 1) * config.indicatorSpace + config.normalWidth * (count - 1) + config.selectedWidth
        // val width = View.resolveSize(indicatorWidth, widthMeasureSpec)
        // val height = View.resolveSize(config.height, heightMeasureSpec)
        val height = config.height
        setMeasuredDimension(indicatorWidth, height)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val count = config.indicatorSize
        if (count <= 1)
            return

        //记录指示器距离最左端的的距离
        var distanceToLeft = 0f
        for (position in 0 until count) {
            var right: Float
            var temp: Int
            when (position) {
                config.currentPosition -> {
                    mPaint.color = config.selectedColor
                    right = distanceToLeft + config.selectedWidth
                    temp = config.selectedWidth + config.indicatorSpace
                }
                else -> {
                    mPaint.color = config.normalColor
                    right = distanceToLeft + config.normalWidth
                    temp = config.normalWidth + config.indicatorSpace
                }
            }

            canvas.drawRoundRect(
                distanceToLeft,
                0f,
                right,
                config.height.toFloat(),
                config.radius.toFloat(),
                config.radius.toFloat(),
                mPaint
            )
            distanceToLeft += temp
        }
    }
}