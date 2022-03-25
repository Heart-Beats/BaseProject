package com.hl.uikit

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup


/**
 * @Author  张磊  on  2021/02/01 at 11:15
 * Email: 913305160@qq.com
 *
 * 自定义 ViewGroup 布局参数示例类
 */
class CornerLayout @JvmOverloads constructor(context: Context?, attrs: AttributeSet? = null) :
    ViewGroup(context, attrs) {

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        measureChildren(widthMeasureSpec, heightMeasureSpec)
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        if (changed) {
            // 只对第一个子View进行layout
            val first: View = getChildAt(0)
            val layoutParams = first.layoutParams as CornerLayoutParams
            val left: Int
            val top: Int
            when (layoutParams.mCorner) {
                CornerLayoutParams.CORNER_LEFT_TOP -> {
                    left = paddingLeft + layoutParams.leftMargin
                    top = paddingTop + layoutParams.topMargin
                }
                CornerLayoutParams.CORNER_RIGHT_TOP -> {
                    top = paddingTop + layoutParams.topMargin
                    left = width - paddingRight - first.measuredWidth - layoutParams.rightMargin
                }
                CornerLayoutParams.CORNER_LEFT_BOTTOM -> {
                    top = height - paddingBottom - first.measuredHeight - layoutParams.bottomMargin
                    left = paddingLeft + layoutParams.leftMargin
                }
                CornerLayoutParams.CORNER_RIGHT_BOTTOM -> {
                    top = height - paddingBottom - layoutParams.bottomMargin - first.measuredHeight
                    left = width - paddingRight - layoutParams.rightMargin - first.measuredWidth
                }
                else -> {
                    left = paddingLeft + layoutParams.leftMargin
                    top = paddingTop + layoutParams.topMargin
                }
            }
            first.layout(left, top, left + first.measuredWidth, top + first.measuredHeight)
        }
    }

    /**
     * 系统创建布局参数的接口
     */
    override fun generateLayoutParams(attrs: AttributeSet?): LayoutParams {
        return CornerLayoutParams(context, attrs)
    }

    /**
     * 系统创建默认布局参数的接口。
     */
    override fun generateDefaultLayoutParams(): LayoutParams {
        return CornerLayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
    }

    /**
     * 检测参数类型是否合法。
     */
    override fun checkLayoutParams(p: LayoutParams?): Boolean {
        return p is CornerLayoutParams
    }

    /**
     * 根据不合法的参数p, 重新创建CornerLayoutParams对象。
     */
    override fun generateLayoutParams(p: LayoutParams?): LayoutParams {
        return if (p is MarginLayoutParams) CornerLayoutParams(p as MarginLayoutParams?) else CornerLayoutParams(p)
    }

    /**
     * 定义布局参数类。
     */
    class CornerLayoutParams : MarginLayoutParams {
        var mCorner = CORNER_LEFT_TOP

        constructor(c: Context, attrs: AttributeSet?) : super(c, attrs) {
            val a = c.obtainStyledAttributes(attrs, R.styleable.UIKitCornerLayout_Layout)
            mCorner = a.getInt(R.styleable.UIKitCornerLayout_Layout_uikit_corner, CORNER_LEFT_TOP)
            a.recycle()
        }

        constructor(width: Int, height: Int) : super(width, height)
        constructor(source: MarginLayoutParams?) : super(source)
        constructor(source: LayoutParams?) : super(source)

        companion object {
            const val CORNER_LEFT_TOP = 0x01
            const val CORNER_RIGHT_TOP = 0x02
            const val CORNER_LEFT_BOTTOM = 0x04
            const val CORNER_RIGHT_BOTTOM = 0x08
        }
    }
}

