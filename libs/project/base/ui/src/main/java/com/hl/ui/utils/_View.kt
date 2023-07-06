package com.hl.ui.utils

import android.view.View
import com.gyf.immersionbar.ktx.navigationBarHeight
import com.gyf.immersionbar.ktx.statusBarHeight

/**
 * 点击事件去除重复点击
 */
fun View.onClick(interval: Long, listener: (View) -> Unit) {
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
fun View.onClick(listener: (View) -> Unit) {
	this.onClick(500, listener)
}

/**
 * 获取状态栏高度
 */
fun View.getStatusBarHeight() = context.statusBarHeight

/**
 * 获取导航栏高度
 */
fun View.getNavigationBarHeight() = context.navigationBarHeight


private fun View.setVisible(visibility: Int) {
	if (this.visibility != visibility) {
		this.visibility = visibility
	}
}

fun View.visible() {
	setVisible(View.VISIBLE)
}

fun View.invisible() {
	setVisible(View.INVISIBLE)
}

fun View.gone() {
	setVisible(View.GONE)
}

fun View.visibleOrGone(show: Boolean) {
	if (show) visible() else gone()
}
