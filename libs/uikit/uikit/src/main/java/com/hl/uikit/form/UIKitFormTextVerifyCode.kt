package com.hl.uikit.form

import android.content.Context
import android.content.res.ColorStateList
import android.text.TextWatcher
import android.text.method.TransformationMethod
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import com.hl.uikit.R
import com.hl.uikit.dpInt
import com.hl.uikit.onClick
import kotlinx.android.synthetic.main.uikit_layout_form_textinput.view.*
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class UIKitFormTextVerifyCode : UIKitFormItemView {

    private var tvSmsCode: TextView? = null
    private var inputView: UIKitFormItemInput? = null

    private var label: String? = null
    private var mLabelSize: Int = 14
    private var mLabelColor: ColorStateList? = null
    private var mLabelMarginEnd: Int = 30

    private var text: String? = null
    private var hintText: String = ""
    private var hintTextColor: ColorStateList? = null
    private var textGravity = Gravity.END
    private var mTextMaxLength: Int = 20

    private var leftWarnText: String? = null
    private var rightWarnText: String? = null

    private var needSmsCode = true


    private val countdownDuration: Int = 60

    private val mProcessListener: (mCount: Int) -> Unit = { mCount ->
        tvSmsCode?.text = "已发送(${mCount})"
        tvSmsCode?.setTextColor(ContextCompat.getColor(context, R.color.uikit_color_4))
        tvSmsCode?.isEnabled = false
    }

    private val mFinishedListener: () -> Unit = {
        tvSmsCode?.text = "重新获取"
        tvSmsCode?.setTextColor(ContextCompat.getColor(context, R.color.uikit_color_3))
        tvSmsCode?.isEnabled = true
    }


    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, R.attr.uikit_formItemTextVerifyCodeStyle)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : this(context, attrs, defStyleAttr, 0)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) : super(
        context,
        attrs,
        defStyleAttr,
        defStyleRes
    ) {
        orientation = VERTICAL
        val taLayout =
            context.obtainStyledAttributes(attrs, R.styleable.UIKitFormTextVerifyCode, defStyleAttr, defStyleRes)

        taLayout.also {
            label = it.getString(R.styleable.UIKitFormTextVerifyCode_uikit_formLabel)
            text = it.getString(R.styleable.UIKitFormTextVerifyCode_uikit_formText)
            mLabelSize =
                it.getDimensionPixelSize(
                    R.styleable.UIKitFormTextVerifyCode_uikit_formLabelSize,
                    (mLabelSize * context.resources.displayMetrics.density).toInt()
                )
            mLabelColor = it.getColorStateList(R.styleable.UIKitFormTextVerifyCode_uikit_formLabelColor)
            mLabelMarginEnd =
                it.getDimensionPixelSize(
                    R.styleable.UIKitFormTextVerifyCode_uikit_formLabelMarginEnd,
                    (mLabelMarginEnd * context.resources.displayMetrics.density).toInt()
                )

            textGravity = it.getInt(R.styleable.UIKitFormTextVerifyCode_uikit_formTextGravity, -1)
            hintText = it.getString(R.styleable.UIKitFormTextVerifyCode_uikit_formTextHint) ?: hintText
            hintTextColor = it.getColorStateList(R.styleable.UIKitFormTextVerifyCode_uikit_formTextHintColor)
            mTextMaxLength =
                it.getInt(R.styleable.UIKitFormTextVerifyCode_uikit_formTextMaxLength, mTextMaxLength)
            leftWarnText = it.getString(R.styleable.UIKitFormTextVerifyCode_uikit_formLeftWarnText)
            rightWarnText = it.getString(R.styleable.UIKitFormTextVerifyCode_uikit_formRightWarnText)
            needSmsCode = it.getBoolean(R.styleable.UIKitFormTextVerifyCode_uikit_formNeedSmsCode, false)
        }.recycle()

        View.inflate(context, R.layout.uikit_layout_form_textinput, this)

        init()
    }

    private fun init() {
        inputView = itemInput
        inputView?.apply {
            setLabel(label, mLabelSize, mLabelColor)
            setLeftLinearLayout(Gravity.CENTER_VERTICAL, mLabelMarginEnd)

            setText(text ?: "")
            setHint(hintText)
            hintTextColor?.let { colors ->
                setHintTextColor(colors)
            }
            setMaxLength(mTextMaxLength)
            setDeletable(false)
            setTextGravity(textGravity)
        }

        setLeftWarnText(leftWarnText)
        setRightWarnText(rightWarnText)

        if (needSmsCode) {
            initVerifyCodeButton()
        }
    }

    fun initVerifyCodeButton(
        clickCondition: () -> Boolean = { true },
        isAutoCountdown: Boolean = true,
        verifyCodeClickListener: (UIKitFormTextVerifyCode) -> Unit = {}
    ) {
        if (tvSmsCode == null) {
            tvSmsCode = AppCompatTextView(context, null, R.attr.uikit_formItemInputSmsCodeStyle)
        } else if (tvSmsCode?.parent != null) {
            val viewGroup = tvSmsCode?.parent as ViewGroup
            viewGroup.removeView(tvSmsCode)
        }
        val dp70 = 70.dpInt
        val dp24 = 24.dpInt
        val dp15 = 15.dpInt
        val lp = LayoutParams(dp70, dp24)
        lp.marginStart = dp15
        inputView?.addView(tvSmsCode, lp)

        tvSmsCode?.onClick {
            if (clickCondition()) {
                verifyCodeClickListener(this)
                if (isAutoCountdown) {
                    timeDown()
                }
            }
        }
    }

    fun timeDown(
        count: Int = countdownDuration,
        processListener: (mCount: Int) -> Unit = mProcessListener,
        finishedListener: () -> Unit = mFinishedListener
    ) {
        MainScope().launch {
            var num = count
            while (num > 0) {
                if (tvSmsCode?.visibility == VISIBLE) {
                    processListener(--num)
                    delay(1000)
                } else {
                    this.cancel()
                    break
                }
            }
            finishedListener()
        }
    }

    fun setTextGravity(gravity: Int) {
        inputView?.setTextGravity(gravity)
    }

    fun setRightWarnText(rightText: CharSequence?) {
        tvRight?.text = rightText ?: ""
        if (!rightText.isNullOrEmpty()) {
            tvLeft?.text = ""
        }
        updateInfoLayout()
    }

    fun setLeftWarnText(leftText: CharSequence?) {
        if (!leftText.isNullOrEmpty()) {
            tvRight?.text = ""
        }
        tvLeft?.text = leftText ?: ""
        updateInfoLayout()
    }

    fun setOnFocusChangeListener(listener: (view: View, hasFocus: Boolean) -> Unit) {
        inputView?.editText?.setOnFocusChangeListener(listener)
    }

    fun setTextWatch(textWatcher: TextWatcher) {
        inputView?.editText?.addTextChangedListener(textWatcher)
    }

    fun getText(): CharSequence {
        return inputView?.editText?.text ?: ""
    }

    fun setDigits(digits: CharSequence) {
        inputView?.setDigits(digits)
    }

    fun setInputType(type: Int) {
        inputView?.setInputType(type)
    }

    fun setTransformationMethod(method: TransformationMethod) {
        val view = inputView?.editText
        if (view is EditText) {
            view.transformationMethod = method
        }
    }

    fun setText(value: CharSequence?) {
        inputView?.setText(value)
    }

    fun setRawInputType(type: Int) {
        inputView?.setRawInputType(type)
    }

    fun addTextChangedListener(watcher: TextWatcher) {
        inputView?.addTextChangedListener(watcher)
    }

    private fun updateInfoLayout() {
        val leftText = tvLeft?.text
        val rightText = tvRight?.text
        if (leftText.isNullOrEmpty() && rightText.isNullOrEmpty()) {
            tvLeft?.visibility = View.GONE
            tvRight?.visibility = View.GONE
            // dividerView?.visibility = View.GONE
        } else {
            tvLeft?.visibility = View.VISIBLE
            tvRight?.visibility = View.VISIBLE
            // dividerView?.visibility = View.VISIBLE
        }
    }
}