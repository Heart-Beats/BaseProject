package com.hl.uikit.form

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Typeface
import android.text.method.DigitsKeyListener
import android.util.AttributeSet
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.uikit_layout_form_number_step.view.*
import kotlinx.android.synthetic.main.uikit_view_number_step.view.*
import com.hl.uikit.R
import com.hl.uikit.UIKitNumberStepView
import com.hl.uikit.addDoubleListener
import com.hl.uikit.integerTextChangeWatch

class UIKitFormNumberStepView : UIKitFormItemView {

    var mLabelBold: Boolean
        get() {
            return tvLabel.typeface == Typeface.DEFAULT_BOLD
        }
        set(value) {
            if (value) {
                tvLabel.typeface = Typeface.DEFAULT_BOLD
            }
        }
    private var mTextBold: Boolean = false
    var mTextMaxLines: Int = 1
    var inputAble: Boolean
        get() {
            return numberStep.inputAble
        }
        set(value) {
            numberStep.inputAble = value
        }


    var mLabel: CharSequence
        get() {
            return tvLabel.text
        }
        set(value) {
            tvLabel.text = value
        }
    var mLabelSize: Float
        get() {
            return tvLabel.textSize
        }
        set(value) {
            tvLabel.setTextSize(TypedValue.COMPLEX_UNIT_SP, value)
        }
    var mLabelColor: ColorStateList
        get() {
            return tvLabel.textColors
        }
        set(value) {
            tvLabel.setTextColor(value)
        }
    var mText: CharSequence
        get() {
            return numberStep.text
        }
        set(value) {
            numberStep.text = value
        }
    var mTextSize: Float
        get() {
            return numberStep.textSize
        }
        set(value) {
            numberStep.textSize = value
        }
    var mTextColor: ColorStateList?
        get() {
            return numberStep?.textColor
        }
        set(value) {
            val context = numberStep?.context
            field = value
            if (context != null) {
                numberStep?.textColor = value ?: context.getDefTextColor()
            }
        }

    var hasUnit: Boolean
        get() {
            return numberStep?.tvUnit?.visibility == View.VISIBLE
        }
        set(value) {
            if (value) {
                numberStep?.tvUnit?.visibility = View.VISIBLE
            } else {
                numberStep?.tvUnit?.visibility = View.GONE
            }

        }
    var unitValue: CharSequence
        get() {
            return numberStep?.tvUnit?.text ?: ""
        }
        set(value) {
            numberStep?.tvUnit?.text = value
        }

    var isInterval: Boolean
        get() {
            return numberStep.isInterval
        }
        set(value) {
            numberStep.isInterval = value
            if (!value) {
                numberStep.inputAble = false
            }
        }

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : this(
        context,
        attrs,
        defStyleAttr,
        0
    )

