package com.hl.baseproject.repository.network.bean

/**
 * @author  张磊  on  2025/08/21 at 17:43
 * Email: 913305160@qq.com
 */

data class CheckPhoneParam(
	/**
	 * 手机号摘要值（sm3）
	 */
	val phoneHash: String
)


data class CheckPhoneBean(
	/**
	 * 校验结果
	 */
	val isSuccess: Boolean
)