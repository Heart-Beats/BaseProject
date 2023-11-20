package com.hl.baseproject.compose.pages

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MenuOpen
import androidx.compose.material3.Divider
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.fragment.app.viewModels
import com.hl.baseproject.R
import com.hl.baseproject.base.BaseComposeFragment
import com.hl.baseproject.base.FlowBaseViewModel
import com.hl.baseproject.compose.AppComposeTheme
import com.hl.baseproject.compose.widgets.RoundImageText
import com.hl.baseproject.compose.widgets.SmartImage
import com.hl.baseproject.compose.widgets.Spacer
import com.hl.baseproject.repository.network.bean.Article
import com.hl.uikit.toast
import com.hl.utils.getAppName
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * @author  张磊  on  2023/05/29 at 17:24
 * Email: 913305160@qq.com
 *
 *
 *   Compose 中的附带效应：它指发生在可组合函数作用域之外的应用状态的变化。例如：当用户点按一个按钮时打开一个新屏幕，或者在应用未连接到互联网时显示一条消息。
 *
 *   LaunchedEffect()： 从可组合项内安全地调用挂起函数时使用，它会在 Compose 中触发协程作用域限定的附带效应。仅在可组合项进入组合树中时才会触发，重组不会触发执行。
 *        · 当 LaunchedEffect 进入组合时，它会启动一个协程，并将代码块作为参数传递。如果 LaunchedEffect 退出组合，协程将取消。
 *        · 该方法有可变数量数量的参数 key，用于在其中一个键发生更改时重新开始效应，即会重启 LaunchedEffect。
 *        · 如需在可组合项的生命周期内仅触发一次附带效应，将常量用作键即可，通常使用 Unit。
 *        · rememberUpdatedState()： 若想获取长期存在的 lambda 或对象表达式最新的值时，使用其进行包装，在使用 LaunchedEffect 时它很常见，否则无法确保传入 LaunchedEffect 中的最新 lambda 得到执行
 *
 *  rememberCoroutineScope():  该方法主要作用是即使不在组合中，也可以启动协程。该方法会返回一个 CoroutineScope，该 CoroutineScope 会绑定到它在组合中的调用点。一旦退出组合，作用域将自动取消。
 *
 *  DisposableEffect():  适用于在键发生变化或可组合项退出组合后需要清理的附带效应，主要用于可组合项退出时安全处置资源，以避免内存泄漏。
 *
 *  produceState():  将任意类型转换为 Compose State<T>, 它会启动一个作用域限定为组合的协程，该协程可使用 value 属性将值推送到返回的 State。与 LaunchedEffect 一样，produceState 也采用键来取消和重新开始计算。
 *
 *
 */
class ComposeSideEffectsFragment : BaseComposeFragment() {

	val viewModel: ComposeSideEffectsViewModel by viewModels()


	@Composable
	override fun Content(savedInstanceState: Bundle?) {
		AppComposeTheme(systemBarInset = false) {
			MainScreen()
		}
	}

	@Composable
	private fun MainScreen() {
		var showSplashScreen by remember { mutableStateOf(true) }
		if (showSplashScreen) {
			SplashScreen {
				showSplashScreen = false
			}
		} else {
			val articleList = viewModel.suggestedDestinations.collectAsState()
			HomeScreen(articleList.value)
		}
	}


	@Composable
	fun SplashScreen(onTimeOut: () -> Unit) {

		val lastedOnTimeOut by rememberUpdatedState(newValue = onTimeOut)

		// 使用常量不会在 onTimeOut 改变时触发附加效应重启
		LaunchedEffect(Unit) {
			delay(2000)
			lastedOnTimeOut()
		}

		val context = LocalContext.current
		DisposableEffect(Unit) {
			this.onDispose {
				context.toast("退出欢迎界面")
			}
		}

		Box {
			SmartImage(imageModel = R.mipmap.app_logo_origin, modifier = Modifier.fillMaxSize())
			Text(text = "正在进入${getAppName()}", color = Color.White, modifier = Modifier.align(Alignment.Center))
		}
	}


	@Preview
	@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
	@OptIn(ExperimentalMaterial3Api::class)
	@Composable
	fun HomeScreen(articleList: List<Article>) {
		val rememberDrawerState = rememberDrawerState(DrawerValue.Closed)
		val rememberCoroutineScope = rememberCoroutineScope()

		ModalNavigationDrawer(drawerState = rememberDrawerState, drawerContent = {
			ModalDrawerSheet(drawerShape = RoundedCornerShape(0), modifier = Modifier.fillMaxWidth(0.75F)) {
				NavigationDrawerItem(label = { Text("侧拉菜单1") }, selected = true, onClick = {
					rememberCoroutineScope.launch {
						rememberDrawerState.close()
					}
				})

				Divider()

				NavigationDrawerItem(label = { Text("侧拉菜单2") }, selected = false, onClick = {
					rememberCoroutineScope.launch {
						rememberDrawerState.close()
					}
				})
			}
		}) {
			Scaffold(topBar = {
				Row {
					IconButton(onClick = {
						rememberCoroutineScope.launch {
							rememberDrawerState.open()
						}
					}) {
						Icon(imageVector = Icons.Default.MenuOpen, contentDescription = "menu")
					}

					Text(
						text = "Compose 附带效应",
						modifier = Modifier
							.offset(x = 4.dp)
							.align(Alignment.CenterVertically)
					)
				}
			}, contentWindowInsets = WindowInsets(top = 0)) {

				Column {
					Text(text = "我是主页", modifier = Modifier.padding(it))

					Spacer(height = 10.dp)

					LazyVerticalGrid(columns = GridCells.Fixed(3)) {
						this.items(articleList.size, contentType = { "文章列表" }) {
							val article = articleList[it]
							RoundImageText(
								imageModel = article.envelopePic ?: "",
								imageDescription = article.title ?: ""
							)
						}
					}
				}
			}
		}
	}
}


class ComposeSideEffectsViewModel : FlowBaseViewModel() {

	private val _suggestedDestinations = MutableStateFlow<List<Article>>(emptyList())

	val suggestedDestinations: StateFlow<List<Article>> = _suggestedDestinations.asStateFlow()

	init {
		serviceLaunch(reqBlock = { service.getHomeArticleList() }, onSuccess = {
			_suggestedDestinations.value = it?.datas ?: listOf()
		})
	}
}