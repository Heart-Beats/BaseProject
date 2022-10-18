package com.hl.shadow.pluginmanager

import android.content.Context
import com.hl.shadow.lib.ShadowConstants
import com.hl.shadow.pluginmanager.base.BaseDynamicLoaderPluginManager

/**
 * @author  张磊  on  2022/09/20 at 18:37
 * Email: 913305160@qq.com
 *
 *  支持初始化为多进程的 PluginManager,  每个对象对应一个 PluginProcessService
 *
 *  @param  context           上下文对象
 *  @param ppsName            pluginProcessServiceName
 */

open class ProcessPluginManager(context: Context, private val ppsName: String) : BaseDynamicLoaderPluginManager(context) {

	override fun getName() = ShadowConstants.PLUGIN_MANAGER_NAME

	override fun getPluginProcessServiceName() = ppsName
}