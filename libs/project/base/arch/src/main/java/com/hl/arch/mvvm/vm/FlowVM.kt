package com.hl.arch.mvvm.vm

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.hl.api.PublicResp
import com.hl.arch.mvvm.api.event.RequestStateEvent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn

abstract class FlowVM : DispatcherVM() {

    private val tag = "FlowVM"

    /**
     * replay:                  对新订阅者重新发送之前已发出的数据项数目
     * extraBufferCapacity：    除 replay 外缓冲的数据项数目。即缓冲区总大小 = replay + extraBufferCapacity
     * onBufferOverflow:        缓冲区中已满时的策略。
     *      默认值：BufferOverflow.SUSPEND（挂起消费者）、DROP_LATEST（丢弃最新值）、DROP_OLDEST（丢弃最老值）。
     *
     * MutableSharedFlow（） 方法默认创建一个缓存大小为 0, 重发数据项为 0 的可读写共享热流，
     *      由于缓存大小为 0，因此新的订阅者获取不到之前的数据，所以非粘性，可作为通知事件
     *      同时需要注意： 新添加的消费者，若流无任何数据生产，则会被一直挂起直至有生产数据
     */
    private val _requestStateFlow by lazy { MutableSharedFlow<RequestStateEvent>() }

    // 请求状态为只读共享流
    internal val requestStateEventFlow by lazy { _requestStateFlow.asSharedFlow() }


    protected fun <BODY> apiFlow(
        needLoading: Boolean,
        reqBlock: suspend () -> PublicResp<BODY>,
        needDispatchFailEvent: Boolean = true,
        onFail: ((failCode: String, failReason: String) -> Unit)? = null,
        onSuccess: (body: BODY?) -> Unit = {}
    ) = flow {
        val resp = reqBlock()
        emit(resp)
    }
        .flowOn(Dispatchers.IO)
        .onEach {
            Log.d(tag, "apiFlow: 收到请求的数据")

            it.dispatchApiEvent(needDispatchFailEvent, onFail, onSuccess)
        }
        .onStart {
            Log.d(tag, "apiFlow: 请求开始")
            if (needLoading) {
                _requestStateFlow.emit(RequestStateEvent.createLoadingEvent())
            }
        }.catch { exception ->
            Log.e(tag, "apiFlow: 请求出错", exception)
            _requestStateFlow.emit(RequestStateEvent.createErrorEvent(exception))
        }
        .onCompletion { exception ->
            // 该方法在流完成或取消时调用，回调参数为取消异常或失败的原因
            if (exception != null) {
                // 异常未被 catch 代码块捕获，不为空
                Log.e(tag, "apiFlow: 请求完成", exception)
            } else {
                Log.d(tag, "apiFlow: 请求完成")
                if (needLoading) {
                    _requestStateFlow.emit(RequestStateEvent.createCompletedEvent())
                }
            }
        }

    /**
     * 返回一个初始值为 null 的 StateFlow
     *
     * @param  needLoading              是否需要 loading 事件
     * @param  reqBlock                 请求的方法
     * @param  needDispatchFailEvent    是否分发请求失败事件
     *
     */
    protected fun <BODY> apiStateFlow(
        needLoading: Boolean,
        reqBlock: suspend () -> PublicResp<BODY>,
        needDispatchFailEvent: Boolean = true,
        onFail: ((failCode: String, failReason: String) -> Unit)? = null,
        onSuccess: (body: BODY?) -> Unit = {}
    ) = apiFlow(needLoading, reqBlock, needDispatchFailEvent, onFail, onSuccess).toStateFlow()


    private fun <T> Flow<T>.toStateFlow() = this.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = null
    )

    protected fun <T> MutableStateFlow<T>.toStateFlow() = this.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = this.value
    )
}