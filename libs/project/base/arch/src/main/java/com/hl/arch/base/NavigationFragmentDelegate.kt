package com.hl.arch.base

import android.util.Log
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.FragmentNavigator
import com.hl.ui.base.BaseFragment
import com.hl.navigatioin.findNavController
import com.hl.navigatioin.getCurrentDestination
import com.hl.navigatioin.getNavController

/**
 * @author  张磊  on  2023/07/07 at 9:26
 * Email: 913305160@qq.com
 */

interface NavigationFragmentDelegate {

	/**
	 * 设置被代理的 BaseFragment
	 */
	fun setDelegateFragment(baseFragment: BaseFragment)

	/**
	 * BaseFragment 试图创建完成的回调，针对 Navigation 可作特殊处理
	 */
	fun onNavigationFragmentViewCreated()

	/**
	 * 判断当前 Fragment 是否为主页面
	 */
	fun isMainPage(): Boolean

	/**
	 * 返回键按下时的回调
	 */
	fun onBackPressed()
}


class BaseNavigationFragmentDelegate : NavigationFragmentDelegate {

	private val TAG = this.javaClass.simpleName

	private lateinit var fragment: BaseFragment

	override fun setDelegateFragment(baseFragment: BaseFragment) {
		this.fragment = baseFragment
	}

	override fun onNavigationFragmentViewCreated() {
		val currentDestination = fragment.getCurrentDestination()

		val toolbar = fragment.toolbar

		Log.d(TAG, "当前 fragment == ${fragment.javaClass.name},  当前标题栏 ======= ${toolbar?.title}")
		Log.d(TAG, "导航当前所在地 label =========== ${currentDestination?.label}")

		if (currentDestination is FragmentNavigator.Destination) {

			if (fragment.javaClass.name == currentDestination.className) {
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

	override fun isMainPage(): Boolean {
		return if (fragment.isNavigationDisplayPage()) {
			Log.d(TAG, "isMainPage =====> ${fragment.javaClass.simpleName} 为 Navigation 页面")
			// 使用 Navigation 时
			true
		} else {
			Log.d(TAG, "isMainPage =====> 是否使用 Navigation : ${fragment.getNavController() != null}")

			// 这里调用父类中的方法， 但这里无法使用 super 以及反射调用父类中 isMainPage 方法
			val isActivityMainPage = fragment.isActivityMainPage()

			if (isActivityMainPage) {
				Log.d(TAG, "isMainPage =====>  ${fragment.javaClass.simpleName} 为 Activity 主页面")
			} else {
				Log.d(TAG, "isMainPage =====>  ${fragment.javaClass.simpleName} 非 Activity 主页面")
			}

			return isActivityMainPage
		}
	}

	override fun onBackPressed() {
		try {
			if (fragment.isNavigationDisplayPage()) {
				fragment.findNavController().popBackStack()
			} else {
				// 此时为未使用 Navigation 的 Activity 的主页面， 调用父类中的方法， 但这里无法使用 super 以及反射调用父类中 onBackPressed 方法
				if (isMainPage()) {
					fragment.requireActivity().finish()
				}
			}
		} catch (e: Exception) {
			fragment.requireActivity().finish()
		}
	}
}

/**
 * 判断当前 Fragment 是否为 Navigation 导航当前显示的页面
 */
fun Fragment.isNavigationDisplayPage(): Boolean {
	val currentDestination = getCurrentDestination()
	// 判断目的地为 fragment  且当前 fragment 为导航当前所在页面
	return currentDestination is FragmentNavigator.Destination && currentDestination.className == this.javaClass.name
}

