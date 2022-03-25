package com.hl.uikit.demo.fragments

import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.fragment_search_bar.*
import com.hl.uikit.ConfigBuilder
import com.hl.uikit.demo.R
import com.hl.uikit.demo.util.StatusBarUtil

class SearchBarFragment : BaseFragment() {
    override val layout: Int
        get() = R.layout.fragment_search_bar

    private val provinceSearchConfigBuilder by lazy {
        ConfigBuilder<String>().apply {
            queryData = listOf("福建", "浙江", "上海", "广东")
//                queryHint = "搜索提示"
            queryCompleteListener = {
                println("queryCompleteListener = ${it.toTypedArray().contentToString()}")
            }
            onQueryCloseListener = {
                println("onQueryCloseListener = ${onQueryCloseListener}")
                false
            }
            imeOptions = EditorInfo.IME_ACTION_GO
        }
    }
    private val citySearchConfigBuilder by lazy {
        ConfigBuilder<String>().apply {
            queryData = listOf("福州", "厦门", "上海", "杭州")
                queryHint = "搜索提示"
            queryCompleteListener = {
                println("queryCompleteListener = ${it.toTypedArray().contentToString()}")
            }
            onQueryCloseListener = {
                println("onQueryCloseListener = ${onQueryCloseListener}")
                false
            }
            imeOptions = EditorInfo.IME_ACTION_DONE
        }
    }


    private val studentSearchConfigBuilder by lazy {
        val  students= mutableListOf<Student>()
        students.apply {
            add(Student("张三","15870678471"))
            add(Student("李四","15070987456") )
            add(Student("王五","18770987456") )
            add(Student("曹王","15270987456") )
        }
        ConfigBuilder<Student>().apply {
            queryData = students.toList()
                queryHint = "请输入姓名或者电话"
            queryProperty= listOf(Student::name,Student::Phone)

            queryCompleteListener = {
                println("queryCompleteListener = ${it.toTypedArray().contentToString()}")
            }
            onQueryCloseListener = {
                println("onQueryCloseListener = ${onQueryCloseListener}")
                false
            }
            imeOptions = EditorInfo.IME_ACTION_DONE
        }
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        toolbar?.setRightActionListener {
//            toolbar?.expandSearchView(studentSearchConfigBuilder)
            toolbar?.expandSearchView(citySearchConfigBuilder)
            toolbar?.expandSearchView()
        }

        val activity = requireActivity()
        StatusBarUtil.transparencyBar(activity.window)
        StatusBarUtil.statusBarDarkMode(activity.window)
        StatusBarUtil.setStatusBarColor(
            activity,
            ContextCompat.getColor(activity, R.color.uikit_color_1)
        )
    }
}

class  Student(var name:String,var Phone:String){
    override fun toString(): String {
        return "Student(name='$name', Phone='$Phone')"
    }
}