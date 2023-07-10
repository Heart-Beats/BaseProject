package com.hl.uikit.form

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.annotation.IntDef
import androidx.annotation.Px
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.view.children
import androidx.core.view.updatePadding
import com.hl.uikit.R
import com.hl.uikit.utils.dpInt
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy
import kotlin.reflect.KFunction0
import kotlin.reflect.KMutableProperty0

/**
 * @Author  张磊  on  2020/10/15 at 9:29
 * Email: 913305160@qq.com
 */
abstract class UIKitFormItemLabel : UIKitFormItemView {

    private companion object {
        const val LEFT_WEIGHT = 0X01
        const val RIGHT_WEIGHT = 0X02
    }

    private var ivLeftIcon: ImageView? = null
    private var mLeftIcon: Drawable? = null
    private var mLeftIconMarginEnd: Int = 8.dpInt

    private var mTagText: CharSequence? = null
    private var mTagTextColor: Int = Color.RED

    private var mLabelBold: Boolean = false
    private var mLabelMarginEnd: Int = 30
    private var mLabel: CharSequence? = null
    private var mLabelSize: Int = 14
    private var mLabelColor: ColorStateList? = null
    private var tvLabel: TextView? = null

    protected val childLeftLinearLayout: LinearLayout = LinearLayout(context)
    private var mChildLabel: List<CharSequence> = listOf()
    private var mChildLabelSize: Int = 12
    private var mChildLabelColor: ColorStateList? = null
    private var mChildLabelMarginTop: Int = 0

    protected val childRightLinearLayout: LinearLayout = LinearLayout(context)
    private var rightLayoutChildMargin = 4.dpInt

    private var childWeight = RIGHT_WEIGHT

