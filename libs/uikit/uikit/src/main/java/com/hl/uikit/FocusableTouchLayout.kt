package com.hl.uikit

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.widget.FrameLayout

class FocusableTouchLayout : FrameLayout {

    private val focusableLayoutHelper by lazy {
        FocusableLayoutHelper()
    }

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    init {
        isFocusableInTouchMode = true
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        focusableLayoutHelper.onLayout(this)
        super.onLayout(changed, left, top, right, bottom)
    }

    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        focusableLayoutHelper.dispatchTouchEvent(event)
        return super.dispatchTouchEvent(event)
    }

}