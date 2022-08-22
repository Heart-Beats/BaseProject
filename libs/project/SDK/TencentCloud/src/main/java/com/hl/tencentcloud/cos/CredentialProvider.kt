package com.hl.tencentcloud.cos

import com.tencent.qcloud.core.auth.ShortTimeCredentialProvider

/**
 * @author  张磊  on  2022/08/22 at 15:44
 * Email: 913305160@qq.com
 */
object CredentialProvider {

	/**
	 * 获取永久密钥
	 *
	 * @param secretId 密钥 ID
	 * @param secretId 密钥 Key
	 */
	fun getForeverCredential(secretId: String, secretKey: String): ShortTimeCredentialProvider {
		// keyDuration 为请求中的密钥有效期，单位为秒
		val oneMonthDuration = 60L * 60 * 24 * 30
		return ShortTimeCredentialProvider(secretId, secretKey, oneMonthDuration)
	}
}