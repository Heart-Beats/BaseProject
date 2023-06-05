package com.hl.baseproject.compose.pages.navigation

import android.os.Parcelable
import androidx.navigation.NavType
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import com.hl.baseproject.compose.utils.parcelableNavType
import com.hl.utils.toJson
import kotlinx.parcelize.Parcelize

/**
 * @author  张磊  on  2023/05/31 at 17:23
 * Email: 913305160@qq.com
 */

/**
 * @param desc   目的地描述
 * @param route  目的地路由部分
 */
@Parcelize
sealed class Destination(val desc: String, protected val route: String) : Parcelable {

	/**
	 * 参数传递使用参数占位符形式：{path}?arg1={arg1}&arg2={arg2}
	 *
	 *     ? 后为可选参数，使用查询参数语法，它必须具有 defaultValue 或 nullability = true（将默认值隐式设置为 null）
	 */
	protected open val pathAndArg: String = ""

	/**
	 * 带有参数的路由
	 */
	val routeWithArgs: String
		get() {
			val routeWithArgs = if (pathAndArg.isNullOrBlank()) {
				route
			} else {
				"$route/$pathAndArg"
			}
			return routeWithArgs
		}

	override fun toString(): String {
		return "Destination(desc='$desc', routeWithArgs='$routeWithArgs')"
	}
}


object Screen1 : Destination("屏幕1", "Screen1")

object Screen2 : Destination("屏幕2", "Screen2")

object Screen3 : Destination("屏幕3", "Screen3") {

	/**
	 * 路径参数 key
	 */
	val path = "path"

	/**
	 * 参数 1 key
	 */
	val arg1 = "arg1"

	/**
	 * 参数 2 key
	 */
	val arg2 = "arg2"

	override val pathAndArg = "{$path}?$arg1={$arg1}&$arg2={$arg2}"

	val arguments = listOf(
		navArgument(path) { this.type = NavType.StringType },
		navArgument(arg1) { this.type = NavType.BoolType },

		// 官方不建议使用传递这种复杂数据形式，而是应该将唯一标识符传递再去检索获取复杂数据
		navArgument(arg2) { this.type = NavType.TestParcelableType },
	)

	// 使用扩展函数获取参数类型，操持格式一致，
	private val NavType.Companion.TestParcelableType
		get() = parcelableNavType<TestParcelable>()

	fun createRouteByPathAndArg(path: String, arg1: Boolean, arg2: TestParcelable) =
		"$route/$path?${this.arg1}=$arg1&${this.arg2}=${arg2.toJson()}"

	@Parcelize
	data class TestParcelable(val desc: String? = null, val routeWithArgs: String? = null) : Parcelable
}

object DeepLinkScreen : Destination("深链接屏幕", "DeepLinkScreen") {

	/**
	 *    若深链接支持被外部应用唤醒，还需要在 Activity 中添加如下配置
	 *
	 *     <intent-filter>
	 *		    <action android:name="android.intent.action.VIEW" />
	 *		    <category android:name="android.intent.category.DEFAULT" />
	 *		    <category android:name="android.intent.category.BROWSABLE" />
	 *		    <data android:scheme="xxx" android:host="xxxx" />
	 *		</intent-filter>
	 */


	public override val pathAndArg = "testArg"

	val arguments = listOf(
		navArgument(pathAndArg) { this.type = NavType.StringType }
	)

	val deepLinks = listOf(
		navDeepLink {
			this.uriPattern = "hl://$route/{$pathAndArg}"
		}
	)
}


fun getAllDestinations() = listOf(Screen1, Screen2, Screen3)


