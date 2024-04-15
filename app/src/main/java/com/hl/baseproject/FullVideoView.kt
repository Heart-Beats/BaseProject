package com.hl.baseproject

import android.content.Context
import android.util.AttributeSet
import android.widget.VideoView


/**
 * @author  张磊  on  2024/03/08 at 20:54
 * Email: 913305160@qq.com
 */

class FullVideoView : VideoView {

	constructor(context: Context) : super(context)
	constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
	constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

	override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
		// 其实就是在这里做了一些处理, 修改为全屏播放
		val width = getDefaultSize(0, widthMeasureSpec)
		val height = getDefaultSize(0, heightMeasureSpec)
		setMeasuredDimension(width, height)
	}
}