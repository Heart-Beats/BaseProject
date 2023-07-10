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
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import androidx.core.view.updatePaddingRelative
import com.hl.uikit.utils.dpInt
import kotlinx.android.synthetic.main.uikit_view_number_step.view.*

class UIKitNumberStepView : LinearLayout {




    companion object {
        const val DEFAULT_TEXTSIZE = 15f
        const val TYPE_DELETE = 0x01
        const val TYPE_ADD = 0x02
    }

    private var mOperatorClickListener: (operatorType: Int) -> Unit = {}
    var textColor: ColorStateList
        get() {
            return tvText.textColors
        }
        set(value) {
            tvText?.setTextColor(value)

            etInput.setTextColor(value)

        }
    var textSize: Float
        get() {
            return tvText.textSize
        }
        set(value) {
            tvText?.setTextSize(TypedValue.COMPLEX_UNIT_SP, value)
        }
    var text: CharSequence
        get() {
            return  if(inputAble){
                etInput.text.toString()
            }else{
                tvText.text
            }
        }
        set(value) {
            tvText.text = value
            etInput.setText(value)
        }
    var operatorVisibility: Int
        get() {
            return tvDelOperator?.visibility ?: View.GONE
        }
        set(value) {
            tvDelOperator?.visibility = value
            tvAddOperator?.visibility = value
        }
    private var operatorPadding: Int
        get() {
            return tvDelOperator?.paddingEnd ?: 0
        }
        set(value) {
            val delStart = tvDelOperator?.paddingStart ?: 0
            val delTop = tvDelOperator?.paddingTop ?: 0
            val delBottom = tvDelOperator?.paddingBottom ?: 0
            tvDelOperator?.setPaddingRelative(delStart, delTop, value, delBottom)
            val addTop = tvAddOperator?.paddingTop ?: 0
            val addEnd = tvAddOperator?.paddingEnd ?: 0
            val addBottom = tvAddOperator?.paddingBottom ?: 0
            tvAddOperator?.setPaddingRelative(value, addTop, addEnd, addBottom)
        }
    var inputAble: Boolean
        get() {
            return tvText.visibility==View.GONE
        }
        set(value) {
            if (value) {
                tvText?.visibility = View.GONE
                etInput?.visibility = View.VISIBLE
            } else {
                tvText?.visibility = View.VISIBLE
                etInput?.visibility = View.GONE
            }
        }
    var hasUnit: Boolean
        get() {
            return tvUnit?.visibility == View.VISIBLE
        }
        set(value) {
            if (value) {
                tvUnit?.visibility = View.VISIBLE
                val paddingEnd = 0.dpInt
                val gravity = Gravity.RIGHT or Gravity.CENTER
               if (tvText?.visibility == View.VISIBLE) {
                    tvText?.gravity= gravity
                    tvText?.updatePaddingRelative(end = paddingEnd)
                }
                if (etInput?.visibility == View.VISIBLE) {
                    etInput?.updatePaddingRelative(end = paddingEnd)
                    etInput?.gravity= gravity
                }
            } else {
                tvUnit?.visibility = View.GONE
                val paddingEnd = 3.dpInt
                if (tvText?.visibility == View.VISIBLE) {
                    tvText?.gravity= Gravity.CENTER
                    tvText?.updatePaddingRelative(end = paddingEnd)
                }
                if (etInput?.visibility == View.VISIBLE) {
                    etInput?.gravity= Gravity.CENTER
                    etInput?.updatePaddingRelative(end = paddingEnd)
                }
            }
        }
     var unitValue:CharSequence
         get() {
             return tvUnit?.text?:""
         }
         set(value) {
             tvUnit?.text=value
         }

     var isInterval:Boolean
         get() {
             return tvDelOperator?.visibility == View.VISIBLE
         }
         set(value) {
             if(value){
                 tvDelOperator?.visibility= View.VISIBLE
                 tvAddOperator?.visibility= View.VISIBLE
             }else{
                 tvDelOperator?.visibility=View.GONE
                 tvAddOperator?.visibility= View.GONE
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

        LayoutInflater.from(context).inflate(R.layout.uikit_view_number_step, this, true)

        val ta = context?.obtainStyledAttributes(attrs, R.styleable.UIKitNumberStepView)

        textColor = ta?.getColorStateList(R.styleable.UIKitNumberStepView_android_textColor)
            ?: ColorStateList.valueOf(
               Color.BLACK
            )

        textSize = ta?.getDimensionPixelSize(
            R.styleable.UIKitNumberStepView_android_textSize,
            DEFAULT_TEXTSIZE.toInt()
        )?.toFloat() ?: DEFAULT_TEXTSIZE

        text = ta?.getString(R.styleable.UIKitNumberStepView_android_text) ?: ""

        operatorVisibility =
            ta?.getInt(R.styleable.UIKitNumberStepView_uikit_operatorVisibility, View.VISIBLE)
                ?: View.VISIBLE

        operatorPadding =
            ta?.getDimensionPixelSize(R.styleable.UIKitNumberStepView_uikit_operatorPadding, 0) ?: 0


        hasUnit= ta?.getBoolean(R.styleable.UIKitNumberStepView_uikit_hasUnit,false)?:false
        unitValue=ta?.getString(R.styleable.UIKitNumberStepView_uikit_unitValue)?:"%"
        setListener()

        var inputFilters = arrayOfNulls<InputFilter>(1)
        inputFilters[0] = InputMoneyFilter()
        etInput.filters=inputFilters

        ta?.recycle()
    }

    fun addDigits(keyListener: DigitsKeyListener){
        etInput.keyListener = keyListener;
    }

    fun setOnOperatorClickListener(listener: (operatorType: Int) -> Unit) {
        mOperatorClickListener = listener
        setListener()
    }

    private fun setListener() {
        tvDelOperator?.setOnClickListener {
            mOperatorClickListener(TYPE_DELETE)
        }
        tvAddOperator?.setOnClickListener {
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