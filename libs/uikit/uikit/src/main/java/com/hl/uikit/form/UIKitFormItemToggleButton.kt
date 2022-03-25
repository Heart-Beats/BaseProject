package com.hl.uikit.form

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.appcompat.widget.AppCompatToggleButton
import com.hl.uikit.R
import com.hl.uikit.dpInt

/**
 * @Author  张磊  on  2020/10/15 at 18:11
 * Email: 913305160@qq.com
 */
class UIKitFormItemToggleButton : UIKitFormItemLabel {

    private var mToggleButtonRes: Int? = null
    private var mToggleButtonCheck = false

    private var mToggleButton: AppCompatToggleButton? = null

    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, R.attr.uikit_formItemToggleButtonStyle)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : this(context, attrs, defStyleAttr, 0)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) : super(
        context, attrs,
        defStyleAttr, defStyleRes
    ) {
        init(context, attrs, defStyleAttr, defStyleRes)
    }

    override fun init(context: Context, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) {
        val ta = context.obtainStyledAttributes(attrs, R.styleable.UIKitFormItemToggleButton, defStyleAttr, defStyleRes)
        ta.also {
            mToggleButtonRes = it.getResourceId(
                R.styleable.UIKitFormItemToggleButton_uikit_formToggleButtonRes,
                R.drawable.uikit_selector_toggle_button_bg
            )
            mToggleButtonCheck = it.getBoolean(R.styleable.UIKitFormItemToggleButton_uikit_formToggleCheck, false)
        }.recycle()
    }


    override fun createRightChildViews(): List<View?> {
        return mutableListOf(createToggleButton())
    }

    private fun createToggleButton(): AppCompatToggleButton? {
        if (mToggleButtonRes != null) {
            mToggleButton = AppCompatToggleButton(context).apply {
                this.text = ""
                this.textOn = ""
                this.textOff = ""
                this.layoutParams = generateDefaultLayoutParams().apply {
                    this.width = 42.dpInt
                    this.height = 26.dpInt
                }
                this.setBackgroundResource(mToggleButtonRes ?: R.drawable.uikit_selector_toggle_button_bg)
                this.isChecked = mToggleButtonCheck
            }
        }
        return mToggleButton
    }

    fun setToggleButton(toggleButtonRes: Int? = mToggleButtonRes, onClickListener: (isCheck: Boolean) -> Unit) {
        if (toggleButtonRes == mToggleButtonRes && mToggleButton != null) {
            setToggleButtonOnClickListener(onClickListener)
        } else {
            mToggleButtonRes = toggleButtonRes

            commonHandelChangeView(
                ::mToggleButtonRes, ::mToggleButton, ::createToggleButton,
                setViewMethod = {
                    mToggleButton?.setBackgroundResource(it)
                }) {
                setToggleButtonOnClickListener(onClickListener)
            }
        }
    }

    private fun setToggleButtonOnClickListener(onClickListener: (isCheck: Boolean) -> Unit) {
        mToggleButton?.setOnClickListener {
            onClickListener(mToggleButton?.isChecked ?: false)
        }
    }

    fun getToggleButton(): AppCompatToggleButton? {
        return mToggleButton
    }
}