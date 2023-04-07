package com.hl.uikit.pickerview

import android.os.Bundle
import android.view.View
import com.hl.uikit.R
import com.hl.uikit.actionsheet.Alert2SheetDialogFragment

open class OptionsPickerDialogFragment<T> : Alert2SheetDialogFragment() {
    private var isLinkageMode: Boolean? = null
    protected var mPickerView: UIKitOptionsExtPickerView<T>? = null
    private val mDefPositions = arrayListOf(0, 0, 0, 0, 0, 0)

    var opt1Pos: Int = 0
        private set(value) {
            mPickerView?.opt1SelectedPosition = value
            mDefPositions[0] = value
            if (mPickerView != null) {
                field = value
            }
        }
        get() = mPickerView?.opt1SelectedPosition ?: 0

    var opt2Pos: Int = 0
        private set(value) {
            mPickerView?.opt2SelectedPosition = value
            mDefPositions[1] = value
            if (mPickerView != null) {
                field = value
            }
        }
        get() = mPickerView?.opt2SelectedPosition ?: 0

    var opt3Pos: Int = 0
        private set(value) {
            mPickerView?.opt3SelectedPosition = value
            mDefPositions[2] = value
            if (mPickerView != null) {
                field = value
            }
        }
        get() = mPickerView?.opt3SelectedPosition ?: 0

    var opt4Pos: Int = 0
        private set(value) {
            mPickerView?.opt4SelectedPosition = value
            mDefPositions[3] = value
            if (mPickerView != null) {
                field = value
            }
        }
        get() = mPickerView?.opt4SelectedPosition ?: 0

    var opt5Pos: Int = 0
        private set(value) {
            mPickerView?.opt5SelectedPosition = value
            mDefPositions[4] = value
            if (mPickerView != null) {
                field = value
            }
        }
        get() = mPickerView?.opt5SelectedPosition ?: 0

    var opt6Pos: Int = 0
        private set(value) {
            mPickerView?.opt6SelectedPosition = value
            mDefPositions[5] = value
            if (mPickerView != null) {
                field = value
            }
        }
        get() = mPickerView?.opt5SelectedPosition ?: 0

    private var mData1: List<T>? = null
    private var mData2: List<T>? = null
    private var mData3: List<T>? = null
    private var mData4: List<T>? = null
    private var mData5: List<T>? = null
    private var mData6: List<T>? = null
    private var mLinkageData2: List<List<T>>? = null
    private var mLinkageData3: List<List<List<T>>>? = null
    protected open val customViewId = R.layout.uikit_options_picker_dialog_fragment

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setCustomView(customViewId)
        mPickerView = view.findViewById(R.id.pickerView)
        when {
            isLinkageMode == true && mData1 != null -> {
                setLinkageData(
                    data1 = mData1!!,
                    data2 = mLinkageData2,
                    data3 = mLinkageData3,
                    data4 = mData4,
                    data5 = mData5,
                    data6 = mData6
                )
            }
            isLinkageMode == false -> {
                setData(
                    data1 = mData1,
                    data2 = mData2,
                    data3 = mData3,
                    data4 = mData4,
                    data5 = mData5,
                    data6 = mData6
                )
            }
        }
        setDefPositions(
            mDefPositions.getOrNull(0),
            mDefPositions.getOrNull(1),
            mDefPositions.getOrNull(2),
            mDefPositions.getOrNull(3),
            mDefPositions.getOrNull(4),
            mDefPositions.getOrNull(5)
        )
    }

    /**
     * 设置不联动列表数据，支持 1 - 6 列数据
     */
    fun setData(
        data1: List<T>? = null,
        data2: List<T>? = null,
        data3: List<T>? = null,
        data4: List<T>? = null,
        data5: List<T>? = null,
        data6: List<T>? = null
    ) {
        isLinkageMode = false
        mData1 = data1
        mData2 = data2
        mData3 = data3
        mData4 = data4
        mData5 = data5
        mData6 = data6
        mPickerView?.setData(data1, data2, data3)
        if (data4 != null) {
            mPickerView?.setData4(data4)
        }
        if (data5 != null) {
            mPickerView?.setData5(data5)
        }
        if (data6 != null) {
            mPickerView?.setData6(data6)
        }
    }

    /**
     * 设置联动列表数据，仅支持 1 - 3 列数据联动
     */
    fun setLinkageData(
        data1: List<T>,
        data2: List<List<T>>? = null,
        data3: List<List<List<T>>>? = null,
        data4: List<T>? = null,
        data5: List<T>? = null,
        data6: List<T>? = null
    ) {
        isLinkageMode = true
        mData1 = data1
        mLinkageData2 = data2
        mLinkageData3 = data3
        mData4 = data4
        mData5 = data5
        mPickerView?.setLinkageData(data1, data2, data3)
        if (data4 != null) {
            mPickerView?.setData4(data4)
        }
        if (data5 != null) {
            mPickerView?.setData5(data5)
        }
        if (data6 != null) {
            mPickerView?.setData6(data6)
        }
    }

    fun setDefPositions(
        opt1Pos: Int? = null,
        opt2Pos: Int? = null,
        opt3Pos: Int? = null,
        opt4Pos: Int? = null,
        opt5Pos: Int? = null,
        opt6Pos: Int? = null
    ) {
        if (opt1Pos != null) {
            this.opt1Pos = opt1Pos
        }
        if (opt2Pos != null) {
            this.opt2Pos = opt2Pos
        }

        if (opt3Pos != null) {
            this.opt3Pos = opt3Pos
        }

        if (opt4Pos != null) {
            this.opt4Pos = opt4Pos
        }

        if (opt5Pos != null) {
            this.opt5Pos = opt5Pos
        }
        if (opt6Pos != null) {
            this.opt6Pos = opt6Pos
        }
    }
}