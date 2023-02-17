package com.hl.arch.api


/**
 * @author  张磊  on  2022/08/31 at 17:05
 * Email: 913305160@qq.com
 */
sealed class ApiEvent(open val code: Int, open var msg: String) {

	data class Success(val successCode: Int, var successMsg: String) : ApiEvent(successCode, successMsg)

	open class Failed(failCode: Int, failMsg: String) : ApiEvent(failCode, failMsg) {
		data class UnknownError(val unknownCode: Int) : Failed(unknownCode, "")
	}
}