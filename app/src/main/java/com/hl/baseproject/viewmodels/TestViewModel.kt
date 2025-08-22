package com.hl.baseproject.viewmodels

import com.elvishew.xlog.XLog
import com.hl.baseproject.base.BaseViewModel
import com.hl.baseproject.repository.network.bean.CheckPhoneParam
import com.hl.baseproject.repository.network.bean.DecryptParams
import com.hl.baseproject.repository.network.bean.EncryptParams
import com.hl.utils.Base64Util
import com.hl.utils.toBase64String

/**
 * @author  张磊  on  2022/08/31 at 17:43
 * Email: 913305160@qq.com
 */
class TestViewModel : BaseViewModel() {

	fun getShareLocalToken() {
		serviceLaunch(reqBlock = { this.getToken() }) {
			XLog.d("获取到的共享 token 结果 == $it")
		}
	}

	fun checkPhone() {
		serviceLaunch(reqBlock = {
			val checkPhoneParam = CheckPhoneParam("FFE624BEEBD1385EB4D1C674902CECC6673422DC114FC9BCADE96914685ECDAC")
			this.checkPhone(checkPhoneParam)
		}) {
			XLog.d("手机号校验结果 == $it")
		}
	}

	var encryptData: String = ""
	var keyId: String = ""

	fun doQuantumEncrypt() {
		serviceLaunch(reqBlock = {
			val encryptParams = EncryptParams(1026, 1, "", "Hello World!".toBase64String())
			this.doQuantumEncrypt(encryptParams)
		}) {
			XLog.d("量子加密结果 == $it")
			this.encryptData = it?.encryptData ?: ""
			this.keyId = it?.keyId ?: ""
		}
	}

	fun doQuantumDecrypt() {
		serviceLaunch(reqBlock = {
			val decryptParams = DecryptParams(1026, 1, "", encryptData, keyId)
			this.doQuantumDecrypt(decryptParams)
		}) {
			XLog.d("量子解密结果 == $it")

			XLog.d("量子解密得到原始数据 == ${Base64Util.decode2String(it?.decryptData)}")
		}
	}
}