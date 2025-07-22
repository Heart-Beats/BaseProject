package com.hl.uikit.utils

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.widget.ImageView
import androidx.annotation.ColorInt
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.math.min

/**
 * @author  张磊  on  2025/05/23 at 17:00
 * Email: 913305160@qq.com
 */

/**
 * 为 ImageView 设置包含指定文本的图像。
 * 该方法会在 ImageView 绘制完成后，在后台线程生成一个包含指定文本的圆形图像，
 * 并将生成的图像设置给 ImageView。
 *
 * @param text 要显示在图像中心的文本。
 * @param textColor 文本的颜色，使用 @ColorInt 注解确保传入有效的颜色值。
 * @param bgColor 背景的颜色，使用 @ColorInt 注解确保传入有效的颜色值。
 */
fun ImageView.setTextImage(text: String, @ColorInt textColor: Int, @ColorInt bgColor: Int, @Px textSize: Float = -1F) {
	this.post {
		MainScope().launch {
			val imageView = this@setTextImage
			val bitmap = withContext(Dispatchers.IO) {
				val bitmap = createBitmap(imageView.width, imageView.height)
				val canvas = Canvas(bitmap)

				// 绘制背景
				canvas.drawColor(bgColor)

				val paint = Paint()
				paint.isAntiAlias = true
				// 绘制文本
				paint.color = textColor
				paint.textSize = if(textSize == -1F) (min(imageView.width, imageView.height) * 0.5).toFloat() else textSize
				paint.textAlign = Paint.Align.CENTER
				val xPos = (canvas.width / 2).toFloat()
				val yPos = (canvas.height / 2 - (paint.descent() + paint.ascent()) / 2)
				canvas.drawText(text, xPos, yPos, paint)
				bitmap
			}

			imageView.setImageBitmap(bitmap)
		}
	}
}
