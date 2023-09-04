package com.hl.uikit.text

import android.content.Context
import android.content.res.ColorStateList
import android.util.AttributeSet
import android.util.TypedValue
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.annotation.Px
import androidx.core.widget.addTextChangedListener
import com.hl.uikit.R
import com.hl.uikit.databinding.UikitTextAreaBinding
import com.hl.uikit.utils.setMaxLength
import com.hl.viewbinding.bindingMerge

class UIKitTextArea : LinearLayout {

    private lateinit var viewBinding: UikitTextAreaBinding

    private val etInput: EditText
        get() = viewBinding.etInput

    private val tvCount: TextView
        get() = viewBinding.tvCount

    private var textAreaHint: CharSequence? = null
    private var textAreaHintColor: ColorStateList? = null
    private var textAreaTextColor: ColorStateList? = null
    private var textAreaTextSize: Int = 14

    private var textAreaMaxCount: Int = 200
    private var textAreaMaxCountTextColor: ColorStateList? = null
    private var textAreaMaxCountTextSize: Int = 13

    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, R.attr.uikit_textAreaStyle)

    constructor(context: Context, attrs: AttributeSet?, defStyle: Int) : super(context, attrs, defStyle) {
        init(attrs, defStyle)
    }


    private fun init(attrs: AttributeSet?, defStyle: Int) {
        this.orientation = VERTICAL

        val ta = context.obtainStyledAttributes(
            attrs, R.styleable.UIKitTextArea, defStyle, 0
        )

        textAreaHint = ta.getString(R.styleable.UIKitTextArea_uikit_textAreaHint)
        textAreaHintColor = ta.getColorStateList(R.styleable.UIKitTextArea_uikit_textAreaHintColor)
        textAreaTextColor = ta.getColorStateList(R.styleable.UIKitTextArea_uikit_textAreaTextColor)
        textAreaTextSize = ta.getDimensionPixelSize(R.styleable.UIKitTextArea_uikit_textAreaTextSize, textAreaTextSize)
        textAreaMaxCount = ta.getInt(R.styleable.UIKitTextArea_uikit_textAreaMaxCount, textAreaMaxCount)
        textAreaMaxCountTextColor = ta.getColorStateList(R.styleable.UIKitTextArea_uikit_textAreaMaxCountTextColor)
        textAreaMaxCountTextSize = ta.getDimensionPixelSize(
            R.styleable.UIKitTextArea_uikit_textAreaMaxCountTextSize,
            textAreaMaxCountTextSize
        )
        ta.recycle()

        viewBinding = bindingMerge()

        initView()
    }

    private fun initView() {
        etInput.addTextChangedListener {
            tvCount.text = "${it?.toString()?.length ?: 0}/$textAreaMaxCount"
        }
        tvCount.text = "${etInput.text.toString().length}/$textAreaMaxCount"

        etInput.setMaxLength(textAreaMaxCount)
        textAreaHint?.run {
            etInput.hint = this
        }
        textAreaHintColor?.run {
            etInput.setHintTextColor(this)
        }
        textAreaTextColor?.run {
            etInput.setTextColor(this)
        }
        etInput.setTextSize(TypedValue.COMPLEX_UNIT_PX, textAreaTextSize.toFloat())

        textAreaMaxCountTextColor?.run {
            tvCount.setTextColor(this)
        }
        tvCount.setTextSize(TypedValue.COMPLEX_UNIT_PX, textAreaMaxCountTextSize.toFloat())
    }

    fun getText() = etInput.text.toString().trim()

    fun setText(text: CharSequence?) {
        etInput.setText(text ?: "")
    }

    fun setTextColor(@ColorInt color: Int) {
        textAreaTextColor = ColorStateList.valueOf(color)
        etInput.setTextColor(textAreaTextColor)
    }

    fun setTextSize(@Px textSize: Int) {
        textAreaTextSize = textSize
        etInput.setTextSize(TypedValue.COMPLEX_UNIT_PX, textAreaTextSize.toFloat())
    }

    fun getTextSize() = textAreaTextSize

    fun setTextHint(text: CharSequence?) {
        textAreaHint = text ?: ""
        etInput.hint = textAreaHint
    }

    fun getTextHint() = textAreaHint

    fun setTextHintColor(@ColorInt color: Int) {
        textAreaHintColor = ColorStateList.valueOf(color)
        etInput.setHintTextColor(textAreaTextColor)
    }

    fun setMaxCount(maxCount: Int) {
        textAreaMaxCount = maxCount
        etInput.setMaxLength(textAreaMaxCount)
    }

    fun getMaxCount() = textAreaMaxCount


    fun setMaxCountTextColor(@ColorInt color: Int) {
        textAreaMaxCountTextColor = ColorStateList.valueOf(color)
        tvCount.setTextColor(textAreaTextColor)
    }

    fun setMaxCountTextSize(@Px textSize: Int) {
        textAreaMaxCountTextSize = textSize
        tvCount.setTextSize(TypedValue.COMPLEX_UNIT_PX, textAreaTextSize.toFloat())
    }

    fun getMaxCountTextSize() = textAreaMaxCountTextSize

}