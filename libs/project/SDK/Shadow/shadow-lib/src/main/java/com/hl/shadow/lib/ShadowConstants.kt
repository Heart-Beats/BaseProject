package com.hl.shadow.lib

object ShadowConstants {

	/**
	 * PluginManager 实现的别名, 用于区分不同PluginManager实现的数据存储路径
	 */
	const val PLUGIN_MANAGER_NAME = "my-plugin-manager"

	/**
	 * demo插件so的abi
	 */
	const val ABI = ""

	/**
	 * 宿主中注册的PluginProcessService实现的类名, 该 PPS 为示例代码中的简易实现，不可更改其中对应插件包的 UUID
	 */
	const val MAIN_PLUGIN_PROCESS_SERVICE_NAME =
		"com.hl.shadow.pps.MainPluginProcessService"

	/**
	 * 宿主中注册的PluginProcessService实现的类名, 该 PPS 为自定义实现，可更改其中对应插件包的 UUID
	 */
	const val DYNAMIC_UUID_PLUGIN_PROCESS_SERVICE_NAME =
		"com.hl.shadow.pps.DynamicUuidPluginProcessService"

	/**
	 * 插件 apk/zip 路径
	 */
	const val KEY_PLUGIN_ZIP_PATH = "pluginZipPath"

	/**
	 * partKey 用来区分入口， 用来实现多个插件不同的加载
	 */
	const val KEY_PLUGIN_PART_KEY = "KEY_PLUGIN_PART_KEY"

	/**
	 * 启动的插件 Activity 或 Service 路径
	 */
	const val KEY_CLASSNAME = "KEY_CLASSNAME"

	/**
	 * 需要传入到启动插件里的参数，为 Bundle，它会传递存放在启动的 Intent 的 extras 字段中
	 */
	const val KEY_EXTRAS = "KEY_EXTRAS"

	/**
	 * 需要直接启动插件的容器类型： Activity 或者 Service
	 */
	const val KEY_FROM_ID = "KEY_FROM_ID"

	/**
	 * 打开 Activity 传入的 formId
	 */
	const val FROM_ID_START_ACTIVITY = 1001L

	/**
	 * 打开 Service 传入的 formId
	 */
	const val FROM_ID_CALL_SERVICE = 1002L

	/**
	 *  启动 Intent 的 action
	 */
	const val KEY_INTENT_ACTION = "KEY_INTENT_ACTION"
}