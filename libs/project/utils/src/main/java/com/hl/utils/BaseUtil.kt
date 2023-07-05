package com.hl.utils

import android.app.Application

/**
 * @author  张磊  on  2022/08/05 at 17:10
 * Email: 913305160@qq.com
 */
object BaseUtil {

	lateinit var app: Application

	fun init(app: Application) {
		this.app = app
	}
}