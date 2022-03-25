package com.hl.uikit.filter


/**
 * Created by chibb on 2019/9/3.
 *
 */
class FilterOption {

    var titleStr:String?=null
    var filterBeans: MutableList<FiltraceKindBean> = mutableListOf()
    /**
     * 是否多选
     */
    var isMultiSelect:Boolean=true
    /**
     *  0  表示通用选择
     *  1 表示输入
     *  2  时间选择
     *  3 有下一级选择子项目
     */
    var optionType=0

}