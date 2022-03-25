package com.hl.uikit.text

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.widget.EditText
import androidx.appcompat.widget.AppCompatTextView
import com.hl.uikit.R


/**
 * Created by chibb on 2019/7/1.
 */
class UIKitSelectTextView : AppCompatTextView {

    private var isDownFlag = true
    var isUp:Boolean
            get() {
                return isDownFlag
            }
    set(value) {
        if (value) {

            setCompoundDrawables(null, null, nav_up, null)
        } else {

            setCompoundDrawables(null, null, nav_down, null)
        }
        isDownFlag=value
    }


    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(context, attrs)
    }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle) {
        init(context, attrs)
    }

    private fun init(context: Context, attrs: AttributeSet) {
        init()

    }

    var nav_up: Drawable? = null
    var nav_down: Drawable? = null
    private fun init() {
        nav_up = context.getDrawable(R.drawable.uikit_ic_select_up)
        nav_up?.setBounds(0, 0, nav_up?.minimumWidth?:0, nav_up?.minimumHeight?:0)
        nav_down = context.getDrawable(R.drawable.uikit_ic_select_down)
        nav_down?.setBounds(0, 0, nav_down?.minimumWidth?:0, nav_down?.minimumHeight?:0)
        setCompoundDrawables(null, null, nav_down, null)

    }


}
