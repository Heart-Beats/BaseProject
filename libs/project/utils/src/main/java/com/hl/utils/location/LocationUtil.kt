package com.hl.utils.location

import com.hl.activityresult.ActivityResultHelper

/**
 * @author  张磊  on  2022/08/30 at 15:04
 * Email: 913305160@qq.com
 */
object LocationUtil {

	/**
	 * 检测 GPS 是否可用， 不可用时会引导用户打开
	 */
	fun checkGpsEnable(activityResultHelper: com.hl.activityresult.ActivityResultHelper, onResult: (isGpsEnable: Boolean) -> Unit) {
		GpsUtil.checkGpsEnable(activityResultHelper, onResult)
	}
}