package com.hl.uikit.pickerview

import android.os.Bundle
import android.view.View
import com.zyyoona7.wheel.IWheelEntity
import com.hl.uikit.R
import java.util.*

open class TimePickerDialogFragment :
    OptionsPickerDialogFragment<TimePickerDialogFragment.BaseItem>() {
    companion object {
        private const val YEAR_COUNT = 50//可以选择的年份个数
    }

    protected var showYear: Boolean = true
    protected var showMonth: Boolean = true
    protected var showDay: Boolean = true
    private var showHour: Boolean = false
    private var showMinute: Boolean = false
    private var showSecond: Boolean = false
    private val opt1Item: MutableList<BaseItem> = mutableListOf()
    private var opt2Item: MutableList<MutableList<BaseItem>> = mutableListOf()
    private var opt3Item: MutableList<MutableList<MutableList<BaseItem>>> = mutableListOf()
    private var opt4Item: MutableList<BaseItem> = mutableListOf()
    private var opt5Item: MutableList<BaseItem> = mutableListOf()
    private var opt6Item: MutableList<BaseItem> = mutableListOf()
    override var rootLayoutId: Int = R.layout.uikit_time_picker_root_dialog_fragment
    var startTime: Calendar? = null
        set(value) {
            field = value.cloneTime()
        }
    var endTime: Calendar = Calendar.getInstance()
        set(value) {
            field = value.cloneTime() ?: value
        }
    protected var pickerTime: Calendar = Calendar.getInstance()
        get() {
            if (showYear) {
                opt1Item.getOrNull(opt1Pos)?.let { yearItem ->
                    val year = yearItem.value
                    field.set(Calendar.YEAR, year)
                }
            }
            if (showMonth) {
                opt2Item.getOrNull(opt1Pos)?.getOrNull(opt2Pos)?.let { monthItem ->
                    val month = monthItem.value - 1
                    field.set(Calendar.MONTH, month)
                }
            }
            if (showDay) {
                opt3Item.getOrNull(opt1Pos)
                    ?.getOrNull(opt2Pos)
                    ?.getOrNull(opt3Pos)
                    ?.let { dayItem ->
                        val day = dayItem.value
                        field.set(Calendar.DAY_OF_MONTH, day)
                    }
            }
            if (showHour) {
                opt4Item.getOrNull(opt4Pos)?.let { hourItem ->
                    val hour = hourItem.value
                    field.set(Calendar.HOUR_OF_DAY, hour)
                }
            }
            if (showMinute) {
                opt5Item.getOrNull(opt5Pos)?.let { minuteItem ->
                    val minute = minuteItem.value
                    field.set(Calendar.MINUTE, minute)
                }
            }
            if (showSecond) {
                opt6Item.getOrNull(opt6Pos)?.let { secondItem ->
                    val second = secondItem.value
                    field.set(Calendar.SECOND, second)
                }
            }
            return field
        }
    var time: Calendar = Calendar.getInstance()
        set(value) {
            field = value.cloneTime()!!
        }
    var timePositiveClickListener: (dialog: TimePickerDialogFragment, time: Calendar) -> Unit =
        { _, _ -> }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initOptData()
        initPickerData()
        initDefPositions()
        positiveClickListener = {
            timePositiveClickListener(this, pickerTime)
        }
    }

    private fun <T> MutableList<T>.showControl(isShow: Boolean): MutableList<T>? {
        return if (isShow) {
            return this
        } else {
            null
        }
    }

    protected fun initPickerData() {
        pickerView?.isResetSelectedPosition = true
        when {
            showYear && !showMonth && !showDay -> {
                setData(
                    data1 = opt1Item,
                    data4 = opt4Item.showControl(showHour),
                    data5 = opt5Item.showControl(showMinute),
                    data6 = opt6Item.showControl(showSecond)
                )
            }
            showYear || showMonth || showDay -> {
                setLinkageData(
                    data1 = opt1Item,
                    data2 = opt2Item.showControl(showMonth),
                    data3 = opt3Item.showControl(showDay),
                    data4 = opt4Item.showControl(showHour),
                    data5 = opt5Item.showControl(showMinute),
                    data6 = opt6Item.showControl(showSecond)
                )
            }
            else -> {
                setData(
                    data4 = opt4Item.showControl(showHour),
                    data5 = opt5Item.showControl(showMinute),
                    data6 = opt6Item.showControl(showSecond)
                )
            }
        }
    }

    protected fun initDefPositions() {
        val calendar = time
        var position = calendar.get(Calendar.YEAR) - opt1Item[0].value
        val yearPosition = if (position in 0 until opt1Item.size) {
            position
        } else {
            0
        }

        position = calendar.get(Calendar.MONTH) + 1 - opt2Item[yearPosition][0].value
        val monthSize = opt2Item[yearPosition].size
        val monthPosition = if (position in 0 until monthSize) {
            position
        } else {
            0
        }
        val daySize = if (showDay) {
            position =
                calendar.get(Calendar.DAY_OF_MONTH) - opt3Item[yearPosition][monthPosition][0].value
            opt3Item[yearPosition][monthPosition].size
        } else {
            -1
        }
        val dayPosition = if (position in 0 until daySize) {
            position
        } else {
            null
        }
        val hourPosition = if (showHour) {
            calendar.get(Calendar.HOUR_OF_DAY)
        } else {
            null
        }
        val minutePosition = if (showMinute) {
            calendar.get(Calendar.MINUTE)
        } else {
            null
        }
        val secondPosition = if (showSecond) {
            calendar.get(Calendar.SECOND)
        } else {
            null
        }
        setDefPositions(
            yearPosition,
            monthPosition,
            dayPosition,
            hourPosition,
            minutePosition,
            secondPosition
        )
    }

    fun setType(
        showYear: Boolean = true,
        showMonth: Boolean = true,
        showDay: Boolean = true,
        showHour: Boolean = false,
        showMinute: Boolean = false,
        showSecond: Boolean = false
    ) {
        this.showYear = showYear
        this.showMonth = showMonth
        this.showDay = showDay
        this.showHour = showHour
        this.showMinute = showMinute
        this.showSecond = showSecond
    }

    protected fun initOptData() {
        val start = generateStartTime()
        val end = endTime

        val startYear = start.get(Calendar.YEAR)
        val startMonth = start.get(Calendar.MONTH) + 1
        val startDay = start.get(Calendar.DAY_OF_MONTH)
        val endYear = end.get(Calendar.YEAR)
        val endMonth = end.get(Calendar.MONTH) + 1
        val endDay = end.get(Calendar.DAY_OF_MONTH)

        val calendar = Calendar.getInstance()
        opt1Item.clear()
        opt2Item.clear()
        opt3Item.clear()
        for (year in startYear..endYear) {
            opt1Item.add(YearItem(year))//年
            val monthList = mutableListOf<BaseItem>()
            opt2Item.add(monthList)//月
            val tempM: MutableList<MutableList<BaseItem>> = mutableListOf()
            opt3Item.add(tempM)//日
            for (month in 1..12) {
                if (year == startYear && month < startMonth) {
                    continue
                }
                if (year == endYear && month > endMonth) {
                    continue
                }
                monthList.add(MonthItem(month))
                calendar.set(year, month - 1, 1)
                val max = calendar.getActualMaximum(Calendar.DATE)
                val dayList = mutableListOf<BaseItem>()
                tempM.add(dayList)
                for (day in 1..max) {
                    if (year == startYear && month == startMonth && day < startDay) {
                        continue
                    }
                    if (year == endYear && month == endMonth && day > endDay) {
                        continue
                    }
                    dayList.add(DayItem(day))
                }
            }
        }
        opt4Item.clear()
        if (showHour) {
            for (hour in 0..23) {
                opt4Item.add(HourItem(hour))
            }
        }
        opt5Item.clear()
        if (showMinute) {
            for (minute in 0..59) {
                opt5Item.add(MinuteItem(minute))
            }
        }
        opt6Item.clear()
        if (showSecond) {
            for (second in 0..59) {
                opt6Item.add(SecondItem(second))
            }
        }
    }

    private fun generateStartTime(): Calendar {
        var localTime = startTime
        return if (localTime != null) {
            localTime
        } else {
            localTime = Calendar.getInstance()
            localTime.timeInMillis = endTime.timeInMillis
            localTime.add(Calendar.YEAR, 1 - YEAR_COUNT)
            localTime.set(Calendar.MONTH, 0)
            localTime.set(Calendar.DAY_OF_MONTH, 1)
            localTime
        }
    }

    private data class YearItem(override var value: Int) : BaseItem(value) {
        override fun getWheelText(): String {
            return "${value}年"
        }
    }

    private data class MonthItem(override var value: Int) : BaseItem(value) {
        override fun getWheelText(): String {
            return "${value}月"
        }
    }

    private data class DayItem(val day: Int) : BaseItem(day) {
        override fun getWheelText(): String {
            return "${day}日"
        }
    }

    private data class HourItem(val hour: Int) : BaseItem(hour) {
        override fun getWheelText(): String {
            return "${hour}时"
        }
    }

    private data class MinuteItem(val minute: Int) : BaseItem(minute) {
        override fun getWheelText(): String {
            return "${minute}分"
        }
    }

    private data class SecondItem(val second: Int) : BaseItem(second) {
        override fun getWheelText(): String {
            return "${second}秒"
        }
    }

    abstract class BaseItem(open val value: Int) : IWheelEntity

    protected fun Calendar?.cloneTime(): Calendar? {
        val localTime = this ?: return null
        return Calendar.getInstance().apply {
            timeInMillis = localTime.timeInMillis
        }
    }
}