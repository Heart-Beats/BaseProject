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

	// 未授权
	private const val UNAUTHORIZED = 401

	// 禁止的
	private const val FORBIDDEN = 403

	// 未找到
	private const val NOT_FOUND = 404

	// 请求超时
	private const val REQUEST_TIMEOUT = 408

	// 内部服务器错误
	private const val INTERNAL_SERVER_ERROR = 500

	// 网关错误
	private const val BAD_GATEWAY = 502

	// 暂停服务
	private const val SERVICE_UNAVAILABLE = 503

	// 网关超时
	private const val GATEWAY_TIMEOUT = 504

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
					UNAUTHORIZED -> responseThrowable.message = "未授权"
					FORBIDDEN -> responseThrowable.message = "禁止访问"
					NOT_FOUND -> responseThrowable.message = "路径未找到"
					REQUEST_TIMEOUT -> responseThrowable.message = "请求超时"
					GATEWAY_TIMEOUT -> responseThrowable.message = "网关超时"
					INTERNAL_SERVER_ERROR -> responseThrowable.message = "内部服务器错误"
					BAD_GATEWAY -> responseThrowable.message = "网关错误"
					SERVICE_UNAVAILABLE -> responseThrowable.message = "暂停服务"
					else -> responseThrowable.message = "网络错误"
				}
				return responseThrowable
			}

			is ServerException -> {
				// 服务器异常
				val resultException = throwable
				responseThrowable = ResponseThrowable(resultException.code, resultException)
				responseThrowable.message = resultException.message ?: "未知错误"
				return responseThrowable
			}

			is JsonParseException, is JSONException, is ParseException -> {
				responseThrowable = ResponseThrowable(ERROR.PARSE_ERROR, throwable)
				responseThrowable.message = "解析错误"
				return responseThrowable
			}

			is ConnectException, is UnknownHostException -> {
				responseThrowable = ResponseThrowable(ERROR.NETWORK_ERROR, throwable)
				responseThrowable.message = "网络异常"
				return responseThrowable
			}

			is SSLHandshakeException -> {
				responseThrowable = ResponseThrowable(ERROR.SSL_ERROR, throwable)
				responseThrowable.message = "证书验证失败"
				return responseThrowable
			}

			is ConnectTimeoutException -> {
				responseThrowable = ResponseThrowable(ERROR.TIMEOUT_ERROR, throwable)
				responseThrowable.message = "连接超时"
				return responseThrowable
			}

			is SocketTimeoutException -> {
				responseThrowable = ResponseThrowable(ERROR.TIMEOUT_ERROR, throwable)
				responseThrowable.message = "连接超时"
				return responseThrowable
			}

			else -> {
				responseThrowable = ResponseThrowable(ERROR.UNKNOWN, throwable)
				responseThrowable.message = throwable?.message ?: "未知错误"
				return responseThrowable
			}
		}
	}


	/**
	 * 约定异常
	 */
	object ERROR {
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
	}

	class ResponseThrowable(var code: Int, throwable: Throwable?) : Exception(throwable) {
		override lateinit var message: String
	}

	class ServerException : RuntimeException() {
		var code: Int = 0
		override var message: String? = null
	}
}


