package com.hl.baseproject.compose.pages

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.compose.animation.animateColor
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.InfiniteRepeatableSpec
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Looks3
import androidx.compose.material.icons.filled.LooksOne
import androidx.compose.material.icons.filled.LooksTwo
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TabPosition
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.fragment.app.activityViewModels
import com.hl.arch.base.ComposeBaseFragment
import com.hl.baseproject.R
import com.hl.baseproject.compose.AppComposeTheme
import com.hl.baseproject.compose.randomColor
import com.hl.baseproject.compose.widgets.CircleImageText
import com.hl.baseproject.compose.widgets.IconMenu
import com.hl.baseproject.compose.widgets.ImageTextCard
import com.hl.baseproject.compose.widgets.SearchBar
import com.hl.baseproject.compose.widgets.TabLayout
import com.hl.baseproject.compose.widgets.TitleCompose
import com.hl.baseproject.compose.widgets.VerticalDivider
import com.hl.baseproject.viewmodels.DataViewModel
import com.hl.uikit.toast

class ComposeLayoutsFragment : ComposeBaseFragment() {

	private val dataViewModel by activityViewModels<DataViewModel>()

	@Composable
	override fun Content(savedInstanceState: Bundle?) {
		val imageUrls by dataViewModel.imagesLiveData.observeAsState(listOf())

		val alignYourBodyDataList = List(imageUrls.size) {
			AlignYourBodyData(imageUrls[it], "测试图片${if (it == 0) "" else it}")
		}

		val favoriteDataList = List(imageUrls.size) {
			FavoriteData(imageUrls[it], "收藏图片${if (it == 0) "" else it}")
		}

		InitPage(alignYourBodyDataList, favoriteDataList)
	}

	private data class AlignYourBodyData(
		val dataUrl: String, val dataDesc: String
	)

	private data class FavoriteData(
		val dataUrl: String, val dataDesc: String
	)

	@OptIn(ExperimentalMaterial3Api::class)
	@Composable
	private fun InitPage(alignYourBodyDataList: List<AlignYourBodyData>, favoriteDataList: List<FavoriteData>) {
		AppComposeTheme(systemBarInset = false) {
			var selectIndex by remember { mutableStateOf(0) }
			val backgroundColor by animateColorAsState(
				if (selectIndex == 0) colorResource(id = R.color.purple_200).copy(0.1F)
				else colorResource(id = com.hl.res.R.color.green_shen).copy(0.1F)
			)
			Scaffold(
				containerColor = backgroundColor,
				topBar = {
					HomeTopBar()
				},
				bottomBar = {
					SootheBottomNavigation {
						selectIndex = it
					}
				}) {
				HomeScreen(alignYourBodyDataList, favoriteDataList, Modifier.padding(it))
			}
		}
	}

	@Composable
	private fun AlignYourBodyRow(alignYourBodyDataList: List<AlignYourBodyData>, modifier: Modifier = Modifier) {
		LazyRow(
			modifier = modifier,
			contentPadding = PaddingValues(horizontal = 16.dp),
			horizontalArrangement = Arrangement.spacedBy(8.dp)
		) {
			items(alignYourBodyDataList) { item ->

				Row {
					CircleImageText(imageModel = item.dataUrl, imageDescription = item.dataDesc)

					// 分隔线添加在这里不会更改 Item 的索引
					VerticalDivider(color = MaterialTheme.colorScheme.primary, thickness = 8.dp)
				}
			}
		}
	}


	@Composable
	private fun FavoriteCollectionsGrid(favoriteDataList: List<FavoriteData>, modifier: Modifier = Modifier) {
		LazyHorizontalGrid(
			// 还可以通过实现 GridCells 接口来动态计算每行的高度
			rows = GridCells.Fixed(2),
			contentPadding = PaddingValues(horizontal = 16.dp),
			// horizontalArrangement = Arrangement.spacedBy(8.dp),
			verticalArrangement = Arrangement.spacedBy(8.dp),
			modifier = modifier.height(120.dp)
		) {
			items(favoriteDataList, span = {
				val isFirst = favoriteDataList.first() == it
				GridItemSpan(if (isFirst) 2 else 1)
			}) {

				Row(verticalAlignment = Alignment.CenterVertically) {
					ImageTextCard(
						imageUrl = it.dataUrl, text = it.dataDesc
					)

					// 分隔线添加在这里不会更改 Item 的索引
					VerticalDivider(color = MaterialTheme.colorScheme.primary.copy(0.5F), thickness = 8.dp)
				}
			}
		}
	}


	@Composable
	private fun HomeTopBar() {
		val tabs = listOf(
			IconMenu(menuText = "选项1", iconPainter = rememberVectorPainter(image = Icons.Default.LooksOne)),
			IconMenu(menuText = "选项2", iconPainter = rememberVectorPainter(image = Icons.Default.LooksTwo)),
			IconMenu(menuText = "选项3", iconPainter = rememberVectorPainter(image = Icons.Default.Looks3)),
		)

		var selectedTabIndex by remember {
			mutableStateOf(0)
		}

		TabLayout(tabs = tabs, selectedTabIndex = selectedTabIndex, indicator = { tabPositions ->
			HomeTopBarIndicator(tabPositions, selectedTabIndex)
		}) {
			selectedTabIndex = it
		}
	}


