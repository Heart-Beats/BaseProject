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
            if (i >= childCount - 1) {
                break
            }
            val child = parent.getChildAt(i)
            parent.getDecoratedBoundsWithMargins(child, mBounds)

            var bottom: Int
            var top: Int
            if (layoutManager.orientation == RecyclerView.VERTICAL) {
                // 纵向
                bottom = mBounds.bottom + child.translationY.roundToInt()
                top = bottom - getDividerHeight()
            } else {
                // 横向
                left = mBounds.right - getDividerHeight()
                top = mBounds.top
                right = mBounds.right
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

            if (!isLastItem(parent, view, state)) {
                // 最后一条不设置偏移

                when (layoutManager.orientation) {
                    RecyclerView.VERTICAL -> outRect.set(0, 0, 0, getDividerHeight())
                    RecyclerView.HORIZONTAL -> outRect.set(0, 0, getDividerHeight(), 0)
                }
            }
        }
    }

    /**
     * 判断是否为最后一条
     */
    private fun isLastItem(parent: RecyclerView, view: View, state: RecyclerView.State): Boolean {
        //整个RecyclerView最后一个item的position
        val lastPosition = state.itemCount - 1

        //获取当前要进行布局的item的position
        val current = parent.getChildLayoutPosition(view)

        return lastPosition == current
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