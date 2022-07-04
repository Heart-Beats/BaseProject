package com.hl.utils.activityResult

import android.content.Intent

/**
 * @author  张磊  on  2022/07/04 at 14:13
 * Email: 913305160@qq.com
 */
interface OnActivityResult {

	/**
	 *  [Activity.RESULT_OK] 时触发的回调
	 *
	 *  @param data 处理完成时返回的 Intent
	 */
	fun onResultOk(data: Intent?)

	/**
	 *  [Activity.RESULT_CANCELED] 时触发的回调
	 *
	 *  @param data  处理完成时返回的 Intent
	 */
	fun onResultCanceled(data: Intent?) {}

	/**
	 * 其他返回 code 时触发的回调
	 *
	 * @param resultCode 结果 code
	 * @param data 处理完成时返回的 Intent
	 */
	fun onResultOther(resultCode: Int, data: Intent?) {}
}