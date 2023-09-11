package com.hl.utils

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.localbroadcastmanager.content.LocalBroadcastManager

/**
 * @author  张磊  on  2023/07/05 at 17:47
 * Email: 913305160@qq.com
 */

/**
 * Activity 注册广播
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

/**
 * Fragment 注册广播
 *
 * @param actions               注册广播的 action
 * @param broadcastPermission   广播所需的权限
 * @param onReceive             收到广播的回调
 */
fun Fragment.registerReceiver(
	vararg actions: String, broadcastPermission: String? = null, onReceive: (BroadcastReceiver, Intent) -> Unit
) {
	val intentFilter = IntentFilter()
	actions.forEach { intentFilter.addAction(it) }

	val receiver = object : BroadcastReceiver() {
		override fun onReceive(context: Context, intent: Intent) {
			onReceive(this, intent)
		}
	}

	val activity = requireActivity()

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

/**
 * 发送本地广播
 *
 * @param intent              intent
 * @param isSync             是否同步发送（阻塞）
 */
fun Context.sendLocalBroadcast(intent: Intent, isSync: Boolean = false) {
	val localBroadcastManager = LocalBroadcastManager.getInstance(this)

	if (!isSync) {
		localBroadcastManager.sendBroadcast(intent)
	} else {
		localBroadcastManager.sendBroadcastSync(intent)
	}
}

/**
 * 发送本地广播
 *
 * @param intent              intent
 * @param isSync             是否同步发送（阻塞）
 */
fun FragmentActivity.sendLocalBroadcast(intent: Intent, isSync: Boolean = false) {
	this.baseContext.sendLocalBroadcast(intent, isSync)
}

/**
 * 发送本地广播
 *
 * @param intent              intent
 * @param isSync             是否同步发送（阻塞）
 */
fun Fragment.sendLocalBroadcast(intent: Intent, isSync: Boolean = false) {
	requireContext().sendLocalBroadcast(intent, isSync)
}


/**
 * Activity 注册本地广播
 *
 * @param actions               注册广播的 action
 * @param onReceive             收到广播的回调
 */
fun FragmentActivity.registerLocalReceiver(vararg actions: String, onReceive: (BroadcastReceiver, Intent) -> Unit) {
	val intentFilter = IntentFilter()
	actions.forEach { intentFilter.addAction(it) }

	val receiver = object : BroadcastReceiver() {
		override fun onReceive(context: Context, intent: Intent) {
			onReceive(this, intent)
		}
	}

	val localBroadcastManager = LocalBroadcastManager.getInstance(this)

	this.lifecycle.addObserver(object : DefaultLifecycleObserver {
		override fun onCreate(owner: LifecycleOwner) {
			localBroadcastManager.registerReceiver(receiver, intentFilter)
		}

		override fun onDestroy(owner: LifecycleOwner) {
			localBroadcastManager.unregisterReceiver(receiver)
		}
	})
}

/**
 * Fragment 注册本地广播
 *
 * @param actions               注册广播的 action
 * @param onReceive             收到广播的回调
 */
fun Fragment.registerLocalReceiver(vararg actions: String, onReceive: (BroadcastReceiver, Intent) -> Unit) {
	val intentFilter = IntentFilter()
	actions.forEach { intentFilter.addAction(it) }

	val receiver = object : BroadcastReceiver() {
		override fun onReceive(context: Context, intent: Intent) {
			onReceive(this, intent)
		}
	}

	val localBroadcastManager = LocalBroadcastManager.getInstance(requireContext())

	this.lifecycle.addObserver(object : DefaultLifecycleObserver {
		override fun onCreate(owner: LifecycleOwner) {
			localBroadcastManager.registerReceiver(receiver, intentFilter)
		}

		override fun onDestroy(owner: LifecycleOwner) {
			localBroadcastManager.unregisterReceiver(receiver)
		}
	})
}