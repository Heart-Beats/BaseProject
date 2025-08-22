package com.hl.baseproject.repository.network.bean

/**
 * @author  张磊  on  2025/08/20 at 18:51
 * Email: 913305160@qq.com
 */

data class EncryptParams(
	/**
	 * 加密算法：  1025:SM4_ECB  1026:SM4_CBC（更推荐，但最好保证初始化向量必须每次都不一样并且同步给解密者）
	 *
	 */
	val mode: Int,

	/**
	 * 填充模式： 0:ZeroPadding （需要将原文长度同步给解密方）  1:PKCS7Padding（更推荐）
	 */
	val paddingMode: Int,

	/**
	 * 初始化向量（HexString）： ECB 模式无需提供， CBC 模式为空则为默认，不为空必须要数据长度为 8 字节
	 *
	 */
	val iv: String? = null,

	/**
	 * 待加密的数据（base64 编码）
	 */
	val data: String,
)

data class EncryptBean(
	/**
	 * 量子应用名称
	 */
	val qmsName: String,

	/**
	 * 加密数据对应的密钥 id
	 */
	val keyId: String,

	/**
	 * 加密后的数据（base64 编码）
	 */
	val encryptData: String,
)