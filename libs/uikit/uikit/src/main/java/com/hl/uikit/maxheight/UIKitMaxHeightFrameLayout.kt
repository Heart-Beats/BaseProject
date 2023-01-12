package com.hl.uikit.maxheight

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import com.hl.uikit.R
import com.hl.uikit.utils.getScreenHeight

class UIKitMaxHeightFrameLayout : FrameLayout {
    private var mMaxHeight: Int? = null

    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : this(context, attrs, defStyleAttr, 0)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) : super(
        context,
        attrs,
        defStyleAttr,
        defStyleRes
    ) {
        init(context, attrs, defStyleAttr, defStyleRes)
    }

    private fun init(context: Context, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) {
        val screenHeight = getScreenHeight()
        val ta = context.obtainStyledAttributes(attrs, R.styleable.UIKitMaxHeightFrameLayout, defStyleAttr, defStyleRes)

        ta.also {
            mMaxHeight = it.getFraction(
                R.styleable.UIKitMaxHeightFrameLayout_uikit_maxScreenHeightPercent,
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