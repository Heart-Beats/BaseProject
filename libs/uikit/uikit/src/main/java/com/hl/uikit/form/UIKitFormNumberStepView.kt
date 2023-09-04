package com.hl.uikit.form

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Typeface
import android.text.method.DigitsKeyListener
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import androidx.core.content.ContextCompat
import com.hl.uikit.R
import com.hl.uikit.UIKitNumberStepView
import com.hl.uikit.addDoubleListener
import com.hl.uikit.databinding.UikitLayoutFormNumberStepBinding
import com.hl.uikit.integerTextChangeWatch
import com.hl.viewbinding.bindingMerge

class UIKitFormNumberStepView : UIKitFormItemView {

    private var viewBinding: UikitLayoutFormNumberStepBinding

    var mLabelBold: Boolean
        get() {
            return viewBinding.tvLabel.typeface == Typeface.DEFAULT_BOLD
        }
        set(value) {
            if (value) {
                viewBinding.tvLabel.typeface = Typeface.DEFAULT_BOLD
            }
        }
    private var mTextBold: Boolean = false
    var mTextMaxLines: Int = 1
    var inputAble: Boolean
        get() {
            return viewBinding.numberStep.inputAble
        }
        set(value) {
            viewBinding.numberStep.inputAble = value
        }


    var mLabel: CharSequence
        get() {
            return viewBinding.tvLabel.text
        }
        set(value) {
            viewBinding.tvLabel.text = value
        }
    var mLabelSize: Float
        get() {
            return viewBinding.tvLabel.textSize
        }
        set(value) {
            viewBinding.tvLabel.setTextSize(TypedValue.COMPLEX_UNIT_SP, value)
        }
    var mLabelColor: ColorStateList
        get() {
            return viewBinding.tvLabel.textColors
        }
        set(value) {
            viewBinding.tvLabel.setTextColor(value)
        }
    var mText: CharSequence
        get() {
            return viewBinding.numberStep.text
        }
        set(value) {
            viewBinding.numberStep.text = value
        }
    var mTextSize: Float
        get() {
            return viewBinding.numberStep.textSize
        }
        set(value) {
            viewBinding.numberStep.textSize = value
        }
    var mTextColor: ColorStateList?
        get() {
            return viewBinding.numberStep.textColor
        }
        set(value) {
            val context = viewBinding.numberStep?.context
            field = value
            if (context != null) {
                viewBinding.numberStep.textColor = value ?: context.getDefTextColor()
            }
        }

    var hasUnit: Boolean
        get() {
            return viewBinding.numberStep.tvUnit?.visibility == View.VISIBLE
        }
        set(value) {
            if (value) {
                viewBinding.numberStep.tvUnit?.visibility = View.VISIBLE
            } else {
                viewBinding.numberStep.tvUnit?.visibility = View.GONE
            }

        }
    var unitValue: CharSequence
        get() {
            return viewBinding.numberStep.tvUnit?.text ?: ""
        }
        set(value) {
            viewBinding.numberStep.tvUnit?.text = value
        }

    var isInterval: Boolean
        get() {
            return viewBinding.numberStep.isInterval
        }
        set(value) {
            viewBinding.numberStep.isInterval = value
            if (!value) {
                viewBinding.numberStep.inputAble = false
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

        viewBinding = bindingMerge()

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
        viewBinding.numberStep.setOnOperatorClickListener { type ->
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
        addDoubleListener(viewBinding.numberStep, minValue, maxValue, defValue)
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
        viewBinding.numberStep.tvDelOperator.isEnabled = enabled
        viewBinding.numberStep.tvAddOperator.isEnabled = enabled
        viewBinding.numberStep.etInput.isEnabled = enabled
        viewBinding.numberStep.tvUnit.isEnabled = enabled
    }

    fun setIntegerStepClickListener(
        minValue: Int,
        maxValue: Int,
        onChange: (num: Int) -> Unit = {}
    ) {
        val numberStep = viewBinding.numberStep

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


