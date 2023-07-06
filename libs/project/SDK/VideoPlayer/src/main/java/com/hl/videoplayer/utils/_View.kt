package com.hl.videoplayer.utils

import android.view.View

/**
 * @author  张磊  on  2023/07/06 at 17:00
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
