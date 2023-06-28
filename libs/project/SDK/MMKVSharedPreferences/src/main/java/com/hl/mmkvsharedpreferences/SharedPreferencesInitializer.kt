package com.hl.mmkvsharedpreferences

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.startup.Initializer

/**
 * @author  张磊  on  2022/12/06 at 16:47
 * Email: 913305160@qq.com
 */


class SharedPreferencesInitializer : Initializer<Unit> {

	private val TAG = this.javaClass.simpleName

	/**
	 * 初始化操作，返回的初始化结果将被缓存
	 */
	override fun create(context: Context) {
		Log.d(TAG, "create: 开始初始化")

		MMKVIniter.init(context.applicationContext as Application)
	}

	/**
	 * 依赖关系，返回值是一个依赖组件的列表， 依赖组件优先初始化
	 */
	override fun dependencies(): List<Class<out Initializer<*>>> {
		return emptyList()
	}
}