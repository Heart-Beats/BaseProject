package com.hl.uikit.image

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import androidx.annotation.ColorInt
import androidx.appcompat.widget.AppCompatImageView
import com.google.android.material.imageview.ShapeableImageView
import com.hl.uikit.R
import kotlin.math.pow
import kotlin.math.sqrt

/**
 * @author  张磊  on  2022/02/12 at 13:47
 * Email: 913305160@qq.com
 */
class UIKitProgressImageView : ShapeableImageView {

    var progress: Int = 0
        set(value) {
            field = value
            invalidate()
        }

    private val paint = Paint()
    private lateinit var progressAreaRect: RectF

    @ColorInt
    private var progressAreaColor: Int = Color.parseColor("#FF272E4B")

    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, R.attr.uikit_progressImageViewStyle)

    constructor(context: Context, attrs: AttributeSet?, defStyle: Int) : super(context, attrs, defStyle) {
        init(attrs, defStyle)
    }

    private fun init(attrs: AttributeSet?, defStyle: Int) {
        val ta = context.obtainStyledAttributes(
            attrs, R.styleable.UIKitProgressImageView, defStyle, 0
        )

        ta.also {
            progressAreaColor = it.getColor(
                R.styleable.UIKitProgressImageView_uikit_progressAreaColor,
                progressAreaColor
            )

            progress = it.getColor(
                R.styleable.UIKitProgressImageView_uikit_progress,
                progress
            )
        }.recycle()

        initPaint()
    }

    private fun initPaint() {
        paint.isAntiAlias = true
        // 60% 透明度
        paint.color =
            Color.argb(
                (255 * 0.6).toInt(),
                Color.red(progressAreaColor),
                Color.green(progressAreaColor),
                Color.blue(progressAreaColor)
            )
        paint.style = Paint.Style.FILL
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        // 大小改变时计算进度区域矩形
        progressAreaRect = getProgressAreaRect(w, h)
    }

    private fun getProgressAreaRect(width: Int, height: Int): RectF {
        //  得到进度区域半径
        val progressAreaRadius = sqrt(width.toDouble().pow(2.0) + height.toDouble().pow(2.0)) / 2

        val centerX = width.toFloat() / 2
        val centerY = height.toFloat() / 2

        return RectF(
            (centerX - progressAreaRadius).toFloat(),
            (centerY - progressAreaRadius).toFloat(),
            (centerX + progressAreaRadius).toFloat(),
            (centerY + progressAreaRadius).toFloat()
        )
    }


    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        drawProgressArea(canvas, progressAreaRect, progress)
    }

    private fun drawProgressArea(canvas: Canvas, progressAreaRect: RectF, progress: Int) {

        //  开始的角度
        val startAngle = (progress.toFloat() / 100) * 360 - 90

        // 设置圆弧扫过的角度
        val sweepAngle = 270 - startAngle

        canvas.drawArc(progressAreaRect, startAngle, sweepAngle, true, paint)
    }
}