package com.hl.uikit.filter

import android.text.InputType
import android.view.inputmethod.EditorInfo

/**
 * @author cxg
 * @date 2017/11/16
 */

class FiltraceKindBean {

    var resourceId: Int = 0
    var resourceIdSelect: Int = 0
    var text: String? = null
    var value:String?=null
    var showName:String ?=null
    /**
     * 对应字段名字
     */
    var fildName:String ?=null
    /**
     * 是否选中
     */
    var isCheck: Boolean = false
    /**
     * 0 表示通用
     * 1 表示 暂时用于时间标题处理
     * 2 第一个单选 其他可以多选
     * 3单独用于组织机构选择
     */
    var filterType=0
    var inputType:Int=InputType.TYPE_CLASS_TEXT
   var imeOptions:Int=EditorInfo.IME_ACTION_NEXT
}
