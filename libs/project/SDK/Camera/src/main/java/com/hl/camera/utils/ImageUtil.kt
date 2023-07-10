package com.hl.camera.utils

import android.graphics.Bitmap
import android.util.Log
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream

/**
 * @author  张磊  on  2023/07/10 at 17:15
 * Email: 913305160@qq.com
 */
internal object ImageUtil {
	private const val TAG = "ImageUtil"

	fun save(
		src: Bitmap,
		file: File,
		format: Bitmap.CompressFormat?,
		quality: Int = 100,
		recycle: Boolean = false
	): Boolean {
		if (isEmptyBitmap(src)) {
			Log.e(TAG, "bitmap is empty.")
			return false
		}
		if (src.isRecycled) {
			Log.e(TAG, "bitmap is recycled.")
			return false
		}
		if (!FileUtil.createFileByDeleteOldFile(file)) {
			Log.e(TAG, "create or delete file <$file> failed.")
			return false
		}
		var os: OutputStream? = null
		var ret = false
		try {
			os = BufferedOutputStream(FileOutputStream(file))
			ret = src.compress(format, quality, os)
			if (recycle && !src.isRecycled) src.recycle()
		} catch (e: IOException) {
			e.printStackTrace()
		} finally {
			try {
				os?.close()
			} catch (e: IOException) {
				e.printStackTrace()
			}
		}
		return ret
	}

	private fun isEmptyBitmap(src: Bitmap?): Boolean {
		return src == null || src?.width == 0 || src.height == 0
	}
}