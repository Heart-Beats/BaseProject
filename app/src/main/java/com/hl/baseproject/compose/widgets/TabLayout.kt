package com.hl.baseproject.compose.widgets

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LooksOne
import androidx.compose.material.icons.filled.LooksTwo
import androidx.compose.material3.Icon
import androidx.compose.material3.TabPosition
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

data class IconMenu(
	val iconPainter: Painter? = null,
	val menuText: String = ""
)

@Composable
fun TabLayout(
	tabs: List<IconMenu>,
	selectedTabIndex: Int = 0,
	indicator: @Composable (tabPositions: List<TabPosition>) -> Unit = @Composable { tabPositions ->
		TabRowDefaults.Indicator(
			Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex])
		)
	},
	modifier: Modifier = Modifier,
	onTabSelected: (selectIndex: Int) -> Unit
) {
	TabRow(selectedTabIndex, indicator = indicator, modifier = modifier) {
		tabs.forEachIndexed { index, iconMenu ->
			Row(
				modifier = modifier
					.clickable { onTabSelected(index) }
					.padding(16.dp),
				horizontalArrangement = Arrangement.Center,
				verticalAlignment = Alignment.CenterVertically
			) {
				iconMenu.iconPainter?.run {
					Icon(painter = this, contentDescription = null)
				}

				Spacer(modifier = Modifier.width(16.dp))
				Text(text = iconMenu.menuText)
			}
		}
	}
}

@Preview(name = "TabLayout")
@Composable
private fun PreviewTabLayout() {
	val tabs = listOf(
		IconMenu(menuText = "选项1", iconPainter = rememberVectorPainter(image = Icons.Default.LooksOne)),
		IconMenu(menuText = "选项2", iconPainter = rememberVectorPainter(image = Icons.Default.LooksTwo))
	)

	TabLayout(tabs = tabs) {

	}
}