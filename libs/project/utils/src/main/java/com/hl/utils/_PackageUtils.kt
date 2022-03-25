package com.hl.utils

import android.content.Context
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.util.Log

/**
 * @Author  张磊  on  2020/12/24 at 14:39
 * Email: 913305160@qq.com
 */


/**
 * get App versionName
 *
 * @param context
 * @return
 */
fun getVersionName(context: Context): String {
    val packageManager = context.packageManager
    val packageInfo: PackageInfo
    var versionName = ""
    try {
        packageInfo = packageManager.getPackageInfo(context.packageName, 0)
        versionName = packageInfo.versionName
    } catch (e: PackageManager.NameNotFoundException) {
        e.printStackTrace()
    }
    return versionName
}

fun getPackageInfo(absPath: String, context: Context): PackageInfo {
    val pm = context.packageManager
    val pkgInfo = pm.getPackageArchiveInfo(absPath, PackageManager.GET_ACTIVITIES)
    if (pkgInfo != null) {
        val appInfo = pkgInfo.applicationInfo
        /* 必须加这两句，不然下面icon获取是default icon而不是应用包的icon */
        appInfo.sourceDir = absPath
        appInfo.publicSourceDir = absPath
        val appName = pm.getApplicationLabel(appInfo).toString() // 得到应用名
        val packageName = appInfo.packageName // 得到包名
        val version = pkgInfo.versionName // 得到版本信息
        // val versionCode = pkgInfo.versionCode
        // /* icon1和icon2其实是一样的 */
        // val icon1 = pm.getApplicationIcon(appInfo) // 得到图标信息
        // val icon2 = appInfo.loadIcon(pm)
        val pkgInfoStr = String.format("PackageName:%s, Vesion: %s, AppName: %s", packageName,
            version, appName)
        Log.d("getPackageInfo", String.format("PkgInfo: %s", pkgInfoStr))
        return pkgInfo
    }
    throw RuntimeException("get packageinfo failure")
}