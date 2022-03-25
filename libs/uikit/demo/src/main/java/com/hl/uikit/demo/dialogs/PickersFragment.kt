package com.hl.uikit.demo.dialogs

import android.os.Bundle
import android.view.View
import kotlinx.android.synthetic.main.activity_pickers.*
import com.hl.uikit.demo.R
import com.hl.uikit.demo.fragments.BaseFragment
import com.hl.uikit.onClick
import com.hl.uikit.pickerview.OptionsPickerDialogFragment
import com.hl.uikit.pickerview.RangeTimePickerDialogFragment
import com.hl.uikit.pickerview.TimePickerDialogFragment
import com.hl.uikit.toast
import java.util.*

class PickersFragment : BaseFragment() {
    override val layout: Int
        get() = R.layout.activity_pickers

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        var picker1Position = 0
        itemPicker1?.onClick {
            OptionsPickerDialogFragment<String>()
                .apply {
                    setDefPositions(opt1Pos = picker1Position)
                    val data1 = listOf(
                        "重庆市", "上海市", "杭州市", "武汉市",
                        "厦门市", "重庆市", "上海市", "杭州市",
                        "武汉市", "厦门市", "重庆市", "上海市",
                        "杭州市", "武汉市", "厦门市"
                    )
                    setData(
                        data1 = data1
                    )
                    positiveClickListener = { dialog ->
                        picker1Position = opt1Pos
                        val value = data1[opt1Pos]
                        toast("选中了 $value")
                        dialog.dismiss()
                    }
                }
                .show(childFragmentManager, "picker")
        }
        var picker2Pos1 = 0
        var picker2Pos2 = 0
        itemPicker2?.onClick {
            OptionsPickerDialogFragment<String>()
                .apply {
                    setDefPositions(opt1Pos = picker2Pos1, opt2Pos = picker2Pos2)
                    positiveClickListener = { _ ->
                        picker2Pos1 = opt1Pos
                        picker2Pos2 = opt2Pos
                    }
                    setData(
                        data1 = listOf("重庆市", "上海市", "杭州市", "武汉市", "厦门市"),
                        data2 = listOf("1", "2", "3", "4", "5")
                    )
                }
                .show(childFragmentManager, "picker")
        }

        var picker3Pos1 = 0
        var picker3Pos2 = 0
        itemPicker3?.onClick {
            OptionsPickerDialogFragment<String>()
                .apply {

                    setDefPositions(opt1Pos = picker3Pos1, opt2Pos = picker3Pos2)

                    val data1 = listOf("重庆市", "上海市", "浙江省", "湖北省", "福建省")

                    val data12 = listOf("重庆市")
                    val data22 = listOf("上海市")
                    val data32 = listOf("杭州市", "温州市", "湖州市", "嘉兴市")
                    val data42 = listOf("武汉市", "黄石市", "襄阳市", "荆州市")
                    val data52 = listOf("福州市", "莆田市")
                    val data2 = listOf(data12, data22, data32, data42, data52)
                    setLinkageData(
                        data1 = data1,
                        data2 = data2
                    )
                    positiveClickListener = { dialog ->
                        picker3Pos1 = opt1Pos
                        picker3Pos2 = opt2Pos
                        val value1 = data1[opt1Pos]
                        val value2 = data2[opt1Pos][opt2Pos]
                        toast("选中了 ，${value1}${value2}")
                        dialog.dismiss()
                    }
                }
                .show(childFragmentManager, "picker")
        }
        var mTime: Calendar = Calendar.getInstance()
        itemPicker4?.onClick {
            TimePickerDialogFragment()
                .apply {
                    startTime = Calendar.getInstance().apply {
                        set(Calendar.YEAR, 2000)
                    }
                    endTime = Calendar.getInstance().apply {
                        set(Calendar.YEAR, 3000)
                    }
                    time = mTime
                    setType(showDay = false)
                    timePositiveClickListener = { dialog, time ->
                        dialog.dismiss()
                        mTime = time
                    }
                }
                .show(childFragmentManager,"picker")
        }

        var picker5Time: Calendar = Calendar.getInstance()
        itemPicker5?.onClick {
            TimePickerDialogFragment()
                .apply {
                    startTime = Calendar.getInstance().apply {
                        set(Calendar.YEAR, 2000)
                        set(Calendar.MONTH,3)
                    }
                    endTime = Calendar.getInstance().apply {
                        set(Calendar.YEAR, 3000)
                    }
                    time = picker5Time
                    timePositiveClickListener = { dialog, time ->
                        dialog.dismiss()
                        println("time = ${time.time.toLocaleString()}")
                        picker5Time = time
                    }
                }
                .show(childFragmentManager,"picker")
        }
        var mRangeStartTime = Calendar.getInstance()
        var mRangeEndTime:Calendar? = null
        itemPicker6?.onClick {
            RangeTimePickerDialogFragment()
                .apply {
                    startTime = Calendar.getInstance().apply {
                        set(Calendar.YEAR, 2000)
                    }
                    endTime = Calendar.getInstance().apply {
                        set(Calendar.YEAR, 3000)
                    }
                    rangeStartTime = mRangeStartTime
                    rangeEndTime = mRangeEndTime
//                    timeDisplayFormat = SimpleDateFormat("HH:mm", Locale.ENGLISH)
                    enableSwitchMode = true //设置为支持切换 日、月 模式
                    rangeTimePositiveClickListener = { dialog, startTime, endTime ->
                        dialog.dismiss()
                        mRangeStartTime = startTime
                        mRangeEndTime = endTime
                        println("startTime = ${startTime?.time?.toLocaleString()}")
                        println("endTime = ${endTime?.time?.toLocaleString()}")
                    }
                }
                .show(childFragmentManager, "picker")
        }
        itemPicker7?.onClick {
            TimePickerDialogFragment()
                .apply {
                    startTime = Calendar.getInstance().apply {
                        set(Calendar.YEAR, 2000)
                    }
                    endTime = Calendar.getInstance().apply {
                        set(Calendar.YEAR, 3000)
                    }
                    time = mTime
                    setType(showHour = true,showMinute = true)
                    timePositiveClickListener = { dialog, time ->
                        dialog.dismiss()
                        mTime = time
                    }
                }
                .show(childFragmentManager,"picker")
        }
    }
}