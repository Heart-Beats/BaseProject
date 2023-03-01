package com.hl.navigatioin.ui

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.annotation.IdRes
import androidx.core.view.forEach
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavOptions
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.lang.ref.WeakReference

/**
 * @author  张磊  on  2023/02/22 at 18:02
 * Email: 913305160@qq.com
 */

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
	this.visibility = if (isMainDestination) View.VISIBLE else View.GONE
}

private fun onNavDestinationSelected(item: MenuItem, navController: NavController): Boolean {
	val navOptions = NavOptions.Builder()
		.setLaunchSingleTop(true)
		.setRestoreState(true)    // 是否保存 fragment 的状态
		.also {
			it.setEnterAnim(com.hl.navigatioin.NavAnimations.NO_ANIM)
				.setExitAnim(com.hl.navigatioin.NavAnimations.NO_ANIM)
				.setPopEnterAnim(com.hl.navigatioin.NavAnimations.NO_ANIM)
				.setPopExitAnim(com.hl.navigatioin.NavAnimations.NO_ANIM)
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
