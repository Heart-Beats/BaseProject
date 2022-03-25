package com.hl.uikit

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView

class KDividerView : AppCompatImageView {
    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, R.attr.uikit_dividerViewStyle)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)
}