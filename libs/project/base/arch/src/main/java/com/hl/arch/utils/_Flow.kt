package com.hl.arch.utils

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.hl.api.PublicResp
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

/**
 * @author  张磊  on  2021/11/07 at 2:10
 * Email: 913305160@qq.com
 */

/**
 *
 * 收集流的每个值，并且在页面进入 STOPPED 状态时停止收集，避免资源浪费
 *
 * 注意：当你从其他页面返回当前页面，页面生命周期回到 STARTED 状态，就会重新收集最新值，即使是热流回调也会重新触发
 */
inline fun <T> Flow<T>.repeatSafeCollect(viewLifecycleOwner: LifecycleOwner, collector: FlowCollector<T>) {

    viewLifecycleOwner.lifecycleScope.launch {
        // repeatOnLifecycle(STARTED) : 该方法会在 STARTED 状态时开始收集流，并且在 RESUMED 状态时保持收集, STOPPED 状态时结束收集，即取消流
        viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
            this@repeatSafeCollect.collect(collector)
        }
    }
}

/**
 *
 * 只收集流的最新值，并且在页面进入 STOPPED 状态时停止收集，避免资源浪费
 *
 * 注意：当你从其他页面返回当前页面，页面生命周期回到 STARTED 状态，就会重新收集最新值，即使是热流回调也会重新触发
 */
inline fun <T> Flow<T>.repeatSafeCollectLatest(viewLifecycleOwner: LifecycleOwner, noinline action: suspend (value: T) -> Unit) {
    viewLifecycleOwner.lifecycleScope.launch {
        // repeatOnLifecycle(STARTED) : 该方法会在 STARTED 状态时开始收集流，并且在 RESUMED 状态时保持收集, STOPPED 状态时结束收集，即取消流
        viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
            this@repeatSafeCollectLatest.collectLatest(action)
        }
    }
}

/**
 * 该方法会在页面显示时收集流，页面退出时停止收集
 *
 *      因为冷流在 collect 时会请求数据，所以如果有需要在进入页面时刷新数据的场景，可以使用冷流调用此方法
 *
 *      否则建议使用热流，保持收集最新的数据但不会触发请求数据
 *
 */
inline fun <T> Flow<PublicResp<T?>>.apiRespRepeatSafeCollect(
    viewLifecycleOwner: LifecycleOwner, crossinline isSuccess: PublicResp<T?>.() -> Boolean,
    crossinline onFail: (failCode: String, failReason: String) -> Unit = { _, _ -> },
    crossinline onSuccess: (value: T) -> Unit
) {
    viewLifecycleOwner.lifecycleScope.launch {

        /**
         * repeatOnLifecycle(STARTED) :
         *
         *  该方法会在 STARTED 状态时挂起执行，DESTROYED 状态时取消，再次进入 STARTED 状态会重新执行
         *
         *  体现在流上会在 STARTED 状态时开始收集流，并且在 RESUMED 状态时保持收集,
         *      STOPPED 状态时结束收集，即取消流, 结合此方式会让 Flow 的收集器如 LiveData 的观察者一般
         */
        viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
            this@apiRespRepeatSafeCollect.collect {

                if (it == null) return@collect

                if (it.isSuccess()) {
                    it.respBody?.run(onSuccess)
                } else {
                    onFail(it.code(), it.message())
                }
            }
        }

    }
}