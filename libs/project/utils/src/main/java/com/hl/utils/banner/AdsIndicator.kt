package com.hl.utils.banner

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import androidx.annotation.Nullable
import com.youth.banner.indicator.BaseIndicator

/**
 * @author  张磊  on  2022/09/02 at 18:00
 * Email: 913305160@qq.com
 */
class AdsIndicator @JvmOverloads constructor(
	context: Context,
	@Nullable attrs: AttributeSet? = null,
	defStyleAttr: Int = 0
) : BaseIndicator(context, attrs, defStyleAttr) {

	override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec)
		val count = config.indicatorSize
		if (count <= 1)
			return

		//间距*（总数-1）+ 默认宽度*(总数-1) + 选择的宽度
		val indicatorWidth =
			(count - 1) * config.indicatorSpace + config.normalWidth * (count - 1) + config.selectedWidth
		// val width = View.resolveSize(indicatorWidth, widthMeasureSpec)
		// val height = View.resolveSize(config.height, heightMeasureSpec)
		val height = config.height
		setMeasuredDimension(indicatorWidth, height)
	}

	override fun onDraw(canvas: Canvas) {
		super.onDraw(canvas)
		val count = config.indicatorSize
		if (count <= 1)
			return

		//记录指示器距离最左端的的距离
		var distanceToLeft = 0f
		for (position in 0 until count) {
			var right: Float
			var temp: Int
			when (position) {
				config.currentPosition -> {
					mPaint.color = config.selectedColor
					right = distanceToLeft + config.selectedWidth
					temp = config.selectedWidth + config.indicatorSpace
				}
				else -> {
					mPaint.color = config.normalColor
					right = distanceToLeft + config.normalWidth
					temp = config.normalWidth + config.indicatorSpace
				}
			}

			canvas.drawRoundRect(
				distanceToLeft,
				0f,
				right,
				config.height.toFloat(),
				config.radius.toFloat(),
				config.radius.toFloat(),
				mPaint
			)
			distanceToLeft += temp
		}
	}
}