package com.hl.arch.mvvm.vm

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hl.arch.mvvm.api.event.UiEvent
import com.hl.arch.mvvm.api.event.dismissLoading
import com.hl.arch.mvvm.api.event.showException
import com.hl.arch.mvvm.liveData.EventLiveData
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

/**
 * @author  张磊  on  2021/11/06 at 18:30
 * Email: 913305160@qq.com
 */
open class LiveDataVM : ViewModel() {

    val uiEvent by lazy {
        EventLiveData<UiEvent>()
    }

    protected fun apiLaunch(
        needLoading: Boolean,
        onException: (Throwable) -> Boolean,
        block: suspend CoroutineScope.() -> Unit
    ): Job {
        return viewModelScope.launch(context = CoroutineExceptionHandler { _, throwable ->
            if (onException(throwable)) {
                return@CoroutineExceptionHandler
            }
            if (needLoading) {
                //请求异常时关闭对话框，展示异常
                uiEvent.dismissLoading()
            }
            uiEvent.showException(throwable)
        }) {
            block()
        }
    }

    override fun onCleared() {
        Log.d("LiveDataVM", "onCleared:  ViewModel(${this.javaClass.simpleName}) 被清除")
        super.onCleared()
    }
}