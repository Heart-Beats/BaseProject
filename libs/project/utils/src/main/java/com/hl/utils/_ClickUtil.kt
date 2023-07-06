package com.hl.utils

import android.view.View
import androidx.annotation.Px
import com.blankj.utilcode.util.ClickUtils

/**
 * @author  张磊  on  2022/12/08 at 15:27
 * Email: 913305160@qq.com
 */

/**
 * 点击事件去除重复点击
 */
internal fun View.onClick(interval: Long, listener: (View) -> Unit) {
	setOnClickListener {
		val time = it.getTag(it.id)?.toString()?.toLongOrNull()
		val currentTime = System.currentTimeMillis()
		if (time == null || currentTime - time > interval) {
			it.setTag(it.id, currentTime)
			listener.invoke(it)
		}
	}
}

/**
 * 点击事件去除重复点击， 默认 500 ms
 */
internal fun View.onClick(listener: (View) -> Unit) {
	this.onClick(500, listener)
}


/**
 * 扩展点击区域
 */
fun View.expandClickArea(@Px expandSize: Int) {
	expandClickArea(expandSize, expandSize, expandSize, expandSize)
}

/**
 * 扩展点击区域
 */
fun View.expandClickArea(
	@Px expandSizeTop: Int,
	@Px expandSizeLeft: Int,
	@Px expandSizeRight: Int,
	@Px expandSizeBottom: Int
) {
	ClickUtils.expandClickArea(this, expandSizeTop, expandSizeLeft, expandSizeRight, expandSizeBottom)
}

/**
 *  view 双击事件
 */
fun View.onDoubleClick(interval: Long = 500, onClick: (View) -> Unit) {
	onMultiClick(2, interval, onClick)
}

/**
 *  view 多次点击触发事件
 */
fun View.onMultiClick(count: Int, interval: Long = 500, onClick: (View) -> Unit) {
	this.setOnClickListener(object : ClickUtils.OnMultiClickListener(count, interval) {
		override fun onTriggerClick(v: View) {
			onClick(v)
		}

		override fun onBeforeTriggerClick(v: View, count: Int) {

		}
	})
}