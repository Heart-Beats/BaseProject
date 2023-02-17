package com.hl.baseproject.base

import com.hl.arch.api.ApiEvent
import com.hl.arch.api.IApiEventProvider

/**
 * @author  张磊  on  2023/02/17 at 17:27
 * Email: 913305160@qq.com
 */
class MyApiEventProvider : IApiEventProvider {

	override fun provideSuccessApiEvent(): ApiEvent.Success {
		return ApiEvent.Success(0, "请求成功")
	}

	override fun provideFailedApiEvents(): List<ApiEvent.Failed> {
		return listOf(ApiEventFailed.Error)
	}
}


sealed class ApiEventFailed(failCode: Int, failMsg: String) : ApiEvent.Failed(failCode, failMsg) {

	object Error : ApiEventFailed(-1, "请求失败")
}
