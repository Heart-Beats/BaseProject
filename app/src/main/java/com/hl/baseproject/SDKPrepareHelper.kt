package com.hl.baseproject

import android.app.Application
import com.hl.umeng.sdk.UMInitUtil
import com.umeng.socialize.PlatformConfig

/**
 * @author  张磊  on  2022/09/09 at 22:54
 * Email: 913305160@qq.com
 */
object SDKPrepareHelper {

	fun preInitSdk(application: Application) {
		// 预初始化友盟
		preInitUM(application)
	}

	private fun preInitUM(application: Application) {

		setPlatformConfigAppKeys()

		UMInitUtil.preInitUM(application) {
			this.isLogEnabled = BuildConfig.DEBUG
			this.appKey = BuildConfig.umengAppKey
			this.secretKey = BuildConfig.umengSecretKey
		}
	}

	private fun setPlatformConfigAppKeys() {

		//  微信设置
		PlatformConfig.setWeixin(BuildConfig.wxAppKey, BuildConfig.wxSecretKey)
		PlatformConfig.setWXFileProvider("${BuildConfig.APPLICATION_ID}.fileprovider")

		// QQ设置
		PlatformConfig.setQQZone(BuildConfig.qqAppKey, BuildConfig.qqSecretKey)
		PlatformConfig.setQQFileProvider("${BuildConfig.APPLICATION_ID}.fileprovider")

		// 企业微信设置
		PlatformConfig.setWXWork(
			BuildConfig.wxWorkAppKey,
			BuildConfig.wxWorkSecretKey,
			"1000002",
			"wwauthac6ffb259ff6f66a000002"
		)
		PlatformConfig.setWXWorkFileProvider("${BuildConfig.APPLICATION_ID}.fileprovider")

		// 支付宝设置
		PlatformConfig.setAlipay(BuildConfig.aliPayAppKey)
	}
}