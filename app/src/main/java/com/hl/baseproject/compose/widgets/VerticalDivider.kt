package com.hl.baseproject.compose.widgets

import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Divider
import androidx.compose.material3.DividerDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp

@Composable
fun VerticalDivider(
	modifier: Modifier = Modifier,
	color: Color = DividerDefaults.color,
	thickness: Dp = DividerDefaults.Thickness
) {
	Divider(
		modifier = modifier
			.fillMaxHeight()
			.width(thickness),
		color = color
	)
}


@Preview(name = "VerticalDivider")
@Composable
private fun PreviewVerticalDivider() {
	VerticalDivider(color = Color.Red)
}