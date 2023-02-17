package com.hl.utils

import android.os.Looper
import com.blankj.utilcode.util.ThreadUtils

/**
 * @author  张磊  on  2023/02/08 at 10:28
 * Email: 913305160@qq.com
 */


/**
 * 是否为主线程
 */
fun isMainThread(): Boolean {
	return Looper.getMainLooper() === Looper.myLooper()
}


/**
 * 在主线程指定延迟时间执行任务
 */
fun runOnUiThread(delayMillis: Long = 0L, runnable: Runnable) {
	ThreadUtils.runOnUiThreadDelayed(runnable, delayMillis)
}