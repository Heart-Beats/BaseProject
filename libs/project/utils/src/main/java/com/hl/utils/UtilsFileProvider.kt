package com.hl.utils

import android.app.Application
import android.util.Log
import androidx.core.content.FileProvider

/**
 * @author  张磊  on  2022/08/05 at 17:05
 * Email: 913305160@qq.com
 *
 * 借助 Provider 来完成一些三方库的初始化
 */
class UtilsFileProvider : FileProvider() {
	private val TAG = "UtilsFileProvider"

	override fun onCreate(): Boolean {
		Log.d(TAG, "onCreate: UtilsFileProvider 开始初始化")
		BaseUtil.init(context?.applicationContext as Application)
		return true
	}
}