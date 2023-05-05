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

class ComposeLayoutsFragment : ComposeBaseFragment() {

	@Composable
	override fun Content(savedInstanceState: Bundle?) {
		InitPage()
	}

	@Preview
	@Composable
	private fun InitPage() {
		AppComposeTheme(systemBarInset = false) {
			Surface(modifier = Modifier.fillMaxSize()) {

				Column(horizontalAlignment = Alignment.CenterHorizontally) {
					Text("Compose Demo 页面")

					Spacer(modifier = Modifier.height(4.dp))

					TextButton(
						onClick = {
							findNavController().navigate(ComposeDemoFragmentDirections.actionComposeDemoFragmentToComposeListFragment())
						}
					) {
						Text(text = "列表测试页")
					}

					TextButton(onClick = {

					}) {
						Text(text = "Compose 中的基本布局")
					}
				}
			}
		}
	}
}