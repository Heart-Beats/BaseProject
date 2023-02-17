package com.youma.yky.app.main.base

import com.hl.arch.api.IApiEventProvider
import com.hl.arch.api.PublicResp
import com.hl.arch.mvvm.vm.BaseLiveDataVM
import com.hl.baseproject.base.MyApiEventProvider
import com.hl.baseproject.repository.network.RequestApiInterface
import com.youma.yky.app.main.Repository

/**
 * @author  张磊  on  2022/01/12 at 12:27
 * Email: 913305160@qq.com
 */
open class BaseViewModel : BaseLiveDataVM(), IApiEventProvider by MyApiEventProvider() {

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