package com.hl.utils

import android.content.Context
import android.net.Uri
import android.os.Build
import androidx.core.content.FileProvider
import java.io.File

/**
 * @author  张磊  on  2022/01/14 at 22:33
 * Email: 913305160@qq.com
 */

fun file2Uri(context: Context, filePath: String): Uri? {
    val file = File(filePath)
    if (!file.exists()) {
        if (file.parentFile?.exists() == false) {
            file.parentFile?.mkdirs()
        }
        if (file.parentFile?.exists() == true) {
            file.createNewFile()
        }
    }
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        // 需要配置文件 fileprovider，注意配置的 authority
        FileProvider.getUriForFile(context, context.packageName + ".fileprovider", file)
    } else {
        Uri.fromFile(file)
    }
}