package com.hl.baseproject.compose.pages.navigation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
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
fun Screen2() {
	AppComposeTheme(systemBarInset = false) {
		Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
			Text("屏幕2")

			Spacer(height = 10.dp)

			var count by rememberSaveable {
				mutableStateOf(0)
			}

			Text("计数：$count")

			IconButton(onClick = { count++ }) {
				Icon(imageVector = Icons.Default.Add, contentDescription = "添加")
			}
		}
	}
}