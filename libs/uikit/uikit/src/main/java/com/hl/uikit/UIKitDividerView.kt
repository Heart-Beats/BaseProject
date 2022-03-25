package com.hl.uikit

import android.content.Context
import android.content.res.Resources
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.DashPathEffect
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import androidx.annotation.ColorInt


class UIKitDividerView : View {

    private val paint = Paint()

    private var dividerColor: Int = Color.WHITE
    private var dividerHeight: Float = 1F
    private var dividerLineType: DividerLineType = DividerLineType.SOLID_PATH
    private var dashWidth: Float = 0F
    private var dashSpaceWidth: Float = 0F

    private val density: Float = Resources.getSystem().displayMetrics.density

    enum class DividerLineType(val code: Int) {
        SOLID_PATH(0), DASH_PATH(1);

        companion object {
            fun createByCode(code: Int): DividerLineType {
                for (value in values()) {
                    if (value.code == code) {
                        return value
                    }
                }
                return SOLID_PATH
            }
        }
    }


    constructor(context: Context) : super(context) {
        init(null, 0)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(attrs, 0)
    }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle) {
        dividerHeight *= density
        dashWidth *= density
        dashSpaceWidth *= density

        init(attrs, defStyle)
    }

    private fun init(attrs: AttributeSet?, defStyle: Int) {
        val ta = context.obtainStyledAttributes(
            attrs, R.styleable.UIKitDividerView, defStyle, 0
        )

        dividerColor = ta.getColor(R.styleable.UIKitDividerView_uikit_dividerColor, dividerColor)
        dividerHeight = ta.getDimension(R.styleable.UIKitDividerView_uikit_dividerHeight, dividerHeight)
        dividerLineType = DividerLineType.createByCode(ta.getInteger(R.styleable.UIKitDividerView_uikit_dividerLineType, 0))
        dashWidth = ta.getDimension(R.styleable.UIKitDividerView_uikit_dashWidth, dividerHeight)
        dashSpaceWidth = ta.getDimension(R.styleable.UIKitDividerView_uikit_dashSpaceWidth, dividerHeight)

        ta.recycle()

        initPaint()
    }

    private fun initPaint() {
        paint.isAntiAlias = true
        paint.style = Paint.Style.FILL
        paint.color = dividerColor
        paint.strokeWidth = dividerHeight
        if (dividerLineType == DividerLineType.DASH_PATH) {
            paint.pathEffect = DashPathEffect(floatArrayOf(dashWidth, dashSpaceWidth), 0F)
        }
    }


    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val width = MeasureSpec.getSize(widthMeasureSpec)
        setMeasuredDimension(width, dividerHeight.toInt())
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val y = dividerHeight / 2
        canvas.drawLine(0F, y, width.toFloat(), y, paint)
    }

    fun setDividerLineType(dividerLineType: DividerLineType, dashWidth: Float = 0F, dashSpaceWidth: Float = 0F) {
        if (dividerLineType == DividerLineType.DASH_PATH) {
            paint.pathEffect = DashPathEffect(floatArrayOf(dashWidth * density, dashSpaceWidth * density), 0F)
        } else {
            paint.pathEffect = null
        }

        invalidate()
    }

    fun setDividerColor(@ColorInt color: Int) {
        paint.color = color

        invalidate()
    }

    fun setDividerHeight(height: Int) {
        paint.strokeWidth = height * density

        invalidate()
    }

}