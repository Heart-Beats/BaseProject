package com.hl.baseproject.compose.widgets

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.paddingFromBaseline
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.hl.baseproject.compose.AppComposeTheme

@Composable
fun TitleCompose(
	title: String, modifier: Modifier = Modifier, content: @Composable () -> Unit
) {
	Column(modifier) {
		Text(
			title,
			style = MaterialTheme.typography.titleMedium,
			modifier = Modifier
				.paddingFromBaseline(top = 40.dp, bottom = 8.dp)
				.padding(horizontal = 16.dp)
		)
		content()
	}
}

@Preview(name = "TitleCompose")
@Composable
private fun PreviewTitleCompose() {
	AppComposeTheme {
		TitleCompose("我是标题") {
			RoundImageText(
				imageModel = "", "测试图片"
			)
		}
	}
}