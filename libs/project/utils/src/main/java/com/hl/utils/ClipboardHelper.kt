package com.hl.utils

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Build

/**
 * @author  张磊  on  2022/06/16 at 20:52
 * Email: 913305160@qq.com
 */

class ClipboardHelper(context: Context) {

	private val cm by lazy {
		context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
	}


	/**
	 * 设置内容到剪切板
	 */
	fun copyText(text: CharSequence?) {
		cm.setPrimaryClip(ClipData.newPlainText(null, text))
	}

	/**
	 * 清除剪切板内容
	 */
	fun clearText() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
			//要api28以上
			cm.clearPrimaryClip()
		} else {
			cm.setPrimaryClip(ClipData.newPlainText(null, null))
		}
	}

	/**
	 * 获取剪切板内容
	 *
	 * @param index 剪切板中第几项
	 */
	fun getText(index: Int): CharSequence {
		return cm.primaryClip?.run {
			if (index < this.itemCount) {
				getItemAt(index).text
			} else {
				""
			}
		} ?: ""
	}
}