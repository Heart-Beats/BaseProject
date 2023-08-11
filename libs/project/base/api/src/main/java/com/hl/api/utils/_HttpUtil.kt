package com.hl.api.utils

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

/**
 * @author  张磊  on  2021/11/05 at 15:02
 * Email: 913305160@qq.com
 */

/**
 * 获取 Content-Length  头对应的大小，单位：Byte
 */
suspend fun String.getHttpContentLength(): Long {
	return try {
		withContext(Dispatchers.IO) {
			val request = Request.Builder()
				.url(this@getHttpContentLength)
				.head()
				.build()

			OkHttpClient().newCall(request).execute().header("Content-Length")?.toLongOrNull() ?: 0
		}
	} catch (e: Exception) {
		Log.d("_HttpUtil", "getHttpContentLength: ",e)
		0
	}
}

/**
 * 从 Url 中获取下载的文件名称
 */
fun String.getDownloadFileNameFromUrl(): String {
	val fileUrlPath = this.substringAfterLast("/")

	// 分割后的 fileUrl 可能包含参数，如：xxx.mp4?width=1280
	return fileUrlPath.substringBefore("?", fileUrlPath)
}

/**
 * 将 URL 转为合法的 URL 地址, 主要供浏览器使用
 */
fun String.getLegalUrl(): String {
	return URLEncoder.encode(this.trim(), StandardCharsets.UTF_8.name())  // 非法字符（中文、空格等）转为合法的 URL 编码
		.replace("+", "%20")    // 还原空格， 用 %20 编码代替
		.replace("%3A", ":")    // 还原 ':'
		.replace("%2F", "/")    // 还原 '/'
}