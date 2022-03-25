package com.hl.uikit.actionsheet

import android.content.Context
import android.graphics.Point
import android.util.AttributeSet
import android.view.WindowManager
import android.widget.FrameLayout
import com.hl.uikit.R

class UIKitActionSheetLayout : FrameLayout {
    private var mMaxHeight: Int? = null
    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : this(
        context,
        attrs,
        defStyleAttr, 0
    )

    constructor(
        context: Context,
        attrs: AttributeSet?,
        defStyleAttr: Int,
        defStyleRes: Int
    ) : super(context, attrs, defStyleAttr, defStyleRes) {
        val ta = context.obtainStyledAttributes(
            attrs,
            R.styleable.UIKitActionSheetLayout,
            defStyleAttr,
            defStyleRes
        )
        val screenHeight = getScreenHeight()
        mMaxHeight = ta.getFraction(
            R.styleable.UIKitActionSheetLayout_uikit_maxScrHeightPer,
            screenHeight,
            screenHeight,
            screenHeight.toFloat()
        ).toInt()
        ta.recycle()
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
    }

    private fun getScreenHeight(): Int {
        val manager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager?
        val point = Point()
        manager?.defaultDisplay?.getSize(point)
        return point.y
    }
}