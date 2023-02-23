package com.hl.utils.navigation.ui

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.MotionEvent
import androidx.annotation.IdRes
import androidx.core.view.children
import androidx.core.view.forEach
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavOptions
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.bottomnavigation.BottomNavigationItemView
import com.google.android.material.bottomnavigation.BottomNavigationMenuView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.hl.uikit.visibleOrGone
import java.lang.ref.WeakReference

/**
 * @author  张磊  on  2023/02/22 at 18:02
 * Email: 913305160@qq.com
 */

/**
 * 拦截 bottomNavigationMenuView 的默认点击与长按处理（消除长按的 toast 提示，且长按处理与点击处理一致）
 */
fun BottomNavigationView.initTouchHandle() {

	//拦截 bottomNavigationMenuView 的点击与长按处理（消除长按的 toast 提示，且长按处理与点击处理一致）
	val bottomNavigationMenuView = this.children.first() as BottomNavigationMenuView
	bottomNavigationMenuView.children.forEach {
		if (it is BottomNavigationItemView) {
			it.setOnTouchListener { v, event ->
				if (event.action == MotionEvent.ACTION_UP) {
					if (event.x >= 0 && event.x <= v.width && event.y >= 0 && event.y <= v.height) {
						//仅当触摸点在 view 之内才执行 view 原有的点击事件
						v.performClick()
					}
				}
				true
			}
		}
	}
}

/**
 * 将 BottomNavigationView 与 ViewPager2 关联起来，实现彼此联动
 *
 * @param viewPager2    联动的 viewPager2
 * @param smoothScroll  点击BottomNavigationView 的菜单项时，viewPager2 跳转时是否需要动画
 */
fun BottomNavigationView.setupWithViewPager2(viewPager2: ViewPager2, smoothScroll: Boolean = true) {
	this.setOnItemSelectedListener { item ->
		if (!item.isChecked) {
			val index = this.menu.children.indexOf(item)
			viewPager2.setCurrentItem(index, smoothScroll)
			true
		} else {
			true
		}
	}

	val bottomNavigationMenuView = this
	viewPager2.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
		override fun onPageSelected(position: Int) {
			bottomNavigationMenuView.selectedItemId = bottomNavigationMenuView.menu.getItem(position).itemId
		}
	})
}

/**
 * 将 BottomNavigationView 与 navController 关联起来，
 *    若导航的目的地非 BottomNavigationView 菜单项 ID, 即非主页时 BottomNavigationView 会隐藏
 */
fun BottomNavigationView.setupWithNavController(navController: NavController) {
	setOnItemSelectedListener { item ->
		if (!item.isChecked) {
			onNavDestinationSelected(item, navController)
		} else {
			true
		}
	}

	val weakReference = WeakReference(this)
	navController.addOnDestinationChangedListener(
		object : NavController.OnDestinationChangedListener {
			override fun onDestinationChanged(
				controller: NavController,
				destination: NavDestination,
				arguments: Bundle?
			) {
				val view = weakReference.get()
				if (view == null) {
					navController.removeOnDestinationChangedListener(this)
					return
				}
				view.menu.forEach { item ->
					if (destination.hierarchy.any { it.id == item.itemId }) {
						item.isChecked = true
					}
				}

				view.setBottomNavigationViewVisibility(navController, destination)
			}
		})
}

private fun BottomNavigationView.setBottomNavigationViewVisibility(
	navController: NavController,
	destination: NavDestination
) {

	val destinationParent = destination.parent

	// 获取前往的目的地是否为 BottomNavigationView 对应的页面
	val isMainDestination = when {
		this.menu.findItem(destination.id) != null -> true

		// 导航到的目的地为 Graph 的起始地而且 Graph 的 id  是在底部菜单中
		destinationParent?.startDestinationId == destination.id
				&& this.menu.findItem(destinationParent.id) != null -> true
		else -> false
	}
	this.visibleOrGone(isMainDestination)
}

private fun onNavDestinationSelected(item: MenuItem, navController: NavController): Boolean {
	val navOptions = NavOptions.Builder()
		.setLaunchSingleTop(true)
		.setRestoreState(true)    // 是否保存 fragment 的状态
		.also {
			it.setEnterAnim(com.hl.utils.navigation.NavAnimations.NO_ANIM)
				.setExitAnim(com.hl.utils.navigation.NavAnimations.NO_ANIM)
				.setPopEnterAnim(com.hl.utils.navigation.NavAnimations.NO_ANIM)
				.setPopExitAnim(com.hl.utils.navigation.NavAnimations.NO_ANIM)
		}
		.apply {
			if (item.order and Menu.CATEGORY_SECONDARY == 0) {

				/*
				*
				导航堆栈： A-->B---> C ---> A
					   setPopUpTo(A, true)： 弹出 A 与目的地之间的堆栈，同时也弹出 A，堆栈中仅有一个目的地
					   setPopUpTo(A, false) ： 弹出 A 与目的地之间的堆栈，不弹出 A，堆栈中有两个目的地
				*
				* */
				setPopUpTo(navController.graph.findStartDestination().id, inclusive = true, saveState = true)
			}
		}
		.build()

	return try {

		navController.navigate(item.itemId, null, navOptions)
		true
		// Return true only if the destination we've navigated to matches the MenuItem
		// navController.currentDestination?.matchDestination(item.itemId) == true
	} catch (e: Exception) {
		false
	}
}

internal fun NavDestination.matchDestination(@IdRes destId: Int): Boolean =
	hierarchy.any { it.id == destId }
