package com.hl.baseproject

import android.app.Application
import android.content.Context
import com.elvishew.xlog.XLog
import com.umeng.analytics.MobclickAgent
import com.umeng.commonsdk.UMConfigure
import com.umeng.message.IUmengRegisterCallback
import com.umeng.message.PushAgent
import com.umeng.socialize.PlatformConfig

/**
 * @author  张磊  on  2022/06/15 at 11:31
 * Email: 913305160@qq.com
 */
object SDKInitHelper {

	fun initSdk(context: Context) {
		val applicationContext = context.applicationContext

		initUM(applicationContext)
	}

	fun preInitSdk(application: Application) {
		// 预初始化友盟
		preInitUM(application)
	}

	private fun preInitUM(application: Application) {

		setPlatformConfigAppKeys()

		// 在此处调用基础组件包提供的初始化函数 相应信息可在应用管理 -> 应用信息 中找到 http://message.umeng.com/list/apps
		// 参数三：渠道名称；
		// 参数四：设备类型，必须参数，传参数为UMConfigure.DEVICE_TYPE_PHONE则表示手机；传参数为UMConfigure.DEVICE_TYPE_BOX则表示盒子；默认为手机；
		// 参数五：Push推送业务的secret 填充Umeng Message Secret对应信息（需替换）
		UMConfigure.setLogEnabled(BuildConfig.DEBUG)
		//HuaWeiRegister.register(this);
		val appKey: String = BuildConfig.umengAppKey
		val secretKey: String = BuildConfig.umengSecretKey

		// 友盟预初始化，不会采集设备信息，也不会向友盟后台上报数据
		UMConfigure.preInit(application, appKey, "Umeng")

		// 选用AUTO页面采集模式
		MobclickAgent.setPageCollectionMode(MobclickAgent.PageMode.AUTO)

		// 当 AndroidManifest.xml 中 package值和 appId (应用包名) 不一致时，需要在初始化前设置资源包名
		PushAgent.getInstance(application).resourcePackageName = BuildConfig.APPLICATION_ID

		//获取消息推送代理示例
		val mPushAgent = PushAgent.getInstance(application)
		//注册推送服务，每次调用register方法都会回调该接口
		mPushAgent.register(object : IUmengRegisterCallback {
			override fun onSuccess(deviceToken: String) {
				//注册成功会返回deviceToken deviceToken是推送消息的唯一标志
				XLog.d("友盟deviceToken：$deviceToken")
			}

			override fun onFailure(s: String, s1: String) {
				XLog.d("友盟deviceToken失败：$s$s1")
			}
		})
		PushAgent.getInstance(application).onAppStart()
	}

	private fun setPlatformConfigAppKeys() {

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

	/**
	 * 直接初始化友盟
	 */
	private fun initUM(applicationContext: Context) {
		val umengAppKey: String = BuildConfig.umengAppKey
		val umengSecretKey: String = BuildConfig.umengSecretKey
		UMConfigure.init(applicationContext, umengAppKey, "Umeng", UMConfigure.DEVICE_TYPE_PHONE, umengSecretKey)
	}

}