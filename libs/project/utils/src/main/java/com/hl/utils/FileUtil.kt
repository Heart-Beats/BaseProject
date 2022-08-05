package com.hl.utils

import com.blankj.utilcode.util.FileUtils
import java.io.*

/**
 * @author  张磊  on  2021/10/28 at 19:59
 * Email: 913305160@qq.com
 */
object FileUtil {

	fun copyFile(srcPath: String, desPath: String): File? {
		val outputFile = File(desPath)

		// 文件不存在时创建文件
		if (!outputFile.exists()) {
			val parentFile = outputFile.parentFile
			if (!parentFile.exists()) {
				parentFile.mkdirs()
			}
			outputFile.createNewFile()
		}

		val bos = BufferedOutputStream(FileOutputStream(File(desPath)))

		return if (copyFile(srcPath, bos)) outputFile else null
	}

	fun copyFile(srcPath: String, outputStream: OutputStream): Boolean {
		try {
			val bis = BufferedInputStream(FileInputStream(srcPath))
			val bos = BufferedOutputStream(outputStream)
			bis.use {
				it.copyTo(bos)
				bos.flush()

				return true
			}
		} catch (e: Exception) {
			return false
		}
	}
}