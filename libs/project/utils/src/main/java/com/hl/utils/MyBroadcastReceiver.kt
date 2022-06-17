package com.hl.utils

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

/**
 * @author  张磊  on  2022/06/16 at 19:27
 * Email: 913305160@qq.com
 */

/**
 * 广播接受器快速实现类
 */
open class MyBroadcastReceiver(val action: (receiver: BroadcastReceiver, context: Context, intent: Intent) -> Unit = { _, _, _ -> }) : BroadcastReceiver() {

	override fun onReceive(context: Context, intent: Intent) {
		action(this, context, intent)
	}
}