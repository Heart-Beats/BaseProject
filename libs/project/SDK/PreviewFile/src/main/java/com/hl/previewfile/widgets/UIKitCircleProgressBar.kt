package com.hl.previewfile.widgets

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import com.hl.previewfile.R

/**
 * @author 张磊  on  2022/04/13 at 14:59
 * Email: 913305160@qq.com
 */
internal class UIKitCircleProgressBar @JvmOverloads constructor(
	context: Context,
	attrs: AttributeSet? = null,
	defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

	private val mPaint: Paint
	private val mRect: RectF
	private var mProgress = 0.0f
	private var mMaxProgress = 100.0f

	var circleColor: Int
	var circleWidth: Int
	var bgCircleWidth: Int
	var bgCircleColor: Int

	var progressBarListener: UIKitCircleProgressBarListener? = null

	var progress: Float
		get() = mProgress
		set(progress) {
			mProgress = Math.max(progress, 0.0f)
			progressBarListener?.onProgressChanged(mProgress)
			this.invalidate()
		}

	var maxProgress: Float
		get() = mMaxProgress
		set(maxProgress) {
			mMaxProgress = if (maxProgress < 0.0f) 100.0f else mMaxProgress
			this.invalidate()
		}


	init {
		circleColor = Color.CYAN
		circleWidth = 20
		bgCircleWidth = 20
		bgCircleColor = Color.TRANSPARENT
		mPaint = Paint()
		mPaint.style = Paint.Style.STROKE
		mPaint.strokeCap = Paint.Cap.ROUND
		mPaint.isAntiAlias = true
		mRect = RectF()
		init(attrs)
	}

	private fun init(attrs: AttributeSet?) {
		val a = this.context.obtainStyledAttributes(attrs, R.styleable.UIKitCircleProgressBar)
		circleWidth = a.getDimensionPixelOffset(R.styleable.UIKitCircleProgressBar_uikit_cpb_width, circleWidth)
		circleColor = a.getColor(R.styleable.UIKitCircleProgressBar_uikit_cpb_color, circleColor)
		bgCircleColor = a.getColor(R.styleable.UIKitCircleProgressBar_uikit_cpb_background, bgCircleColor)
		mProgress = a.getFloat(R.styleable.UIKitCircleProgressBar_uikit_cpb_progress, mProgress)
		mMaxProgress = a.getFloat(R.styleable.UIKitCircleProgressBar_uikit_cpb_max_progress, mMaxProgress)
		bgCircleWidth =
			a.getDimensionPixelOffset(R.styleable.UIKitCircleProgressBar_uikit_cpb_background_width, bgCircleWidth)
		a.recycle()
	}

	override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
		super.onSizeChanged(w, h, oldw, oldh)
		mRect.left = (this.paddingLeft + circleWidth / 2).toFloat()
		mRect.top = (this.paddingTop + circleWidth / 2).toFloat()
		mRect.right = (w - this.paddingRight - circleWidth / 2).toFloat()
		mRect.bottom = (h - this.paddingBottom - circleWidth / 2).toFloat()
	}

	override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec)
		val width = resolveSize(MeasureSpec.getSize(widthMeasureSpec), widthMeasureSpec)
		val height = resolveSize(MeasureSpec.getSize(heightMeasureSpec), heightMeasureSpec)
		setMeasuredDimension(width, height)
	}

	override fun onDraw(canvas: Canvas) {
		super.onDraw(canvas)
		val degree = 360.0f * mProgress / mMaxProgress
		mPaint.strokeWidth = bgCircleWidth.toFloat()
		mPaint.color = bgCircleColor
		canvas.drawArc(mRect, 0.0f, 360.0f, false, mPaint)
		mPaint.strokeWidth = circleWidth.toFloat()
		mPaint.color = circleColor
		canvas.drawArc(mRect, -90.0f, if (degree <= 0.0f) 1.0f else degree, false, mPaint)
	}

	interface UIKitCircleProgressBarListener {
		/**
		 * 进度改变
		 *
		 * @param progress 进度
		 */
		fun onProgressChanged(progress: Float)
	}
}