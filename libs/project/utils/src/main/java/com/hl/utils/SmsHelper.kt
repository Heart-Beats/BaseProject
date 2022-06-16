package com.hl.utils

import android.app.Activity
import android.app.PendingIntent
import android.content.Intent
import android.content.IntentFilter
import android.telephony.SmsManager
import android.util.Log
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner


/**
 * @author  张磊  on  2022/06/16 at 19:13
 * Email: 913305160@qq.com
 */

class SmsHelper(val activity: FragmentActivity) {

	companion object {
		private const val TAG = "SmsHelper"

		//处理返回的发送状态
		private const val SENT_SMS_ACTION = "SENT_SMS_ACTION"

		//处理返回的接收状态
		private const val DELIVERED_SMS_ACTION = "DELIVERED_SMS_ACTION"
	}


	/**
	 * 发送短信的回调结果
	 */
	private var sendSmsResultCallBack: (Boolean) -> Unit = {}

	/**
	 * 	发送短信状态
	 */
	private val sendBroadcastReceiver by lazy {
		MyBroadcastReceiver { receiver, _, intent ->
			if (intent.action == SENT_SMS_ACTION) {
				// 回调通知发送短信结果
				sendSmsResultCallBack(receiver.resultCode == Activity.RESULT_OK)
			}
		}
	}


	/**
	 * 接收短信状态
	 */
	private val deliverBroadcastReceiver by lazy {
		MyBroadcastReceiver { receiver, context, intent ->
			if (intent.action == DELIVERED_SMS_ACTION) {
				Log.d(TAG, "收信人已经成功接收")
			}
		}
	}


	private val smsManager: SmsManager by lazy {
		activity.getSystemService(SmsManager::class.java)
	}

	init {
		activity.lifecycle.addObserver(object : DefaultLifecycleObserver {
			override fun onCreate(owner: LifecycleOwner) {
				// 注册广播
				registerReceivers()
			}

			override fun onDestroy(owner: LifecycleOwner) {
				// 注销广播
				unRegisterReceivers()
			}
		})

	}


	private fun registerReceivers() {
		val sendIntentFilter = IntentFilter(SENT_SMS_ACTION)
		activity.registerReceiver(sendBroadcastReceiver, sendIntentFilter)

		val deliverIntentFilter = IntentFilter(DELIVERED_SMS_ACTION)
		activity.registerReceiver(deliverBroadcastReceiver, deliverIntentFilter)
	}

	private fun unRegisterReceivers() {
		activity.unregisterReceiver(sendBroadcastReceiver)
		activity.unregisterReceiver(deliverBroadcastReceiver)
	}


	fun sendMessage(phoneNumber: String, message: String, sendSmsResultCallBack: (Boolean) -> Unit = {}) {
		this.sendSmsResultCallBack = sendSmsResultCallBack

		val sentIntent = Intent(SENT_SMS_ACTION)
		val sentPI = PendingIntent.getBroadcast(activity, 0, sentIntent, PendingIntent.FLAG_UPDATE_CURRENT)

		val deliverIntent = Intent(DELIVERED_SMS_ACTION)
		val deliverPI = PendingIntent.getBroadcast(activity, 0, deliverIntent, PendingIntent.FLAG_UPDATE_CURRENT)

		if (message.length > 70) {
			// 将短信切割成多个部分
			val msgParts: ArrayList<String> = smsManager.divideMessage(message)
			val sentIntents = ArrayList<PendingIntent>()
			val deliverIntents = ArrayList<PendingIntent>()
			repeat(msgParts.size) {
				sentIntents.add(sentPI)
				deliverIntents.add(deliverPI)
			}
			smsManager.sendMultipartTextMessage(phoneNumber, null, msgParts, sentIntents, deliverIntents)
		} else {
			smsManager.sendTextMessage(phoneNumber, null, message, sentPI, deliverPI)
		}
	}

	fun sendMessage(phoneNumbers: List<String>? = null, message: String, sendSmsResultCallBack: (Boolean) -> Unit = {}) {
		phoneNumbers?.forEach {
			sendMessage(it, message, sendSmsResultCallBack)
		}
	}

}