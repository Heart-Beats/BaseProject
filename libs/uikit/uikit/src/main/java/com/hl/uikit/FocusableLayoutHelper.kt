package com.hl.uikit

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.graphics.Rect
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText

class FocusableLayoutHelper {

    private var mFocusableViews = mutableListOf<View>()
    private var mParent: ViewGroup? = null

    fun onLayout(parent: ViewGroup) {
        mFocusableViews.clear()
        generationFocusableViews(parent)
    }

    private fun generationFocusableViews(parent: ViewGroup) {
        mParent = parent
        val childCount = parent.childCount
        for (i in 0 until childCount) {
            val child = parent.getChildAt(i)
            if (child is ViewGroup) {
                generationFocusableViews(child)
            } else {
                if (child is EditText) {
                    mFocusableViews.add(child)
                }
            }
        }
    }

    fun dispatchTouchEvent(event: MotionEvent) {
        val x = event.x.toInt()
        val y = event.y.toInt()
        var touchFocusable = false
        for (v in mFocusableViews) {
            if (v.touchOnView(x, y)) {
                touchFocusable = true
                break
            }
        }
        val context = mParent?.context
        if (!touchFocusable) {
            context?.getActivity()?.currentFocus?.let {
                it.clearFocus()
                val manager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
                manager?.hideSoftInputFromWindow(it.windowToken, InputMethodManager.SHOW_FORCED)
            }
        }
    }

    private fun View.touchOnView(x: Int, y: Int): Boolean {
        val rect = Rect()
        getDrawingRect(rect)
        val location = IntArray(2)
        getLocationOnScreen(location)
        rect.left = location[0]
        rect.top = location[1]
        rect.right = rect.right + location[0]
        rect.bottom = rect.bottom + location[1]
        return rect.contains(x, y)
    }

    private fun Context.getActivity(): Activity? {
        if (this is Activity) {
            return this
        }
        if (this is ContextWrapper) {
            val context = baseContext
            return context.getActivity()
        }
        return null
    }
}