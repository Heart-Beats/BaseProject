package com.hl.utils

import android.graphics.Paint
import android.widget.TextView
import androidx.annotation.Px
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * 倒计时
 */
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

/**
 * 验证码格式的倒计时
 */
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

/**
 * 给 TextView 添加下划线
 */
fun TextView.addUnderline() {
	val textPaint = this.paint
	textPaint.flags = Paint.UNDERLINE_TEXT_FLAG //下划线
	textPaint.isAntiAlias = true //抗锯齿
}

/**
 * TextView 未绘制时获取行数
 */
fun TextView.getLines(@Px textViewWidth: Int) {
	TextViewLinesUtil.getTextViewLines(this, textViewWidth)
}