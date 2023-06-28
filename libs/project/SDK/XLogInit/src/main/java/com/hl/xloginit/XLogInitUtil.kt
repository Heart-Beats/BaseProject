package com.hl.xloginit

import androidx.core.content.edit
import androidx.core.text.isDigitsOnly
import com.elvishew.xlog.LogConfiguration
import com.elvishew.xlog.LogLevel
import com.elvishew.xlog.XLog
import com.elvishew.xlog.internal.DefaultsFactory
import com.elvishew.xlog.printer.file.FilePrinter
import com.elvishew.xlog.printer.file.backup.NeverBackupStrategy
import com.elvishew.xlog.printer.file.clean.NeverCleanStrategy
import com.hl.mmkvsharedpreferences.sharedPreferences
import java.io.File
import java.io.FileFilter
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * @author  张磊  on  2022/08/24 at 15:41
 * Email: 913305160@qq.com
 */
object XLogInitUtil {

	/**
	 * 上次写入日志文件名称的 key
	 */
	private const val LAST_WRITE_X_LOG_FILE_NAME_KEY = "LAST_WRITE_X_LOG_FILE_NAME_KEY"


	/**
	 * 初始化 XLog
	 *
	 * @param config 相关配置选项
	 */
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
	 * 初始化 XLog, 默认日志写入文件中
	 *
	 * @param tagName    日志文件最小上传大小, 单位: MB
	 * @param isPrintLog    日志文件最小上传大小, 单位: MB
	 * @param logFileMinUploadMB    日志文件最小上传大小, 单位: MB
	 * @param onUploadFiles        需要上传的日志文件集合
	 */
	fun initWithWriteFile(
		tagName: String = "X-LOG",
		isPrintLog: Boolean = true,
		logFileMinUploadMB: Long = 5L,
		onUploadFiles: (List<File>) -> Unit = {}
	) {
		val triple = getFilePrinter(logFileMinUploadMB)
		val lastLogFileName = triple.first
		val parentFile = triple.second
		val filePrinter = triple.third

		// 初始化 X-Log
		init {
			this.tagName = tagName
			this.isPrintLog = isPrintLog
			this.filePrinter = filePrinter
		}

		val uploadFiles = parentFile.listFiles(FileFilter {
			it.name != lastLogFileName
		})?.toList()

		onUploadFiles(uploadFiles ?: listOf())
	}

	private fun getFilePrinter(logFileMinUploadMB: Long): Triple<String, File, FilePrinter> {
		val format = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
		val nowDate = format.format(Date())
		val defaultLogFileName = "${nowDate}_log.txt"

		val sharedPreferences = sharedPreferences()
		var lastLogFileName = sharedPreferences.getString(LAST_WRITE_X_LOG_FILE_NAME_KEY, defaultLogFileName)
			?: defaultLogFileName

		// 上次写入日志文件的日期非当前日期，使用当前日期写入日志文件
		if (!lastLogFileName.startsWith(nowDate)) {
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
	/**
	 * 日志 Tag
	 */
	var tagName: String = "X-LOG",

	/**
	 * 是否打印日志
	 */
	var isPrintLog: Boolean = true,

	/**
	 * 日志文件打印
	 */
	var filePrinter: FilePrinter? = null,
)