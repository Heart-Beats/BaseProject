package com.hl.uikit

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.text.InputFilter
import android.text.Spanned
import android.text.method.DigitsKeyListener
import android.util.AttributeSet
import android.util.Log
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.updatePaddingRelative
import com.hl.uikit.databinding.UikitViewNumberStepBinding
import com.hl.uikit.utils.dpInt
import com.hl.viewbinding.bindingMerge

class UIKitNumberStepView : LinearLayout {

    private lateinit var viewBinding: UikitViewNumberStepBinding

    val tvUnit: TextView by lazy { viewBinding.tvUnit }
    val tvDelOperator: ImageView by lazy { viewBinding.tvDelOperator }
    val tvAddOperator: ImageView by lazy { viewBinding.tvAddOperator }
    val etInput: EditText by lazy { viewBinding.etInput }


    companion object {
        const val DEFAULT_TEXT_SIZE = 15f
        const val TYPE_DELETE = 0x01
        const val TYPE_ADD = 0x02
    }

    private var mOperatorClickListener: (operatorType: Int) -> Unit = {}
    var textColor: ColorStateList
        get() {
            return viewBinding.tvText.textColors
        }
        set(value) {
            viewBinding.tvText.setTextColor(value)
            viewBinding.etInput.setTextColor(value)
        }

    var textSize: Float
        get() {
            return viewBinding.tvText.textSize
        }
        set(value) {
            viewBinding.tvText.setTextSize(TypedValue.COMPLEX_UNIT_SP, value)
        }
    var text: CharSequence
        get() {
            return  if(inputAble){
                viewBinding.etInput.text.toString()
            }else{
                viewBinding.tvText.text
            }
        }
        set(value) {
            viewBinding.tvText.text = value
            viewBinding.etInput.setText(value)
        }
    var operatorVisibility: Int
        get() {
            return viewBinding.tvDelOperator?.visibility ?: View.GONE
        }
        set(value) {
            viewBinding.tvDelOperator.visibility = value
            viewBinding.tvAddOperator.visibility = value
        }
    private var operatorPadding: Int
        get() {
            return viewBinding.tvDelOperator.paddingEnd
        }
        set(value) {
            val delStart = viewBinding.tvDelOperator.paddingStart
            val delTop = viewBinding.tvDelOperator.paddingTop
            val delBottom = viewBinding.tvDelOperator.paddingBottom
            viewBinding.tvDelOperator.setPaddingRelative(delStart, delTop, value, delBottom)
            val addTop = viewBinding.tvAddOperator.paddingTop
            val addEnd = viewBinding.tvAddOperator.paddingEnd
            val addBottom = viewBinding.tvAddOperator.paddingBottom
            viewBinding.tvAddOperator.setPaddingRelative(value, addTop, addEnd, addBottom)
        }
    var inputAble: Boolean
        get() {
            return viewBinding.tvText.visibility == View.GONE
        }
        set(value) {
            if (value) {
                viewBinding.tvText.visibility = View.GONE
                viewBinding.etInput.visibility = View.VISIBLE
            } else {
                viewBinding.tvText.visibility = View.VISIBLE
                viewBinding.etInput.visibility = View.GONE
            }
        }
    var hasUnit: Boolean
        get() {
            return viewBinding.tvUnit.visibility == View.VISIBLE
        }
        set(value) {
            val tvText = viewBinding.tvText
            val etInput = viewBinding.etInput

            if (value) {
                viewBinding.tvUnit.visibility = View.VISIBLE
                val paddingEnd = 0.dpInt
                val gravity = Gravity.RIGHT or Gravity.CENTER
                if (tvText.visibility == View.VISIBLE) {
                    tvText.gravity = gravity
                    tvText.updatePaddingRelative(end = paddingEnd)
                }
                if (etInput.visibility == View.VISIBLE) {
                    etInput.updatePaddingRelative(end = paddingEnd)
                    etInput.gravity = gravity
                }
            } else {
                viewBinding.tvUnit.visibility = View.GONE
                val paddingEnd = 3.dpInt
                if (tvText.visibility == View.VISIBLE) {
                    tvText.gravity = Gravity.CENTER
                    tvText.updatePaddingRelative(end = paddingEnd)
                }
                if (etInput.visibility == View.VISIBLE) {
                    etInput.gravity = Gravity.CENTER
                    etInput.updatePaddingRelative(end = paddingEnd)
                }
            }
        }
     var unitValue:CharSequence
         get() {
             return viewBinding.tvUnit.text ?: ""
         }
         set(value) {
             viewBinding.tvUnit.text = value
         }

