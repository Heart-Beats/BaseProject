package com.hl.arch.base

import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.navigation.fragment.FragmentNavigator
import com.hl.arch.R
import com.hl.utils.OnPaletteColorParse
import com.hl.utils.PaletteUtil
import com.hl.utils.getColorByRes
import com.hl.utils.navigation.findNavController
import com.hl.utils.navigation.getCurrentDestination
import com.hl.utils.navigation.getNavController

/**
 * @author  张磊  on  2023/06/15 at 17:38
 * Email: 913305160@qq.com
 */
abstract class BaseFragment : com.hl.ui.base.BaseFragment() {

	protected val TAG = this.javaClass.simpleName

	/**
	 * 该方法可重写更改状态栏颜色, 默认与标题栏保持同色
	 */
	override fun getStatusBarColor(): Int {
		return getColorByRes(R.color.colorTitlePrimary)
	}

	/**
	 * 从 bitmap 中分析修改状态栏对应的颜色以及字体颜色
	 *
	 * @param bitmap              需要分析的 bitmap
	 * @param onPaletteColorParse 分析结果的回调
	 */
	protected fun changeStatusBarStyleFromBitmap(bitmap: Bitmap, onPaletteColorParse: OnPaletteColorParse? = null) {
		PaletteUtil.getColorFromBitmap(bitmap) { rgb, bodyTextColor, titleTextColor, isLight ->
			onPaletteColorParse?.invoke(rgb, bodyTextColor, titleTextColor, isLight)
			immersionBar?.apply {
				statusBarColorInt(rgb)
				statusBarDarkFont(isLight)
			}?.init()
		}
	}


	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)

		val currentDestination = getCurrentDestination()

		Log.d(TAG, "当前 fragment == ${this.javaClass.name},  当前标题栏 ======= ${toolbar?.title}")
		Log.d(TAG, "导航当前所在地 label =========== ${currentDestination?.label}")

		if (currentDestination is FragmentNavigator.Destination) {

			if (this.javaClass.name == currentDestination.className) {
				// 判断当前 fragment 是否为导航当前所在页面

				if (toolbar?.title.isNullOrEmpty()) {
					//当前页面 无 title 时的处理

					currentDestination.label = when (currentDestination.className) {
						// MainHomeFragment::class.java.name -> "首页"
						// MainMrchFragment::class.java.name -> "商户中心"
						// MainMeFragment::class.java.name -> "我的"
						else -> {
							""
						}
					}
				} else {
					//当前页面有 title
					currentDestination.label = toolbar?.title
				}
			}
		}
	}

	/**
	 * 判断当前 Fragment 是否为主页面
	 */
	override fun isMainPage(): Boolean {
		return if (isNavigationPage()) {
			Log.d(TAG, "isMainPage =====> ${this.javaClass.simpleName} 为 Navigation 页面")
			// 使用 Navigation 时
			true
		} else {
			Log.d(TAG, "isMainPage =====> 是否使用 Navigation : ${getNavController() != null}")
			return super.isMainPage()
		}
	}

	override fun onBackPressed() {
		try {
			if (isNavigationPage()) {
				findNavController().popBackStack()
			} else {
				// 此时为未使用 Navigation 的 Activity 的主页面
				super.onBackPressed()
			}
		} catch (e: Exception) {
			requireActivity().finish()
		}
	}


	/**
	 * 判断当前 Fragment 是否为 Navigation 导航中的页面
	 */
	protected fun isNavigationPage(): Boolean {
		val currentDestination = getCurrentDestination()
		// 判断目的地为 fragment  且当前 fragment 为导航当前所在页面
		return currentDestination is FragmentNavigator.Destination && this.javaClass.name == currentDestination.className
	}
}