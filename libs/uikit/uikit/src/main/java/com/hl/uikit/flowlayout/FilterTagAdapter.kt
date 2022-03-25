package com.hl.uikit.flowlayout

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.hl.uikit.R
import com.hl.uikit.filter.FiltraceKindBean

/**
 * Created by chibb on 2019/8/30.
 *
 */
class FilterTagAdapter internal constructor(  var mContext:Context,
                                              var viewBeanList:MutableList<FiltraceKindBean> ):TagAdapter<FiltraceKindBean>(viewBeanList) {

   override fun getView(parent: FlowLayout, position: Int, filtraceKindBean: FiltraceKindBean): View {
        val ll = LayoutInflater.from(mContext).inflate(
            R
                .layout.uikit_view_filtrace_item,
            null, false
        ) as LinearLayout
        (ll.findViewById(R.id.iv) as ImageView).setImageResource(
            viewBeanList[position]
                .resourceId
        )
       (ll.findViewById(R.id.tv) as TextView).text = filtraceKindBean.text

        // 第一次进来默认选中第一条
           if(viewBeanList[position].isCheck) {
               (ll.findViewById(R.id.iv) as ImageView).setImageResource(
                   viewBeanList[position]
                       .resourceIdSelect
               )
               (ll.findViewById(R.id.ll_item) as View).setBackgroundResource(
                   R.drawable
                       .uikit_shape_bg_filtrate_selected
               )
               (ll.findViewById(R.id.tv) as TextView).setTextColor(
                   mContext.resources.getColor(
                       R.color.uikit_main_color

                   )
               )
           }

        return ll
    }

    class ViewHolder{
        var iv:ImageView?=null
        var view:View?=null
        var tv:TextView?=null
    }


}