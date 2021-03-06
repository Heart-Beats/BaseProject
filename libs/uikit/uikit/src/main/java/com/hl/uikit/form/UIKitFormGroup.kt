package com.hl.uikit.form

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.util.AttributeSet
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.annotation.RequiresApi
import androidx.core.view.children
import com.hl.uikit.FocusableLayoutHelper
import com.hl.uikit.R

open class UIKitFormGroup : UIKitFormItemView {

    private val focusableLayoutHelper by lazy {
        FocusableLayoutHelper()
    }

    private val dividerDrawable = ColorDrawable()
    private var dividerEnable = true
    private var dividerColor: ColorStateList = ColorStateList.valueOf(Color.BLACK)
    private var dividerHeight = 2
    private var dividerPaddingStart = 0
    private var dividerPaddingEnd = 0
    private var dividerGravity = DividerGravity.BOTTOM
    private var dividerNeedLast = false

    private var lastChildOriginalDividerEnabled: Pair<View, Boolean>? = null

    constructor(context: Context) : this(context, null, R.attr.uikit_formGroupStyle)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, R.attr.uikit_formGroupStyle)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : this(context, attrs, defStyleAttr, 0)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) : super(
        context,
        attrs,
        defStyleAttr,
        defStyleRes
    ) {
        init(context, attrs, defStyleAttr, defStyleRes)
    }

    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        if (isFocusableInTouchMode) {
            focusableLayoutHelper.dispatchTouchEvent(event)
        }
        return super.dispatchTouchEvent(event)
    }

    @SuppressLint("CustomViewStyleable")
    private fun init(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int, defStyleRes: Int) {
        val ta = context.obtainStyledAttributes(attrs, R.styleable.UIKitFormGroup, defStyleAttr, defStyleRes)
        dividerEnable = ta.getBoolean(R.styleable.UIKitFormGroup_uikit_formDividerEnable, true)
        dividerColor = ta.getColorStateList(R.styleable.UIKitFormGroup_uikit_formDividerColor)
            ?: ColorStateList.valueOf(Color.BLACK)
        dividerHeight = ta.getDimensionPixelSize(R.styleable.UIKitFormGroup_uikit_formDividerHeight, 2)
        dividerPaddingStart =
            ta.getDimensionPixelSize(R.styleable.UIKitFormGroup_uikit_formDividerPaddingStart, 0)
        dividerPaddingEnd = ta.getDimensionPixelSize(R.styleable.UIKitFormGroup_uikit_formDividerPaddingEnd, 0)
        ta.getInt(R.styleable.UIKitFormGroup_uikit_formDividerGravity, DividerGravity.BOTTOM.value).run {
            dividerGravity = buildDividerGravityFromValue(this) ?: DividerGravity.BOTTOM
        }
        dividerNeedLast = ta.getBoolean(R.styleable.UIKitFormGroup_uikit_formDividerNeedLast, false)
        ta.recycle()
    }

    override fun generateDefaultLayoutParams(): LinearLayout.LayoutParams {
        return LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, this)
    }

    override fun generateLayoutParams(p: ViewGroup.LayoutParams?): LinearLayout.LayoutParams {
        return LayoutParams(p, this)
    }

    override fun generateLayoutParams(attrs: AttributeSet?): LinearLayout.LayoutParams {
        return LayoutParams(context, attrs, this)
    }

    override fun checkLayoutParams(p: ViewGroup.LayoutParams?): Boolean {
        return p is LayoutParams
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        var dividerSumHeight = 0
        children.forEachIndexed { index, childView ->
            if (childView.visibility != View.GONE) {
                val childLayoutParams = childView.layoutParams
                if (childLayoutParams is LayoutParams) {
                    if (childLayoutParams.dividerGravity == DividerGravity.BOTTOM) {
                        //???????????? view ????????????????????????????????????????????????????????????
                        if (childView == lastChildOriginalDividerEnabled?.first) {
                            childLayoutParams.dividerEnable = lastChildOriginalDividerEnabled?.second ?: false
                        }

                        if (index == childCount - 1) {
                            //???????????????????????????????????????????????????
                            lastChildOriginalDividerEnabled = Pair(childView, childLayoutParams.dividerEnable)
                            // ???????????????????????????????????????
                            childLayoutParams.dividerEnable = dividerNeedLast
                        }
                    }

                    if (childLayoutParams.dividerEnable) {
                        dividerSumHeight += childLayoutParams.dividerHeight
                    }
                    // if (childLayoutParams.gravity == DividerGravity.BOTH) {
                    //
                    // } else {
                    // }
                }
            }
        }

        setMeasuredDimension(this.measuredWidth, this.measuredHeight + dividerSumHeight)
    }

    /**
     * @param changed  ?????????????????????ViewGroup??????????????????????????????????????????
     * @param left,top,right,bottom       ?????? ViewGroup ?????????????????????????????????????????? ?????????????????????????????????
     */
    @RequiresApi(Build.VERSION_CODES.N)
    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        if (isFocusableInTouchMode) {
            focusableLayoutHelper.onLayout(this)
        }

        if (orientation == VERTICAL) {
            layoutVertical(left, top, right, bottom)
        } else {
            super.onLayout(changed, left, top, right, bottom)
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun layoutVertical(left: Int, top: Int, right: Int, bottom: Int) {
        val paddingLeft: Int = paddingLeft
        var childTop: Int
        var childLeft: Int

        // Where right end of child should go
        val width = right - left
        val childRight: Int = width - paddingRight

        // Space available for child
        val childSpace: Int = width - paddingLeft - paddingRight
        val count: Int = childCount

        val majorGravity: Int = gravity and Gravity.VERTICAL_GRAVITY_MASK
        val minorGravity: Int = gravity and Gravity.RELATIVE_HORIZONTAL_GRAVITY_MASK

        val totalLengthField = LinearLayout::class.java.getDeclaredField("mTotalLength")
        totalLengthField.isAccessible = true
        val mTotalLength = totalLengthField.get(this) as Int
        totalLengthField.isAccessible = false

        childTop = when (majorGravity) {
            Gravity.BOTTOM ->                // mTotalLength contains the padding already
                paddingTop + bottom - top - mTotalLength
            Gravity.CENTER_VERTICAL -> paddingTop + (bottom - top - mTotalLength) / 2
            Gravity.TOP -> paddingTop
            else -> paddingTop
        }
        var i = 0
        while (i < count) {
            val child: View = getChildAt(i)
            if (child.visibility != GONE) {
                val childWidth = child.measuredWidth
                val childHeight = child.measuredHeight
                val lp = child.layoutParams as LayoutParams
                var gravity = lp.gravity
                if (gravity < 0) {
                    gravity = minorGravity
                }
                val layoutDirection = layoutDirection
                val absoluteGravity = Gravity.getAbsoluteGravity(gravity, layoutDirection)
                childLeft = when (absoluteGravity and Gravity.HORIZONTAL_GRAVITY_MASK) {
                    Gravity.CENTER_HORIZONTAL -> (paddingLeft + (childSpace - childWidth) / 2
                            + lp.leftMargin) - lp.rightMargin
                    Gravity.END -> childRight - childWidth - lp.rightMargin
                    Gravity.START -> paddingLeft + lp.leftMargin
                    else -> paddingLeft + lp.leftMargin
                }
                val (hasDividerBefore, dividerHeight) = hasDividerBeforeChildAt(i, lp)
                if (hasDividerBefore) {
                    childTop += dividerHeight
                }
                childTop += lp.topMargin
                child.layout(childLeft, childTop, childLeft + childWidth, childTop + childHeight)
                childTop += childHeight + lp.bottomMargin
                i += 0
            }
            i++
        }
    }

    private fun hasDividerBeforeChildAt(childIndex: Int, childLayoutParams: LayoutParams): Pair<Boolean, Int> {
        if (childIndex == childCount) {
            // Check whether the end divider should draw.
            val hasDivider = childLayoutParams.dividerEnable && dividerGravity == DividerGravity.BOTTOM
            return Pair(hasDivider, childLayoutParams.dividerHeight)
        }
        val allViewsAreGoneBefore: Boolean = allViewsAreGoneBefore(childIndex)
        return if (allViewsAreGoneBefore) {
            // This is the first view that's not gone, check if beginning divider is enabled.
            val hasDivider = childLayoutParams.dividerEnable && dividerGravity == DividerGravity.TOP
            Pair(hasDivider, childLayoutParams.dividerHeight)
        } else {
            val lastChildLayoutParams = getChildAt(childIndex - 1).layoutParams as LayoutParams
            // ????????? View ?????????????????????????????????????????????????????????????????????????????????
            val lastChildDividerHeight =
                if (lastChildLayoutParams.dividerEnable) lastChildLayoutParams.dividerHeight else 0

            // ???????????????????????????????????????????????????????????????
            val selfDividerHeight = if (childLayoutParams.dividerEnable) {
                if (dividerGravity == DividerGravity.TOP) childLayoutParams.dividerHeight else 0
            } else 0

            //?????????????????? View ?????????????????????????????????????????????????????? View ??????????????????????????? View ??????????????????
            val dividerHeight = if (dividerGravity == DividerGravity.TOP) selfDividerHeight else lastChildDividerHeight
            Pair(dividerHeight > 0, dividerHeight)
        }
    }

    private fun allViewsAreGoneBefore(childIndex: Int): Boolean {
        for (i in childIndex - 1 downTo 0) {
            val child: View = getChildAt(i)
            if (child.visibility != GONE) {
                return false
            }
        }
        return true
    }


    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.save()
        drawDividers(canvas)
        canvas.restore()
    }

    private fun drawDividers(canvas: Canvas) {
        children.forEach { child ->
            if (child.isShown || child.isInEditMode) {
                val params = child.layoutParams
                if (params is LayoutParams && params.hasDividerBefore()) {
                    val top = child.top - params.dividerHeight
                    drawDivider(canvas, top, params)
                }
                if (params is LayoutParams && params.hasDividerAfter()) {
                    val top = child.bottom
                    drawDivider(canvas, top, params)
                }
            }
        }
    }

    private fun drawDivider(canvas: Canvas, top: Int, params: LayoutParams) {
        dividerDrawable.color = params.dividerColor?.defaultColor ?: Color.TRANSPARENT
        dividerDrawable.setBounds(
            paddingStart + params.dividerPaddingStart,
            top,
            width - paddingEnd - params.dividerPaddingEnd,
            top + params.dividerHeight
        )
        dividerDrawable.draw(canvas)
    }

    private fun LayoutParams.hasDividerBefore(): Boolean {
        return dividerEnable
                && dividerHeight > 0
                && (dividerGravity == DividerGravity.TOP /*|| dividerGravity == DividerGravity.BOTH*/)
    }

    private fun LayoutParams.hasDividerAfter(): Boolean {
        return dividerEnable
                && dividerHeight > 0
                && (dividerGravity == DividerGravity.BOTTOM /*|| dividerGravity == DividerGravity.BOTH*/)
    }

    enum class DividerGravity(val value: Int) {
        TOP(1),
        BOTTOM(2),

        // BOTH ????????????????????????????????????
        // BOTH(3)
    }

    private fun buildDividerGravityFromValue(formValue: Int): DividerGravity? {
        for (dividerGravity in DividerGravity.values()) {
            if (dividerGravity.value == formValue) {
                return dividerGravity
            }
        }
        return null
    }

    class LayoutParams : LinearLayout.LayoutParams {
        private var layoutView: UIKitFormGroup
        var dividerEnable: Boolean = false
        var dividerColor: ColorStateList? = null
        var dividerHeight: Int = 0
        var dividerPaddingStart: Int = 0
        var dividerPaddingEnd: Int = 0
        var dividerGravity: DividerGravity = DividerGravity.BOTTOM

        @SuppressLint("CustomViewStyleable")
        constructor(c: Context, attrs: AttributeSet?, layoutView: UIKitFormGroup) : super(c, attrs) {
            this.layoutView = layoutView
            init(c, attrs)
        }

        /**
         *     ????????????????????????????????? view ??????????????????????????? view ??? xml????????????????????????????????????????????????????????????
         * ?????? UIKitFormGroup ????????????????????????
         */
        private fun init(context: Context, attrs: AttributeSet?) {
            val ta =
                context.obtainStyledAttributes(attrs, R.styleable.UIKitFormGroup_Layout, R.attr.uikit_formItemStyle, 0)
            dividerEnable =
                ta.getBoolean(R.styleable.UIKitFormGroup_Layout_uikit_formDividerEnable, layoutView.dividerEnable)
            dividerColor = ta.getColorStateList(R.styleable.UIKitFormGroup_Layout_uikit_formDividerColor)
                ?: layoutView.dividerColor
            dividerHeight = ta.getDimensionPixelSize(
                R.styleable.UIKitFormGroup_Layout_uikit_formDividerHeight,
                layoutView.dividerHeight
            )
            dividerPaddingStart = ta.getDimensionPixelSize(
                R.styleable.UIKitFormGroup_Layout_uikit_formDividerPaddingStart,
                layoutView.dividerPaddingStart
            )
            dividerPaddingEnd = ta.getDimensionPixelSize(
                R.styleable.UIKitFormGroup_Layout_uikit_formDividerPaddingEnd,
                layoutView.dividerPaddingEnd
            )
            dividerGravity = layoutView.dividerGravity

            ta.recycle()
        }

        constructor(width: Int, height: Int, layoutView: UIKitFormGroup) : super(width, height) {
            this.layoutView = layoutView
            val context = layoutView.context
            init(context, null)
        }

        constructor(width: Int, height: Int, weight: Float, layoutView: UIKitFormGroup) : super(width, height, weight) {
            this.layoutView = layoutView
            val context = layoutView.context
            init(context, null)
        }

        constructor(p: ViewGroup.LayoutParams?, layoutView: UIKitFormGroup) : super(p) {
            this.layoutView = layoutView
            val context = layoutView.context
            init(context, null)
        }

        constructor(source: MarginLayoutParams?, layoutView: UIKitFormGroup) : super(source) {
            this.layoutView = layoutView
            val context = layoutView.context
            init(context, null)
        }

        override fun toString(): String {
            return "LayoutParams(dividerEnable=$dividerEnable, dividerColor=${dividerColor?.defaultColor} " + "dividerHeight=$dividerHeight, " +
                    "dividerPaddingStart=$dividerPaddingStart, dividerPaddingEnd=$dividerPaddingEnd, dividerGravity=$dividerGravity)"
        }
    }
}