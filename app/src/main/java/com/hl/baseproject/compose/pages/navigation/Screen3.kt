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
 * @author  张磊  on  2023/05/31 at 18:02
 * Email: 913305160@qq.com
 */

@Composable
fun Screen3(path: String?, arg1: Boolean?, arg2: Screen3.TestParcelable?) {
	AppComposeTheme(systemBarInset = false) {
		Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
			Text("屏幕3")

			if (!path.isNullOrBlank()) {
				Spacer(height = 10.dp)

				Text(text = "传递过来的测试路径： $path")
			}

			if (arg1 != null) {
				Spacer(height = 10.dp)

				Text(text = "传递过来的测试参数1： $arg1")
			}

			if (arg2 != null) {
				Spacer(height = 10.dp)

				Text(text = "传递过来的测试参数2： $arg2")
			}
		}
	}
}