package com.hl.shadow.pluginmanager

import android.content.Context
import com.tencent.shadow.core.manager.BasePluginManager
import com.tencent.shadow.core.manager.installplugin.InstalledDao
import com.tencent.shadow.core.manager.installplugin.InstalledPluginDBHelper
import com.tencent.shadow.core.manager.installplugin.UnpackManager

/**
 * @author  张磊  on  2022/09/20 at 18:37
 * Email: 913305160@qq.com
 *
 *  支持初始化为多进程的 PluginManager,  每个对象对应一个 PluginProcessService
 *
 *  @param  context           上下文对象
 *  @param ppsName            pluginProcessServiceName
 */

class ProcessPluginManager2(context: Context, ppsName: String) : ProcessPluginManager(context, ppsName) {

	private var managerName: String = ""
	private var ppsName: String = ""

	constructor(context: Context, managerName: String, ppsName: String) : this(context, ppsName) {
		this.managerName = managerName
		this.ppsName = ppsName

		val basePluginManagerClazz = BasePluginManager::class.java

		// 使用反射更改相关初始化值
		setFiledValue(basePluginManagerClazz, "mUnpackManager", UnpackManager(mHostContext.filesDir, name))
		setFiledValue(
			basePluginManagerClazz,
			"mInstalledDao",
			InstalledDao(InstalledPluginDBHelper(mHostContext, name))
		)
	}

	override fun getName(): String {
		return managerName
	}

	override fun getPluginProcessServiceName(): String {
		return ppsName
	}

	private fun <T> setFiledValue(clazz: Class<T>, filedName: String, value: Any) {
		val declaredField = clazz.getDeclaredField(filedName)
		declaredField.isAccessible = true
		declaredField.set(this, value)
		declaredField.isAccessible = false
	}
}