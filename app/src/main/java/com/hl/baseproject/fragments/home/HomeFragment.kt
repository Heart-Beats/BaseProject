package com.hl.baseproject.fragments.home

import android.graphics.Color
import android.os.Bundle
import com.hl.arch.mvvm.vm.activityViewModels
import com.hl.arch.utils.getColorByRes
import com.hl.arch.web.navigateToWeb
import com.hl.baseproject.base.BaseFragment
import com.hl.baseproject.databinding.FragmentHomeBinding
import com.hl.baseproject.fragments.home.banner.CommonBannerAdapter
import com.hl.baseproject.repository.network.bean.BannerData
import com.hl.baseproject.viewmodels.HomeViewModel
import com.hl.uikit.UIKitCollapsingToolbarLayout
import com.hl.utils.*
import com.hl.utils.banner.AdsIndicator
import com.youth.banner.Banner
import com.youth.banner.config.IndicatorConfig
import com.youth.banner.indicator.Indicator
import com.youth.banner.listener.OnBannerListener
import com.youth.banner.listener.OnPageChangeListener

/**
 * @author  张磊  on  2023/02/13 at 14:46
 * Email: 913305160@qq.com
 */
class HomeFragment : BaseFragment<FragmentHomeBinding>() {

	private val homeViewModel by activityViewModels<HomeViewModel>()

	private val commonBannerAdapter by lazy { CommonBannerAdapter(mutableListOf()) }

	/**
	 *  标记状态栏是否已折叠
	 */
	private var toolbarIsCollapseFlag = false
		set(value) {
			field = value
			// 更改状态栏字体颜色
			immersionBar.statusBarDarkFont(field).init()
		}

	// 状态栏颜色默认透明
	override fun getStatusBarColor() = Color.TRANSPARENT

	override fun onBackPressed() {
		launchHome()
	}

	override fun onResume() {
		super.onResume()
		setStatusBarImmerseFromView(viewBinding.statusView)
		toolbarIsCollapseFlag = toolbarIsCollapseFlag
	}

	override fun FragmentHomeBinding.onViewCreated(savedInstanceState: Bundle?) {
		this.initTitleLayout()

		homeViewModel.getTopBannerList()
		homeViewModel.topBannerListLiveData.onceLastObserve(viewLifecycleOwner) {
			it ?: return@onceLastObserve

			// 更新轮播图数据, 不要使用 adapter 更新，否则开始位置会不对
			val banner = this.topBanner as Banner<BannerData, CommonBannerAdapter>
			banner.setDatas(it)

			if (it.isEmpty()) {
				banner.removeIndicator()
			}
		}
	}

	private fun FragmentHomeBinding.initTitleLayout() {
		this.collapsingToolbar.addOnScrimsListener(object : UIKitCollapsingToolbarLayout.OnScrimsListener {
			override fun onScrimsStateChange(shown: Boolean) {
				val tintColor = if (shown) getColorByRes(com.hl.res.R.color._FF31415F) else Color.WHITE
				ivHomeMenu.setColor(tintColor)
				homeTitle.setTextColor(tintColor)

				toolbarIsCollapseFlag = shown
			}
		})

		this.updateTitle()
		this.initTopBanner()
	}

	private fun FragmentHomeBinding.updateTitle() {
		GlideUtil.load(requireContext(), getAppIconId(), this.ivLogo)
		this.homeTitle.text = getAppName()
	}

	/**
	 * 根据指定的图片地址修改状态栏以及标题栏的字体样式
	 */
	private fun updateStatusBarAndTitleStyleFromImageUrl(url: String) {
		GlideUtil.loadUrl2Bitmap(requireContext(), url) {
			changeStatusBarStyleFromBitmap(it) { _, bodyTextColor, _, _ ->
				viewBinding.ivHomeMenu.setColor(bodyTextColor)
				// viewBinding.ivLogo.setColor(bodyTextColor)
				viewBinding.homeTitle.setTextColor(bodyTextColor)
			}
		}
	}

	private fun FragmentHomeBinding.initTopBanner() {
		val banner = this.topBanner as Banner<BannerData, CommonBannerAdapter>
		banner.initTopBanner(commonBannerAdapter) { data, _ ->
			val jumpUrl = data.url
			if (!jumpUrl.isNullOrBlank()) {
				navigateToWeb(jumpUrl, isNeedTitle = true)
			}
		}

		banner.addOnPageChangeListener(object : OnPageChangeListener {
			override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
			}

			override fun onPageSelected(position: Int) {
				val url = commonBannerAdapter.getDatas()[position].imagePath ?: return
				updateStatusBarAndTitleStyleFromImageUrl(url)
			}

			override fun onPageScrollStateChanged(state: Int) {
			}
		})
	}

	private fun Banner<BannerData, CommonBannerAdapter>.initTopBanner(
		adAdapter: CommonBannerAdapter, listener:
		OnBannerListener<BannerData>
	) {
		this.also {
			if (it.adapter == null) {
				setAdapter(adAdapter)
			}
		}
			.addBannerLifecycleObserver(viewLifecycleOwner)
			.setLoopTime(3000)
			.setBannerRound(0F)
			.setOnBannerListener(listener)

		this.initBannerIndicator(null)
	}

	private fun Banner<BannerData, CommonBannerAdapter>.initBannerIndicator(indicator: Indicator?) {
		if (this.indicator == null) {
			val indicatorHeight = 2.dpInt
			val indicatorNormalWidth = 10.dpInt
			val indicatorSelectWidth = 10.dpInt

			val indicatorSpace = 10.dpInt

			val indicatorMarginTop = 68.dpInt
			val indicatorMarginBottom = 8.dpInt

			this.setIndicator(indicator ?: AdsIndicator(context), indicator == null)
				.setIndicatorRadius(1.dpInt)
				.setIndicatorGravity(IndicatorConfig.Direction.CENTER)
				.setIndicatorNormalColor(Color.parseColor("#DBE7FF"))
				.setIndicatorSelectedColor(getColorByRes(com.hl.arch.R.color.main_color))
				.setIndicatorHeight(indicatorHeight)
				.setIndicatorWidth(indicatorNormalWidth, indicatorSelectWidth)
				.setIndicatorSpace(indicatorSpace)
				.setIndicatorMargins(
					IndicatorConfig.Margins(0, indicatorMarginTop, 0, indicatorMarginBottom)
				)
		}
	}
}
