package com.hl.baseproject.compose.utils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow
import kotlinx.coroutines.flow.FlowCollector

/**
 * @author  张磊  on  2023/06/02 at 9:23
 * Email: 913305160@qq.com
 */

/**
 * 使用 Flow 来观察当前对象值的变化
 */
@Composable
fun <T> T.toFlowCollect(vararg keys: Any?, collector: FlowCollector<T>) {
	LaunchedEffect(keys) {
		snapshotFlow {
			this@toFlowCollect
		}.collect(collector)
	}
}