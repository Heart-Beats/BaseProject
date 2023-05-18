package com.hl.baseproject.compose.widgets

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.paddingFromBaseline
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.hl.baseproject.R
import com.hl.baseproject.compose.AppComposeTheme

@Composable
fun RoundImageText(
	imageModel: Any,
	imageDescription: String = "",
	imageSize: Dp = 88.dp,
	roundDp: Dp = 0.dp,
	modifier: Modifier = Modifier
) {
	RoundImageText(imageModel, imageDescription, imageSize, roundDp, false, modifier)
}

@Composable
fun CircleImageText(
	imageModel: Any,
	imageDescription: String = "",
	imageSize: Dp = 88.dp,
	modifier: Modifier = Modifier
) {
	RoundImageText(imageModel, imageDescription, imageSize, 0.dp, true, modifier)
}

/**
 * @param imageModel         图片的路径，可为资源 id
 * @param imageDescription   图片的描述
 * @param imageSize          图片的大小
 * @param roundDp            图片的圆角大小， isCircle 为 true 时该值无用
 * @param isCircle           是否为圆形图片
 */
@Composable
private fun RoundImageText(
	imageModel: Any,
	imageDescription: String = "",
	imageSize: Dp = 88.dp,
	roundDp: Dp = 0.dp,
	isCircle: Boolean = false,
	modifier: Modifier = Modifier
) {
	Column(
		horizontalAlignment = Alignment.CenterHorizontally,
		modifier = modifier
	) {

		// 设置图片的裁剪样式
		val shape = if (isCircle) CircleShape else RoundedCornerShape(roundDp)

		SmartImage(
			imageModel = imageModel,
			imageDescription = imageDescription,
			modifier = Modifier
				.size(imageSize)
				.clip(shape)
		)

		if (imageDescription.isNotBlank()) {
			Text(
				imageDescription, style = MaterialTheme.typography.titleSmall,
				modifier = Modifier.paddingFromBaseline(
					top = 24.dp, bottom = 8.dp
				)
			)
		}
	}
}

@Preview(name = "RoundImageText")
@Composable
private fun PreviewRoundImage() {
	AppComposeTheme {
		RoundImageText(R.mipmap.app_logo_origin, imageDescription = "测试图片", roundDp = 15.dp)
	}
}

@Preview(name = "CircleImageText")
@Composable
private fun PreviewCircleImageText() {
	AppComposeTheme {
		CircleImageText(R.mipmap.app_logo_origin, imageDescription = "测试图片")
	}
}