package com.hl.utils

import android.graphics.Color
import android.widget.TextView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

fun TextView.timeDown(scope: CoroutineScope, count: Int, countFormatText: String = "%s", endAction: () -> Unit = {}) {
	scope.launch {
		var count = count
		while (count > 0) {
			text = String.format(countFormatText, count)
			count--
			delay(1000)
			if (count == 0) {
				endAction()
			}
		}
	}
}

fun TextView.verifyCodeCountDown(scope: CoroutineScope, countFormatText: String = "已发送(%s)", endText: String = "重新获取") {
	scope.launch {
		var count = 60
		while (count > 0) {
			count--
			isEnabled = false
			text = String.format(countFormatText, count)
			delay(1000)
			if (count == 0) {
				isEnabled = true
				text = endText
			}
		}
	}
}

fun TextView.setCompareTextColor(percent: Double) {
	val colorString = when {
		percent.toInt() == 0 -> "#ff333333"
		percent > 0 -> "#ffff3b30"
		else -> "#ff00b578"
	}
	setTextColor(Color.parseColor(colorString))
}