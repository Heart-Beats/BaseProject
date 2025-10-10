package com.hl.utils.views

import androidx.lifecycle.findViewTreeLifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

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

/**
 *  RecyclerView 平滑滚动到指定位置的 item 底部可见
 *
 * @param position 要滚动到可见的 item 位置
 */
fun RecyclerView.smoothScroll2ItemBottom(position: Int) {
    val recyclerView = this

    val coroutineContext = Dispatchers.IO + CoroutineExceptionHandler { _, throwable ->
        throwable.printStackTrace()
    }

    this.findViewTreeLifecycleOwner()?.lifecycleScope?.launch(coroutineContext) {
        val layoutManager = recyclerView.layoutManager
        if (layoutManager is LinearLayoutManager) {

            if (position > layoutManager.findLastVisibleItemPosition()) {
                // 确保目标项处于屏幕上
                recyclerView.scrollToPosition(position)
            }

            val viewHolder = recyclerView.findViewHolderForAdapterPosition(position)
            viewHolder?.itemView?.let {
                it.post {
                    try {
                        val location = IntArray(2)
                        it.getLocationOnScreen(location)

                        val itemBottom = location[1] + it.height

                        recyclerView.getLocationOnScreen(location)
                        val recyclerViewBottom = location[1] + recyclerView.height

                        // item 底部超出 recyclerView 底部，需要滚动到可见
                        if (itemBottom > recyclerViewBottom) {
                            val offset = itemBottom - recyclerViewBottom

                            MainScope().launch {
                                recyclerView.smoothScrollBy(0, offset)
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        }
    }
}