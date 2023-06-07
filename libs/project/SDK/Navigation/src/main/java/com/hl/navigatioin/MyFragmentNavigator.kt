package com.hl.navigatioin


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
		return if (!destination.isNeedSpecialHandle()) {
			val transformNavOptions = transformNavOptions(navOptions)
			super.navigate(destination, args, transformNavOptions, navigatorExtras)
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
			null
		}
	}

	/**
	 *  将布局文件中的 NavOptions 转换为添加统一动画的 NavOptions
	 */
	private fun transformNavOptions(navOptions: NavOptions?): NavOptions {
		Log.d(TAG, "transformNavOptions --- 处理前 navOptions 中设置的动画 == ${navOptions?.toAnimString()}")

		val enterAnim = handleDefaultAnim(navOptions, NavAnimateScene.ENTER_ANIM)
		val exitAnim = handleDefaultAnim(navOptions, NavAnimateScene.EXIT_ANIM)
		val popEnterAnim = handleDefaultAnim(navOptions, NavAnimateScene.POP_ENTER_ANIM)
		val popExitAnim = handleDefaultAnim(navOptions, NavAnimateScene.POP_EXIT_ANIM)

		val newNavOptions = NavOptions.Builder().apply {
			this.setEnterAnim(enterAnim)
			this.setExitAnim(exitAnim)
			this.setPopEnterAnim(popEnterAnim)
			this.setPopExitAnim(popExitAnim)

			navOptions?.also {
				this.setRestoreState(it.shouldRestoreState())
				this.setLaunchSingleTop(it.shouldLaunchSingleTop())

				// popUpTo 属性: 在当前路由堆栈表中，一直将页面出栈，直到指定的页面为止
				// popUpToInclusive：代表包含关系，是否也弹出指定的页面
				this.setPopUpTo(it.popUpToId, it.isPopUpToInclusive(), it.shouldPopUpToSaveState())
			}
		}.build()

		Log.d(TAG, "transformNavOptions ---  处理后的动画 == ${newNavOptions.toAnimString()}")
		return newNavOptions
	}

	private fun Destination.isNeedSpecialHandle(): Boolean {
		return myNavHostFragment.getSpecialDeepLinks().map { Uri.parse(it) }.any { this.hasDeepLink(it) }
	}

	/**
	 * @param  navOptions 经过 NavController 处理后的导航选项，只要 action 存在，它就不会为 null，即使调用 navigate() 传入 null
	 * @param  navAnimateName 处理的动画属性名
	 * @return 动画对应的资源 id , -1 即无动画
	 */
	private fun handleDefaultAnim(navOptions: NavOptions?, @NavAnimateScene navAnimateName: String): Int {
		val commonNavAnimations = myNavHostFragment.getCommonNavAnimations()
		val (originAnim, commonAnim) = when (navAnimateName) {
			NavAnimateScene.ENTER_ANIM -> Pair(navOptions?.enterAnim, commonNavAnimations?.enterAnim)
			NavAnimateScene.EXIT_ANIM -> Pair(navOptions?.exitAnim, commonNavAnimations?.exitAnim)
			NavAnimateScene.POP_ENTER_ANIM -> Pair(navOptions?.popEnterAnim, commonNavAnimations?.popEnterAnim)
			NavAnimateScene.POP_EXIT_ANIM -> Pair(navOptions?.popExitAnim, commonNavAnimations?.popExitAnim)
			else -> Pair(NavAnimations.NO_ANIM, NavAnimations.NO_ANIM)
		}

		return when (originAnim) {
			null, NavAnimations.NO_ANIM -> -1  // 原始动画不存在或设置没有动画使用 Navigation 动画的默认值
			-1 -> commonAnim ?: originAnim     // 原始动画为默认值时采用设置的共通动画，共通动画不存在时采用 Navigation 动画的默认值
			else -> originAnim                 // 采用 XML 中单独设置的动画
		}
	}

	private fun NavOptions.toAnimString(): String {
		return "NavOptions(mEnterAnim=$enterAnim , mExitAnim=$exitAnim , mPopEnterAnim=$popEnterAnim , mPopExitAnim=$popExitAnim )"
	}
}