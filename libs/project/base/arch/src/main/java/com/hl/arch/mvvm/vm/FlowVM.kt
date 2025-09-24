package com.hl.arch.mvvm.vm

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.hl.api.PublicResp
import com.hl.api.event.ApiEvent
import com.hl.api.event.IApiEventProvider
import com.hl.arch.mvvm.api.event.UiEvent
import com.hl.arch.mvvm.api.event.dismissLoading
import com.hl.arch.mvvm.api.event.showException
import com.hl.arch.mvvm.api.event.showLoading
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch

abstract class FlowVM : DispatcherVM(), IApiEventProvider {

    private val tag = "FlowVM"

    // 使用 ShareFlow 目前在 ViewModel 的 init 方法中请求数据，会导致相关事件收集丢失，因为这时页面的 onViewModelCreated 还没有被调用
    internal val uiEvent by lazy { MutableSharedFlow<UiEvent>() }

    protected fun <BODY> apiLaunch(
        needLoading: Boolean = true,
        needDispatchFailEvent: Boolean = true,
        reqBlock: suspend CoroutineScope.() -> PublicResp<BODY>,
        onFail: ((failCode: String, failReason: String) -> Unit)? = null,
        onSuccess: (body: BODY?) -> Unit = {}
    ) {
        if (needLoading) {
            // 请求开始开启 loading
            uiEvent.showLoading(viewModelScope)
        }

        reqBlock.launchAction {
            this.onSuccess {
                if (needLoading) {
                    // 请求结束关闭 loading
                    uiEvent.dismissLoading(viewModelScope)
                }

                onSuccess(it)
            }

            this.onFail { failCode, failReason ->
                if (needLoading) {
                    // 请求结束关闭 loading
                    uiEvent.dismissLoading(viewModelScope)
                }

                if (isRequestExceptionByCode(failCode.toInt())) {
                    uiEvent.showException(viewModelScope, Throwable(failReason))
                    return@onFail
                }

                if (needDispatchFailEvent) {
                    val apiEvent = createApiEvent(failCode.toInt(), failReason)
                    Log.i(tag, "根据请求创建的 apiEvent -----------> $apiEvent")

                    viewModelScope.launch {
                        apiEventFailedFlow.emit(apiEvent as ApiEvent.Failed)
                    }
                } else {
                    // 不分发请求错误事件时，使用 onFail 回传错误信息
                    onFail?.invoke(failCode, failReason)
                }
            }
        }
    }
}