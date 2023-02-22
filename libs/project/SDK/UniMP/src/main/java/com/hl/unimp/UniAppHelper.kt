package com.hl.unimp

import android.content.Context
import android.util.Log
import io.dcloud.common.util.RuningAcitvityUtil
import io.dcloud.feature.sdk.DCSDKInitConfig
import io.dcloud.feature.sdk.DCUniMPSDK
import io.dcloud.feature.sdk.Interface.*
import io.dcloud.feature.unimp.config.UniMPOpenConfiguration
import io.dcloud.feature.unimp.config.UniMPReleaseConfiguration
import org.json.JSONObject

/**
 * @author  张磊  on  2023/02/20 at 15:57
 * Email: 913305160@qq.com
 */
object UniMPHelper {

	private const val TAG = "UniAppHelper"

	/**
	 * 是否为 UniApp 小程序进程， 只有非小程序进程才需要初始化正常的三方 SDK
	 */
	fun isUniMPProcess(context: Context): Boolean {
		return RuningAcitvityUtil.getAppName(context.applicationContext).contains("unimp")
	}

	/**
	 * 初始化 UniApp 小程序框架
	 */
	fun initUniMP(context: Context, uniSDKInitConfigBuilder: DCSDKInitConfig.Builder.() -> Unit) {
		val config = DCSDKInitConfig.Builder().apply(uniSDKInitConfigBuilder).build()
		DCUniMPSDK.getInstance().initialize(context, config) {
			if (it) {
				Log.d(TAG, "initUniApp: 初始化 UniApp 成功")
			} else {
				Log.d(TAG, "initUniApp: 初始化 UniApp 失败")
			}
		}
	}

	/**
	 * 从 WGT 文件中启动 uni 小程序
	 *
	 *  @param context
	 *  @param appid                       :  uni 小程序对应的 appid
	 *  @param uniMPReleaseConfiguration   : 释放  Wgt 文件的相关配置
	 *  @param openArgumentsBlock          : 启动 uni小程序的启动参数
	 */
	fun openUniMPFromWgt(
		context: Context, appid: String,
		uniMPReleaseConfigurationBlock: UniMPReleaseConfiguration.() -> Unit,
		openArgumentsBlock: UniMPOpenConfiguration.() -> Unit = { }
	) {
		val uniMPReleaseConfiguration = UniMPReleaseConfiguration().apply { uniMPReleaseConfigurationBlock() }
		uniMPSdk.releaseWgtToRunPath(appid, uniMPReleaseConfiguration) { code, pArgs ->
			Log.d(TAG, "UniMPReleaseCallBack: code = $code, pArgs = $pArgs")
			if (code == 1) {
				openUniMPCheckExistsApp(context, appid, openArgumentsBlock)
			} else {
				Log.e(TAG, "releaseWgtToRunPath 失败，appid ==$appid")
			}
		}
	}

	/**
	 *  检查 uni 小程序是否存在并启动 ，前提需要已集成小程序资源： https://nativesupport.dcloud.net.cn/UniMPDocs/UseSdk/android.html#导入小程序应用资源
	 *
	 *  @param context
	 *  @param appid                       :  uni 小程序对应的 appid
	 *  @param openArguments               : 启动 uni小程序的启动参数
	 *
	 *  @return 小程序资源存在且启动成功返回 true，否则返回 false
	 */
	fun openUniMPCheckExistsApp(
		context: Context,
		appid: String,
		openArgumentsBlock: UniMPOpenConfiguration.() -> Unit = { }
	): Boolean {
		val uniMPOpenConfiguration = UniMPOpenConfiguration().apply { openArgumentsBlock() }
		if (isExistsApp(appid)) {
			uniMPSdk.openUniMP(context, appid, uniMPOpenConfiguration)
			return true
		} else {
			Log.e(TAG, "openUniMP: isExistsApp $appid == false")
			return false
		}
	}

	/**
	 * 检查 appid 对应的小程序资源是否存在
	 */
	fun isExistsApp(appid: String): Boolean {
		return uniMPSdk.isExistsApp(appid)
	}

	/**
	 *  获取 uni小程序运行路径
	 *  路径格式： "/xxx/xxx/宿主包名/files/apps/"
	 */
	fun getUniMPAppBasePath(context: Context): String {
		return uniMPSdk.getAppBasePath(context)
	}

	/**
	 * 获取已运行过的小程序应用版本信息,没有运行过的小程序是无法正常获取到版本信息的。返回值需要判空处理!!!
	 */
	fun getAppVersionInfo(appid: String): JSONObject? {
		return uniMPSdk.getAppVersionInfo(appid)
	}

	/**
	 * 设置 menu 点击事件回调接口
	 */
	fun setDefaultMenuButtonClickCallBack(callBack: IMenuButtonClickCallBack) {
		uniMPSdk.setDefMenuButtonClickCallBack(callBack)
	}

	/**
	 * 设置小程序关闭事件监听
	 */
	fun setUniMPOnCloseCallBack(callBack: IUniMPOnCloseCallBack) {
		uniMPSdk.setUniMPOnCloseCallBack(callBack)
	}

	/**
	 * 设置小程序胶囊按钮点击 "X" 关闭事件监听，设置后原关闭逻辑将不再执行！交由宿主实现相关逻辑
	 */
	fun setCapsuleCloseButtonClickCallBack(callBack: IDCUniMPOnCapsuleCloseButtontCallBack) {
		uniMPSdk.setCapsuleCloseButtonClickCallBack(callBack)
	}

	/**
	 * 设置小程序胶囊按钮点击"..."菜单事件监听，设置后原菜单弹窗逻辑将不再执行！交由宿主实现相关逻辑
	 */
	fun setCapsuleMenuButtonClickCallBack(callBack: IDCUniMPOnCapsuleMenuButtontCallBack) {
		uniMPSdk.setCapsuleMenuButtonClickCallBack(callBack)
	}

	/**
	 * 设置监听小程序发送给宿主的事件, 可通过此实现小程序与宿主的通信
	 */
	fun setOnUniMPEventCallBack(callBack: IOnUniMPEventCallBack) {
		uniMPSdk.setOnUniMPEventCallBack(callBack)
	}
}

private val uniMPSdk: DCUniMPSDK by lazy {
	if (DCUniMPSDK.getInstance().isInitialize) {
		DCUniMPSDK.getInstance()
	} else {
		throw IllegalStateException("Uni 小程序还未初始化，请先初始化！")
	}
}
