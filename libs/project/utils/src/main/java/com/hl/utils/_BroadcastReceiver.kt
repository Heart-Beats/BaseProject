package com.hl.utils

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.hl.mmkvsharedpreferences.getApp

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
	this.lifecycle.registerReceiver(this, actions = actions, broadcastPermission, onReceive)
}

/**
 * Fragment 注册广播, 需要在 onViewCreated 之后调用
 *
 * @param actions               注册广播的 action
 * @param broadcastPermission   广播所需的权限
 * @param onReceive             收到广播的回调
 */
fun Fragment.registerReceiver(
	vararg actions: String, broadcastPermission: String? = null, onReceive: (BroadcastReceiver, Intent) -> Unit
) {
	this.viewLifecycleOwner.lifecycle.registerReceiver(
		requireContext(),
		actions = actions,
		broadcastPermission,
		onReceive
	)
}

/**
 * Lifecycle 注册本地广播
 *
 * @param [context]  Context
 * @param [actions] 注册广播的 action
 * @param [broadcastPermission] 广播权限
 * @param [onReceive] 收到广播的回调
 */
fun Lifecycle.registerReceiver(
	context: Context,
	vararg actions: String,
	broadcastPermission: String? = null,
	onReceive: (BroadcastReceiver, Intent) -> Unit
) {
	val intentFilter = IntentFilter()
	actions.forEach { intentFilter.addAction(it) }

	val receiver = object : BroadcastReceiver() {
		override fun onReceive(context: Context, intent: Intent) {
			onReceive(this, intent)
		}
	}

	// onCreate 方法注册广播会在 LifecycleOwner 的 onCreate 方法返回后调用, 这里采用直接调用即注册
	if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
		// Android 8.0 以上
		context.registerReceiver(receiver, intentFilter, broadcastPermission, null)
	} else {
		context.registerReceiver(receiver, intentFilter)
	}

	this.addObserver(object : DefaultLifecycleObserver {
		override fun onDestroy(owner: LifecycleOwner) {
			context.unregisterReceiver(receiver)
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
	this.lifecycle.registerLocalReceiver(actions = actions, onReceive = onReceive)
}

/**
 * Fragment 注册本地广播, 需要在 onViewCreated 之后调用
 *
 * @param actions               注册广播的 action
 * @param onReceive             收到广播的回调
 */
fun Fragment.registerLocalReceiver(vararg actions: String, onReceive: (BroadcastReceiver, Intent) -> Unit) {
	this.viewLifecycleOwner.lifecycle.registerLocalReceiver(actions = actions, onReceive = onReceive)
}

/**
 * Lifecycle 注册本地广播
 *
 * @param actions               注册广播的 action
 * @param onReceive             收到广播的回调
 */
fun Lifecycle.registerLocalReceiver(vararg actions: String, onReceive: (BroadcastReceiver, Intent) -> Unit) {
	val intentFilter = IntentFilter()
	actions.forEach { intentFilter.addAction(it) }

	val receiver = object : BroadcastReceiver() {
		override fun onReceive(context: Context, intent: Intent) {
			onReceive(this, intent)
		}
	}

	val localBroadcastManager = LocalBroadcastManager.getInstance(getApp())

	// onCreate 方法注册广播会在 LifecycleOwner 的 onCreate 方法返回后调用, 这里采用直接调用即注册
	localBroadcastManager.registerReceiver(receiver, intentFilter)

	this.addObserver(object : DefaultLifecycleObserver {
		override fun onDestroy(owner: LifecycleOwner) {
			localBroadcastManager.unregisterReceiver(receiver)
		}
	})
}