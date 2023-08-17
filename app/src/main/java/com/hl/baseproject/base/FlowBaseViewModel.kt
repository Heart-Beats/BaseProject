package com.hl.baseproject.base

import androidx.lifecycle.viewModelScope
import com.hl.api.PublicResp
import com.hl.arch.api.IApiEventProvider
import com.hl.arch.mvvm.vm.BaseFlowVM
import com.hl.baseproject.repository.Repository
import com.hl.baseproject.repository.network.RequestApiInterface
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

/**
 * @author  张磊  on  2023/05/30 at 14:29
 * Email: 913305160@qq.com
 */
open class FlowBaseViewModel : BaseFlowVM(), IApiEventProvider by MyApiEventProvider() {

	protected val service: RequestApiInterface by lazy { Repository.buildApi() }

	protected fun <BODY> serviceLaunch(
		needLoading: Boolean = true,
		needDispatchFailEvent: Boolean = true,
		reqBlock: suspend RequestApiInterface.() -> PublicResp<BODY>,
		onFail: ((failCode: String, failReason: String) -> Unit)? = null,
		onSuccess: (body: BODY?) -> Unit = {}
	) {
		viewModelScope.launch {
			apiFlow(needLoading, needDispatchFailEvent, { service.reqBlock() }, onFail, onSuccess).collect()
		}
	}
}