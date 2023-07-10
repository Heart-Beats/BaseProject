package com.hl.uikit.form

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Typeface
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.AppCompatTextView
import com.hl.uikit.R
import com.hl.uikit.utils.onClick

class UIKitFormHeaderActionText : UIKitFormItemLabel {

    private var tvText: TextView? = null
    private var mTextBold: Boolean = false
    private var mText: CharSequence? = null
    private var mTextSize: Int = 0
    private var mTextColor: ColorStateList? = null

    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, R.attr.uikit_formHeaderActionTextStyle)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : this(context, attrs, defStyleAttr, 0)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) : super(
        context, attrs,
        defStyleAttr, defStyleRes
    ) {
        init(context, attrs, defStyleAttr, defStyleRes)
    }

    override fun init(context: Context, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) {
        val ta = context.obtainStyledAttributes(attrs, R.styleable.UIKitFormHeaderActionText, defStyleAttr, defStyleRes)
        ta.also {
            mText = it.getString(R.styleable.UIKitFormHeaderActionText_uikit_formActionText)
            mTextSize =
                it.getDimensionPixelSize(R.styleable.UIKitFormHeaderActionText_uikit_formActionTextSize, 14)
            mTextColor = it.getColorStateList(R.styleable.UIKitFormHeaderActionText_uikit_formActionTextColor)
            mTextBold = it.getBoolean(R.styleable.UIKitFormHeaderActionText_uikit_formActionTextBold, false)
        }.recycle()
    }


    override fun createRightChildViews(): List<View?> {
        return listOf(createText())
    }

    private fun createText(): TextView? {
        if (!mText.isNullOrEmpty()) {
            tvText = onCreateTextView().apply {
                this.text = mText ?: ""
                this.setTextSize(TypedValue.COMPLEX_UNIT_PX, mTextSize * 1.0f)
                if (mTextColor != null) {
                    setTextColor(mTextColor)
                }
                this.gravity = Gravity.END
                layoutParams = LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f)
            }
            setTextBold(mTextBold)
        }
        return tvText
    }

    private fun onCreateTextView(): TextView {
        return AppCompatTextView(context, null, android.R.attr.textViewStyle)
    }

    fun setTextBold(isBold: Boolean) {
        mTextBold = isBold
        val tvText = tvText ?: return
        tvText.typeface = if (isBold) {
            Typeface.DEFAULT_BOLD
        } else {
            null
        }
    }

    fun setText(text: CharSequence?, onClickListener: ((v: View) -> Unit)? = null) {
        mText = text

        commonHandelChangeView(this::mText, this::tvText, this::createText, setViewMethod = {
            tvText?.setText(it)
        }) {
            setTextAction(onClickListener)
        }
    }

    fun setTextAction(onClickListener: ((v: View) -> Unit)? = null) {
        if (onClickListener != null) {
            tvText?.onClick(onClickListener)
        }
    }
}