    private var mLeftLayoutGravity = Gravity.TOP or Gravity.START
    private var mRightLayoutGravity = Gravity.CENTER_VERTICAL
    private var mCanSelectable = false

    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, R.attr.uikit_formItemLabelStyle)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : this(context, attrs, defStyleAttr, 0)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) : super(
        context, attrs,
        defStyleAttr, defStyleRes
    ) {
        val ta = context.obtainStyledAttributes(attrs, R.styleable.UIKitFormItemLabel, defStyleAttr, defStyleRes)
        ta.also {
            mTagText = it.getString(R.styleable.UIKitFormItemLabel_uikit_formTagText)
            mTagTextColor = it.getColor(R.styleable.UIKitFormItemLabel_uikit_formTagTextColor, mTagTextColor)

            mLabel = it.getString(R.styleable.UIKitFormItemLabel_uikit_formLabel)
            mLabelBold = it.getBoolean(R.styleable.UIKitFormItemLabel_uikit_formLabelBold, false)
            mLabelSize =
                it.getDimensionPixelSize(R.styleable.UIKitFormItemLabel_uikit_formLabelSize, mLabelSize)
            mLabelMarginEnd =
                it.getDimensionPixelSize(R.styleable.UIKitFormItemLabel_uikit_formLabelMarginEnd, mLabelMarginEnd)
            mLabelColor = it.getColorStateList(R.styleable.UIKitFormItemLabel_uikit_formLabelColor)
            mLeftIcon = it.getDrawable(R.styleable.UIKitFormItemLabel_uikit_formLeftIcon)
            mLeftIconMarginEnd =
                it.getDimensionPixelSize(R.styleable.UIKitFormItemLabel_uikit_formLeftIconMarginEnd, mLeftIconMarginEnd)

            it.getString(R.styleable.UIKitFormItemLabel_uikit_formChildLabel)?.run {
                mChildLabel = this.split(",")
            }
            mChildLabelSize = it.getDimensionPixelSize(
                R.styleable.UIKitFormItemLabel_uikit_formChildLabelSize,
                mChildLabelSize
            )
            mChildLabelColor = it.getColorStateList(R.styleable.UIKitFormItemLabel_uikit_formChildLabelColor)
            mChildLabelMarginTop =
                it.getDimensionPixelSize(
                    R.styleable.UIKitFormItemLabel_uikit_formChildLabelMarginTop,
                    mChildLabelMarginTop
                )
            childRightLinearLayout.orientation =
                it.getInt(R.styleable.UIKitFormItemLabel_uikit_formRightLayoutOrientation, HORIZONTAL)

            rightLayoutChildMargin =
                it.getDimensionPixelSize(
                    R.styleable.UIKitFormItemLabel_uikit_formRightLayoutChildMargin,
                    rightLayoutChildMargin
                )
            childWeight = it.getInteger(R.styleable.UIKitFormItemLabel_uikit_formChildWeight, RIGHT_WEIGHT)

            mLeftLayoutGravity = it.getInt(R.styleable.UIKitFormItemLabel_uikit_formLeftLayoutGravity, -1)
            mRightLayoutGravity = it.getInt(R.styleable.UIKitFormItemLabel_uikit_formRightLayoutGravity, -1)
            mCanSelectable = it.getBoolean(R.styleable.UIKitFormItemLabel_uikit_formCanSelectable, false)
        }.recycle()

        initThisView(context, attrs, defStyleAttr, defStyleRes)
    }

    /**
     * 该方法强制子类重写， 因为目前子类左右布局中的 View 添加由该父类完成，重写此方法确保子类创建之前添加的 view 的属性都已完成初始化
     *
     * 若要确保子类构造完成之后相关的属性正确，则需在子类的构造方法中调用此方法
     *
     * 类初始化顺序： 子类开始初始化 --> 父类初始化（父类未初始化过） --> 父类静态变量/静态代码块 --> 父类成员变量/普通代码块 --> 父类构造函数
     *                      --> 父类完成初始化 --> 子类静态变量/静态代码块 --> 子类成员变量/普通代码块 --> 子类构造函数  --> 子类完成初始化
     */
    abstract fun init(context: Context, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int)

    private fun initThisView(context: Context, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int){
        init(context, attrs, defStyleAttr, defStyleRes)
        initChildLeftLinearLayout()
        setLabelBold(mLabelBold)
        setLeftIcon(mLeftIcon)
        createRightLayout()
        setCanSelectable(mCanSelectable)
    }

    private fun initChildLeftLinearLayout() {
        val index = if (ivLeftIcon == null) {
            0
        } else {
            1
        }

        if (childLeftLinearLayout.parent == null) {
            val lp = getChildLeftLinearLayoutLayoutParams()
            lp.marginEnd = mLabelMarginEnd
            lp.gravity = mLeftLayoutGravity
            childLeftLinearLayout.layoutParams = lp
            childLeftLinearLayout.orientation = VERTICAL
            addView(childLeftLinearLayout, index, lp)
        }

        setLabel(mLabel)
        setChildLabels(mChildLabel)
    }

    protected open fun getChildLeftLinearLayoutLayoutParams(): LayoutParams {
        return when (childWeight) {
            RIGHT_WEIGHT -> generateDefaultLayoutParams()
            LEFT_WEIGHT -> LayoutParams(0, LayoutParams.WRAP_CONTENT, 1f)
            else -> generateDefaultLayoutParams()
        }
    }

    fun setLabel(label: CharSequence?, defStyleAttr: Int = android.R.attr.textViewStyle) {
        mLabel = label
        if (tvLabel == null) {
            tvLabel = AppCompatTextView(context, null, defStyleAttr)
            childLeftLinearLayout.addView(tvLabel, generateDefaultLayoutParams())
        } else if (tvLabel?.parent == null) {
            childLeftLinearLayout.addView(tvLabel, generateDefaultLayoutParams())
        }
        tvLabel?.text = getStyleText()
        tvLabel?.apply {
            if (mLabelColor != null) {
                // setTextColor(mLabelColor)
            }
            setTextSize(TypedValue.COMPLEX_UNIT_PX, mLabelSize * 1.0f)
        }

        // mTagText 存在时更新 paddingLeft
        tvLabel?.paint?.measureText(mTagText?.toString() ?: "")?.run {
            if (paddingLeft > this) {
                updatePadding(left = (paddingLeft - this).toInt())
            }
        }
    }

    /**
     * 通过 SpannableStringBuilder 构建特定格式的文本
     */
    private fun getStyleText(): CharSequence? {
        return SpannableStringBuilder()
            .append(mTagText ?: "", ForegroundColorSpan(mTagTextColor), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            .append(
                mLabel ?: "",
                ForegroundColorSpan(mLabelColor?.defaultColor ?: Color.BLACK),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
    }


    fun setLabel(label: CharSequence?, @Px labelSize: Int = mLabelSize, labelColor: ColorStateList?) {
        setLabel(label)
        tvLabel?.apply {
            if (labelColor != null) {
                setTextColor(labelColor)
            }
            setTextSize(TypedValue.COMPLEX_UNIT_PX, labelSize * 1.0f)
        }

    }

    fun setLeftLinearLayout(@GravityFlag gravity: Int, @Px labelMarginEnd: Int = mLabelMarginEnd) {
        val layoutParams = childLeftLinearLayout.layoutParams as LayoutParams
        layoutParams.gravity = gravity
        layoutParams.marginEnd = labelMarginEnd
    }

    fun setChildLabels(
        childLabels: List<CharSequence>,
        childLabelSize: Int = mChildLabelSize,
        childLabelColor: ColorStateList? = mChildLabelColor,
        childLabelMarginTop: Int = mChildLabelMarginTop,
        defStyleAttr: Int = android.R.attr.textViewStyle
    ) {
        mChildLabel = childLabels
        mChildLabelSize = childLabelSize
        mChildLabelColor = childLabelColor
        mChildLabelMarginTop = childLabelMarginTop

        //移除所有子label
        childLeftLinearLayout.childCount.run {
            if (this > 1) {
                childLeftLinearLayout.removeViews(1, this - 1)
            }
        }

        for (childLabelText in childLabels) {
            val childLabel = AppCompatTextView(context, null, defStyleAttr)
            childLabel.text = childLabelText
            childLabel.setTextSize(TypedValue.COMPLEX_UNIT_PX, childLabelSize.toFloat())
            childLabelColor?.run {
                childLabel.setTextColor(this)
            }

            val lp = generateDefaultLayoutParams().apply {
                this.topMargin = childLabelMarginTop
            }

            childLeftLinearLayout.addView(childLabel, lp)
        }
    }

    fun setLabelBold(isBold: Boolean) {
        mLabelBold = isBold
        val tvLabel = tvLabel ?: return
        tvLabel.typeface = if (isBold) {
            Typeface.DEFAULT_BOLD
        } else {
            null
        }
    }

    fun setLeftIcon(icon: Drawable?) {
        mLeftIcon = icon
        val index = 0
        if (icon != null) {
            if (ivLeftIcon == null) {
                val lp = generateDefaultLayoutParams()
                lp.marginEnd = mLeftIconMarginEnd
                ivLeftIcon = AppCompatImageView(context, null)
                addView(ivLeftIcon, index, lp)
            } else if (ivLeftIcon?.parent == null) {
                addView(ivLeftIcon, index)
            }
            ivLeftIcon?.setImageDrawable(icon)
        } else {
            ivLeftIcon?.let {
                removeView(it)
                ivLeftIcon = null
            }
        }
    }

    /**
     * 表单item右边view容器，请重写 createRightChildViews 添加子view
     */
    private fun createRightLayout() {
        if (childRightLinearLayout.parent == null) {
            addView(childRightLinearLayout, getChildRightLinearLayoutLayoutParams().apply {
                this.gravity = mRightLayoutGravity
            })
        }
        if (childRightLinearLayout.orientation == HORIZONTAL) {
            childRightLinearLayout.gravity = Gravity.CENTER_VERTICAL or Gravity.END
        } else {
            childRightLinearLayout.gravity = Gravity.CENTER_HORIZONTAL or Gravity.END
        }
        setRightChildViews(*createRightChildViews().filterNotNull().toTypedArray())
        setRightLayoutChildMargin(this.rightLayoutChildMargin)
    }

    protected open fun getChildRightLinearLayoutLayoutParams(): LayoutParams {
        return when (childWeight) {
            RIGHT_WEIGHT -> LayoutParams(0, LayoutParams.WRAP_CONTENT, 1f)
            LEFT_WEIGHT -> generateDefaultLayoutParams()
            else -> LayoutParams(0, LayoutParams.WRAP_CONTENT, 1f)
        }
    }

    protected open fun createRightChildViews(): List<View?> {
        return listOf()
    }

    fun setRightLayoutChildMargin(marginPx: Int) {
        this.rightLayoutChildMargin = marginPx
        // // 给线性布局添加透明divider ， 在某些机型上，添加 divider 会导致线性布局开始位置也会空出一个 divider 大小
        // childRightLinearLayout.dividerDrawable = ShapeDrawable().apply {
        //     this.paint.color =  Color.BLACK
        //     if (childRightLinearLayout.orientation == HORIZONTAL) {
        //         this.intrinsicWidth = marginPx
        //     } else {
        //         this.intrinsicHeight = marginPx
        //     }
        // }
        // childRightLinearLayout.showDividers = SHOW_DIVIDER_MIDDLE

        if (childRightLinearLayout.orientation == HORIZONTAL) {
            childRightLinearLayout.children.forEachIndexed { index, view ->
                val layoutParams = view.layoutParams as LayoutParams
                layoutParams.marginEnd = if (index != childRightLinearLayout.childCount - 1) marginPx else 0
                view.layoutParams = layoutParams
            }
        } else {
            childRightLinearLayout.children.forEachIndexed { index, view ->
                if (index != childRightLinearLayout.childCount - 1) {
                    val layoutParams = view.layoutParams as LayoutParams
                    layoutParams.bottomMargin = if (index != childRightLinearLayout.childCount - 1) marginPx else 0
                    view.layoutParams = layoutParams
                }
            }
        }
    }


    fun setRightChildView(childView: View?, index: Int = 0) {
        if (childView == null) {
            if (childRightLinearLayout.getChildAt(index) != null) {
                childRightLinearLayout.removeViewAt(index)
            }
        } else if (childView.parent == null) {
            //View创建成功后若未设置过 layoutParams 默认为空
            val lp = childView.layoutParams
            if (lp == null) {
                childRightLinearLayout.addView(childView, index, generateDefaultLayoutParams())
            } else {
                //UIKitFormItemLabel 实际为横向线性布局，因此 generateDefaultLayoutParams() 得到的宽高都是自适应
                if (childRightLinearLayout.orientation == VERTICAL) {
                    childRightLinearLayout.addView(childView, index, generateDefaultLayoutParams())
                } else {
                    childRightLinearLayout.addView(childView, index)
                }
            }
        }
    }

    fun setRightChildViews(vararg childViews: View) {
        childViews.forEachIndexed { index, view ->
            setRightChildView(view, index)
        }
    }

    fun setCanSelectable(
        canSelectable: Boolean,
        @DrawableRes backgroundResId: Int = R.drawable.uikit_form_item_selector
    ) {
        mCanSelectable = canSelectable
        if (mCanSelectable) {
            setBackgroundResource(backgroundResId)
        }
    }

    /**
     * 该方法仅可在更改右边子view属性时才可调用
     */
    protected fun <V1, V2 : View, V3> commonHandelChangeView(
        changeViewAttr: KMutableProperty0<V1?>,
        changeView: KMutableProperty0<V2?>,
        createViewMethod: KFunction0<V2?>,
        changeViewIndex: Int = 0,
        setViewMethod: ((V1) -> V3)?,
        changViewOpeListener: () -> Unit = {},
    ) {

        val attrValue = changeViewAttr.get()
        var view = changeView.get()

        //设置的属性为空且属性对应的view不为空
        if (attrValue == null && view != null) {
            //移除对应的view
            setRightChildView(null, changeViewIndex)
            changeView.set(null)
        } else if (attrValue != null) {
            //设置的属性不为空但对应的view为空
            if (view == null) {
                //调用创建该view的方法,创建成功添加view
                createViewMethod()?.apply {
                    changeView.set(this)
                    //设值之后需要重新取值
                    view = changeView.get()
                    setRightChildView(view, changeViewIndex)
                }
            } else {
                //设置的属性不为空且对应的view也存在，调用对应的set方法设置属性
                setViewMethod?.invoke(attrValue)
            }
            changViewOpeListener()
        }

        //添加或移除右边 View 时重置子 view 之间的距离
        setRightLayoutChildMargin(this.rightLayoutChildMargin)
    }

}

@Retention(RetentionPolicy.SOURCE)
@IntDef(
    Gravity.TOP,
    Gravity.BOTTOM,
    Gravity.START,
    Gravity.END,
    Gravity.CENTER_HORIZONTAL,
    Gravity.CENTER_VERTICAL,
    Gravity.CENTER
)
annotation class GravityFlag