    @SuppressLint("CustomViewStyleable")
    constructor(
        context: Context,
        attrs: AttributeSet?,
        defStyleAttr: Int,
        defStyleRes: Int
    ) : super(context, attrs, defStyleAttr, defStyleRes) {
        orientation = VERTICAL
        val inflater = LayoutInflater.from(context)
        inflater.inflate(R.layout.uikit_layout_form_number_step, this, true)

        val ta = context.obtainStyledAttributes(
            attrs,
            R.styleable.UIKitFormNumberStepView,
            defStyleAttr,
            defStyleRes
        )
        mLabelBold =
            ta.getBoolean(R.styleable.UIKitFormNumberStepView_uikit_formStepLabelBold, false)
        mLabel = ta.getString(R.styleable.UIKitFormNumberStepView_uikit_formStepLabel) ?: ""
        mLabelSize = ta.getFloat(R.styleable.UIKitFormNumberStepView_uikit_formStepLabelSize, 14f)
        mLabelColor =
            ta.getColorStateList(R.styleable.UIKitFormNumberStepView_uikit_formStepLabelColor)
                ?: ColorStateList.valueOf(0xFF333333.toInt())
        mText = ta.getString(R.styleable.UIKitFormNumberStepView_uikit_formStepText) ?: ""
        mTextSize = ta.getFloat(R.styleable.UIKitFormNumberStepView_uikit_formStepTextSize, 14f)
        mTextColor =
            ta.getColorStateList(R.styleable.UIKitFormNumberStepView_uikit_formStepTextColor)
                ?: context.getDefTextColor()
        mTextBold = ta.getBoolean(R.styleable.UIKitFormNumberStepView_uikit_formStepTextBold, false)
        mTextMaxLines =
            ta.getInteger(R.styleable.UIKitFormNumberStepView_uikit_formStepTextMaxLines, 1)

        hasUnit = ta.getBoolean(R.styleable.UIKitFormNumberStepView_uikit_formStepHasUnit, false)
        unitValue = ta.getString(R.styleable.UIKitFormNumberStepView_uikit_formStepUnitValue) ?: ""
        unitValue = ta.getString(R.styleable.UIKitFormNumberStepView_uikit_formStepUnitValue) ?: ""
        inputAble = ta.getBoolean(R.styleable.UIKitFormNumberStepView_uikit_formStepInputAble, true)
        isInterval =
            ta.getBoolean(R.styleable.UIKitFormNumberStepView_uikit_formStepIsInterval, true)
        numberStep.setOnOperatorClickListener { type ->
            when (type) {
                UIKitNumberStepView.TYPE_ADD -> {
                    onAdd(1)

                }
                UIKitNumberStepView.TYPE_DELETE -> {
                    onDelete(1)

                }
            }

        }
    }

    private fun Context.getDefTextColor():ColorStateList{
        return ContextCompat.getColorStateList(
            context,
            R.color.uikit_form_stepper_color_selector
        )!!
    }

    fun setDoubleOperatorClickListener(minValue: Double, maxValue: Double, defValue: String?) {
        addDoubleListener(numberStep, minValue, maxValue, defValue)
    }


    var onAdd: (num: Int) -> Unit = {}
    var onDelete: (num: Int) -> Unit = {}

    fun setOperatorClickListener(
        onAdd: (num: Int) -> Unit = {},
        onDelete: (num: Int) -> Unit = {}
    ) {
        this.onAdd = onAdd
        this.onDelete = onDelete
    }

    fun setStepperEnabled(enabled: Boolean) {
        numberStep.tvDelOperator.isEnabled = enabled
        numberStep.tvAddOperator.isEnabled = enabled
        numberStep.etInput.isEnabled = enabled
        numberStep.tvUnit.isEnabled = enabled
    }

    fun setIntegerStepClickListener(
        minValue: Int,
        maxValue: Int,
        onChange: (num: Int) -> Unit = {}
    ) {
        if (inputAble) {
            numberStep.addDigits(DigitsKeyListener.getInstance("1234567890"))
            numberStep.etInput.addTextChangedListener(
                integerTextChangeWatch(
                    minValue,
                    maxValue,
                    after = {
                        val newValue = it.toString().toDoubleOrNull() ?: 0.00
                        numberStep.tvAddOperator.isEnabled = newValue < maxValue
                        numberStep.tvDelOperator.isEnabled = newValue > minValue
                    })
            )
        }

        numberStep.setOnOperatorClickListener { type ->
            val oldValue = numberStep.text.toString().toIntOrNull() ?: 0
            var newValue = oldValue
            when (type) {
                UIKitNumberStepView.TYPE_ADD -> {
                    if (oldValue < maxValue)
                        newValue = oldValue + 1
                    numberStep.text = newValue.toString()

                }

                UIKitNumberStepView.TYPE_DELETE -> {
                    if (oldValue > minValue && oldValue > 0) {

                        newValue = oldValue - 1
                        numberStep.text = newValue.toString()
                    }
                }
                else -> {
                }
            }

            numberStep.tvAddOperator.isEnabled = newValue < maxValue
            numberStep.tvDelOperator.isEnabled = newValue > minValue
            if (oldValue != newValue) {
                onChange(newValue)
            }
        }
    }
}


