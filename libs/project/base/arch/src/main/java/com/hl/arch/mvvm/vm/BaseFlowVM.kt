package com.youma.arch.mvvm.vm

import androidx.lifecycle.viewModelScope
import com.youma.arch.mvvm.PublicResp
import kotlinx.coroutines.CoroutineScope

/**
 * @author  张磊  on  2021/11/03 at 14:19
 * Email: 913305160@qq.com
 */
abstract class BaseFlowVM : FlowVM() {


    /**
     * 请求获取 Flow， 该流仅收集时才会生产数据
     */
    protected fun <BODY> apiFlow(
        needLoading: Boolean = true,
        needDisPatchEvent: Boolean = true,
        needFailToast: Boolean = true,
        reqBlock: suspend CoroutineScope.() -> PublicResp<BODY>
    ) = apiFlow(needLoading, { reqBlock(viewModelScope) }, needDisPatchEvent, needFailToast)

    /**
     * 请求获取 StateFlow，该流会直接生产数据且保存最新的数据，同 LiveData 一般
     */
    protected fun <BODY> apiStateFlow(
        needLoading: Boolean = true,
        needDisPatchEvent: Boolean = true,
        needFailToast: Boolean = true,
        reqBlock: suspend CoroutineScope.() -> PublicResp<BODY>
    ) =
        apiStateFlow(needLoading, { reqBlock(viewModelScope) }, needDisPatchEvent, needFailToast)

}