package com.hl.api.logger

import com.hl.api.logger.JsonUtil.decodeUnicode
import okhttp3.logging.HttpLoggingInterceptor

/**
 * @author 张磊  on  2021/08/11 at 15:07
 * Email: 913305160@qq.com
 */
internal class HttpLogger : HttpLoggingInterceptor.Logger {

	private val mMessage = StringBuilder()

	override fun log(message: String) {
		// 请求或者响应开始
		var message = message
		if (message.startsWith("--> POST")) {
			mMessage.setLength(0)
		}
		// 以{}或者[]形式的说明是响应结果的json数据，需要进行格式化
		if (message.startsWith("{") && message.endsWith("}")
			|| message.startsWith("[") && message.endsWith("]")
		) {
			mMessage.append(JsonUtil.formatJson(decodeUnicode(message)))
		}
		// 响应结束，打印整条日志
		if (message.startsWith("<-- END HTTP")) {
			println(mMessage.toString())
		}
	}
}