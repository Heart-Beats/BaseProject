package com.hl.utils

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.net.Uri

/**
 * @author  张磊  on  2023/11/08 at 11:49
 * Email: 913305160@qq.com
 */

/**
 * 获取手机内安装的浏览器
 */
fun Context.getBrowserList(): List<ResolveInfo> {
	val packageManager = this.packageManager
	val intent = Intent(Intent.ACTION_VIEW)
	intent.addCategory(Intent.CATEGORY_BROWSABLE)
	intent.data = Uri.parse("http://")
	var activities = packageManager.queryIntentActivities(intent, PackageManager.MATCH_ALL)

	// 过滤淘宝、京东
	// activities= activities.filter {
	// 	val defaultBrowser = activities[0]
	// 	defaultBrowser.priority == it.priority && defaultBrowser.isDefault == it.isDefault
	// }

	return activities
}

/**
 * 启动指定浏览器
 * @param resolveInfo 可以打开链接的 Intent 信息
 * @param url         要启动的链接
 */
fun Context.toBrowser(resolveInfo: ResolveInfo, url: String) {
	val uri = Uri.parse(url)
	val intent = Intent(Intent.ACTION_VIEW, uri)
	intent.setClassName(resolveInfo.getPackageName(), resolveInfo.getLaunchActivityClassName())
	startActivity(intent)
}

/**
 * 启动指定浏览器
 * @param packageName 相应的应用程序 packageName
 * @param className   要启动的 activity
 * @param url         要启动的链接
 */
fun Context.toBrowser(packageName: String, className: String, url: String) {
	val uri = Uri.parse(url)
	val intent = Intent(Intent.ACTION_VIEW, uri)
	intent.setClassName(packageName, className)
	startActivity(intent)
}