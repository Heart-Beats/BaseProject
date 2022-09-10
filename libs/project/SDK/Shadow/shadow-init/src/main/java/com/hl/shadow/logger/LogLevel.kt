package com.hl.shadow.logger

/**
 * @author  张磊  on  2022/09/09 at 19:53
 * Email: 913305160@qq.com
 */
enum class LogLevel(val level: Int) {
	TRACE(1),
	DEBUG(2),
	INFO(3),
	WARN(4),
	ERROR(5);

	companion object {

		@JvmStatic
		fun getLogLevelByLevel(level: Int): LogLevel {
			return values().first() { it.level == level }
		}
	}
}