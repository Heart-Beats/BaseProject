package com.hl.web.utils

import android.os.Looper
import com.blankj.utilcode.util.ThreadUtils

/**
 * @author  张磊  on  2023/02/08 at 10:28
 * Email: 913305160@qq.com
 */


/**
 * 是否为主线程
 */
internal fun isMainThread(): Boolean {
	return Looper.getMainLooper() === Looper.myLooper()
}


/**
 * 在主线程指定延迟时间执行任务
 */
internal fun runOnUiThread(delayMillis: Long = 0L, runnable: Runnable) {
	ThreadUtils.runOnUiThreadDelayed(runnable, delayMillis)
}

/**
 * 在主线程指定延迟时间执行任务后，并在当前线程可同步获取执行结果
 */
internal fun <R> runOnUiThreadReturn(delayMillis: Long = 0L, runnable: () -> R): R? {
	if (isMainThread()) {
		return runnable()
	}

	val lock = Object()
	return synchronized(lock) {
		var result: R? = null
		runOnUiThread(delayMillis) {
			// wait()  和 notify()  都需要调用对象在所在线程有锁，因此需要进行两次 synchronized
			synchronized(lock) {
				result = runnable()
				lock.notifyAll()
			}
		}

		// 子线程时切换到主线程执行，并同步等待主线程执行完毕
		lock.wait()

		result
	}

	// 子线程时切换到主线程执行，并同步等待主线程执行完毕,  使用闭锁方式进行同步实现
	// val countDownLatch = CountDownLatch(1)
	//
	// var functionInvokeResult: Any? = null
	// runOnUiThread {
	// 	functionInvokeResult = runnable()
	// 	countDownLatch.countDown()
	// }
	//
	// val await = countDownLatch.await(5L, TimeUnit.SECONDS)
	// if (await) {
	// 	return functionInvokeResult
	// } else {
	// 	throw IllegalStateException("${method.name} 方法执行超过 5s")
	// }
}