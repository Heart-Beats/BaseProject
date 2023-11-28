package com.hl.utils

import android.content.Context
import android.content.pm.ResolveInfo
import android.graphics.drawable.Drawable

/**
 * @author  张磊  on  2023/11/08 at 11:49
 * Email: 913305160@qq.com
 */


/**
 * 获取应用包名
 */
fun ResolveInfo.getPackageName(): String = this.activityInfo.packageName

/**
 * 获取启动 activity 类名
 */
fun ResolveInfo.getLaunchActivityClassName(): String = this.activityInfo.name

/**
 * 获取应用名称
 */
fun ResolveInfo.getAppName(context: Context) = this.loadLabel(context.packageManager).toString()

/**
 * 获取应用图标
 */
fun ResolveInfo.getAppIcon(context: Context): Drawable = this.loadIcon(context.packageManager)