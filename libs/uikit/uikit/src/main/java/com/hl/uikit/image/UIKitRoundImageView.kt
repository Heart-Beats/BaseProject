package com.hl.uikit.image

import android.content.Context
import android.graphics.*
import android.os.Build
import android.util.AttributeSet
import androidx.annotation.ColorInt
import androidx.annotation.Px
import androidx.appcompat.widget.AppCompatImageView
import com.hl.uikit.R
import com.hl.uikit.utils.dpInt

class UIKitRoundImageView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : AppCompatImageView(
    context, attrs, defStyleAttr
) {
    /**
     *  是否显示为圆形，如果为圆形则设置的corner无效
     */
    private var isCircle = false

    /**
     * border、inner_border是否覆盖图片
     */
    private var isCoverSrc = true

    /**
     * 边框宽度
     */
    private var borderWidth = 0.dpInt

    /**
     * 边框颜色
     */
    private var borderColor = Color.WHITE

    /**
     * 内层边框宽度
     */
    private var innerBorderWidth = 0.dpInt

    /**
     * 内层边框充色
     */
    private var innerBorderColor = Color.WHITE

    /**
     * 统一设置圆角半径，优先级高于单独设置每个角的半径
     */
    private var cornerRadius = 0.dpInt

    /**
     * 左上角圆角半径
     */
    private var cornerTopLeftRadius = 0.dpInt

    /**
     * 右上角圆角半径
     */
    private var cornerTopRightRadius = 0.dpInt

    /**
     * 左下角圆角半径
     */
    private var cornerBottomLeftRadius = 0.dpInt

    /**
     * 右下角圆角半径
     */
    private var cornerBottomRightRadius = 0.dpInt

    /**
     * 遮罩颜色
     */
    private var maskColor = 0

    private var xfermode: Xfermode? = null

    private var currentWidth = 0
    private var currentHeight = 0
    private var radius = 0f
    private val borderRadii: FloatArray
    private val srcRadii: FloatArray

    /**
     *  图片占的矩形区域
     */
    private var srcRectF: RectF

    /**
     * 边框的矩形区域
     */
    private val borderRectF: RectF

    private val paint: Paint

    /**
     *  用来裁剪图片的ptah
     */
    private val path: Path

    /**
     * 图片区域大小的path
     */
    private var srcPath: Path? = null

    init {
        val ta = context.obtainStyledAttributes(attrs, R.styleable.UIKitRoundImageView, 0, 0)
        for (i in 0 until ta.indexCount) {
            when (val attr = ta.getIndex(i)) {
                R.styleable.UIKitRoundImageView_uikit_is_cover_src -> {
                    isCoverSrc = ta.getBoolean(attr, isCoverSrc)
                }
                R.styleable.UIKitRoundImageView_uikit_is_circle -> {
                    isCircle = ta.getBoolean(attr, isCircle)
                }
                R.styleable.UIKitRoundImageView_uikit_border_width -> {
                    borderWidth = ta.getDimensionPixelSize(attr, borderWidth)
                }
                R.styleable.UIKitRoundImageView_uikit_border_color -> {
                    borderColor = ta.getColor(attr, borderColor)
                }
                R.styleable.UIKitRoundImageView_uikit_inner_border_width -> {
                    innerBorderWidth = ta.getDimensionPixelSize(attr, innerBorderWidth)
                }
                R.styleable.UIKitRoundImageView_uikit_inner_border_color -> {
                    innerBorderColor = ta.getColor(attr, innerBorderColor)
                }
                R.styleable.UIKitRoundImageView_uikit_corner_radius -> {
                    cornerRadius = ta.getDimensionPixelSize(attr, cornerRadius)
                }
                R.styleable.UIKitRoundImageView_uikit_corner_top_left_radius -> {
                    cornerTopLeftRadius = ta.getDimensionPixelSize(attr, cornerTopLeftRadius)
                }
                R.styleable.UIKitRoundImageView_uikit_corner_top_right_radius -> {
                    cornerTopRightRadius = ta.getDimensionPixelSize(attr, cornerTopRightRadius)
                }
                R.styleable.UIKitRoundImageView_uikit_corner_bottom_left_radius -> {
                    cornerBottomLeftRadius = ta.getDimensionPixelSize(attr, cornerBottomLeftRadius)
                }
                R.styleable.UIKitRoundImageView_uikit_corner_bottom_right_radius -> {
                    cornerBottomRightRadius = ta.getDimensionPixelSize(attr, cornerBottomRightRadius)
                }
                R.styleable.UIKitRoundImageView_uikit_mask_color -> {
                    maskColor = ta.getColor(attr, maskColor)
                }
            }
        }
        ta.recycle()
        borderRadii = FloatArray(8)
        srcRadii = FloatArray(8)
        borderRectF = RectF()
        srcRectF = RectF()
        paint = Paint()
        path = Path()
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.O_MR1) {
            xfermode = PorterDuffXfermode(PorterDuff.Mode.DST_IN)
        } else {
            xfermode = PorterDuffXfermode(PorterDuff.Mode.DST_OUT)
            srcPath = Path()
        }
        calculateRadii()
        clearInnerBorderWidth()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        currentWidth = w
        currentHeight = h
        initBorderRectF()
        initSrcRectF()
    }

    override fun onDraw(canvas: Canvas) {
        // 使用图形混合模式来显示指定区域的图片
        canvas.saveLayer(srcRectF, null, Canvas.ALL_SAVE_FLAG)
        if (!isCoverSrc) {
            val sx = 1.0f * (currentWidth - 2 * borderWidth - 2 * innerBorderWidth) / currentWidth
            val sy = 1.0f * (currentHeight - 2 * borderWidth - 2 * innerBorderWidth) / currentHeight
            // 缩小画布，使图片内容不被borders覆盖
            canvas.scale(sx, sy, currentWidth / 2.0f, currentHeight / 2.0f)
        }
        super.onDraw(canvas)
        paint.reset()
        path.reset()
        if (isCircle) {
            path.addCircle(currentWidth / 2.0f, currentHeight / 2.0f, radius, Path.Direction.CCW)
        } else {
            path.addRoundRect(srcRectF, srcRadii, Path.Direction.CCW)
        }
        paint.isAntiAlias = true
        paint.style = Paint.Style.FILL
        paint.xfermode = xfermode
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.O_MR1) {
            canvas.drawPath(path, paint)
        } else {
            srcPath!!.addRect(srcRectF, Path.Direction.CCW)
            // 计算tempPath和path的差集
            srcPath!!.op(path, Path.Op.DIFFERENCE)
            canvas.drawPath(srcPath!!, paint)
            srcPath!!.reset()
        }
        paint.xfermode = null

        // 绘制遮罩
        if (maskColor != 0) {
            paint.color = maskColor
            canvas.drawPath(path, paint)
        }
        // 恢复画布
        canvas.restore()
        // 绘制边框
        drawBorders(canvas)
    }

    private fun drawBorders(canvas: Canvas) {
        if (isCircle) {
            if (borderWidth > 0) {
                drawCircleBorder(canvas, borderWidth, borderColor, radius - borderWidth / 2.0f)
            }
            if (innerBorderWidth > 0) {
                drawCircleBorder(
                    canvas,
                    innerBorderWidth,
                    innerBorderColor,
                    radius - borderWidth - innerBorderWidth / 2.0f
                )
            }
        } else {
            if (borderWidth > 0) {
                drawRectFBorder(canvas, borderWidth, borderColor, borderRectF, borderRadii)
            }
        }
    }

    private fun drawCircleBorder(canvas: Canvas, borderWidth: Int, borderColor: Int, radius: Float) {
        initBorderPaint(borderWidth, borderColor)
        path.addCircle(currentWidth / 2.0f, currentHeight / 2.0f, radius, Path.Direction.CCW)
        canvas.drawPath(path, paint)
    }

    private fun drawRectFBorder(canvas: Canvas, borderWidth: Int, borderColor: Int, rectF: RectF, radii: FloatArray) {
        initBorderPaint(borderWidth, borderColor)
        path.addRoundRect(rectF, radii, Path.Direction.CCW)
        canvas.drawPath(path, paint)
    }

    private fun initBorderPaint(borderWidth: Int, borderColor: Int) {
        path.reset()
        paint.strokeWidth = borderWidth.toFloat()
        paint.color = borderColor
        paint.style = Paint.Style.STROKE
    }

    /**
     * 计算外边框的RectF
     */
    private fun initBorderRectF() {
        if (!isCircle) {
            borderRectF[borderWidth / 2.0f, borderWidth / 2.0f, currentWidth - borderWidth / 2.0f] =
                currentHeight - borderWidth / 2.0f
        }
    }

    /**
     * 计算图片原始区域的RectF
     */
    private fun initSrcRectF() {
        if (isCircle) {
            radius = currentWidth.coerceAtMost(currentHeight) / 2.0f
            srcRectF[currentWidth / 2.0f - radius, currentHeight / 2.0f - radius, currentWidth / 2.0f + radius] =
                currentHeight / 2.0f + radius
        } else {
            srcRectF[0f, 0f, currentWidth.toFloat()] = currentHeight.toFloat()
            if (isCoverSrc) {
                srcRectF = borderRectF
            }
        }
    }

    /**
     * 计算RectF的圆角半径
     */
    private fun calculateRadii() {
        if (isCircle) {
            return
        }
        if (cornerRadius > 0) {
            for (i in borderRadii.indices) {
                borderRadii[i] = cornerRadius.toFloat()
                srcRadii[i] = cornerRadius - borderWidth / 2.0f
            }
        } else {
            borderRadii[1] = cornerTopLeftRadius.toFloat()
            borderRadii[0] = borderRadii[1]
            borderRadii[3] = cornerTopRightRadius.toFloat()
            borderRadii[2] = borderRadii[3]
            borderRadii[5] = cornerBottomRightRadius.toFloat()
            borderRadii[4] = borderRadii[5]
            borderRadii[7] = cornerBottomLeftRadius.toFloat()
            borderRadii[6] = borderRadii[7]
            srcRadii[1] = cornerTopLeftRadius - borderWidth / 2.0f
            srcRadii[0] = srcRadii[1]
            srcRadii[3] = cornerTopRightRadius - borderWidth / 2.0f
            srcRadii[2] = srcRadii[3]
            srcRadii[5] = cornerBottomRightRadius - borderWidth / 2.0f
            srcRadii[4] = srcRadii[5]
            srcRadii[7] = cornerBottomLeftRadius - borderWidth / 2.0f
            srcRadii[6] = srcRadii[7]
        }
    }

    private fun calculateRadiiAndRectF(reset: Boolean) {
        if (reset) {
            cornerRadius = 0
        }
        calculateRadii()
        initBorderRectF()
        invalidate()
    }

    /**
     * 目前圆角矩形情况下不支持inner_border，需要将其置0
     */
    private fun clearInnerBorderWidth() {
        if (!isCircle) {
            innerBorderWidth = 0
        }
    }

    fun isCoverSrc(isCoverSrc: Boolean) {
        this.isCoverSrc = isCoverSrc
        initSrcRectF()
        invalidate()
    }

    fun isCircle(isCircle: Boolean) {
        this.isCircle = isCircle
        clearInnerBorderWidth()
        initSrcRectF()
        invalidate()
    }

    fun setBorderWidth(@Px borderWidth: Int) {
        this.borderWidth = borderWidth
        calculateRadiiAndRectF(false)
    }

    fun setBorderColor(@ColorInt borderColor: Int) {
        this.borderColor = borderColor
        invalidate()
    }

    fun setInnerBorderWidth(@Px innerBorderWidth: Int) {
        this.innerBorderWidth = innerBorderWidth
        clearInnerBorderWidth()
        invalidate()
    }

    fun setInnerBorderColor(@ColorInt innerBorderColor: Int) {
        this.innerBorderColor = innerBorderColor
        invalidate()
    }

    fun setCornerRadius(@Px cornerRadius: Int) {
        this.cornerRadius = cornerRadius
        calculateRadiiAndRectF(false)
    }

    fun setCornerTopLeftRadius(@Px cornerTopLeftRadius: Int) {
        this.cornerTopLeftRadius = cornerTopLeftRadius
        calculateRadiiAndRectF(true)
    }

    fun setCornerTopRightRadius(@Px cornerTopRightRadius: Int) {
        this.cornerTopRightRadius = cornerTopRightRadius
        calculateRadiiAndRectF(true)
    }

    fun setCornerBottomLeftRadius(@Px cornerBottomLeftRadius: Int) {
        this.cornerBottomLeftRadius = cornerBottomLeftRadius
        calculateRadiiAndRectF(true)
    }

    fun setCornerBottomRightRadius(@Px cornerBottomRightRadius: Int) {
        this.cornerBottomRightRadius = cornerBottomRightRadius
        calculateRadiiAndRectF(true)
    }

    fun setMaskColor(@ColorInt maskColor: Int) {
        this.maskColor = maskColor
        invalidate()
    }
}