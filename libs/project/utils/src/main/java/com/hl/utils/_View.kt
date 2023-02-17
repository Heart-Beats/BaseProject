package com.hl.utils

import android.view.View

/**
 * @author  张磊  on  2023/02/16 at 10:45
 * Email: 913305160@qq.com
 */



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