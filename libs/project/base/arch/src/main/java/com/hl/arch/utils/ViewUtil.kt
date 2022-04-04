package com.hl.arch.utils

import android.os.SystemClock
import android.view.View
import android.view.ViewGroup
import androidx.core.view.get

/**
 * @Author  张磊  on  2020/09/23 at 18:05
 * Email: 913305160@qq.com
 */

/**
 * 用于从 ViewGroup 及其所有后代子 View 中查找指定的 View 类型
 * @param  findViewType 需要查找的子 View 类型
 * @return 若找到返回第一个找到的 子 View， 否则返回 null
 */
fun <T : View> View.traverseFindFirstViewByType(findViewType: Class<T>): T? {
	val parentView = this
	if (findViewType.isInstance(parentView)) {
		@Suppress("UNCHECKED_CAST")
		return parentView as T
	} else {
		if (parentView is ViewGroup) {
			for (i in 0 until parentView.childCount) {
				val childView = parentView[i]
				val findView = childView.traverseFindFirstViewByType(findViewType)
				if (findView != null) {
					return findView
				}
			}
		}
		return null
	}
}

/**
 * 该方法用于 View 固定位置，调用此方法后，view 不会受到某些因素导致位置变化
 */
fun View.fixedPosition() {
	val onLayoutChangeListener = View.OnLayoutChangeListener { v, _, _, _, _, oldLeft, oldTop, oldRight, oldBottom ->
		v?.left = oldLeft
		v?.top = oldTop
		v?.right = oldRight
		v?.bottom = oldBottom
	}
	this.post {
		this.addOnLayoutChangeListener(onLayoutChangeListener)
	}
}


private var lastClickTime = SystemClock.elapsedRealtime()

/**
 * @param  clickDuration 点击间隔生效时间（单位：ms），此时间之内重复点击不生效
 * @param  clickListener 点击事件回调函数
 */
fun View.onClick(clickDuration: Long = 500, clickListener: View.OnClickListener) {

	this.setOnClickListener {
		val currentClickTime = SystemClock.elapsedRealtime()
		if (currentClickTime - lastClickTime > clickDuration) {
			clickListener.onClick(it)
			lastClickTime = currentClickTime
		}
	}
}

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