package com.hl.baseproject.compose.widgets

import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import coil.compose.AsyncImage
import com.hl.baseproject.R
import com.hl.baseproject.compose.AppComposeTheme

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
		val context = LocalContext.current
		val appIconId: Int = try {
			context.packageManager.getApplicationInfo(context.packageName, 0).icon
		} catch (e: Exception) {
			R.mipmap.app_logo_origin
		}

		val imageResource = if (imageModel is Int) imageModel else appIconId

		Image(
			painter = painterResource(imageResource),
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
	AppComposeTheme {
		SmartImage("")
	}
}