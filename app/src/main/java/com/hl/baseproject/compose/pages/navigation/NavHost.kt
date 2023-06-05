package com.hl.baseproject.compose.pages.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable

/**
 * @author  张磊  on  2023/06/05 at 17:23
 * Email: 913305160@qq.com
 */


@Composable
fun AppNavHost(navController: NavHostController, modifier: Modifier = Modifier, builder: NavGraphBuilder.() -> Unit) {
	NavHost(
		navController = navController,
		startDestination = Screen1.routeWithArgs,
		modifier = modifier
	) {
		this.apply(builder)

		this.composable(Screen2.routeWithArgs) {
			Screen2()
		}

		this.composable(Screen3.routeWithArgs, arguments = Screen3.arguments) { navBackStackEntry ->
			navBackStackEntry.arguments?.run {
				val path = this.getString(Screen3.path)
				val arg1 = this.getBoolean(Screen3.arg1)
				val arg2 = this.getParcelable<Screen3.TestParcelable>(Screen3.arg2)
				Screen3(path, arg1, arg2)
			}
		}

		this.composable(
			DeepLinkScreen.routeWithArgs,
			arguments = DeepLinkScreen.arguments,
			deepLinks = DeepLinkScreen.deepLinks
		) { navBackStackEntry ->
			val argument = navBackStackEntry.arguments?.getString(DeepLinkScreen.pathAndArg)
			DeepLinkScreen(argument)
		}
	}
}