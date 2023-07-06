package com.hl.previewfile.utils

import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.os.Build
import com.hl.mmkvsharedpreferences.getApp

/**
 * @author  张磊  on  2023/07/06 at 13:49
 * Email: 913305160@qq.com
 */
object AppUtil {

	private val pm: PackageManager = getApp().getPackageManager()

	/**
	 * 获取应用程序信息
	 *
	 * @param [pkgName] 包名
	 * @return [ApplicationInfo]
	 */
	private fun getApplicationInfo(pkgName: String): ApplicationInfo? {
		if (pkgName.isBlank()) return null

		return try {
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
				pm.getApplicationInfo(pkgName, PackageManager.ApplicationInfoFlags.of(0L))
			} else {
				pm.getApplicationInfo(pkgName, 0)
			}
		} catch (e: Exception) {
			e.printStackTrace()
			null
		}
	}


	/**
	 * 应用程序是否已安装
	 * @param [pkgName] 包名
	 * @return [Boolean]
	 */
	fun isAppInstalled(pkgName: String): Boolean {
		return getApplicationInfo(pkgName)?.enabled ?: false
	}


	/**
	 * 获取应用程序名字
	 * @param [pkgName] 包名
	 */
	fun getAppName(pkgName: String = getApp().packageName): String {
		return getApplicationInfo(pkgName)?.loadLabel(pm)?.toString() ?: ""
	}
}