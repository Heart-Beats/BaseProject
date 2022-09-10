package com.hl.baseproject

import android.app.Application
import android.os.Build
import android.webkit.WebView
import com.elvishew.xlog.XLog
import com.hl.shadow.logger.AndroidLoggerFactory
import com.hl.shadow.logger.LogLevel
import com.hl.utils.DeviceInfoUtil
import com.hl.utils.isMainProcess
import com.hl.utils.isProcess
import com.tencent.shadow.core.common.LoggerFactory
import com.tencent.shadow.dynamic.host.DynamicRuntime

/**
 * @author  张磊  on  2022/06/10 at 16:13
 * Email: 913305160@qq.com
 */
class MyApplication : Application() {

	override fun onCreate() {
		super.onCreate()

		if (isMainProcess()) {

			SDKPrepareHelper.preInitSdk(this)
			SDKInitHelper.initSdk(this)

			val deviceAllInfo = DeviceInfoUtil.getDeviceAllInfo(this)
			XLog.d("运行设备信息-------------->\n $deviceAllInfo")

		} else if (this.isProcess(":plugin")) {
			println("当前为插件进程--------------------")

			// if (BuildConfig.DEBUG) {
			// 	// 多进程时调试使用 ，该函数会等待调试器attach（附着进程）。该函数在调试器attach后立刻返回，
			// 	Debug.waitForDebugger()
			// }

			try {
				LoggerFactory.getILoggerFactory()
			} catch (e: Exception) {
				// 插件进程也有 log 打印需要初始化
				val logLevel = if (BuildConfig.DEBUG) LogLevel.DEBUG else LogLevel.INFO
				LoggerFactory.setILoggerFactory(AndroidLoggerFactory.getInstance(logLevel) { logLevel, message, t ->

				})
			}

			//在全动态架构中，Activity组件没有打包在宿主而是位于被动态加载的 runtime，
			//为了防止插件crash后，系统自动恢复crash前的Activity组件，此时由于没有加载runtime而发生classNotFound异常，导致二次crash
			//因此这里恢复加载上一次的runtime
			DynamicRuntime.recoveryRuntime(this)

			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
				WebView.setDataDirectorySuffix("plugins")
			}
		}
	}
}