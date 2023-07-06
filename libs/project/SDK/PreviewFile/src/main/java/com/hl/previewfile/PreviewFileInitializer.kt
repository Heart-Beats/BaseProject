package com.hl.previewfile

import android.content.Context
import android.util.Log
import androidx.startup.Initializer
import com.hl.download.DownloadInitializer
import kotlin.concurrent.thread

/**
 * @author  张磊  on  2022/12/06 at 16:47
 * Email: 913305160@qq.com
 */
class PreviewFileInitializer : Initializer<Unit> {

	private val TAG = this.javaClass.simpleName

	/**
	 * 初始化操作，返回的初始化结果将被缓存
	 */
	override fun create(context: Context) {
		Log.d(TAG, "create: 开始初始化")

		// 异步初始化相关库
		thread {
			X5Helper.initX5(context)
		}
	}

	/**
	 * 依赖关系，返回值是一个依赖组件的列表， 依赖组件优先初始化
	 */
	override fun dependencies(): List<Class<out Initializer<*>>> {
		return listOf(DownloadInitializer::class.java)
	}

}