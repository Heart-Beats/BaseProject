package com.hl.uikit.text

import android.content.Context
import android.content.res.ColorStateList
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.widget.addTextChangedListener
import com.hl.uikit.R
import com.hl.uikit.setMaxLength

class UIKitTextArea : LinearLayout {

    private lateinit var etInput: EditText

    private var textAreaHint: String? = null
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

        View.inflate(context, R.layout.uikit_text_area, this)

        initView()
    }

    private fun initView() {
        etInput = findViewById(R.id.etInput)
        val tvCount = findViewById<TextView>(R.id.tvCount)

        etInput.addTextChangedListener {
            tvCount?.text = "${it?.toString()?.length ?: 0}/$textAreaMaxCount"
        }

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
}