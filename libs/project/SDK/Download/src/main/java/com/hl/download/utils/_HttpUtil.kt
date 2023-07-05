package com.hl.download.utils

import com.elvishew.xlog.XLog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request

/**
 * @author  张磊  on  2021/11/05 at 15:02
 * Email: 913305160@qq.com
 */

/**
 * 获取 Content-Length  头对应的大小，单位：Byte
 */
internal suspend fun String.getHttpContentLength(): Long {
	return try {
		withContext(Dispatchers.IO) {
			val request = Request.Builder()
				.url(this@getHttpContentLength)
				.head()
				.build()

			OkHttpClient().newCall(request).execute().header("Content-Length")?.toLongOrNull() ?: 0
		}
	} catch (e: Exception) {
		XLog.e(e)

		0
	}
}