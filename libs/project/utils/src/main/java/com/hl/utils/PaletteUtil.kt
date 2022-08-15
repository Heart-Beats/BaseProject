package com.hl.utils

import android.graphics.Bitmap
import androidx.core.graphics.ColorUtils
import androidx.palette.graphics.Palette

/**
 * @author  张磊  on  2022/08/15 at 15:41
 * Email: 913305160@qq.com
 */

/**
 * 取色板工具类
 */
object PaletteUtil {

	@JvmStatic
	fun getColorFromBitmap(bitmap: Bitmap, onPaletteColorParse: OnPaletteColorParse) {
		val colorCount = 5
		Palette
			.from(bitmap)
			.maximumColorCount(colorCount)
			.setRegion(0, 0, bitmap.width, bitmap.height)
			.generate {
				it ?: return@generate

				//获取某种特性颜色的样品
				var mostPopularSwatch: Palette.Swatch? = null
				for (swatch in it.swatches) {
					if (mostPopularSwatch == null || swatch.population > mostPopularSwatch.population) {
						mostPopularSwatch = swatch
					}
				}

				mostPopularSwatch = it.dominantSwatch

				//谷歌推荐的：图片的整体的颜色rgb的混合值---主色调
				val rgb = mostPopularSwatch?.rgb ?: return@generate
				//谷歌推荐：图片中间的文字颜色
				val bodyTextColor = mostPopularSwatch.bodyTextColor
				// 谷歌推荐：作为标题的颜色（有一定的和图片的对比度的颜色值）
				val titleTextColor = mostPopularSwatch.titleTextColor

				// 获取颜色值的亮度, 越大越亮， 颜色越浅
				val isLight = ColorUtils.calculateLuminance(rgb) > 0.5

				onPaletteColorParse(rgb, bodyTextColor, titleTextColor, isLight)
			}
	}
}

typealias OnPaletteColorParse = (rgb: Int, bodyTextColor: Int, titleTextColor: Int, isLight: Boolean) -> Unit