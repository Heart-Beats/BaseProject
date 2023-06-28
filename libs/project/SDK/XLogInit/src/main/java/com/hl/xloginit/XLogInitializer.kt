package com.hl.xloginit

import android.content.Context
import android.util.Log
import androidx.startup.Initializer
import com.hl.mmkvsharedpreferences.SharedPreferencesInitializer

/**
 * @author  张磊  on  2022/12/06 at 16:47
 * Email: 913305160@qq.com
 */
class XLogInitializer : Initializer<Unit> {

	private val TAG = this.javaClass.simpleName

	/**
	 * 初始化操作，返回的初始化结果将被缓存
	 */
	override fun create(context: Context) {
		Log.d(TAG, "create: 开始初始化")
		XLogInitUtil.init()
	}

	/**
	 * 依赖关系，返回值是一个依赖组件的列表， 依赖组件优先初始化
	 */
	override fun dependencies(): List<Class<out Initializer<*>>> {
		return listOf(SharedPreferencesInitializer::class.java)
	}
}