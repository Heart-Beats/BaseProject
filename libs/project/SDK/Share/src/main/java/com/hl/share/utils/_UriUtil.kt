package com.hl.share.utils

import android.content.Context
import android.net.Uri
import android.os.Build
import androidx.core.content.FileProvider
import java.io.File

/**
 * @author  张磊  on  2022/01/14 at 22:33
 * Email: 913305160@qq.com
 */

internal fun Context.file2Uri(filePath: String): Uri {
	val file = File(filePath)
	if (!file.exists()) {
		val parentFile = file.parentFile
		assert(parentFile != null)
		if (!parentFile.exists()) {
			parentFile.mkdirs()
		} else {
			file.createNewFile()
		}
	}
	return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
		// 需要配置文件 fileprovider，注意配置的 authority
		val app = this.applicationContext
		FileProvider.getUriForFile(app, app.packageName + ".fileprovider", file)
	} else {
		Uri.fromFile(file)
	}
}

internal fun Context.file2Uri(file: File) = this.file2Uri(file.absolutePath)