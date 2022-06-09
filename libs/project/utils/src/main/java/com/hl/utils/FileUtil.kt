package com.hl.utils

import java.io.*

/**
 * @author  张磊  on  2021/10/28 at 19:59
 * Email: 913305160@qq.com
 */
object FileUtil {

	fun copyFile(srcPath: String, desPath: String): File? {
		val outputFile = File(desPath)
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