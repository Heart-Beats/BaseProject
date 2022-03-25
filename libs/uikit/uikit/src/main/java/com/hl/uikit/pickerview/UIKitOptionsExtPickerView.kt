package com.hl.uikit.pickerview

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import com.zyyoona7.picker.OptionsPickerView
import com.zyyoona7.wheel.WheelView
import com.hl.uikit.R

class UIKitOptionsExtPickerView<T> : OptionsPickerView<T> {
    companion object {
        private const val ID_OPTIONS_WV_4 = 4
        private const val ID_OPTIONS_WV_5 = 5
        private const val ID_OPTIONS_WV_6 = 6
    }

    private var mOnOptionsExtSelectedListener: ((
        opt1Pos: Int, opt1Data: T?,
        opt2Pos: Int, opt2Data: T?,
        opt3Pos: Int, opt3Data: T?,
        opt4Pos: Int, opt4Data: T?,
        opt5Pos: Int, opt5Data: T?,
        opt6Pos: Int, opt6Data: T?
    ) -> Unit)? = null
    private lateinit var mOptionsWv4: WheelView<T>
    private lateinit var mOptionsWv5: WheelView<T>
    private lateinit var mOptionsWv6: WheelView<T>
    var opt4SelectedPosition: Int = 0
        set(value) {
            field = value
            mOptionsWv4.selectedItemPosition = value
        }
        get() = mOptionsWv4.selectedItemPosition
    var opt5SelectedPosition: Int = 0
        set(value) {
            field = value
            mOptionsWv5.selectedItemPosition = value
        }
        get() = mOptionsWv5.selectedItemPosition
    var opt6SelectedPosition: Int = 0
        set(value) {
            field = value
            mOptionsWv6.selectedItemPosition = value
        }
        get() = mOptionsWv6.selectedItemPosition

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(
        context,
        attrs,
        R.attr.uikit_optionsPickerViewStyle
    )

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init()
        val ta = context.obtainStyledAttributes(
            attrs,
            R.styleable.UIKitOptionsExtPickerView,
            defStyleAttr,
            0
        )
        val textSize =
            ta.getDimensionPixelSize(R.styleable.UIKitOptionsExtPickerView_android_textSize, 20)
                .toFloat()
        setTextSize(textSize, false)
        val textColor =
            ta.getColorStateList(R.styleable.UIKitOptionsExtPickerView_android_textColor)
        if (textColor != null) {
            setNormalItemTextColor(textColor.defaultColor)
            val stateSet = intArrayOf(android.R.attr.state_selected)
            setSelectedItemTextColor(textColor.getColorForState(stateSet, textColor.defaultColor))
        }
        val curved = ta.getBoolean(R.styleable.UIKitOptionsExtPickerView_uikit_wheelCurved, false)
        setCurved(curved)
        val isShowDivider =
            ta.getBoolean(R.styleable.UIKitOptionsExtPickerView_uikit_showWheelDivider, false)
        setShowDivider(isShowDivider)
        val dividerColor =
            ta.getColorStateList(R.styleable.UIKitOptionsExtPickerView_uikit_wheelDividerColor)
        if (dividerColor != null) {
            setDividerColor(dividerColor.defaultColor)
        }
        val dividerHeight = ta.getDimensionPixelSize(
            R.styleable.UIKitOptionsExtPickerView_uikit_wheelDividerHeight,
            0
        )
        if (dividerHeight > 0) {
            setDividerHeight(dividerHeight.toFloat())
        }
        val lineSpacing = ta.getDimensionPixelSize(
            R.styleable.UIKitOptionsExtPickerView_uikit_wheelLineSpacing,
            0
        )
        if (lineSpacing > 0) {
            setLineSpacing(lineSpacing.toFloat())
        }
        val visibleItems =
            ta.getInteger(R.styleable.UIKitOptionsExtPickerView_uikit_wheelVisibleItems, -1)
        if (visibleItems > 0) {
            setVisibleItems(visibleItems)
        }
        setCurvedArcDirectionFactor(0.1f)
        setRefractRatio(0.9f)
        ta.recycle()
    }

    override fun setRefractRatio(curvedRefractRatio: Float) {
        super.setRefractRatio(curvedRefractRatio)
        mOptionsWv4.refractRatio = curvedRefractRatio
        mOptionsWv5.refractRatio = curvedRefractRatio
        mOptionsWv6.refractRatio = curvedRefractRatio
    }

    override fun setCurvedArcDirectionFactor(curvedArcDirectionFactor: Float) {
        super.setCurvedArcDirectionFactor(curvedArcDirectionFactor)
        mOptionsWv4.curvedArcDirectionFactor = curvedArcDirectionFactor
        mOptionsWv5.curvedArcDirectionFactor = curvedArcDirectionFactor
        mOptionsWv6.curvedArcDirectionFactor = curvedArcDirectionFactor
    }

    private fun init() {
        mOptionsWv4 = WheelView(context)
        mOptionsWv4.id = ID_OPTIONS_WV_4
        mOptionsWv5 = WheelView(context)
        mOptionsWv5.id = ID_OPTIONS_WV_5
        mOptionsWv6 = WheelView(context)
        mOptionsWv6.id = ID_OPTIONS_WV_6

        val layoutParams = LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT)
        layoutParams.weight = 1f
        addView(mOptionsWv4, layoutParams)
        addView(mOptionsWv5, layoutParams)
        addView(mOptionsWv6, layoutParams)

        mOptionsWv4.onItemSelectedListener = this
        mOptionsWv5.onItemSelectedListener = this
        mOptionsWv6.onItemSelectedListener = this
        mOptionsWv4.isAutoFitTextSize = true
        mOptionsWv5.isAutoFitTextSize = true
        mOptionsWv6.isAutoFitTextSize = true

        mOptionsWv4.onWheelChangedListener = this
        mOptionsWv5.onWheelChangedListener = this
        mOptionsWv6.onWheelChangedListener = this

        mOptionsWv4.visibility = View.GONE
        mOptionsWv5.visibility = View.GONE
        mOptionsWv6.visibility = View.GONE
    }

    override fun setTextSize(textSize: Float, isSp: Boolean) {
        super.setTextSize(textSize, isSp)
        mOptionsWv4.setTextSize(textSize, isSp)
        mOptionsWv5.setTextSize(textSize, isSp)
        mOptionsWv6.setTextSize(textSize, isSp)
    }

    override fun setCurved(isCurved: Boolean) {
        super.setCurved(isCurved)
        mOptionsWv4.isCurved = isCurved
        mOptionsWv5.isCurved = isCurved
        mOptionsWv6.isCurved = isCurved
    }

    override fun setShowDivider(isShowDivider: Boolean) {
        super.setShowDivider(isShowDivider)
        mOptionsWv4.isShowDivider = isShowDivider
        mOptionsWv5.isShowDivider = isShowDivider
        mOptionsWv6.isShowDivider = isShowDivider
    }

    override fun setDividerColor(dividerColor: Int) {
        super.setDividerColor(dividerColor)
        mOptionsWv4.dividerColor = dividerColor
        mOptionsWv5.dividerColor = dividerColor
        mOptionsWv6.dividerColor = dividerColor
    }

    override fun setDividerHeight(dividerHeight: Float, isDp: Boolean) {
        super.setDividerHeight(dividerHeight, isDp)
        mOptionsWv4.setDividerHeight(dividerHeight, isDp)
        mOptionsWv5.setDividerHeight(dividerHeight, isDp)
        mOptionsWv6.setDividerHeight(dividerHeight, isDp)
    }

    override fun setLineSpacing(lineSpacing: Float, isDp: Boolean) {
        super.setLineSpacing(lineSpacing, isDp)
        mOptionsWv4.setLineSpacing(lineSpacing, isDp)
        mOptionsWv5.setLineSpacing(lineSpacing, isDp)
        mOptionsWv6.setLineSpacing(lineSpacing, isDp)
    }

    override fun setVisibleItems(visibleItems: Int) {
        super.setVisibleItems(visibleItems)
        mOptionsWv4.visibleItems = visibleItems
        mOptionsWv5.visibleItems = visibleItems
        mOptionsWv6.visibleItems = visibleItems
    }

    override fun setSelectedItemTextColor(selectedItemTextColor: Int) {
        super.setSelectedItemTextColor(selectedItemTextColor)
        mOptionsWv4.selectedItemTextColor = selectedItemTextColor
        mOptionsWv5.selectedItemTextColor = selectedItemTextColor
        mOptionsWv6.selectedItemTextColor = selectedItemTextColor
    }

    override fun setNormalItemTextColor(textColor: Int) {
        super.setNormalItemTextColor(textColor)
        mOptionsWv4.normalItemTextColor = textColor
        mOptionsWv5.normalItemTextColor = textColor
        mOptionsWv6.normalItemTextColor = textColor
    }

    fun setData4(data4: List<T>) {
        if (data4.isNotEmpty()) {
            mOptionsWv4.visibility = View.VISIBLE
            mOptionsWv4.data = data4
        } else {
            mOptionsWv4.visibility = View.GONE
        }
    }

    fun setData5(data5: List<T>) {
        if (data5.isNotEmpty()) {
            mOptionsWv5.visibility = View.VISIBLE
            mOptionsWv5.data = data5
        } else {
            mOptionsWv5.visibility = View.GONE
        }
    }

    fun setData6(data6: List<T>) {
        if (data6.isNotEmpty()) {
            mOptionsWv6.visibility = View.VISIBLE
            mOptionsWv6.data = data6
        } else {
            mOptionsWv6.visibility = View.GONE
        }
    }

    fun setOpt4SelectedPosition(position: Int, isSmoothScroll: Boolean, smoothDuration: Int) {
        mOptionsWv4.setSelectedItemPosition(position, isSmoothScroll, smoothDuration)
    }

    fun setOpt5SelectedPosition(position: Int, isSmoothScroll: Boolean, smoothDuration: Int) {
        mOptionsWv5.setSelectedItemPosition(position, isSmoothScroll, smoothDuration)
    }

    fun setOpt6SelectedPosition(position: Int, isSmoothScroll: Boolean, smoothDuration: Int) {
        mOptionsWv6.setSelectedItemPosition(position, isSmoothScroll, smoothDuration)
    }

    fun setOnOptionsExtSelectedListener(
        listener: (
            opt1Pos: Int, opt1Data: T?,
            opt2Pos: Int, opt2Data: T?,
            opt3Pos: Int, opt3Data: T?,
            opt4Pos: Int, opt4Data: T?,
            opt5Pos: Int, opt5Data: T?,
            opt6Pos: Int, opt6Data: T?
        ) -> Unit
    ) {
        mOnOptionsExtSelectedListener = listener
    }

    override fun onItemSelected(wheelView: WheelView<T>?, data: T, position: Int) {
        super.onItemSelected(wheelView, data, position)

        val opt1Pos = opt1SelectedPosition
        val opt2Pos = opt2SelectedPosition
        val opt3Pos = opt3SelectedPosition
        val opt4Pos = mOptionsWv4.selectedItemPosition
        val opt5Pos = mOptionsWv5.selectedItemPosition
        val opt6Pos = mOptionsWv6.selectedItemPosition
        val isOpt1Shown = optionsWv1.visibility == View.VISIBLE
        val isOpt2Shown = optionsWv2.visibility == View.VISIBLE
        val isOpt3Shown = optionsWv3.visibility == View.VISIBLE
        val isOpt4Shown = mOptionsWv4.visibility == View.VISIBLE
        val isOpt5Shown = mOptionsWv5.visibility == View.VISIBLE
        val isOpt6Shown = mOptionsWv6.visibility == View.VISIBLE

        val opt1Data = if (isOpt1Shown) {
            optionsWv1.selectedItemData
        } else {
            null
        }

        val opt2Data = if (isOpt2Shown) {
            optionsWv2.selectedItemData
        } else {
            null
        }

        val opt3Data = if (isOpt3Shown) {
            optionsWv3.selectedItemData
        } else {
            null
        }

        val opt4Data = if (isOpt4Shown) {
            mOptionsWv4.selectedItemData
        } else {
            null
        }

        val opt5Data = if (isOpt5Shown) {
            mOptionsWv5.selectedItemData
        } else {
            null
        }
        val opt6Data = if (isOpt6Shown) {
            mOptionsWv6.selectedItemData
        } else {
            null
        }
        mOnOptionsExtSelectedListener?.invoke(
            opt1Pos, opt1Data,
            opt2Pos, opt2Data,
            opt3Pos, opt3Data,
            opt4Pos, opt4Data,
            opt5Pos, opt5Data,
            opt6Pos, opt6Data
        )
    }
}