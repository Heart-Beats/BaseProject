package com.hl.utils.navigation

import android.util.Log
import androidx.fragment.app.Fragment
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.NavGraph
import androidx.navigation.fragment.NavHostFragment
import com.hl.navigatioin.utils.ReflectHelper
import java.util.Deque

/**
 * @author  张磊  on  2023/02/22 at 17:03
 * Email: 913305160@qq.com
 */

/**
 * Find a [NavController] given a [Fragment]
 *
 * Calling this on a Fragment that is not a [NavHostFragment] or within a [NavHostFragment]
 * will result in an [IllegalStateException]
 */
fun Fragment.findNavController(): NavController =
	NavHostFragment.findNavController(this)

/**
 * 获取 Navigation 中上个页面标题信息
 */
fun Fragment.getLastPage(): CharSequence {
	val navBackStackEntryDeque =
		ReflectHelper(NavController::class.java).getFiledValue<Deque<NavBackStackEntry>>(
			findNavController(), "mBackStack"
		)

	val stringBuilder = StringBuilder()
	stringBuilder.append(" -------- 导航栈 ----------- \n")
	navBackStackEntryDeque?.forEachIndexed { index, navBackStackEntry ->
		repeat(index + 1) {
			stringBuilder.append("-")
		}
		stringBuilder.append("> ${navBackStackEntry.destination} \n")
		stringBuilder.append("\n")
	}

	Log.d("Fragment", "getLastPage: $stringBuilder")
	val destinationList = navBackStackEntryDeque?.filter {
		it.destination !is NavGraph
	}?.map {
		it.destination
	}

	return destinationList?.getOrNull(destinationList.size - 2)?.label ?: ""
}

fun Fragment.getNavController(): NavController? {
	return try {
		findNavController()
	} catch (e: Exception) {
		null
	}
}

/**
 * 获取 Navigation 当前目的地
 */
fun Fragment.getCurrentDestination(): NavDestination? {
	return getNavController()?.currentDestination
}
