package com.hl.utils

import android.util.Base64
import javax.crypto.Cipher
import javax.crypto.SecretKey
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.DESKeySpec
import javax.crypto.spec.DESedeKeySpec

/**
 * @Author  张磊  on  2020/09/16 at 16:29
 * Email: 913305160@qq.com
 */
object DESUtil {

	/*
	* 3DES数据块长度为64位，所以IV长度需要为8个字符（ECB模式不用IV），
	* 密钥长度为16或24个字符（8个字符以内则结果与DES相同），
	* IV与密钥超过长度则截取，不足则在末尾填充'\0'补足
	* */

	private const val iv = "01234567"

	/**
	 * 加解密算法枚举
	 * algorithm ： 算法
	 * transformation : 算法/反馈模式/填充模式
	 */
	private enum class Encryption(val algorithm: String, val transformation: String) {
		DES(algorithm = "DES", transformation = "DES"),
		DES3(algorithm = "desede", transformation = "desede/ECB/PKCS5Padding")
	}


	/**
	 * 3DES 加密
	 * @param   src 明文
	 * @param   key 秘钥，必须是长度大于等于 3*8 = 24 位
	 * @return  密文
	 */
	fun encrypt3DES(src: String, key: String): String? {
		return try {
			val desEncrypt = desEncrypt(Cipher.ENCRYPT_MODE, src.toByteArray(), key, Encryption.DES3)
			val encodeBuffer = Base64.encode(desEncrypt, Base64.DEFAULT)
			String(encodeBuffer)
		} catch (e: Exception) {
			println("3DES加密失败")
			println(e)
			""
		}
	}

	/**
	 * 3DES 解密
	 * @param   encryptText 秘文
	 * @param   key 秘钥，必须是长度大于等于 3*8 = 24 位
	 * @return  明文
	 */
	fun decrypt3DES(encryptText: String?, key: String): String? {
		return try {// --通过base64,将字符串转成byte数组
			val decode = Base64.decode(encryptText, Base64.DEFAULT)
			val desEncrypt = desEncrypt(Cipher.DECRYPT_MODE, decode, key, Encryption.DES3)
			String(desEncrypt)
		} catch (e: Exception) {
			println("3DES解密失败")
			println(e)
			""
		}
	}

	/**
	 * DES 加密
	 * @param   src 明文
	 * @param   key 秘钥，必须是长度大于等于 3*8 = 24 位
	 * @return  密文
	 */
	fun encryptDES(src: String, key: String): String? {
		return try {
			val desEncrypt = desEncrypt(Cipher.ENCRYPT_MODE, src.toByteArray(), key, Encryption.DES)
			val encodeBuffer = Base64.encode(desEncrypt, Base64.DEFAULT)
			String(encodeBuffer)
		} catch (e: Exception) {
			println("DES加密失败")
			println(e)
			""
		}
	}

	/**
	 * DES 解密
	 * @param   encryptText 秘文
	 * @param   key 秘钥，必须是长度大于等于 3*8 = 24 位
	 * @return  明文
	 */
	fun decryptDES(encryptText: String?, key: String): String? {
		return try {// --通过base64,将字符串转成byte数组
			val decode = Base64.decode(encryptText, Base64.DEFAULT)
			val desEncrypt = desEncrypt(Cipher.DECRYPT_MODE, decode, key, Encryption.DES)
			String(desEncrypt)
		} catch (e: Exception) {
			println("DES解密失败")
			println(e)
			""
		}
	}

	@Throws(Exception::class)
	private fun desEncrypt(mode: Int, content: ByteArray, key: String, encryption: Encryption): ByteArray {
		val dks = when (encryption) {
            Encryption.DES -> {
                DESKeySpec(key.toByteArray())
            }
            Encryption.DES3 -> {
                DESedeKeySpec(key.toByteArray())
            }
		}
		val keyFactory: SecretKeyFactory = SecretKeyFactory.getInstance(encryption.algorithm)
		val secureKey: SecretKey = keyFactory.generateSecret(dks)
		val cipher: Cipher = Cipher.getInstance(encryption.transformation)
		cipher.init(mode, secureKey)
		return cipher.doFinal(content)
	}
}