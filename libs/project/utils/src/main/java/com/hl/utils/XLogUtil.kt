package com.hl.utils

import com.elvishew.xlog.flattener.ClassicFlattener
import com.elvishew.xlog.printer.file.FilePrinter
import com.elvishew.xlog.printer.file.backup.BackupStrategy
import com.elvishew.xlog.printer.file.backup.FileSizeBackupStrategy2
import com.elvishew.xlog.printer.file.clean.CleanStrategy
import com.elvishew.xlog.printer.file.clean.FileLastModifiedCleanStrategy
import com.elvishew.xlog.printer.file.naming.DateFileNameGenerator
import com.elvishew.xlog.printer.file.naming.FileNameGenerator
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

/**
 * @author  张磊  on  2022/08/19 at 14:00
 * Email: 913305160@qq.com
 */
object XLogUtil {

	/**
	 * 日志文件最大的大小，默认 10M , 超过即触发备份
	 */
	private const val logFileMaxSize = 10L * 1024 * 1024

	/**
	 * 日志文件最大备份个数
	 */
	private const val logFileMaxBackCount = 5

	/**
	 * 日志文件最大保留时间期限， 默认一周（7天）
	 */
	private const val logFileMaxSaveTime = 7L * 1000 * 60 * 60 * 24

	/**
	 * 根据时间戳生成文件名的文件名称生成器
	 */
	private val defaultFileNameGenerator = MyDateFileNameGenerator()

	private val defaultBackupStrategy = FileSizeBackupStrategy2(logFileMaxSize, logFileMaxBackCount)

	private val defaultCleanStrategy = FileLastModifiedCleanStrategy(logFileMaxSaveTime)


	/**
	 *
	 */
	fun getFilePrinter(
		logFolderPath: String? = null,
		fileNameGenerator: FileNameGenerator = defaultFileNameGenerator,
		backupStrategy: BackupStrategy = defaultBackupStrategy,
		cleanStrategy: CleanStrategy = defaultCleanStrategy
	): FilePrinter {

		val xLogFolderPath = if (logFolderPath == null) {
			val externalFilesDir = BaseUtil.app.getExternalFilesDir(null)
			File(externalFilesDir, "XLog").absolutePath
		} else {
			logFolderPath
		}

		return FilePrinter.Builder(xLogFolderPath) // 指定保存日志文件的路径
			.fileNameGenerator(fileNameGenerator) // 指定日志文件名生成器，默认为 ChangelessFileNameGenerator("log")
			.backupStrategy(backupStrategy) // 指定日志文件备份策略，默认为 FileSizeBackupStrategy(1024 * 1024)
			.cleanStrategy(cleanStrategy) // 指定日志文件清除策略，默认为 NeverCleanStrategy()
			.flattener(ClassicFlattener()) // 指定日志平铺器，默认为 DefaultFlattener
			.build()
	}

	private class MyDateFileNameGenerator : DateFileNameGenerator() {

		var mLocalDateFormat = object : ThreadLocal<SimpleDateFormat>() {
			override fun initialValue(): SimpleDateFormat {
				return SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
			}
		}

		override fun generateFileName(logLevel: Int, timestamp: Long): String {
			val sdf = mLocalDateFormat.get()!!
			return "${sdf.format(Date(timestamp))}.txt"
		}
	}
}