package com.hl.smsutil

import android.app.Activity
import android.app.PendingIntent
import android.content.Intent
import android.net.Uri
import android.telephony.PhoneNumberUtils
import android.telephony.SmsManager
import android.util.Log
import androidx.fragment.app.FragmentActivity


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

	private val smsManager: SmsManager by lazy {
		activity.getSystemService(SmsManager::class.java)
	}

	init {
		// 注册广播
		registerReceivers()
	}


	private fun registerReceivers() {
		// 发送短信状态广播
		activity.registerReceiver(SENT_SMS_ACTION) { receiver, intent ->
			if (intent.action == SENT_SMS_ACTION) {
				// 回调通知发送短信结果
				sendSmsResultCallBack(receiver.resultCode == Activity.RESULT_OK)
			}
		}

		// 接收短信状态广播
		activity.registerReceiver(DELIVERED_SMS_ACTION) { receiver, intent ->
			if (intent.action == DELIVERED_SMS_ACTION) {
				Log.d(TAG, "收信人已经成功接收")
			}
		}
	}


	/**
	 * 后台发送短信 ，可获取发送的状态
	 */
	fun sendMessageInBackground(phoneNumber: String, message: String, sendSmsResultCallBack: (Boolean) -> Unit = {}) {
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

	/**
	 * 后台群发短信， 可获取发送的状态
	 */
	fun sendMessageInBackground(
		phoneNumbers: List<String>? = null,
		message: String,
		sendSmsResultCallBack: (Boolean) -> Unit = {}
	) {
		phoneNumbers?.forEach {
			sendMessageInBackground(it, message, sendSmsResultCallBack)
		}
	}

	/**
	 * 前台可由用户选择发送短信， 无法获取发送的状态
	 */
	fun sendMessage(message: String, phoneNumber: String? = null, sendSmsResultCallBack: (Boolean) -> Unit = {}) {
		if (phoneNumber == null || PhoneNumberUtils.isGlobalPhoneNumber(phoneNumber)) {
			val intent = Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:"))
			phoneNumber?.run { intent.putExtra("address", phoneNumber) }
			intent.putExtra("sms_body", message)
			activity.startActivity(intent)

			// 此种情况无法获取发送的结果状态
		} else {
			Log.e(TAG, "非合法的号码格式！")
			sendSmsResultCallBack(false)
		}
	}

}