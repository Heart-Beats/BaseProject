package com.hl.utils

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner

/**
 * @author  张磊  on  2023/07/05 at 17:47
 * Email: 913305160@qq.com
 *
 * @param actions               注册广播的 action
 * @param broadcastPermission   广播所需的权限
 * @param onReceive             收到广播的回调
 */

fun FragmentActivity.registerReceiver(
	vararg actions: String, broadcastPermission: String? = null, onReceive: (BroadcastReceiver, Intent) -> Unit
) {
	val intentFilter = IntentFilter()
	actions.forEach { intentFilter.addAction(it) }

	val receiver = object : BroadcastReceiver() {
		override fun onReceive(context: Context, intent: Intent) {
			onReceive(this, intent)
		}
	}

	val activity = this

	this.lifecycle.addObserver(object : DefaultLifecycleObserver {
		override fun onCreate(owner: LifecycleOwner) {
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
				// Android 8.0 以上
				activity.registerReceiver(receiver, intentFilter, broadcastPermission, null)
			} else {
				activity.registerReceiver(receiver, intentFilter)
			}
		}

		override fun onDestroy(owner: LifecycleOwner) {
			activity.unregisterReceiver(receiver)
		}
	})
}