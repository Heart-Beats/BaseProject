package com.hl.baseproject.fragments.home

import android.graphics.Color
import android.os.Bundle
import androidx.core.view.updatePadding
import com.blankj.utilcode.util.AppUtils
import com.gyf.immersionbar.ktx.statusBarHeight
import com.hl.arch.mvvm.vm.activityViewModels
import com.hl.arch.utils.getColorByRes
import com.hl.arch.web.navigateToWeb
import com.hl.baseproject.base.BaseFragment
import com.hl.baseproject.databinding.FragmentHomeBinding
import com.hl.baseproject.repository.network.bean.BannerData
import com.hl.uikit.UIKitCollapsingToolbarLayout
import com.hl.utils.GlideUtil
import com.hl.utils.banner.AdsIndicator
import com.hl.utils.dpInt
import com.hl.utils.onceLastObserve
import com.hl.utils.setColor
import com.youma.yky.app.main.main.newhome.banner.CommonBannerAdapter
import com.youma.yky.app.main.viewmodels.HomeViewModel
import com.youth.banner.Banner
import com.youth.banner.config.IndicatorConfig
import com.youth.banner.indicator.Indicator
import com.youth.banner.listener.OnBannerListener

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

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		homeViewModel.getTopBannerList()
	}

	override fun onResume() {
		super.onResume()

		toolbarIsCollapseFlag = toolbarIsCollapseFlag
	}

	override fun FragmentHomeBinding.onViewCreated(savedInstanceState: Bundle?) {
		this.initTitleLayout()

		homeViewModel.topBannerListLiveData.onceLastObserve(viewLifecycleOwner) {
			it ?: return@onceLastObserve

			// 更新轮播图数据, 不要使用 adapter 更新，否则开始位置会不对
			val banner = this.topBanner as Banner<BannerData, CommonBannerAdapter>
			banner.setDatas(it)

			if (it.size == 0) {
				banner.removeIndicator()
			}
		}
	}

	private fun FragmentHomeBinding.initTitleLayout() {
		// 页面初始化时动态更新 toolbar  的边距
		this.homeTitleLayout.updatePadding(
			left = 15.dpInt,
			top = 10.dpInt + statusBarHeight,
			right = 15.dpInt,
			bottom = 10.dpInt
		)

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
		GlideUtil.load(requireContext(), AppUtils.getAppIconId(), this.ivLogo)
		this.homeTitle.text = AppUtils.getAppName()
	}

	private fun FragmentHomeBinding.initTopBanner() {
		val banner = this.topBanner as Banner<BannerData, CommonBannerAdapter>
		banner.initTopBanner(commonBannerAdapter) { data, _ ->
			val jumpUrl = data.url
			if (!jumpUrl.isNullOrBlank()) {
				navigateToWeb(jumpUrl, isNeedTitle = true)
			}
		}
	}

	private fun Banner<BannerData, CommonBannerAdapter>.initTopBanner(
		adAdapter: CommonBannerAdapter, listener:
		OnBannerListener<BannerData>
	) {
		this.setAdapter(adAdapter)
			.addBannerLifecycleObserver(viewLifecycleOwner)
			.setLoopTime(3000)
			.setBannerRound(0F)
			.setOnBannerListener(listener)

		this.initBannerIndicator(null)
	}

	private fun Banner<BannerData, CommonBannerAdapter>.initBannerIndicator(indicator: Indicator?) {
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
			.setIndicatorSelectedColor(Color.parseColor("#FFFFFF"))
			.setIndicatorHeight(indicatorHeight)
			.setIndicatorWidth(indicatorNormalWidth, indicatorSelectWidth)
			.setIndicatorSpace(indicatorSpace)
			.setIndicatorMargins(
				IndicatorConfig.Margins(0, indicatorMarginTop, 0, indicatorMarginBottom)
			)

	}
}
