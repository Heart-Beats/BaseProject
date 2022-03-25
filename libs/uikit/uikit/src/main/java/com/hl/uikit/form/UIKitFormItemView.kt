package com.hl.uikit.form

import android.content.Context
import android.util.AttributeSet
import android.widget.LinearLayout
import com.hl.uikit.R

open class UIKitFormItemView : LinearLayout {
    constructor(context: Context?) : this(context, null)
    constructor(context: Context?, attrs: AttributeSet?) : this(context, attrs, R.attr.uikit_formItemStyle)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr, R.style.UiKit_FormItemStyle)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes)
}