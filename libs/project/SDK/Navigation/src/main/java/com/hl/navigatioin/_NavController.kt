package com.hl.navigatioin

import android.net.Uri
import android.util.Log
import androidx.navigation.NavController
import androidx.navigation.NavDeepLink
import androidx.navigation.NavDestination
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavOptions
import androidx.navigation.navOptions
import com.hl.navigatioin.utils.ReflectHelper
import java.util.regex.Pattern

/**
 * @Author  张磊  on  2021/03/16 at 18:24
 * Email: 913305160@qq.com
 */

const val TAG = "NavController"

/**
 * 确保返回堆栈顶部最多只有给定目的地的一个副本, 即多次导航同一目的地，栈顶只会存在一个目的地
 */
fun NavController.navigateSingleTopTo(route: String) {
    this.navigate(route) {
        // 弹出到导航图的起始目的地, 即最终只剩下起始目的地和本次目的地
        this.popUpTo(
            this@navigateSingleTopTo.graph.findStartDestination().id
        ) {
            // 是否保存弹出目的地的状态
            saveState = true
        }

        // 确保返回堆栈顶部最多只有给定目的地的一个副本
        this.launchSingleTop = true
        // 是否恢复之前保存的状态
        this.restoreState = true
    }
}

fun NavController.navigateFromUrl(
    url: String,
    navOptions: NavOptions? = navOptions {
        this.anim {
            this.enter = -1
            this.exit = -1
            this.popEnter = -1
            this.popExit = -1
        }
    }
) {
    val uri = Uri.parse(url)
    if (uri.scheme == "http" || uri.scheme == "https") {
        val newUri = Uri.parse("${uri.scheme}://?url=${uri}")

        if (this.graph.hasDeepLink(newUri)) {
            // handleMatchDeepLinkForUrl(this, newUri)
            this.navigate(newUri, navOptions)
        } else {
            Log.e(TAG, "navigateFromUrl: 未找到与 $newUri 所匹配的深链接页面")
        }
    } else if (uri.scheme == "native") {
        if (this.graph.hasDeepLink(uri)) {
            this.navigate(uri, navOptions)
        } else {
            Log.e(TAG, "navigateFromUrl: 未找到与 $uri 所匹配的深链接页面")
        }
    }
}

private fun handleMatchDeepLinkForUrl(navController: NavController, newUri: Uri) {
    //将自身导航以及所有子 NavDestination 添加到列表中 -----> 过滤出匹配给定深链接的 NavDestination
    val navDestinationList = mutableListOf<NavDestination>(navController.graph)
    navController.graph.forEach {
        navDestinationList.add(it)
    }
    navDestinationList.filter {
        it.hasDeepLink(newUri)
    }.forEach {
        val deepLinks = ReflectHelper(NavDestination::class.java).getFiledValue<MutableList<NavDeepLink>>(it, "deepLinks")
        deepLinks?.onEach { navDeepLink ->
            navDeepLink.uriPattern?.run {
                val reflectHelper = ReflectHelper(NavDeepLink::class.java)
                when {
                    this.startsWith("http://") -> Pattern.compile("""^\Qhttp://url=\E(.+?)""")
                    this.startsWith("https://") -> Pattern.compile("""^\Qhttps://url=\E(.+?)""")
                    else -> reflectHelper.getFiledValue<Pattern>(navDeepLink, "pattern")
                }?.also { pattern ->
                    reflectHelper.setFiledValue(navDeepLink, "pattern", pattern)
                }
            }
        }
    }
}