package com.hl.utils

import android.os.Build
import android.os.Process
import com.blankj.utilcode.util.AppUtils
import com.blankj.utilcode.util.DeviceUtils
import com.blankj.utilcode.util.FileUtils
import com.blankj.utilcode.util.ProcessUtils
import com.blankj.utilcode.util.Utils
import java.io.File
import java.io.PrintWriter
import java.io.StringWriter
import java.lang.Thread.UncaughtExceptionHandler
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * @author  张磊  on  2024/03/20 at 10:07
 * Email: 913305160@qq.com
 */
object MyCrashHandler : UncaughtExceptionHandler {

	private val defaultHandler = Thread.getDefaultUncaughtExceptionHandler()

	private val startTime = Date()

	private const val timeFormatterStr = "yyyy-MM-dd'T'HH:mm:ss.SSSZ"

	private var onPrintExceptionHandler: ((stacktrace: String) -> Unit)? = null

	init {
		Thread.setDefaultUncaughtExceptionHandler(this)
	}

	fun setOnPrintException(onPrintExceptionHandler: (stacktrace: String) -> Unit) {
		this.onPrintExceptionHandler = onPrintExceptionHandler
	}


	override fun uncaughtException(thread: Thread, throwable: Throwable) {
		val emergency = getEmergency(Date(), thread, throwable)

		onPrintExceptionHandler?.invoke(emergency)

		defaultHandler?.uncaughtException(thread, throwable)
	}


	private fun getEmergency(crashTime: Date, thread: Thread, throwable: Throwable): String {

		// stack stace
		val sw = StringWriter()
		val pw = PrintWriter(sw)
		throwable.printStackTrace(pw)
		val stacktrace = sw.toString()

		val appId = AppUtils.getAppPackageName()
		val appVersion = AppUtils.getAppVersionName()
		val processName = ProcessUtils.getCurrentProcessName()

		return buildString {
			append(getLogHeader(startTime, crashTime, appId, appVersion))
			append("pid: ${Process.myPid()}, tid: ${Process.myTid()}, name: ${thread.name}  >>> $processName <<<\n")
			append("\n")
			append("java stacktrace:\n")
			append(stacktrace)
			append("\n")
			append(getBuildId(stacktrace))
		}
	}


	private fun getLogHeader(startTime: Date, crashTime: Date, appId: String, appVersion: String): String {
		val timeFormatter: DateFormat = SimpleDateFormat(timeFormatterStr, Locale.getDefault())

		return buildString {
			append("*** *** *** *** *** *** *** *** *** *** *** *** *** *** *** ***\n")
			append("Crash type: 'java'\n")
			append("Start time: '${timeFormatter.format(startTime)}'\n")
			append("Crash time: '${timeFormatter.format(crashTime)}'\n")
			append("App ID: '$appId'\n")
			append("App version: '$appVersion'\n")
			append("Rooted: '${if (AppUtils.isAppRoot()) "Yes" else "No"}'\n")
			append("API level: '${Build.VERSION.SDK_INT}'\n")
			append("OS version: '${Build.VERSION.RELEASE}'\n")
			append("ABI list: '${DeviceUtils.getABIs().contentToString()}'\n")
			append("Manufacturer: '${Build.MANUFACTURER}'\n")
			append("Brand: '${Build.BRAND}'\n")
			append("Model: '${DeviceUtils.getModel()}'\n")
			append("Build fingerprint: '${Build.FINGERPRINT}'\n")
		}
	}

	private fun getBuildId(stacktrace: String): String {
		var buildId = ""
		val libPathList: MutableList<String> = ArrayList()
		if (stacktrace.contains("UnsatisfiedLinkError")) {
			var libInfo: String? = null
			val tempLibPathStr =
				stacktrace.split("\"".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray() // " is the delimiter
			for (libPathStr in tempLibPathStr) {
				if (libPathStr.isEmpty() || !libPathStr.endsWith(".so")) continue
				libPathList.add(libPathStr)
				val libName = libPathStr.substring(libPathStr.lastIndexOf('/') + 1)
				libPathList.add(Utils.getApp().applicationInfo.nativeLibraryDir + "/" + libName)
				libPathList.add("/vendor/lib/$libName")
				libPathList.add("/vendor/lib64/$libName")
				libPathList.add("/system/lib/$libName")
				libPathList.add("/system/lib64/$libName")
				libInfo = getLibInfo(libPathList)
			}
			buildId = """
	    	build id:
	    	$libInfo
	    	
	    	""".trimIndent()
		}
		return buildId
	}

	private fun getLibInfo(libPathList: List<String>): String {
		val sb = StringBuilder()
		for (libPath in libPathList) {
			val libFile = File(libPath)
			if (libFile.exists() && libFile.isFile) {
				val md5 = FileUtils.getFileMD5(libFile)
				val timeFormatter: DateFormat = SimpleDateFormat(timeFormatterStr, Locale.getDefault())
				val lastTime = Date(libFile.lastModified())
				sb.append("    ").append(libPath).append("(BuildId: unknown. FileSize: ").append(libFile.length())
					.append(". LastModified: ")
					.append(timeFormatter.format(lastTime)).append(". MD5: ").append(md5).append(")\n")
			} else {
				sb.append("    ").append(libPath).append(" (Not found)\n")
			}
		}
		return sb.toString()
	}
}