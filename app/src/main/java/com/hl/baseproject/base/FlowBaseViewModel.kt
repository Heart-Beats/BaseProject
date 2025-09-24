package com.hl.baseproject.base

import com.hl.api.PublicResp
import com.hl.api.event.IApiEventProvider
import com.hl.arch.mvvm.vm.FlowVM
import com.hl.baseproject.repository.Repository
import com.hl.baseproject.repository.network.RequestApiInterface

/**
 * @author  张磊  on  2023/05/30 at 14:29
 * Email: 913305160@qq.com
 */
open class FlowBaseViewModel : FlowVM(), IApiEventProvider by MyApiEventProvider() {

	protected val service: RequestApiInterface by lazy { Repository.buildApi() }

	protected fun <BODY> serviceLaunch(
		needLoading: Boolean = true,
		needDispatchFailEvent: Boolean = true,
		reqBlock: suspend RequestApiInterface.() -> PublicResp<BODY>,
		onFail: ((failCode: String, failReason: String) -> Unit)? = null,
		onSuccess: (body: BODY?) -> Unit = {}
	) {
		apiLaunch(needLoading, needDispatchFailEvent, { service.reqBlock() }, onFail, onSuccess)
	}
}