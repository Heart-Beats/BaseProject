package com.hl.baseproject.configs

import com.hl.baseproject.BuildConfig

/**
 * @author  张磊  on  2023/02/16 at 18:05
 * Email: 913305160@qq.com
 */
object AppConfig {
	/**
	 * 是否为调试模式
	 */
	var isDebug = BuildConfig.DEBUG

	/**
	 * 请求域名
	 */
	var BASE_URL = ""


	fun init(isDebug: Boolean, baseUrl: String) {
		this.isDebug = isDebug
		this.BASE_URL = baseUrl
	}

}