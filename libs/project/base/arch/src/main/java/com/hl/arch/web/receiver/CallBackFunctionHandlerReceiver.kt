package com.hl.arch.web.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.hl.arch.web.H5Constants
import com.hl.arch.web.bean.H5Return
import com.hl.arch.web.bean.NotifyCallBackFunctionData
import com.hl.utils.GsonUtil

/**
 * @author  张磊  on  2022/06/17 at 19:58
 * Email: 913305160@qq.com
 */
internal object CallBackFunctionHandlerReceiver : BroadcastReceiver() {

	override fun onReceive(context: Context, intent: Intent?) {
		if (intent?.action == H5Constants.ACTION_CALL_BACK) {
			// 获取  CallBackFunction 对应的 key, 需要主动存放在 intent 中
			intent?.getStringExtra(H5Constants.ACTION_CALL_BACK_FUNCTION_KEY)?.run {
				val callBackFunction = CallBackFunctionDataStore.getCallBackFunction(this)

				val notifyCallBackFunctionData =
					intent.getSerializableExtra(H5Constants.ACTION_CALL_BACK_DATA_KEY) as NotifyCallBackFunctionData

				val data = notifyCallBackFunctionData?.data?.run {
					GsonUtil.toJson(this)
				} ?: ""

				if (notifyCallBackFunctionData.status) {
					callBackFunction?.onCallBack(H5Return.success(data))
				} else {
					callBackFunction?.onCallBack(H5Return.fail(data))
				}

				// 处理完成之后主动移除 CallBackFunction
				CallBackFunctionDataStore.removeCallBackFunction(this)
			}
		}
	}

}