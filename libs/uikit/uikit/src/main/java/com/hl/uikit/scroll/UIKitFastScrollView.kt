package com.hl.uikit.scroll

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.animation.DecelerateInterpolator
import android.widget.ScrollView

/**
 * UIKIT 快速滚动视图, 支持拖动指示器快速定位到 ScrollView 的相对位置
 * @author ZhangLei
 * @date 2026/07/27
 */
class UIKitFastScrollView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ScrollView(context, attrs, defStyleAttr) {

    private val density = resources.displayMetrics.density
    private fun dpF(value: Float) = value * density

    // ─── 颜色 ──────────────────────────────────────────────
    /** 指示条基色（淡灰色） */
    private val thumbBaseColor  = 0xFFB0B0B0.toInt() and 0x00FFFFFF // 保留 RGB
    /** 闲置时整体透明度 40% */
    private val thumbIdleAlpha  = 102   // 255 * 0.4
    /** 按压/拖拽时整体透明度 80% */
    private val thumbDragAlpha  = 204   // 255 * 0.8

    private val thumbPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = thumbBaseColor or (thumbIdleAlpha shl 24)
        style = Paint.Style.FILL
    }

    // ─── 尺寸 ──────────────────────────────────────────────
    private val scrollerWidth   = dpF(24f)
    private val thumbDrawWidth  = dpF(8f)
    private val thumbMinSize    = dpF(35f)
    private val scrollerPad     = dpF(3f)

    // ─── 状态 ──────────────────────────────────────────────
    private var thumbTop    = 0f
    private var thumbHeight = 0f
    private var isFastScrolling = false
    private var hasUserInteracted = false
    private var initialSettleStarted = false

    // ─── 动画 ──────────────────────────────────────────────
    /** 当前动画 alpha 乘数 0..1 */
    private var animAlpha = 1f
    private var fadeAnimator: ValueAnimator? = null
    private var hideRunnable: Runnable? = null
    private var settleRunnable: Runnable? = null

    // ─── 回调 ──────────────────────────────────────────────
    private var onUserScrollListener: ((isScrolling: Boolean, scrollY: Int) -> Unit)? = null

    init {
        isVerticalScrollBarEnabled   = false
        isHorizontalScrollBarEnabled = false
        isScrollbarFadingEnabled     = false
    }

    fun setOnUserScrollListener(l: (Boolean, Int) -> Unit)   { onUserScrollListener = l }
    fun fastScrollToBottom() { post { fullScroll(FOCUS_DOWN) } }

    // ═══ 布局 & 滚动 ═══════════════════════════════════════

    override fun requestLayout() {
        super.requestLayout()
        post { recalc() }
    }

    override fun onScrollChanged(l: Int, t: Int, oldl: Int, oldt: Int) {
        super.onScrollChanged(l, t, oldl, oldt)
        if (!isFastScrolling) recalc()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        post { recalc() }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        post { recalc() }
    }

    private fun recalc() {
        val range = computeVerticalScrollRange()
        val extent = scrollExtent()
        if (range <= extent) {
            if (thumbHeight != 0f) { thumbHeight = 0f; invalidate() }
            return
        }
        val newH = maxOf(thumbMinSize, extent * (extent.toFloat() / range))
        if (thumbHeight != newH) thumbHeight = newH
        val offset = computeVerticalScrollOffset()
        thumbTop = calcTop(offset, range, extent)
        invalidate()

        // 首次内容溢出、且无用户交互时，自动启动滚动稳定检测
        // （这样进入页面自动滚到底部后，指示条也会在滚动结束时隐藏）
        if (!hasUserInteracted && !initialSettleStarted) {
            initialSettleStarted = true
            startSettleCheck()
        }
    }

    private fun scrollExtent(): Int = height.takeIf { it > 0 } ?: measuredHeight

    private fun calcTop(offset: Int, range: Int, extent: Int): Float {
        val maxScroll = range - extent
        if (maxScroll <= 0) return scrollerPad
        val track = scrollExtent() - thumbHeight - scrollerPad * 2
        if (track <= 0f) return scrollerPad
        return scrollerPad + (offset.toFloat() / maxScroll) * track
    }

    // ═══ 动画控制 ═══════════════════════════════════════════

    private fun cancelHide() {
        hideRunnable?.let { removeCallbacks(it) }
        hideRunnable = null
    }

    private fun cancelSettle() {
        settleRunnable?.let { removeCallbacks(it) }
        settleRunnable = null
    }

    /** 500ms 后淡出 */
    private fun scheduleHide() {
        cancelHide()
        val r = Runnable { fadeTo(0f) }
        hideRunnable = r
        postDelayed(r, 500L)
    }

    /**
     * 手指松开后不立即隐藏。每 50ms 检查 scrollY 是否稳定，
     * 连续两次无变化后才触发淡出，避免惯性滚动中被隐藏。
     */
    private fun startSettleCheck() {
        cancelSettle()
        val check = object : Runnable {
            var lastY = scrollY
            var stableCount = 0

            override fun run() {
                val curY = scrollY
                if (curY == lastY) {
                    stableCount++
                    if (stableCount >= 2) {
                        settleRunnable = null
                        scheduleHide()
                        return
                    }
                } else {
                    stableCount = 0
                    lastY = curY
                }
                postDelayed(this, 50L)
            }
        }
        settleRunnable = check
        postDelayed(check, 50L)
    }

    /** 立即显示指示条（用户交互触发），取消所有延迟/检测 */
    private fun showNow() {
        hasUserInteracted = true
        cancelHide()
        cancelSettle()
        fadeTo(1f)
    }

    /** 平滑过渡到目标 alpha */
    private fun fadeTo(target: Float) {
        fadeAnimator?.cancel()
        if (animAlpha == target) return
        val start = animAlpha
        val anim = ValueAnimator.ofFloat(0f, 1f).apply {
            duration = 200L
            interpolator = DecelerateInterpolator()
            addUpdateListener { a ->
                animAlpha = start + (target - start) * a.animatedFraction
                updatePaintAlpha()
                invalidate()
            }
            addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    animAlpha = target
                    updatePaintAlpha()
                    invalidate()
                }
            })
            start()
        }
        fadeAnimator = anim
    }

    private fun updatePaintAlpha() {
        val base = if (isFastScrolling) thumbDragAlpha else thumbIdleAlpha
        thumbPaint.alpha = (base * animAlpha).toInt().coerceIn(0, 255)
    }

    // ═══ 绘制 ═══════════════════════════════════════════════

    override fun dispatchDraw(canvas: Canvas) {
        super.dispatchDraw(canvas)
        drawThumb(canvas)
    }

    override fun onDrawForeground(canvas: Canvas) {
        foreground?.draw(canvas)
        drawThumb(canvas)
    }

    private fun drawThumb(canvas: Canvas) {
        if (thumbHeight <= 0f || height <= 0f || animAlpha <= 0.01f) return

        val sy = computeVerticalScrollOffset()
        val cx = width - scrollerWidth / 2f
        val half = thumbDrawWidth / 2f
        val r = RectF(cx - half, thumbTop + sy, cx + half, thumbTop + thumbHeight + sy)
        canvas.drawRoundRect(r, thumbDrawWidth / 2f, thumbDrawWidth / 2f, thumbPaint)
    }

    // ═══ 触摸 ═══════════════════════════════════════════════

    override fun onInterceptTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN) {
            showNow()
        }
        if (thumbHeight > 0f &&
            event.action == MotionEvent.ACTION_DOWN &&
            event.x >= width - scrollerWidth
        ) {
            isFastScrolling = true
            parent?.requestDisallowInterceptTouchEvent(true)
            onUserScrollListener?.invoke(true, scrollY)
            updatePaintAlpha()
            return true
        }
        return super.onInterceptTouchEvent(event)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (isFastScrolling) return handleFast(event)
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                showNow()
                onUserScrollListener?.invoke(true, scrollY)
            }
            MotionEvent.ACTION_UP,
            MotionEvent.ACTION_CANCEL -> {
                startSettleCheck()
                onUserScrollListener?.invoke(false, scrollY)
            }
        }
        return super.onTouchEvent(event)
    }

    private fun handleFast(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                showNow()
                updatePaintAlpha()
                dragTo(event.y); invalidate()
            }
            MotionEvent.ACTION_MOVE -> {
                dragTo(event.y); invalidate()
            }
            MotionEvent.ACTION_UP,
            MotionEvent.ACTION_CANCEL -> {
                isFastScrolling = false
                updatePaintAlpha()
                startSettleCheck()
                onUserScrollListener?.invoke(false, scrollY)
                invalidate()
            }
        }
        return true
    }

    private fun dragTo(touchY: Float) {
        val range = computeVerticalScrollRange()
        val extent = scrollExtent()
        if (range <= extent) return
        val maxScroll = range - extent
        val track = scrollExtent() - thumbHeight - scrollerPad * 2
        if (track <= 0f) return
        thumbTop = (touchY - thumbHeight / 2f).coerceIn(scrollerPad, scrollerPad + track)
        val scrollY = ((thumbTop - scrollerPad) / track * maxScroll).toInt().coerceIn(0, maxScroll)
        scrollTo(0, scrollY)
    }
}
