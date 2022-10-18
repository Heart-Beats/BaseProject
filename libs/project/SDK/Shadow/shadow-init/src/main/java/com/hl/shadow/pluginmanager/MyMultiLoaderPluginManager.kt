package com.hl.shadow.pluginmanager

import android.content.Context
import android.os.Bundle
import com.hl.shadow.lib.ShadowConstants
import com.tencent.shadow.dynamic.host.EnterCallback
import com.tencent.shadow.dynamic.manager.BaseMultiLoaderPluginManager

/**
 * @author  张磊  on  2022/09/27 at 10:31
 * Email: 913305160@qq.com
 */

/**
 *  @param pluginName  多 Loader 的 PPS 加载插件包区分不同插件包的标识
 */
open class MyMultiLoaderPluginManager(context: Context, private val pluginName: String) : BaseMultiLoaderPluginManager(context) {


	override fun getName() = ShadowConstants.PLUGIN_MANAGER_NAME

	override fun getPluginProcessServiceName() = ShadowConstants.MULTI_LOADER_PLUGIN_PROCESS_SERVICE_NAME

	/**
	 * 多 Loader 的 PPS，需要 hack 多个 RuntimeContainer，因此需要使用 pluginKey 来作为插件的身份标识
	 * Note：一个插件包有一份 loader、一份 runtime、多个 pluginPart，该 key 与插件包一一对应
	 */
	override fun getPluginKey() = pluginName


	/**
	 * @param context  context
	 * @param fromId   标识本次请求的来源位置，用于区分入口
	 * @param bundle   参数列表
	 * @param callback 用于从PluginManager实现中返回View
	 */
	override fun enter(context: Context, fromId: Long, bundle: Bundle, callback: EnterCallback?) {
		// // 插件 zip 包地址，可以直接写在这里，也用Bundle可以传进来
		// val pluginZipPath = bundle.getString(ShadowConstants.KEY_PLUGIN_ZIP_PATH) ?: return
		//
		// //插件名称取插件包不带扩展的文件名称
		// pluginName = File(pluginZipPath).nameWithoutExtension

		super.enter(context, fromId, bundle, callback)
	}
}