	@Composable
	private fun HomeTopBarIndicator(tabPositions: List<TabPosition>, selectedTabIndex: Int) {
		println("指示器位置-------------")

		// 获取到 currentTabPosition 中对应的偏移，设置给可组合项的 offset 修饰符，即已可实现指示器功能
		// Box(
		// 	modifier = Modifier
		// 		.offset(x = currentTabPosition.left)
		// 		.width(currentTabPosition.width)
		// 		.background(color = Color.Cyan.copy(0.3F), shape = RoundedCornerShape(4.dp))
		// 		.padding(4.dp)
		// 		.fillMaxSize()
		// )


		val updateTransition = updateTransition(selectedTabIndex, "Tab indicator")

		val indicatorLeft by updateTransition.animateDp(transitionSpec = {
			if (selectedTabIndex isTransitioningTo selectedTabIndex + 1) {
				// 从左往右移动时，左边缘移动较慢
				spring(stiffness = Spring.StiffnessVeryLow)
			} else {
				// 从右往左移动时，右边缘移动较慢
				spring(stiffness = Spring.StiffnessMedium)
			}
		}, label = "Indicator left") {
			tabPositions[it].left
		}

		val indicatorRight by updateTransition.animateDp(
			transitionSpec = {
				if (selectedTabIndex isTransitioningTo selectedTabIndex + 1) {
					// 从左往右移动时，右边缘移动较快
					spring(stiffness = Spring.StiffnessMedium)
				} else {
					// 从右往左移动时，左边缘移动较块
					spring(stiffness = Spring.StiffnessVeryLow)
				}
			},
			label = "Indicator right"
		) {
			tabPositions[it].right
		}

		@SuppressLint("UnusedTransitionTargetStateParameter")
		val borderColor by updateTransition.animateColor(
			label = "Border color"
		) {
			randomColor()
		}

		Box(
			modifier = Modifier
				.wrapContentSize(align = Alignment.BottomStart)
				.offset(x = indicatorLeft)
				.width(indicatorRight - indicatorLeft)
				// .border(2.dp, borderColor, RoundedCornerShape(4.dp))
				.background(
					Brush.horizontalGradient(
						colors = listOf(
							MaterialTheme.colorScheme.primary.copy(0.5F),
							borderColor.copy(0.5F)
						)
					),
					RoundedCornerShape(4.dp)
				)
				.padding(4.dp)
				.fillMaxSize()
		)

	}

	@Composable
	private fun HomeScreen(
		alignYourBodyDataList: List<AlignYourBodyData>,
		favoriteDataList: List<FavoriteData>,
		modifier: Modifier = Modifier,
	) {

		Column(
			modifier
				.padding(horizontal = 16.dp)
				.verticalScroll(rememberScrollState())
		) {
			Spacer(Modifier.height(16.dp))

			SearchBar(hint = "请输入搜索内容", modifier = Modifier.padding(horizontal = 16.dp))

			TitleCompose("用户信息") {
				AlignYourBodyRow(alignYourBodyDataList)
			}

			TitleCompose("最爱·收藏") {
				FavoriteCollectionsGrid(favoriteDataList)
			}

			Spacer(Modifier.height(16.dp))

			RotateColorCircle()
		}
	}

	@Preview(name = "PreviewHomeScreen", showBackground = true)
	@Composable
	private fun PreviewHomeScreen() {
		val alignYourBodyDataList = List(20) {
			AlignYourBodyData("", "测试图片${if (it == 0) "" else it}")
		}

		val favoriteDataList = List(20) {
			FavoriteData("", "收藏图片${if (it == 0) "" else it}")
		}

		AppComposeTheme {
			HomeScreen(alignYourBodyDataList, favoriteDataList)
		}
	}

	@Composable
	private fun SootheBottomNavigation(modifier: Modifier = Modifier, onSelectedIndex: (Int) -> Unit = {}) {
		var selectIndex by rememberSaveable { mutableStateOf(0) }

		NavigationBar(
			containerColor = MaterialTheme.colorScheme.background, modifier = modifier
		) {
			this.NavigationBarItem(selected = selectIndex == 0, icon = {
				Icon(
					imageVector = Icons.Default.Spa, contentDescription = null
				)
			}, label = {
				Text("首页")
			}, onClick = {
				selectIndex = 0

				onSelectedIndex(selectIndex)
			})

			this.NavigationBarItem(selected = selectIndex == 1, icon = {
				Icon(
					imageVector = Icons.Default.AccountCircle, contentDescription = null
				)
			}, label = {
				Text("我的")
			}, onClick = {
				selectIndex = 1

				onSelectedIndex(selectIndex)
			})
		}
	}

	@Composable
	fun RotateColorCircle() {
		val infiniteTransition = rememberInfiniteTransition()

		val degrees by infiniteTransition.animateFloat(
			initialValue = 0F,
			targetValue = 360F,
			animationSpec = InfiniteRepeatableSpec(
				animation = keyframes {
					this.durationMillis = 4000
					0.5f at 180
				},
				repeatMode = RepeatMode.Reverse
			)
		)

		ColorCircle(modifier = Modifier.rotate(degrees)) {
			toast("不会停止，必须移除才可以")
		}
	}

	@Preview
	@Composable
	private fun ColorCircle(modifier: Modifier = Modifier, onClick: () -> Unit = {}) {
		Box(
			modifier = modifier
				.clickable { onClick() }
				.size(100.dp)
				.background(
					Brush.sweepGradient(listOf(Color.Red, Color.Green, Color.Blue)),
					CircleShape
				)
		)
	}
}