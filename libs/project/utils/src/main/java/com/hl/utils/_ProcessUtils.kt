package com.hl.utils

import android.content.Context
import com.blankj.utilcode.util.ProcessUtils

/**
 * @author  张磊  on  2021/05/20 at 10:00
 * Email: 913305160@qq.com
 */

fun Context.isProcess(processName: String): Boolean {
	val currentProcessName = ProcessUtils.getCurrentProcessName()
	return currentProcessName.endsWith(processName)
}

fun Context.isMainProcess() = isProcess(this.applicationContext.packageName)