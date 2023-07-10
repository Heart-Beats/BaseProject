package com.hl.camera.utils

import java.io.File
import java.io.IOException

/**
 * @author  张磊  on  2023/07/10 at 17:19
 * Email: 913305160@qq.com
 */
internal object FileUtil {

	fun createFileByDeleteOldFile(file: File?): Boolean {
		if (file == null) return false
		// file exists and unsuccessfully delete then return false
		if (file.exists() && !file.delete()) return false

		return if (!createOrExistsDir(file.parentFile)) false else try {
			file.createNewFile()
		} catch (e: IOException) {
			e.printStackTrace()
			false
		}
	}

	fun createOrExistsDir(file: File?): Boolean {
		return file != null && if (file.exists()) file.isDirectory else file.mkdirs()
	}

	fun getFileByPath(filePath: String?): File? {
		return if (filePath.isNullOrBlank()) null else File(filePath)
	}

	fun delete(filePath: String?): Boolean {
		return delete(getFileByPath(filePath))
	}

	fun delete(file: File?): Boolean {
		if (file == null) return false
		return if (file.isDirectory) {
			deleteDir(file)
		} else {
			deleteFile(file)
		}
	}

	private fun deleteDir(dir: File?): Boolean {
		if (dir == null) return false
		// dir doesn't exist then return true
		if (!dir.exists()) return true
		// dir isn't a directory then return false
		if (!dir.isDirectory) return false
		val files = dir.listFiles()
		if (files != null && files.size > 0) {
			for (file in files) {
				if (file.isFile) {
					if (!file.delete()) return false
				} else if (file.isDirectory) {
					if (!deleteDir(file)) return false
				}
			}
		}
		return dir.delete()
	}

	private fun deleteFile(file: File?): Boolean {
		return file != null && (!file.exists() || file.isFile && file.delete())
	}
}