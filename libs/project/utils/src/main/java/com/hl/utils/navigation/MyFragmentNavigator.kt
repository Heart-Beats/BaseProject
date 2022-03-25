package com.hl.utils.navigation

import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavDestination
import androidx.navigation.NavOptions
import androidx.navigation.Navigator
import androidx.navigation.fragment.FragmentNavigator

@Navigator.Name("fragment")
class MyFragmentNavigator(
	private val myNavHostFragment: MyNavHostFragment, private val manager: FragmentManager,
	private val containerId: Int
) : FragmentNavigator(myNavHostFragment.requireContext(), manager, containerId) {

	private val TAG = "MyFragmentNavigator"

	/**
	 * 新版本 NavController.navigate() 实际调用方法
	 */
	override fun navigate(
		entries: List<NavBackStackEntry>,
		navOptions: NavOptions?,
		navigatorExtras: Navigator.Extras?
	) {
		val transformNavOptions = transformNavOptions(navOptions)

		Log.d(TAG, "navigate: 处理后的动画 == ${transformNavOptions.toAnimString()}")
		// 处理得到转换后添加统一动画的 navOptions 再由父类处理页面跳转
		super.navigate(entries, transformNavOptions, navigatorExtras)
	}

	/**
	 * 旧版本 NavController.navigate() 实际调用方法, 会在某些情况下触发调用
	 */
	override fun navigate(
		destination: Destination,
		args: Bundle?,
		navOptions: NavOptions?,
		navigatorExtras: Navigator.Extras?
	): NavDestination? {
		if (!destination.isNeedSpecialHandle()) {
			val transformNavOptions = transformNavOptions(navOptions)

			Log.d(TAG, "navigate: 处理后的动画 == ${transformNavOptions.toAnimString()}")
			return super.navigate(destination, args, transformNavOptions, navigatorExtras)
		} else {
			val fragmentTag = destination.label?.toString()
			val findFragment = manager.findFragmentByTag(fragmentTag)
			manager.beginTransaction().run {
				if (findFragment != null) {
					// 防止因 add 导致的 Fragment 无限添加
					this.remove(findFragment)
				}
				this.add(containerId, Class.forName(destination.className) as Class<out Fragment>, args, fragmentTag)
			}.commit()
			return null
		}
	}

	/**
	 *  将布局文件中的 NavOptions 转换为添加统一动画的 NavOptions
	 */
	private fun transformNavOptions(navOptions: NavOptions?): NavOptions {
		val enterAnim = handleDefaultAnim(navOptions, MyNavHostFragment.ENTER_ANIM)
		val exitAnim = handleDefaultAnim(navOptions, MyNavHostFragment.EXIT_ANIM)
		val popEnterAnim = handleDefaultAnim(navOptions, MyNavHostFragment.POP_ENTER_ANIM)
		val popExitAnim = handleDefaultAnim(navOptions, MyNavHostFragment.POP_EXIT_ANIM)

		val newNavOptions = NavOptions.Builder().apply {
			this.setEnterAnim(enterAnim)
			this.setExitAnim(exitAnim)
			this.setPopEnterAnim(popEnterAnim)
			this.setPopExitAnim(popExitAnim)

			navOptions?.also {
				this.setRestoreState(it.shouldRestoreState())
				this.setLaunchSingleTop(it.shouldLaunchSingleTop())
				this.setPopUpTo(it.popUpToId, it.isPopUpToInclusive(), it.shouldPopUpToSaveState())
			}
		}.build()
		return newNavOptions
	}

	private fun Destination.isNeedSpecialHandle(): Boolean {
		return myNavHostFragment.getSpecialDeepLinks().map { Uri.parse(it) }.any { this.hasDeepLink(it) }
	}

	/**
	 * @param  navOptions 经过 NavController 处理后的导航选项，只要 action 存在，它就不会为 null，即使调用 navigate() 传入 null
	 * @param  propertyName 处理的动画属性名
	 * @return 动画对应的资源 id , -1 即无动画
	 */
	private fun handleDefaultAnim(navOptions: NavOptions?, propertyName: String): Int {
		try {
			//使用反射取得所需的属性对应的 get 方法
			val methodName = "get${propertyName[0].uppercaseChar()}" + propertyName.substring(1)
			val getPropertyFunction = NavOptions::class.java.getMethod(methodName)
			Log.d(
				TAG, "handleDefaultAnim--$propertyName: navOptions中设置的动画 ==" +
						" ${navOptions?.toAnimString()}"
			)

			return if (navOptions == null || getPropertyFunction.invoke(navOptions) == NavAnimations.NO_ANIM) {
				-1
			} else if (getPropertyFunction.invoke(navOptions) == -1) {
				// 当 navOptions 为空或者 navOptions 未设置动画（解析 action 时动画资源默认值都为 -1 ） ----> 使用共通动画
				myNavHostFragment.getCommonNavAnimations()?.run {
					val getAnimFunction = this::class.java.getMethod(methodName)
					getAnimFunction.invoke(this) as? Int ?: -1
				} ?: -1
			} else {
				getPropertyFunction.invoke(navOptions) as? Int ?: -1
			}
		} catch (e: Exception) {
			Log.e(TAG, "handleDefaultAnim: ", e)
			return -1
		}
	}

	private fun NavOptions.toAnimString(): String {
		return "NavOptions(mEnterAnim=$enterAnim , mExitAnim=$exitAnim , mPopEnterAnim=$popEnterAnim , mPopExitAnim=$popExitAnim )"
	}
}