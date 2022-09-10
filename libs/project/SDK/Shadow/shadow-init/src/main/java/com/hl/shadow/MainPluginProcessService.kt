package com.hl.shadow

import com.tencent.shadow.dynamic.host.PluginProcessService

/**
 * @Author  张磊  on  2021/04/08 at 15:15
 * Email: 913305160@qq.com
 */

/**
 * 一个 PluginProcessService（简称PPS）代表一个插件进程。插件进程由 PPS 启动触发启动。
 * 新建 PPS 子类允许一个宿主中有多个互不影响的插件进程。
 */
class MainPluginProcessService : PluginProcessService() {

	// init {
	//     LoadPluginCallback.setCallback(object : Callback() {
	//         fun beforeLoadPlugin(partKey: String) {
	//             Log.d("PluginProcessPPS", "beforeLoadPlugin($partKey)")
	//         }
	//
	//         fun afterLoadPlugin(partKey: String, applicationInfo: ApplicationInfo, pluginClassLoader: ClassLoader, pluginResources: Resources?) {
	//             Log.d("PluginProcessPPS", "afterLoadPlugin(" + partKey + "," + applicationInfo.className + "{metaData=" + applicationInfo.metaData + "}" + "," + pluginClassLoader + ")")
	//         }
	//     })
	// }

}