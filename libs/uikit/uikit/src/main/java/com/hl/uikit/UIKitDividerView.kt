package com.hl.uikit

import android.content.Context
import android.content.res.Resources
import android.graphics.Canvas
import android.graphics.DashPathEffect
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import androidx.annotation.ColorInt
import androidx.core.content.ContextCompat


class UIKitDividerView : View {

    private companion object {
        const val ORIENTATION_HORIZONTAL = 0
    }

    private val paint = Paint()

    private var dividerColor: Int = ContextCompat.getColor(context, R.color.uikit_dividerColor)
    private var dividerThickness: Float = 1F
    private var dividerLineType: DividerLineType = DividerLineType.SOLID_PATH
    private var dashWidth: Float = 0F
    private var dashSpaceWidth: Float = 0F
    private var dividerOrientation = ORIENTATION_HORIZONTAL

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


    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, R.attr.uikit_dividerViewStyle)

    constructor(context: Context, attrs: AttributeSet?, defStyle: Int) : super(context, attrs, defStyle) {
        dividerThickness *= density
        dashWidth *= density
        dashSpaceWidth *= density

        init(attrs, defStyle)
    }

    private fun init(attrs: AttributeSet?, defStyle: Int) {
        val ta = context.obtainStyledAttributes(
            attrs, R.styleable.UIKitDividerView, defStyle, 0
        )

        dividerColor = ta.getColor(R.styleable.UIKitDividerView_uikit_dividerColor, dividerColor)
        dividerThickness = ta.getDimension(R.styleable.UIKitDividerView_uikit_dividerThickness, dividerThickness)
        dividerLineType =
            DividerLineType.createByCode(ta.getInteger(R.styleable.UIKitDividerView_uikit_dividerLineType, 0))
        dashWidth = ta.getDimension(R.styleable.UIKitDividerView_uikit_dashWidth, dividerThickness)
        dashSpaceWidth = ta.getDimension(R.styleable.UIKitDividerView_uikit_dashSpaceWidth, dividerThickness)
        dividerOrientation = ta.getInt(R.styleable.UIKitDividerView_uikit_dividerOrientation, dividerOrientation)

        ta.recycle()

        initPaint()
    }

    private fun initPaint() {
        paint.isAntiAlias = true
        paint.style = Paint.Style.FILL
        paint.color = dividerColor

        // 画笔宽度等于分割线高度
        paint.strokeWidth = dividerThickness
        if (dividerLineType == DividerLineType.DASH_PATH) {
            paint.pathEffect = DashPathEffect(floatArrayOf(dashWidth, dashSpaceWidth), 0F)
        }
    }


    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val originWidth = MeasureSpec.getSize(widthMeasureSpec)
        val originHeight = MeasureSpec.getSize(heightMeasureSpec)

        if (dividerOrientation == ORIENTATION_HORIZONTAL) {
            val height = resolveSize(dividerThickness.toInt(), heightMeasureSpec)
            setMeasuredDimension(originWidth, height)
        } else {
            val width = resolveSize(dividerThickness.toInt(), widthMeasureSpec)
            setMeasuredDimension(width, originHeight)
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        // 绘制的左边缘
        val drawStart = paddingStart.toFloat()
        // 绘制的右边缘
        val drawEnd = width.toFloat() - paddingEnd.toFloat()
        // 绘制的上边缘
        val drawTop = paddingTop.toFloat()
        // 绘制的下边缘
        val drawBottom = height - paddingBottom.toFloat()

        // 绘制的总宽度
        val drawWidth = drawEnd - drawStart
        // 绘制的总高度
        val drawHeight = drawBottom - drawTop


        val drawLineCenter = dividerThickness / 2
        if (dividerOrientation == ORIENTATION_HORIZONTAL) {
            val drawCenterY = drawTop + drawLineCenter
            canvas.drawLine(drawStart, drawCenterY, drawStart + drawWidth, drawCenterY, paint)
        } else {
            val drawCenterX = drawStart + drawLineCenter
            canvas.drawLine(drawCenterX, drawTop, drawCenterX, drawTop + drawHeight, paint)
        }
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