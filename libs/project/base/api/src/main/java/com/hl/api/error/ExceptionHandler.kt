package com.hl.api.error

import android.net.ParseException
import com.google.gson.JsonParseException
import org.apache.http.conn.ConnectTimeoutException
import org.json.JSONException
import retrofit2.HttpException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import javax.net.ssl.SSLHandshakeException

object ExceptionHandler {

	/**
	 * 约定异常
	 */
	object ERROR {
		// 未授权
		const val UNAUTHORIZED = 401

		// 禁止的
		const val FORBIDDEN = 403

		// 未找到
		const val NOT_FOUND = 404

		// 请求超时
		const val REQUEST_TIMEOUT = 408

		// 内部服务器错误
		const val INTERNAL_SERVER_ERROR = 500

		// 网关错误
		const val BAD_GATEWAY = 502

		// 暂停服务
		const val SERVICE_UNAVAILABLE = 503

		// 网关超时
		const val GATEWAY_TIMEOUT = 504

		/**
		 * 未知错误
		 */
		const val UNKNOWN: Int = 1000

		/**
		 * 解析错误
		 */
		const val PARSE_ERROR: Int = 1001

		/**
		 * 网络错误
		 */
		const val NETWORK_ERROR: Int = 1002


		/**
		 * 证书出错
		 */
		const val SSL_ERROR: Int = 1003

		/**
		 * 连接超时
		 */
		const val TIMEOUT_ERROR: Int = 1004

		val errorMap = hashMapOf(
			UNAUTHORIZED to "未授权",
			FORBIDDEN to "禁止访问",
			NOT_FOUND to "路径未找到",
			REQUEST_TIMEOUT to "请求超时",
			GATEWAY_TIMEOUT to "网关超时",
			INTERNAL_SERVER_ERROR to "内部服务器错误",
			BAD_GATEWAY to "网关错误",
			SERVICE_UNAVAILABLE to "暂停服务",

			PARSE_ERROR to "解析错误",
			NETWORK_ERROR to "网络错误",
			SSL_ERROR to "证书出错",
			TIMEOUT_ERROR to "连接超时",
			UNKNOWN to "未知错误",
		)

		// 添加 get 运算符重载
		operator fun get(errorCode: Int): String {
			return errorMap[errorCode]?.also {
				// 未知错误时，将错误码和错误信息添加到 errorMap 中， 这里主要针对 HttpException 异常
				errorMap.put(errorCode,  "网络错误")
			} ?: "未知错误"
		}
	}

	/**
	 * 处理异常
	 */
	fun handleException(throwable: Throwable?): ResponseThrowable {
		// 返回时抛出异常
		val responseThrowable: ResponseThrowable
		when (throwable) {
			is HttpException -> {
				val httpStatusCode = throwable.code()
				responseThrowable = ResponseThrowable(httpStatusCode, throwable)
				when (httpStatusCode) {
					ERROR.UNAUTHORIZED -> responseThrowable.message = ERROR[ERROR.UNAUTHORIZED]
					ERROR.FORBIDDEN -> responseThrowable.message = ERROR[ERROR.FORBIDDEN]
					ERROR.NOT_FOUND -> responseThrowable.message = ERROR[ERROR.NOT_FOUND]
					ERROR.REQUEST_TIMEOUT -> responseThrowable.message = ERROR[ERROR.REQUEST_TIMEOUT]
					ERROR.GATEWAY_TIMEOUT -> responseThrowable.message = ERROR[ERROR.GATEWAY_TIMEOUT]
					ERROR.INTERNAL_SERVER_ERROR -> responseThrowable.message = ERROR[ERROR.INTERNAL_SERVER_ERROR]
					ERROR.BAD_GATEWAY -> responseThrowable.message = ERROR[ERROR.BAD_GATEWAY]
					ERROR.SERVICE_UNAVAILABLE -> responseThrowable.message = ERROR[ERROR.SERVICE_UNAVAILABLE]
					else -> responseThrowable.message = ERROR[httpStatusCode]
				}
				return responseThrowable
			}

			is JsonParseException, is JSONException, is ParseException -> {
				responseThrowable = ResponseThrowable(ERROR.PARSE_ERROR, throwable)
				responseThrowable.message = ERROR[ERROR.PARSE_ERROR]
				return responseThrowable
			}

			is ConnectException, is UnknownHostException -> {
				responseThrowable = ResponseThrowable(ERROR.NETWORK_ERROR, throwable)
				responseThrowable.message = ERROR[ERROR.NETWORK_ERROR]
				return responseThrowable
			}

			is SSLHandshakeException -> {
				responseThrowable = ResponseThrowable(ERROR.SSL_ERROR, throwable)
				responseThrowable.message = ERROR[ERROR.SSL_ERROR]
				return responseThrowable
			}

			is ConnectTimeoutException -> {
				responseThrowable = ResponseThrowable(ERROR.TIMEOUT_ERROR, throwable)
				responseThrowable.message = ERROR[ERROR.TIMEOUT_ERROR]
				return responseThrowable
			}

			is SocketTimeoutException -> {
				responseThrowable = ResponseThrowable(ERROR.TIMEOUT_ERROR, throwable)
				responseThrowable.message = ERROR[ERROR.TIMEOUT_ERROR]
				return responseThrowable
			}

			else -> {
				responseThrowable = ResponseThrowable(ERROR.UNKNOWN, throwable)
				responseThrowable.message = throwable?.message ?: ERROR[ERROR.UNKNOWN]
				return responseThrowable
			}
		}
	}

	/**
	 * 是否是请求异常的错误码
	 */
	fun isRequestExceptionByCode(errorCode: Int): Boolean {
		return ERROR.errorMap.containsKey(errorCode)
	}

	class ResponseThrowable(var code: Int, throwable: Throwable?) : Exception(throwable) {
		override lateinit var message: String
	}
}


