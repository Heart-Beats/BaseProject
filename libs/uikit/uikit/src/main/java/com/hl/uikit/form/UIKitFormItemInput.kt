package com.hl.uikit.form

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.text.method.DigitsKeyListener
import android.util.AttributeSet
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.core.view.updatePaddingRelative
import com.hl.uikit.R
import com.hl.uikit.utils.onClick

class UIKitFormItemInput : UIKitFormItemText {
    private var mOnFocusChangeListener: ((v: View, hasFocus: Boolean) -> Unit)? = null
    private var mDeletable: Boolean = false
    internal var editText: TextView? = null
    private val mTextWatchers by lazy {
        arrayListOf<TextWatcher>()
    }

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, R.attr.uikit_formItemInputStyle)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : this(context, attrs, defStyleAttr, 0)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) : super(
        context, attrs,
        defStyleAttr, defStyleRes
    ) {
        init(context, attrs, defStyleAttr, defStyleRes)
    }

    override fun init(context: Context, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) {
        super.init(context, attrs, defStyleAttr, defStyleRes)
        val ta = context.obtainStyledAttributes(attrs, R.styleable.UIKitFormItemInput, defStyleAttr, defStyleRes)

        ta.also {
            mDeletable = it.getBoolean(R.styleable.UIKitFormItemInput_uikit_formTextDeletable, false)
        }.recycle()
    }

    //重写该方法解决 mText == null 时，TextView未创建导致后续即使调用setText() 由于反射布局也无法预览
    override fun hasHintText() = true

    override fun onCreateTextView(textCustom: Boolean): TextView {
        val textView: TextView = if (textCustom) {
            AppCompatTextView(context, null)
        } else {
            AppCompatEditText(context, null)
        }
        return textView.apply {
            editText = this
            background = ColorDrawable(Color.TRANSPARENT)
            if (mTextMaxLines != 0) {
                this.maxLines = mTextMaxLines
            }
            updatePaddingRelative(0, 0, 0, 0)
            imeOptions = EditorInfo.IME_ACTION_NEXT
            this.layoutParams = LayoutParams(0, LayoutParams.WRAP_CONTENT, 1f)
        }
    }

    //重写该方法主要解决 setRightIcon() 使用反射导致布局无法预览
    override fun createRightIcon(): ImageView? {
        if (!mText.isNullOrEmpty() && mDeletable) {
            mRightImageView = AppCompatImageView(context, null).apply {
                this.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.uikit_icon_input_delete))
                this.scaleType = ImageView.ScaleType.FIT_XY
                this.onClick {
                    super.setText("")
                }
            }
        }
        return mRightImageView
    }

    override fun setText(text: CharSequence?) {
        super.setText(text)
        if (mDeletable) {
            deletableByText()
        }
    }

    private fun deletableByText() {
        if (editText?.hasFocus() == true) {
            if (mText.isNullOrEmpty()) {
                setDeleteIconVisibility(View.GONE)
            } else {
                setDeleteIconVisibility(View.VISIBLE)
            }
        } else {
            setDeleteIconVisibility(View.GONE)
        }
    }

    fun setDeleteIconVisibility(visibility: Int) {
        if (!mDeletable) {
            throw IllegalStateException("当前删除按钮状态不可用，app:uikit_formTextDeletable 必须设置为 true")
        }
        if (mRightImageView == null) {
            setRightIcon(ContextCompat.getDrawable(context, R.drawable.uikit_icon_input_delete)) {
                setText("")
            }
        }
        mRightImageView?.visibility = visibility
    }

    fun setDeletable(deletable: Boolean) {
        mDeletable = deletable
    }

    fun setDigits(digits: CharSequence) {
        editText?.keyListener = DigitsKeyListener.getInstance(digits.toString())
    }

    fun setHint(hint: CharSequence) {
        editText?.hint = hint
    }

    fun setInputType(type: Int) {
        editText?.inputType = type
    }

    fun setRawInputType(type: Int) {
        editText?.setRawInputType(type)
    }

    fun setHintTextColor(colors: ColorStateList) {
        editText?.setHintTextColor(colors)
    }

    fun setFilters(Filters: Array<InputFilter>) {
        editText?.filters = Filters
    }

    fun setMaxLength(length: Int) {
        val filters = editText?.filters?.toMutableList() ?: mutableListOf()
        filters.add(InputFilter.LengthFilter(length))
        editText?.filters = filters.toTypedArray()
    }

    fun setOnFocusChangeListener(onFocusChangeListener: (v: View, hasFocus: Boolean) -> Unit) {
        mOnFocusChangeListener = onFocusChangeListener
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        if (!mDeletable) {
            return
        }
        addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(
                s: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                mText = s
                if (mDeletable) {
                    deletableByText()
                }
            }
        })
        editText?.setOnFocusChangeListener { v, hasFocus ->
            mOnFocusChangeListener?.invoke(v, hasFocus)
            if (!mDeletable) {
                return@setOnFocusChangeListener
            }
            if (hasFocus && !mText.isNullOrEmpty()) {
                setDeleteIconVisibility(View.VISIBLE)
            } else {
                setDeleteIconVisibility(View.GONE)
            }
        }
    }

    override fun onDetachedFromWindow() {
        for (watch in mTextWatchers) {
            editText?.removeTextChangedListener(watch)
        }
        mTextWatchers.clear()
        super.onDetachedFromWindow()
    }
}