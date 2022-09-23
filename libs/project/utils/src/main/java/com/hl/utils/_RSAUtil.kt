package com.hl.utils

import com.blankj.utilcode.util.EncodeUtils
import com.blankj.utilcode.util.EncryptUtils

/**
 * @author  张磊  on  2021/11/11 at 10:50
 * Email: 913305160@qq.com
 */


fun String.encodeToRSA(publicKey: String?): String {
	val base = EncryptUtils.encryptRSA2Base64(
		this.toByteArray(),
		EncodeUtils.base64Decode(publicKey), 2048, "RSA/ECB/PKCS1Padding"
	)

	return EncodeUtils.base64Encode2String(EncodeUtils.base64Decode(base))
}