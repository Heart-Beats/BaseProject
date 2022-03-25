package com.hl.uikit.recyclerview.decoration

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Rect
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration
import kotlin.math.roundToInt

class RecyclerViewDividerDecoration : ItemDecoration() {
    var paddingStart: Int = 0
    var paddingEnd: Int = 0
    var divider: Drawable? = null
    var color: Int? = null
    var height: Int? = null
    private val mBounds = Rect()

    override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        if (parent.layoutManager == null || (divider == null && (color == null || height ?: 0 == 0))) {
            return
        }
        c.save()
        val left: Int
        val right: Int
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
            val bottom = mBounds.bottom + child.translationY.roundToInt()
            val dividerDrawable = divider ?: ColorDrawable(color ?: Color.TRANSPARENT)
            val dividerHeight = getDividerHeight()
            val top = bottom - dividerHeight
            dividerDrawable.setBounds(left, top, right, bottom)
            dividerDrawable.draw(c)
        }
        c.restore()
    }

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        outRect.set(0, 0, 0, getDividerHeight())
    }

    private fun getDividerHeight(): Int {
        return when {
            height != null -> {
                height!!
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