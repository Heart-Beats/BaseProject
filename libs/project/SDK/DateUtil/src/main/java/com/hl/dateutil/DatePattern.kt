package com.hl.dateutil

import androidx.annotation.StringDef
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy

/**
 * @author  张磊  on  2023/02/24 at 14:42
 * Email: 913305160@qq.com
 */

@Retention(RetentionPolicy.SOURCE)
@StringDef(
	DatePattern.YMD,
	DatePattern.MD,
	DatePattern.YMD_HMS,
	DatePattern.HMS,
	DatePattern.HM,
	DatePattern.HMSS,
)
annotation class DatePattern {
	companion object {
		/**
		 * xxxx年-xx月-xx日
		 */
		const val YMD = "yyyy-MM-dd"

		/**
		 * xx月-xx日
		 */
		const val MD = "yyyy-MM-dd"

		/**
		 * xxxx年-xx月-xx日 xx时:xx分:xx秒
		 */
		const val YMD_HMS = "yyyy-MM-dd HH:mm:ss"

		/**
		 * xx时:xx分:xx秒
		 */
		const val HMS = "HH:mm:ss"

		/**
		 * xx时:xx分
		 */
		const val HM = "HH:mm"

		/**
		 * xx时:xx分:xx秒.XXX毫秒
		 */
		const val HMSS = "HH:mm:ss.Sss"
	}
}