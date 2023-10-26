package com.hl.utils.views

import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView

/**
 * @Author  张磊  on  2021/01/14 at 16:41
 * Email: 913305160@qq.com
 */

/**
 * 使 RecyclerView 的 ItemDecoration 仅以当前设置的生效
 */
fun RecyclerView.setItemDecoration(decor: RecyclerView.ItemDecoration) {
    for (i in this.itemDecorationCount - 1 downTo 0) {
        this.removeItemDecoration(this.getItemDecorationAt(i))
    }
    this.addItemDecoration(decor)
}

/**
 * 给 RecyclerView 每项设置触摸处理
 */
fun RecyclerView.setItemTouchHelper(callBack: ItemTouchHelper.Callback) {
    ItemTouchHelper(callBack).attachToRecyclerView(this)
}