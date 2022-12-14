package com.hl.utils

import android.os.Build

/**
 * @author  张磊  on  2022/12/09 at 12:48
 * Email: 913305160@qq.com
 */
object BuildVersionUtil {

	/**
	 * Android 的版本值，  如： Android 10 对应 29
	 */
	private val versionValue: Int = Build.VERSION.SDK_INT

	/**
	 * Android 的版本名称， 如： Android 10 对应 10
	 */
	private val versionName: String = Build.VERSION.RELEASE


	/**
	 * 是否 Android 12 及以上
	 */
	@JvmStatic
	fun isOver12(): Boolean {
		return Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
	}

	/**
	 * 是否 Android 11  及以上
	 */
	@JvmStatic
	fun isOver11(): Boolean {
		return Build.VERSION.SDK_INT >= Build.VERSION_CODES.R
	}

	/**
	 * 是否 Android 10  及以上
	 */
	@JvmStatic
	fun isOver10(): Boolean {
		return Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q
	}

	/**
	 * 是否 Android 8.0  及以上
	 */
	@JvmStatic
	fun isOver8(): Boolean {
		return Build.VERSION.SDK_INT >= Build.VERSION_CODES.O
	}

	/**
	 * 是否 Android 6.0  及以上
	 */
	@JvmStatic
	fun isOver6(): Boolean {
		return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
	}
}