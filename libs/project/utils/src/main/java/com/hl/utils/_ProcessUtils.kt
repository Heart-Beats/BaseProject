package com.hl.utils

import android.app.ActivityManager
import android.app.Application
import android.content.Context
import android.os.Process

/**
 * @author  张磊  on  2021/05/20 at 10:00
 * Email: 913305160@qq.com
 */

fun Context.isProcess(processName: String): Boolean {
	var currentProcessName = ""
	val manager = this.getSystemService(Application.ACTIVITY_SERVICE) as ActivityManager
	for (processInfo in manager.runningAppProcesses) {
		if (processInfo.pid == Process.myPid()) {
			currentProcessName = processInfo.processName
			break
		}
	}

	return currentProcessName.endsWith(processName)
}

fun Context.isMainProcess() = isProcess(this.applicationContext.packageName)