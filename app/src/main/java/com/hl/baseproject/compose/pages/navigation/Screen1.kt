package com.hl.baseproject.compose.pages.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.hl.baseproject.compose.AppComposeTheme
import com.hl.baseproject.compose.widgets.Spacer

/**
 * @author  张磊  on  2023/05/31 at 18:02
 * Email: 913305160@qq.com
 */

@Composable
fun Screen1(onClickScreen2: () -> Unit, onClickScreen3: () -> Unit, onClickDeepLinkScreen: () -> Unit) {
	AppComposeTheme(systemBarInset = false) {
		Box(modifier = Modifier.fillMaxWidth()) {
			Column(modifier = Modifier.align(Alignment.Center)) {
				Text("屏幕1")

				Spacer(height = 10.dp)

				TextButton(onClick = onClickScreen2) {
					Text(text = "前往屏幕二")
				}

				Spacer(height = 10.dp)

				TextButton(onClick = onClickScreen3) {
					Text(text = "前往屏幕三")
				}

				Spacer(height = 10.dp)

				TextButton(onClick = onClickDeepLinkScreen) {
					Text(text = "前往深链接页面")
				}
			}
		}
	}
}