     var isInterval:Boolean
         get() {
             return viewBinding.tvDelOperator?.visibility == View.VISIBLE
         }
         set(value) {
             val tvDelOperator = viewBinding.tvDelOperator
             val tvAddOperator = viewBinding.tvAddOperator
             if (value) {
                 tvDelOperator.visibility = View.VISIBLE
                 tvAddOperator.visibility = View.VISIBLE
             } else {
                 tvDelOperator.visibility = View.GONE
                 tvAddOperator.visibility = View.GONE
             }
         }



    constructor(context: Context?) : super(context) {
        init(context, null)
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        init(context, attrs)
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init(context, attrs)
    }

    private fun init(context: Context?, attrs: AttributeSet?) {
        viewBinding = bindingMerge()

        val ta = context?.obtainStyledAttributes(attrs, R.styleable.UIKitNumberStepView)

        textColor = ta?.getColorStateList(R.styleable.UIKitNumberStepView_android_textColor)
            ?: ColorStateList.valueOf(
               Color.BLACK
            )

        textSize = ta?.getDimensionPixelSize(
            R.styleable.UIKitNumberStepView_android_textSize,
            DEFAULT_TEXT_SIZE.toInt()
        )?.toFloat() ?: DEFAULT_TEXT_SIZE

        text = ta?.getString(R.styleable.UIKitNumberStepView_android_text) ?: ""

        operatorVisibility =
            ta?.getInt(R.styleable.UIKitNumberStepView_uikit_operatorVisibility, View.VISIBLE)
                ?: View.VISIBLE

        operatorPadding =
            ta?.getDimensionPixelSize(R.styleable.UIKitNumberStepView_uikit_operatorPadding, 0) ?: 0


        hasUnit = ta?.getBoolean(R.styleable.UIKitNumberStepView_uikit_hasUnit, false) ?: false
        unitValue = ta?.getString(R.styleable.UIKitNumberStepView_uikit_unitValue) ?: "%"
        setListener()

        var inputFilters = arrayOfNulls<InputFilter>(1)
        inputFilters[0] = InputMoneyFilter()
        viewBinding.etInput.filters = inputFilters

        ta?.recycle()
    }

    fun addDigits(keyListener: DigitsKeyListener) {
        viewBinding.etInput.keyListener = keyListener;
    }

    fun setOnOperatorClickListener(listener: (operatorType: Int) -> Unit) {
        mOperatorClickListener = listener
        setListener()
    }

    private fun setListener() {
        viewBinding.tvDelOperator.setOnClickListener {
            mOperatorClickListener(TYPE_DELETE)
        }
        viewBinding.tvAddOperator.setOnClickListener {
            mOperatorClickListener(TYPE_ADD)
        }
    }

}

class InputMoneyFilter(private val floatNum:Int = 2)  :InputFilter{

    override fun filter(source: CharSequence?, start: Int, end: Int, d: Spanned?, dstart: Int, dend: Int): CharSequence? {
        val dest = d ?: return ""
        Log.d("InputMoneyFilter", " source:  "+source.toString() +" dest:  " + dest)
        if (source.toString() == "." && dstart == 0 && dend == 0) {//判断小数点是否在第一位

//            money.setText( "0"+source+dest);//给小数点前面加0
//            money.setSelection(2);//设置光标
            return "0$source"
        }
        if(source.toString()==(".")&& dest.toString().contains(".")){
            return ""
        }
        if(source.toString()==("0")&& dest.toString()=="0"){
            return ""
        }


        if (dest.toString().indexOf(".") != -1 && (dest.length - dest.toString().indexOf(".")) > floatNum) {//判断小数点是否存在并且小数点后面是否已有两个字符

            if ((dest.length - dstart) < 3) {//判断现在输入的字符是不是在小数点后面
                return "";//过滤当前输入的字符
            }
            return ""
        }

        return source
    }

}