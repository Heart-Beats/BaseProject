package com.hl.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.annotation.RequiresApi
import androidx.core.content.FileProvider
import java.io.File

/**
 * @author  张磊  on  2021/10/28 at 19:18
 * Email: 913305160@qq.com
 */
object AppInstallUtil {

	val REQ_INSTALL_PERMISSION = 1000


	/**
	 * 安装应用
	 *
	 * @param context
	 * @param filePath
	 * @return
	 */
	fun installApk(context: Context, filePath: String) {
		try {
			//8.0需要申请安装权限
			if (BuildVersionUtil.isOver8()) {
				val b = context.packageManager.canRequestPackageInstalls()
				if (b) {
					val installAppIntent = getInstallAppIntent(context, filePath)
					context.startActivity(installAppIntent)
				} else {
					//请求安装未知应用来源的权限
					startInstallPermissionSettingActivity(context)
				}
			} else {
				val installAppIntent = getInstallAppIntent(context, filePath)
				context.startActivity(installAppIntent)
			}
		} catch (e: Exception) {
			e.printStackTrace()
		}
	}

	/**
	 * 开启安装APK权限(适配8.0)
	 *
	 * @param context
	 */
	@RequiresApi(api = Build.VERSION_CODES.O)
	private fun startInstallPermissionSettingActivity(context: Context) {
		val packageURI = Uri.parse("package:" + context.packageName)
		val intent = Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES, packageURI)
		if (context is Activity) {
			context.startActivityForResult(intent, REQ_INSTALL_PERMISSION)
		} else {
			context.startActivity(intent)
		}
	}

	/**
	 * 调往系统APK安装界面（适配7.0）
	 *
	 * @return
	 */
	private fun getInstallAppIntent(context: Context, filePath: String): Intent? {
		//apk文件的本地路径
		val apkFile = File(filePath)
		if (!apkFile.exists()) {
			return null
		}
		val intent = Intent(Intent.ACTION_VIEW)
		val contentUri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
		// 需要配置文件 fileProvider，注意配置的 authority
			FileProvider.getUriForFile(context, context.packageName + ".fileProvider", apkFile)
		else Uri.fromFile(apkFile)

		intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
			intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
		}
		intent.setDataAndType(contentUri, "application/vnd.android.package-archive")
		return intent
	}
}