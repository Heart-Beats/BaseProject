package com.hl.baseproject.compose.pages

import android.content.Intent
import android.os.Bundle
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.mapSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.net.toUri
import androidx.navigation.NavDeepLinkRequest
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.hl.arch.base.ComposeBaseFragment
import com.hl.baseproject.compose.pages.navigation.AppNavHost
import com.hl.baseproject.compose.pages.navigation.DeepLinkScreen
import com.hl.baseproject.compose.pages.navigation.Destination
import com.hl.baseproject.compose.pages.navigation.Screen1
import com.hl.baseproject.compose.pages.navigation.Screen2
import com.hl.baseproject.compose.pages.navigation.Screen3
import com.hl.baseproject.compose.pages.navigation.getAllDestinations
import com.hl.baseproject.compose.utils.toFlowCollect
import com.hl.utils.navigation.navigateSingleTopTo

/**
 * @author  张磊  on  2023/05/31 at 16:43
 * Email: 913305160@qq.com
 */
class ComposeNavigationFragment : ComposeBaseFragment() {

	@Composable
	override fun Content(savedInstanceState: Bundle?) {
		ComposeNavigationApp()
	}

	@Preview
	@OptIn(ExperimentalMaterial3Api::class)
	@Composable
	fun ComposeNavigationApp() {
		val navController = rememberNavController()
		val bottomNavigationBarState = RememberBottomNavigationBarState(initDestination = Screen1)
		val bottomAllDestinations = getAllDestinations()

		Scaffold(bottomBar = {
			BottomNavigationBar(bottomAllDestinations, bottomNavigationBarState)

			bottomNavigationBarState.currentDestination.toFlowCollect(bottomNavigationBarState) {
				when (it) {
					Screen3 -> {
						val testParcelable = Screen3.TestParcelable(it.desc, it.routeWithArgs)
						val route = Screen3.createRouteByPathAndArg("testPath", true, testParcelable)
						navController.navigateSingleTopTo(route)
					}

					DeepLinkScreen -> {
						//注意深链接时必须使用深链接的请求方式，参数为对应的 deepLink

						val deepLinkUri = "hl://DeepLinkScreen/我是深链接参数".toUri()
						val navDeepLinkRequest = NavDeepLinkRequest(Intent(Intent.ACTION_VIEW, deepLinkUri))

						// navController.navigate(deepLinkUri)
						navController.navigate(navDeepLinkRequest)
					}

					else -> {
						navController.navigateSingleTopTo(it.routeWithArgs)
					}
				}

			}
		}) { innerPadding ->

			navController.addOnDestinationChangedListener { _, destination, _ ->
				// 判断当前导航目的地是否在底部菜单中，在的话修改当前的目的地状态， 主要是针对返回按键时修改相关状态
				bottomAllDestinations.find { it.routeWithArgs == destination.route }?.run {
					bottomNavigationBarState.updateCurrentDestination(this)
				}
			}

			AppNavHost(navController, modifier = Modifier.padding(innerPadding)) {
				this.composable(Screen1.routeWithArgs) {
					Screen1(onClickScreen2 = {
						// 这里采用更改状态的方式触发 navController 的导航行为
						bottomNavigationBarState.updateCurrentDestination(Screen2)
					}, onClickScreen3 = {
						bottomNavigationBarState.updateCurrentDestination(Screen3)
					}) {
						bottomNavigationBarState.updateCurrentDestination(DeepLinkScreen)
					}
				}
			}
		}
	}

	@Composable
	private fun BottomNavigationBar(
		bottomAllDestinations: List<Destination>,
		bottomNavigationBarState: BottomNavigationBarState
	) {
		val currentDestination = bottomNavigationBarState.currentDestination
		NavigationBar {
			bottomAllDestinations.forEach {
				this.NavigationBarItem(selected = currentDestination == it, label = {
					Text(text = it.desc)
				}, icon = {}, onClick = {
					bottomNavigationBarState.updateCurrentDestination(it)
				})
			}
		}
	}

	@Composable
	private fun RememberBottomNavigationBarState(initDestination: Destination) =
		rememberSaveable(initDestination, saver = BottomNavigationBarState.Saver) {
			BottomNavigationBarState(initDestination)
		}


	private class BottomNavigationBarState(initDestination: Destination) {
		// 借助伴生对象可实现静态扩展方法
		companion object

		var currentDestination by mutableStateOf(initDestination)
			private set

		/**
		 * 更新当前的目的地
		 */
		fun updateCurrentDestination(destination: Destination) {
			currentDestination = destination
		}
	}

	private val BottomNavigationBarState.Companion.Saver: Saver<BottomNavigationBarState, *>
		get() = mapSaver(
			// save 默认只可保存存储在 Bundle 内的对象， 普通类最简单的方法是实现序列化
			save = {
				mapOf("currentDestination" to it.currentDestination)
			},
			restore = {
				val currentDestination = it["currentDestination"] as? Destination

				currentDestination?.run { BottomNavigationBarState(this) }
			})
}