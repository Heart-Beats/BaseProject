package com.hl.arch.web.bean

import java.io.Serializable

/**
 * @author  张磊  on  2022/06/17 at 20:42
 * Email: 913305160@qq.com
 */

data class NotifyCallBackFunctionData(
	val status: Boolean,

	val data: Any? = null
) : Serializable