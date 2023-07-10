package com.hl.uikit.pickerview

import android.os.Bundle
import android.util.SparseArray
import android.view.View
import android.widget.CheckedTextView
import android.widget.TextView
import com.hl.uikit.R
import com.hl.uikit.utils.onClick
import com.zyyoona7.wheel.WheelView
import java.text.SimpleDateFormat
import java.util.*

class RangeTimePickerDialogFragment : TimePickerDialogFragment() {
    companion object {
        private val MODE_MONTH_FORMAT = SimpleDateFormat("yyyy-MM", Locale.ENGLISH)
        private val MODE_DAY_FORMAT = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
        private val MODE_DEF_FORMAT = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH)
        private const val TYPE_MONTH_START = 0
        private const val TYPE_MONTH_END = 1
        private const val TYPE_DAY_START = 2
        private const val TYPE_DAY_END = 3
    }

    override val customViewId: Int
        get() = R.layout.uikit_range_time_picker_dialog_fragment

    private var mMonthMode: Boolean? = null
    private var isFromPickerEvent: Boolean = true

    var enableSwitchMode: Boolean = true
    var timeDisplayFormat: SimpleDateFormat? = null

    private val rangeTimes = SparseArray<Calendar>()

    private var btnSwitchMode: TextView? = null
    private var ctvStart: CheckedTextView? = null
    private var ctvEnd: CheckedTextView? = null

    /**
     * 仅在 enableSwitchMode == true 时，monthMode 有效
     * monthMode：true，设置开始月份
     * monthMode：false，设置开始日期
     */
    private val setMonthRangeStartTime: (monthMode: Boolean, time: Calendar?) -> Unit =
        { monthMode, time ->
            if (enableSwitchMode) {
                val type = when (monthMode) {
                    true -> TYPE_MONTH_START
                    false -> TYPE_DAY_START
                }
                rangeTimes.put(type, time)
            }
            rangeStartTime = time
        }

    private val getMonthRangeStartTime: (monthMode: Boolean, defTime: Calendar?) -> Calendar? =
        { monthMode, defTime ->
            if (monthMode) {
                rangeTimes.get(TYPE_MONTH_START)
            } else {
                rangeTimes.get(TYPE_DAY_START)
            } ?: defTime
        }

    /**
     * 仅在 enableSwitchMode == true 时，monthMode 有效
     * monthMode：true，设置结束月份
     * monthMode：false，设置结束日期
     */
    private val setMonthRangeEndTime: (monthMode: Boolean, time: Calendar?) -> Unit =
        { monthMode, time ->
            if (enableSwitchMode) {
                val type = when (monthMode) {
                    true -> TYPE_MONTH_END
                    false -> TYPE_DAY_END
                }
                rangeTimes.put(type, time)
            }
            rangeEndTime = time
        }

    private val getMonthRangeEndTime: (monthMode: Boolean, defTime: Calendar?) -> Calendar? =
        { monthMode, defTime ->
            if (monthMode) {
                rangeTimes.get(TYPE_MONTH_END)
            } else {
                rangeTimes.get(TYPE_DAY_END)
            } ?: defTime
        }

    var rangeStartTime: Calendar? = null
        set(value) {
            field = value.cloneTime()
            val format = getTimeFormat()
            ctvStart?.text = when {
                mMonthMode == true -> if (value != null) {
                    format.format(value.time)
                } else {
                    "开始月份"
                }
                mMonthMode == false -> if (value != null) {
                    format.format(value.time)
                } else {
                    "开始日期"
                }
                !showYear && !showMonth -> if (value != null) {
                    format.format(value.time)
                } else {
                    "开始时间"
                }
                else -> "timeDisplayFormat"
            }
        }

    var rangeEndTime: Calendar? = null
        set(value) {
            field = value.cloneTime()
            val format = getTimeFormat()
            ctvEnd?.text = when {
                mMonthMode == true -> if (value != null) {
                    MODE_MONTH_FORMAT.format(value.time)
                } else {
                    "结束月份"
                }
                mMonthMode == false -> if (value != null) {
                    MODE_DAY_FORMAT.format(value.time)
                } else {
                    "结束日期"
                }
                !showYear && !showMonth -> if (value != null) {
                    format.format(value.time)
                } else {
                    "结束时间"
                }
                else -> "timeDisplayFormat"
            }
        }

    var rangeTimePositiveClickListener: (dialog: RangeTimePickerDialogFragment, startTime: Calendar?, endTime: Calendar?) -> Unit =
        { _, _, _ -> }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView(view)

        updateMonthMode()
        enableSwitchMode = showYear && (showMonth || showDay) && enableSwitchMode //仅仅在显示 年月、年月日 时支持
        setMonthRangeEndTime(
            isMonthMode(), getMonthRangeEndTime(isMonthMode(), rangeEndTime)
        )
        updateModeDisplay()
        initSwitchModeListener()
        initOptionsSelectedListener()
        initCtvListener()
        ctvStart?.isChecked = true
        if (rangeStartTime != null) {
            time = rangeStartTime!!
            initDefPositions()
        }
        positiveClickListener = {
            rangeTimePositiveClickListener(this, rangeStartTime, rangeEndTime)
        }
        btnSwitchMode?.visibility = if (enableSwitchMode) {
            View.VISIBLE
        } else {
            View.GONE
        }
    }

    private fun initView(rootView: View) {
        btnSwitchMode = rootView.findViewById(R.id.btnSwitchMode)
        ctvStart = rootView.findViewById(R.id.ctvStart)
        ctvEnd = rootView.findViewById(R.id.ctvEnd)
    }

    private fun updateMonthMode() {
        mMonthMode = when {
            showYear && showMonth && !showDay -> true
            showYear && showMonth && showDay -> false
            else -> null
        }
    }

    private fun initCtvListener() {
        ctvStart?.onClick {
            ctvStart?.isChecked = true
            ctvEnd?.isChecked = false
            if (rangeStartTime == null) {
                setMonthRangeStartTime(isMonthMode(), Calendar.getInstance())
            }
            time = rangeStartTime!!
            initDefPositions()
        }
        ctvEnd?.onClick {
            ctvStart?.isChecked = false
            ctvEnd?.isChecked = true
            if (rangeEndTime == null) {
                setMonthRangeEndTime(isMonthMode(), Calendar.getInstance())
            }
            time = rangeEndTime!!
            initDefPositions()
        }
    }

    fun isMonthMode(): Boolean {
        return mMonthMode == true
    }

    fun getTimeFormat(): SimpleDateFormat {
        return if (timeDisplayFormat != null) {
            timeDisplayFormat!!
        } else {
            when (mMonthMode) {
                true -> MODE_MONTH_FORMAT
                false -> MODE_DAY_FORMAT
                else -> MODE_DEF_FORMAT
            }
        }
    }

    private fun initOptionsSelectedListener() {
        mPickerView?.setOnPickerScrollStateChangedListener {
            if (it == WheelView.SCROLL_STATE_DRAGGING) {
                isFromPickerEvent = true
            }
        }
        mPickerView?.setOnOptionsExtSelectedListener { _, _,
                                                       _, _,
                                                       _, _,
                                                       _, _,
                                                       _, _,
                                                       _, _ ->
            if (!isFromPickerEvent) {
                return@setOnOptionsExtSelectedListener
            }
            if (isCheckedStart()) {
                setMonthRangeStartTime(isMonthMode(), pickerTime.cloneTime())
            } else {
                setMonthRangeEndTime(isMonthMode(), pickerTime.cloneTime())
            }
        }
    }

    private fun initSwitchModeListener() {
        btnSwitchMode?.onClick {
            ctvStart?.performClick()
            showDay = !showDay
            updateMonthMode()
            updateModeDisplay()
//            time = if (isCheckedStart()) {
//                rangeStartTime ?: Calendar.getInstance()
//            } else {
//                rangeEndTime ?: Calendar.getInstance()
//            }
            initOptData()
            initPickerData()
            initDefPositions()
        }
    }

    private fun isCheckedStart(): Boolean {
        return ctvStart?.isChecked == true
    }

    private fun updateModeDisplay() {
        setMonthRangeStartTime(
            isMonthMode(), getMonthRangeStartTime(isMonthMode(), rangeStartTime)
        )
        setMonthRangeEndTime(
            isMonthMode(), getMonthRangeEndTime(isMonthMode(), null)
        )
        if (!enableSwitchMode) {
            return
        }
        btnSwitchMode?.text = when (mMonthMode) {
            true -> "按月选择"
            else -> "按日选择"
        }
    }
}