package com.hl.mmkvsharedpreferences

import android.app.Application
import com.tencent.mmkv.MMKV
import kotlin.concurrent.thread

/**
 * @author  张磊  on  2023/06/28 at 13:59
 * Email: 913305160@qq.com
 */


internal object MMKVIniter {

	internal lateinit var globalApp: Application

	fun init(app: Application) {
		globalApp = app

		thread {
			// 初始化 MMKV
			MMKV.initialize(app)
		}
	}
}


/**
 * 获取全局的 APP 对象
 */
fun getApp() = MMKVIniter.globalApp