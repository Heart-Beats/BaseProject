package com.hl.utils

import com.blankj.utilcode.util.ThreadUtils

/**
 * @author  张磊  on  2023/02/08 at 10:28
 * Email: 913305160@qq.com
 */

/**
 * 在 UI 线程指定延迟时间执行任务
 */
fun Any.runOnUiThread(delayMillis: Long = 0L, runnable: Runnable) {
	ThreadUtils.runOnUiThreadDelayed(runnable, delayMillis)
}