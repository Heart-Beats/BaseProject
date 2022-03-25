package com.hl.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.fragment.app.Fragment

/**
 * @author  张磊  on  2021/11/05 at 17:19
 * Email: 913305160@qq.com
 */

fun Context.launchHome() {
	startActivity(Intent(Intent.ACTION_MAIN).apply {
		flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
		addCategory(Intent.CATEGORY_HOME)
	})
}

fun Fragment.launchHome() {
	startActivity(Intent(Intent.ACTION_MAIN).apply {
		flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
		addCategory(Intent.CATEGORY_HOME)
	})
}

fun Activity.launchHome() {
	moveTaskToBack(true)
}