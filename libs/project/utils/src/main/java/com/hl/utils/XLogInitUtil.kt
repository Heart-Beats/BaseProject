package com.hl.utils

import androidx.core.content.edit
import androidx.core.text.isDigitsOnly
import com.elvishew.xlog.LogConfiguration
import com.elvishew.xlog.LogLevel
import com.elvishew.xlog.XLog
import com.elvishew.xlog.internal.DefaultsFactory
import com.elvishew.xlog.printer.file.FilePrinter
import com.elvishew.xlog.printer.file.backup.NeverBackupStrategy
import com.elvishew.xlog.printer.file.clean.NeverCleanStrategy
import com.hl.utils.date.toFormatString
import java.io.File
import java.io.FileFilter
import java.util.*

/**
 * @author  张磊  on  2022/08/24 at 15:41
 * Email: 913305160@qq.com
 */
object XLogInitUtil {

	/**
	 * 上次写入日志文件名称的 key
	 */
	private const val LAST_WRITE_X_LOG_FILE_NAME_KEY = "LAST_WRITE_X_LOG_FILE_NAME_KEY"


	fun init(config: XLogInitConfig.() -> Unit = {}) {
		val initConfig = XLogInitConfig().apply(config)

		val logConfig = LogConfiguration.Builder()
			.tag(initConfig.tagName)
			.logLevel(if (initConfig.isPrintLog) LogLevel.ALL else LogLevel.INFO)
			.enableStackTrace(1)
			.addInterceptor {
				// 添加日志拦截器
				it
			}
			.build()

		val printers = arrayOf(DefaultsFactory.createPrinter(), initConfig.filePrinter).filterNotNull()
		XLog.init(logConfig, *printers.toTypedArray())
	}


	/**
	 * @param isWrite2File          是否需要写入日志到文件
	 * @param logFileMinUploadMB    日志文件最小上传大小, 单位: MB
	 * @param onUploadFiles        需要上传的日志文件集合
	 */
	fun initWithWriteFile(
		isWrite2File: Boolean = true,
		logFileMinUploadMB: Long = 5L,
		onUploadFiles: (List<File>) -> Unit = {}
	) {
		val triple = getFilePrinter(logFileMinUploadMB)
		var lastLogFileName = triple.first
		val parentFile = triple.second
		val filePrinter = triple.third

		// 初始化 X-Log
		init {
			this.filePrinter = if (isWrite2File) filePrinter else null
		}

		// 仅有日志写入文件时，才通知需要上传的文件
		if (isWrite2File) {
			val uploadFiles = parentFile.listFiles(FileFilter {
				it.name != lastLogFileName
			})?.toList()

			onUploadFiles(uploadFiles ?: listOf())
		}
	}

	private fun getFilePrinter(logFileMinUploadMB: Long): Triple<String, File, FilePrinter> {
		val nowDate = Date().toFormatString("yyyy-MM-dd")
		val defaultLogFileName = "${nowDate}_log.txt"

		val sharedPreferences = BaseUtil.app.sharedPreferences()
		var lastLogFileName = sharedPreferences.getString(LAST_WRITE_X_LOG_FILE_NAME_KEY, defaultLogFileName)
			?: defaultLogFileName

		// 上次写入日志文件的日期非当前日期，使用当前日期写入日志文件
		if (lastLogFileName.startsWith(nowDate)) {
			lastLogFileName = defaultLogFileName
		}


		val logFolderPath = XLogUtil.defaultLogFolderPath
		val lastLogFile = File(logFolderPath, lastLogFileName)


		// 上次日志文件存在且大于最小上传大小
		if (lastLogFile.exists() && lastLogFile.length() >= logFileMinUploadMB * 1024 * 1024) {
			// 获取日志文件序号
			var serialNumber = lastLogFileName.substringAfterLast("_").first().toString()
			if (!serialNumber.isDigitsOnly()) {
				serialNumber = "0"
			}

			//创建新的当前日志文件
			lastLogFileName = "${nowDate}_log_${serialNumber.toInt() + 1}.txt"
		}

		val parentFile = File(logFolderPath)

		// 日志文件写入策略， 不备份不清理
		val filePrinter = XLogUtil.getFilePrinterByName(
			fileName = lastLogFileName,
			backupStrategy = NeverBackupStrategy(),
			cleanStrategy = NeverCleanStrategy()
		)

		sharedPreferences.edit {
			this.putString(LAST_WRITE_X_LOG_FILE_NAME_KEY, lastLogFileName)
		}
		return Triple(lastLogFileName, parentFile, filePrinter)
	}
}


data class XLogInitConfig(
	var tagName: String = "X-LOG",
	var isPrintLog: Boolean = true,
	var filePrinter: FilePrinter? = null,
)