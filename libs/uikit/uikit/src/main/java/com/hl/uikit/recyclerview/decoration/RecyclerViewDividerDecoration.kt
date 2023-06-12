package com.hl.uikit.recyclerview.decoration

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Rect
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration
import kotlin.math.roundToInt

class RecyclerViewDividerDecoration : ItemDecoration() {
    var paddingStart: Int = 0
    var paddingEnd: Int = 0
    var divider: Drawable? = null
    var color: Int? = null

    /**
     * 分割线的大小
     */
    var dividerSpace: Int? = null

    private val mBounds = Rect()

    override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        val layoutManager = parent.layoutManager
        if (layoutManager !is LinearLayoutManager || (divider == null && (color == null || (dividerSpace ?: 0) == 0))) {
            return
        }
        c.save()
        var left: Int
        var right: Int
        if (parent.clipToPadding) {
            left = parent.paddingStart + paddingStart
            right = parent.width - parent.paddingEnd - paddingEnd
        } else {
            left = paddingStart
            right = parent.width - paddingEnd
        }
        val childCount = parent.childCount
        for (i in 0 until childCount) {
            if (i == 0) {
                // 跳过绘制第一条的装饰间隔效果
                continue
            }
            val child = parent.getChildAt(i)
            parent.getDecoratedBoundsWithMargins(child, mBounds)

            var bottom: Int
            var top: Int
            if (layoutManager.orientation == RecyclerView.VERTICAL) {
                // 纵向
                top = mBounds.top + child.translationY.roundToInt()
                bottom = top + getDividerHeight()
            } else {
                // 横向
                left = mBounds.left + child.translationX.roundToInt()
                top = mBounds.top
                right = left + getDividerHeight()
                bottom = mBounds.bottom
            }

            val dividerDrawable = divider ?: ColorDrawable(color ?: Color.TRANSPARENT)
            dividerDrawable.setBounds(left, top, right, bottom)
            dividerDrawable.draw(c)
        }
        c.restore()
    }

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {

        val layoutManager = parent.layoutManager
        if (layoutManager is LinearLayoutManager) {

            if (!isFirstItem(parent, view, state)) {
                // 第一条不设置上偏移，可解决动态添加 item 间隔无效的问题

                when (layoutManager.orientation) {
                    RecyclerView.VERTICAL -> outRect.set(0, getDividerHeight(), 0, 0)
                    RecyclerView.HORIZONTAL -> outRect.set(getDividerHeight(), 0, 0, 0)
                }
            }
        }
    }

    /**
     * 判断是否为第一条
     */
    private fun isFirstItem(parent: RecyclerView, view: View, state: RecyclerView.State): Boolean {
        //获取当前要进行布局的item的position
        val current = parent.getChildLayoutPosition(view)
        return current == 0
    }

    private fun getDividerHeight(): Int {
        return when {
            dividerSpace != null -> {
                dividerSpace!!
            }
            divider != null -> {
                divider?.intrinsicHeight ?: 0
            }
            else -> {
                0
            }
        }
    }
}