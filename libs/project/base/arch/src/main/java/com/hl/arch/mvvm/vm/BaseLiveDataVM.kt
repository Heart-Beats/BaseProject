package com.hl.arch.mvvm.vm

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.hl.arch.mvvm.PublicResp
import com.hl.arch.mvvm.api.event.dismissLoading
import com.hl.arch.mvvm.api.event.showException
import com.hl.arch.utils.setSafeValue
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import java.util.concurrent.TimeoutException

/**
 * @author  张磊  on  2021/11/09 at 11:19
 * Email: 913305160@qq.com
 */
abstract class BaseLiveDataVM : LiveDataVM() {

    private val TAG = "BaseLiveDataVM"

    protected fun <BODY> apiLaunch(
        needLoading: Boolean = true,
        needDisPatchEvent: Boolean = true,
        needFailToast: Boolean = true,
        reqBlock: suspend CoroutineScope.() -> PublicResp<BODY>,
        onFail: ((failCode: String, failReason: String) -> Unit)? = null,
        onSuccess: (body: BODY?) -> Unit = {}
    ) {
        apiLaunch(needLoading) {
            val publicResp = reqBlock()

            // 请求结束关闭 loadin , 分发数据
            uiEvent.dismissLoading()
            publicResp.dispatchApiEvent(needDisPatchEvent, needFailToast, onFail, onSuccess)
        }
    }

    /**
     * 该方法用来分发请求完成后对应的事件
     *
     * @param  needDispatchEvent 是否分发事件（所有非请求成功的事件）
     * @param  needDispatchFail 是否分发请求失败事件
     * @param  onFail           失败时的处理
     * @param  onSuccess        请求成功时的处理
     *
     */
    abstract fun <BODY, T : PublicResp<BODY>> T.dispatchApiEvent(
        needDisPatchEvent: Boolean = true, needFailToast: Boolean = true,
        onFail: ((failCode: String, failReason: String) -> Unit)? = null,
        onSuccess: (body: BODY?) -> Unit = {}
    )

    private fun apiLaunch(needLoading: Boolean, block: suspend CoroutineScope.() -> Unit): Job {
        return apiLaunch(needLoading, onException = {
            val message = when (it) {
                is SocketTimeoutException, is UnknownHostException, is TimeoutException, is ConnectException -> {
                    "网络异常，请检查网络"
                }
                else -> {
                    "${it.javaClass.simpleName} : ${it.message}"
                }
            }
            Log.e(TAG, "apiLaunch: 请求异常 -----> $message", it)

            if (needLoading) {
                //请求异常时关闭对话框，展示异常
                uiEvent.dismissLoading()
            }
            uiEvent.showException(Throwable(message))
            true
        }, block = block)
    }

    /**
     * 将请求的相应数据转换为对应的 LiveData
     *
     *      每次调用都会产生一个新的 LiveData，适用于获取事件结果而非状态
     *      仅在成功时会更新数据
     */
    protected fun <BODY> createApiLaunchLiveData(
        needLoading: Boolean = true,
        needDisPatchEvent: Boolean = true,
        needFailToast: Boolean = true,
        reqBlock: suspend CoroutineScope.() -> PublicResp<BODY>,
        onFail: ((failCode: String, failReason: String) -> Unit)? = null,
    ): LiveData<BODY?> {
        val respLiveData = MutableLiveData<BODY?>()
        apiLaunch(needLoading, needDisPatchEvent, needFailToast, reqBlock, onFail = { failCode, failReason ->
            onFail?.invoke(failCode, failReason)
        }) {
            respLiveData.setSafeValue(it)
        }
        return respLiveData
    }
}