package com.hl.baseproject.compose.widgets

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.hl.baseproject.compose.AppComposeTheme

@Composable
fun ImageTextCard(
	imageUrl: String,
	text: String,
	modifier: Modifier = Modifier
) {
	Surface(shape = MaterialTheme.shapes.small, modifier = modifier) {
		Row(modifier = Modifier.width(192.dp), verticalAlignment = Alignment.CenterVertically) {
			SmartImage(
				imageModel = imageUrl,
				modifier = Modifier.size(56.dp)
			)

			Text(
				text,
				style = MaterialTheme.typography.bodySmall,
				modifier = Modifier.padding(horizontal = 16.dp)
			)
		}
	}
}

@Preview(name = "ImageTextCard")
@Composable
private fun PreviewImageTextCard() {
	AppComposeTheme {
		ImageTextCard("", "测试文本", modifier = Modifier.padding(8.dp))
	}
}