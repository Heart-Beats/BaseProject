package com.hl.baseproject.compose.pages

import android.os.Bundle
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.fragment.app.activityViewModels
import com.hl.arch.base.ComposeBaseFragment
import com.hl.baseproject.compose.AppComposeTheme
import com.hl.baseproject.compose.widgets.CircleImageText
import com.hl.baseproject.compose.widgets.ImageTextCard
import com.hl.baseproject.compose.widgets.SearchBar
import com.hl.baseproject.compose.widgets.TitleCompose
import com.hl.baseproject.viewmodels.DataViewModel

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
			Scaffold(
				containerColor = MaterialTheme.colorScheme.background,
				bottomBar = {
					SootheBottomNavigation()
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
				CircleImageText(imageModel = item.dataUrl, imageDescription = item.dataDesc)
			}
		}
	}


	@Composable
	private fun FavoriteCollectionsGrid(favoriteDataList: List<FavoriteData>, modifier: Modifier = Modifier) {
		LazyHorizontalGrid(
			rows = GridCells.Fixed(2),
			contentPadding = PaddingValues(horizontal = 16.dp),
			horizontalArrangement = Arrangement.spacedBy(8.dp),
			verticalArrangement = Arrangement.spacedBy(8.dp),
			modifier = modifier.height(120.dp)
		) {
			items(favoriteDataList) {
				ImageTextCard(imageUrl = it.dataUrl, text = it.dataDesc)
			}
		}
	}

	@Composable
	private fun HomeScreen(
		alignYourBodyDataList: List<AlignYourBodyData>,
		favoriteDataList: List<FavoriteData>,
		modifier: Modifier = Modifier
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
	private fun SootheBottomNavigation(modifier: Modifier = Modifier) {
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
			})

			this.NavigationBarItem(selected = selectIndex == 1, icon = {
				Icon(
					imageVector = Icons.Default.AccountCircle, contentDescription = null
				)
			}, label = {
				Text("我的")
			}, onClick = {
				selectIndex = 1
			})
		}
	}
}