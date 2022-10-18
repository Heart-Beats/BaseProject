package com.hl.shadow.pluginmanager

import android.content.Context
import com.hl.shadow.lib.ShadowConstants
import com.hl.shadow.pluginmanager.base.BaseDynamicLoaderPluginManager


/**
 * @author 张磊  on  2021/04/08 at 17:22
 * Email: 913305160@qq.com
 */
open class MyPluginManager(context: Context) : BaseDynamicLoaderPluginManager(context) {

	override fun getName() = ShadowConstants.PLUGIN_MANAGER_NAME

	override fun getPluginProcessServiceName() = ShadowConstants.MAIN_PLUGIN_PROCESS_SERVICE_NAME

}