package com.hl.baseproject.compose.pages

import android.os.Bundle
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.hl.arch.base.ComposeBaseFragment
import com.hl.baseproject.compose.AppComposeTheme
import com.hl.utils.navigation.findNavController

class ComposeDemoFragment : ComposeBaseFragment() {

	@Composable
	override fun Content(savedInstanceState: Bundle?) {
		InitPage()
	}

	@Preview
	@Composable
	private fun InitPage() {
		AppComposeTheme(systemBarInset = false) {

			//当设置任何元素的颜色时，最好使用 Surface 来实现此目的，它会设置适当的内容颜色 CompositionLocal 值。请慎用直接 Modifier.background 调用，这种调用不会设置适当的内容颜色。
			Surface(modifier = Modifier.fillMaxSize()) {

				Column(horizontalAlignment = Alignment.CenterHorizontally) {
					Text("Compose Demo 页面")

					Spacer(modifier = Modifier.height(4.dp))

					TextButton(onClick = {
						val navDirections = ComposeDemoFragmentDirections.actionComposeDemoFragmentToComposeListFragment()
						findNavController().navigate(navDirections)
					}) {
						Text(text = "列表测试页")
					}

					TextButton(onClick = {
						val navDirections = ComposeDemoFragmentDirections.actionComposeDemoFragmentToComposeLayoutsFragment()
						findNavController().navigate(navDirections)
					}) {
						Text(text = "Compose 中的基本布局")
					}
				}
			}
		}
	}
}