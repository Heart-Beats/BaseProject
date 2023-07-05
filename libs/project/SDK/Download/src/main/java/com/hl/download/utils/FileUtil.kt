package com.hl.download.utils

import java.io.File
import java.io.IOException

/**
 * @author  张磊  on  2023/07/05 at 11:18
 * Email: 913305160@qq.com
 */
internal object FileUtil {

	/**
	 * 创建一个文件， 如果文件存在，则什么也不做
	 *
	 * @param [file]      需要创建的文件
	 * @return [Boolean]  创建成功或者文件已存在
	 */
	fun createOrExistsFile(file: File?): Boolean {
		if (file == null) return false

		if (file.exists()) return file.isFile

		if (!createOrExistsDir(file.parentFile)) return false

		return try {
			file.createNewFile()
		} catch (e: IOException) {
			e.printStackTrace()
			false
		}
	}

	private fun createOrExistsDir(file: File?): Boolean {
		return file != null && if (file.exists()) file.isDirectory else file.mkdirs()
	}
}