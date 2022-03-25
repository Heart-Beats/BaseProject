package com.youma.arch.utils

import android.util.Log
import androidx.fragment.app.Fragment
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.NavGraph
import androidx.navigation.fragment.findNavController
import java.util.*

/**
 * @Author  张磊  on  2020/08/19 at 16:20
 * Email: 913305160@qq.com
 */

fun Fragment.getLastPage(): CharSequence {
    val navBackStackEntryDeque = NavController::class.java
        .getFiledValue<NavController, Deque<NavBackStackEntry>>(findNavController(), "mBackStack")

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


private fun <T, V> Class<T>.getFiledValue(obj: T, filedName: String): V? {
    return try {
        val field = this.getDeclaredField(filedName)
        field.isAccessible = true
        val get = field.get(obj)
        field.isAccessible = false
        get as? V
    } catch (e: Exception) {
        null
    }
}

fun Fragment.getNavController(): NavController? {
    return try {
        findNavController()
    } catch (e: Exception) {
        null
    }
}

fun Fragment.getCurrentDestination(): NavDestination? {
    return getNavController()?.currentDestination
}
