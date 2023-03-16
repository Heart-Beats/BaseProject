package com.hl.umeng.sdk

import android.content.Context
import com.umeng.analytics.MobclickAgent
import com.umeng.commonsdk.UMConfigure
import com.umeng.message.PushAgent
import com.umeng.message.api.UPushRegisterCallback
import kotlin.concurrent.thread

/**
 * 友盟初始化工具类
 *
 * @author  张磊  on  2023/03/16 at 13:43
 * Email: 913305160@qq.com
 */
object UMInitUtil {

	private val umInitBuilder = UMInitBuilder()

	/**
	 * 友盟推送客户端对象
	 */
	private var pushAgent: PushAgent? = null


	/**
	 * 友盟预初始化，可在用户同意隐私政策之前调用
	 */
	fun preInitUM(context: Context, umInitBuilderBlock: UMInitBuilder.() -> Unit) {
		umInitBuilder.apply(umInitBuilderBlock)

		UMConfigure.setLogEnabled(umInitBuilder.isLogEnabled)
		val appKey = umInitBuilder.appKey
		val secretKey = umInitBuilder.secretKey
		val channel = umInitBuilder.channel

		// 友盟预初始化，不会采集设备信息，也不会向友盟后台上报数据
		UMConfigure.preInit(context.applicationContext, appKey, channel)
	}

	/**
	 * 直接初始化友盟
	 */
	fun initUM(
		context: Context,
		pushRegisterCallback: UPushRegisterCallback,
		pushAgentBlock: PushAgent.() -> Unit = {}
	) {
		thread(name = "友盟初始化线程") {
			// 在此处调用基础组件包提供的初始化函数 相应信息可在应用管理 -> 应用信息 中找到 http://message.umeng.com/list/apps
			// 参数三：渠道名称；
			// 参数四：设备类型，必须参数，传参数为UMConfigure.DEVICE_TYPE_PHONE则表示手机；传参数为UMConfigure.DEVICE_TYPE_BOX则表示盒子；默认为手机；
			// 参数五：Push推送业务的secret 填充Umeng Message Secret对应信息（需替换）

			val appKey = umInitBuilder.appKey
			val secretKey = umInitBuilder.secretKey
			val channel = umInitBuilder.channel
			UMConfigure.init(context.applicationContext, appKey, channel, UMConfigure.DEVICE_TYPE_PHONE, secretKey)

			// 选用AUTO页面采集模式
			MobclickAgent.setPageCollectionMode(MobclickAgent.PageMode.AUTO)

			//获取消息推送代理示例
			pushAgent = PushAgent.getInstance(context).apply {

				// 当 AndroidManifest.xml 中 package 值和 appId (应用包名) 不一致时，需要在初始化时设置为 AndroidManifest.xml 中 package 名
				this.resourcePackageName = context.packageName

				//注册推送服务，每次调用register方法都会回调该接口
				this.register(pushRegisterCallback)
				this.onAppStart()

				this.pushAgentBlock()
			}
		}
	}
}


class UMInitBuilder {

	/**
	 * 友盟日志输出开关， 生产环境建议关闭日志开关
	 */
	var isLogEnabled = false

	var appKey = ""
	var secretKey = ""
	var channel = "Umeng"
}