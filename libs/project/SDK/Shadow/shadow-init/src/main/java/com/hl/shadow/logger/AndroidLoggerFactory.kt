package com.hl.shadow.logger

import com.tencent.shadow.core.common.ILoggerFactory
import com.tencent.shadow.core.common.Logger
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentMap

/**
 * @Author 张磊  on  2021/04/08 at 15:35
 * Email: 913305160@qq.com
 */
class AndroidLoggerFactory private constructor(val logLevel: LogLevel, val onShadowLog: OnShadowLog) : ILoggerFactory {


	private val loggerMap: ConcurrentMap<String, Logger> = ConcurrentHashMap()

	override fun getLogger(name: String): Logger {
		val simpleLogger = loggerMap[name]
		return if (simpleLogger != null) {
			simpleLogger
		} else {
			val newInstance: Logger = object : MyLogger(name) {

				public override fun log(logLevel: LogLevel, message: String, t: Throwable?) {
					onShadowLog(logLevel, message, t)
				}

				public override fun initLogLevel(): LogLevel {
					return logLevel
				}
			}
			val oldInstance = loggerMap.putIfAbsent(name, newInstance)
			oldInstance ?: newInstance
		}
	}

	companion object {

		private var sInstance: AndroidLoggerFactory? = null

		fun getInstance(logLevel: LogLevel, onShadowLog: OnShadowLog): AndroidLoggerFactory {
			if (sInstance == null) {
				sInstance = AndroidLoggerFactory(logLevel, onShadowLog)
			}
			return sInstance as AndroidLoggerFactory
		}
	}
}

typealias OnShadowLog = (logLevel: LogLevel, message: String, t: Throwable?) -> Unit