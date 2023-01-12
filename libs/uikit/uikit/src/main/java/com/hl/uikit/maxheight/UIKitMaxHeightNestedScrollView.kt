package com.hl.uikit.maxheight

import android.content.Context
import android.util.AttributeSet
import androidx.core.widget.NestedScrollView
import com.hl.uikit.R
import com.hl.uikit.utils.getScreenHeight

/**
 * @author  张磊  on  2023/01/12 at 15:41
 * Email: 913305160@qq.com
 */
class UIKitMaxHeightNestedScrollView : NestedScrollView {
	private var mMaxHeight: Int? = null

	constructor(context: Context) : this(context, null)

	constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

	constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
		init(context, attrs, defStyleAttr)
	}

	private fun init(context: Context, attrs: AttributeSet?, defStyleAttr: Int) {
		val screenHeight = getScreenHeight()
		val ta = context.obtainStyledAttributes(attrs, R.styleable.UIKitMaxHeightNestedScrollView, defStyleAttr, 0)

		ta.also {
			mMaxHeight = it.getFraction(
				R.styleable.UIKitMaxHeightNestedScrollView_uikit_maxScreenHeightPercent,
				screenHeight,
				screenHeight,
				screenHeight.toFloat()
			).toInt()
		}.recycle()
	}

	override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
		if (mMaxHeight == null) {
			super.onMeasure(widthMeasureSpec, heightMeasureSpec)
			return
		}
		val maxHeight = mMaxHeight!!
		val heightSize = MeasureSpec.getSize(heightMeasureSpec)
		if (maxHeight <= heightSize) {
			super.onMeasure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(maxHeight, MeasureSpec.AT_MOST))
		} else {
			super.onMeasure(widthMeasureSpec, heightMeasureSpec)
		}
	}

	fun setMaxHeight(maxHeight: Int) {
		mMaxHeight = maxHeight
		requestLayout()
	}
}