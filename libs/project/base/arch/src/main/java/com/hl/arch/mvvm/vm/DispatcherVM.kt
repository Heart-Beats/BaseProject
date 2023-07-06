package com.hl.arch.mvvm.vm

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asFlow
import com.hl.arch.api.ApiEvent
import com.hl.arch.api.IApiEventProvider
import com.hl.arch.api.PublicResp
import com.hl.arch.mvvm.api.event.setSafeValue
import com.hl.arch.mvvm.liveData.EventLiveData

/**
 * @author  张磊  on  2023/05/30 at 11:32
 * Email: 913305160@qq.com
 */
abstract class DispatcherVM : ViewModel(), IApiEventProvider {
	private val tag = "DispatcherVM"

	/**
	 * 接口请求事件 ------ LiveData
	 */
	val apiEventFailedLiveData by lazy { EventLiveData<ApiEvent.Failed>() }

	/**
	 * 接口请求事件 ------ Flow
	 */
	val apiEventFailedFlow = apiEventFailedLiveData.asFlow()

	/**
	 * 该方法用来分发请求完成后对应的事件
	 *
	 * @param  needDispatchFailEvent    是否分发请求失败事件
	 * @param  onFail                   失败时的处理
	 * @param  onSuccess                请求成功时的处理
	 *
	 */
	protected open fun <BODY, T : PublicResp<BODY>> T.dispatchApiEvent(
		needDispatchFailEvent: Boolean = true,
		onFail: ((failCode: String, failReason: String) -> Unit)? = null,
		onSuccess: (body: BODY?) -> Unit = {}
	) {
		val apiEvent = createApiEvent(this.code().toInt(), this.message())
		Log.i(tag, "根据请求创建的 apiEvent -----------> $apiEvent")

		when (apiEvent) {
			is ApiEvent.Success -> onSuccess(this.respBody)
			is ApiEvent.Failed -> {

				if (needDispatchFailEvent) {
					apiEventFailedLiveData.setSafeValue(apiEvent)
				} else {
					// 不分发请求错误事件时，使用 onFail 回传错误信息
					onFail?.invoke(code(), message())
				}
			}
		}
	}
}