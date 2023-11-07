package com.hl.utils

import android.widget.TextView
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.elvishew.xlog.XLog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

/**
 * @author  张磊  on  2023/10/30 at 16:42
 * Email: 913305160@qq.com
 */


/**
 * 读取文件的每一行到 TextView 中
 */
fun LifecycleOwner.readFileEachLineToTextView(file: File, textView: TextView) {
	// 是否取消读取
	var isReadStop = false

	this.lifecycle.addObserver(object : DefaultLifecycleObserver {
		override fun onStart(owner: LifecycleOwner) {
			isReadStop = false
		}

		override fun onStop(owner: LifecycleOwner) {
			isReadStop = true
		}
	})

	this.lifecycleScope.launch(Dispatchers.IO) {
		var readLength = 0
		try {
			file.bufferedReader().use { br ->
				val charArray = CharArray(8 * 1024)
				do {
					while (!isReadStop && br.read(charArray).also { readLength = it } > 0) {
						withContext(Dispatchers.Main) {
							val readString = charArray.take(readLength).joinToString(separator = "")
							textView.append(readString)
						}
					}
				} while (readLength != -1)
			}
		} catch (e: java.lang.Exception) {
			XLog.e("读取日志文件出错")
		}
	}
}