package com.hl.uikit.filter

import java.util.*

/**
 * Created by chibb on 2019/9/3.
 *
 */
class FilterOptions {
    var options:MutableList<FilterOption>?=null
    var isSelectDate:Boolean=false
    var isMustSelectTime:Boolean=false
    var startDate:Date?=null
    var endDate:Date?=null
}