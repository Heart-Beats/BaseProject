package com.hl.baseproject.compose.pages.navigation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.hl.baseproject.compose.AppComposeTheme
import com.hl.baseproject.compose.widgets.Spacer

/**
 * @author  张磊  on  2023/06/02 at 17:54
 * Email: 913305160@qq.com
 */

@Composable
fun DeepLinkScreen(argument: String?) {
	AppComposeTheme(systemBarInset = false) {
		Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
			Text("屏幕4 - 隐式深链接")

			if (!argument.isNullOrBlank()) {
				Spacer(height = 10.dp)

				Text(text = "传递过来的参数： $argument")
			}
		}
	}
}