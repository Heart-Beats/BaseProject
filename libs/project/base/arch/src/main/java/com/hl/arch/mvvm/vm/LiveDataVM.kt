package com.hl.arch.mvvm.vm

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.hl.api.PublicResp
import com.hl.api.event.ApiEvent
import com.hl.api.event.IApiEventProvider
import com.hl.arch.mvvm.api.event.UiEvent
import com.hl.arch.mvvm.api.event.dismissLoading
import com.hl.arch.mvvm.api.event.setSafeValue
import com.hl.arch.mvvm.api.event.showException
import com.hl.arch.mvvm.api.event.showLoading
import com.hl.arch.mvvm.liveData.EventLiveData
import com.hl.utils.setSafeValue
import kotlinx.coroutines.CoroutineScope

/**
 * @author  张磊  on  2021/11/06 at 18:30
 * Email: 913305160@qq.com
 */
abstract class LiveDataVM : DispatcherVM(), IApiEventProvider {

    private val tag = "LiveDataVM"

    internal val uiEvent by lazy {
        EventLiveData<UiEvent>()
    }

    protected fun <BODY> apiLaunch(
        needLoading: Boolean = true,
        needDispatchFailEvent: Boolean = true,
        reqBlock: suspend CoroutineScope.() -> PublicResp<BODY>,
        onFail: ((failCode: String, failReason: String) -> Unit)? = null,
        onSuccess: (body: BODY?) -> Unit = {}
    ) {
        if (needLoading) {
            // 请求开始开启 loading
            uiEvent.showLoading()
        }

        reqBlock.launchAction {
            this.onSuccess {
                if (needLoading) {
                    // 请求结束关闭 loading
                    uiEvent.dismissLoading()
                }

                onSuccess(it)
            }

            this.onFail { failCode, failReason ->
                if (needLoading) {
                    // 请求结束关闭 loading
                    uiEvent.dismissLoading()
                }

                if (isRequestExceptionByCode(failCode.toInt())) {
                    uiEvent.showException(Throwable(failReason))
                    return@onFail
                }

                if (needDispatchFailEvent) {
                    val apiEvent = createApiEvent(failCode.toInt(), failReason)
                    Log.i(tag, "根据请求创建的 apiEvent -----------> $apiEvent")
                    apiEventFailedLiveData.setSafeValue(apiEvent as? ApiEvent.Failed)
                } else {
                    // 不分发请求错误事件时，使用 onFail 回传错误信息
                    onFail?.invoke(failCode, failReason)
                }
            }
        }
    }

    /**
     * 将请求的相应数据转换为对应的 LiveData
     *
     *      每次调用都会产生一个新的 LiveData，适用于获取事件结果而非状态
     *      仅在成功时会更新数据
     */
    protected fun <BODY> createApiLaunchLiveData(
        needLoading: Boolean = true,
        needDispatchFailEvent: Boolean = true,
        reqBlock: suspend CoroutineScope.() -> PublicResp<BODY>,
        onFail: ((failCode: String, failReason: String) -> Unit)? = null,
    ): LiveData<BODY?> {
        val respLiveData = MutableLiveData<BODY?>()
        apiLaunch(needLoading, needDispatchFailEvent, reqBlock, onFail = { failCode, failReason ->
            onFail?.invoke(failCode, failReason)
        }) {
            respLiveData.setSafeValue(it)
        }
        return respLiveData
    }
}