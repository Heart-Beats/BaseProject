package com.hl.baseproject.compose.widgets

import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.tooling.preview.Preview
import coil.compose.AsyncImage
import com.hl.baseproject.R

/**
 *
 *  智能 Image: 预览时使用 compose 自带的 Image，运行时采用 coil 的 AsyncImage（其无法进行直接预览）
 *
 * @param imageModel         图片的路径，可为资源 id
 * @param imageDescription   图片的描述
 */
@Composable
fun SmartImage(
	imageModel: Any,
	imageDescription: String = "",
	modifier: Modifier = Modifier
) {
	if (LocalView.current.isInEditMode) {
		val imageResource = if (imageModel is Int) imageModel else 0

		Image(
			painter = BitmapPainter(ImageBitmap.imageResource(imageResource)),
			contentDescription = imageDescription,
			contentScale = ContentScale.Crop,
			modifier = modifier
		)

	} else {
		AsyncImage(
			model = imageModel,
			contentDescription = imageDescription,
			contentScale = ContentScale.Crop,
			modifier = modifier
		)
	}
}

@Preview(name = "SmartImage")
@Composable
private fun PreviewSmartImage() {
	SmartImage(R.mipmap.app_logo_origin)
}