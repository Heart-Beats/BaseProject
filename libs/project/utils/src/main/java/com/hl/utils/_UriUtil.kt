package com.hl.utils

import android.net.Uri
import android.os.Build
import androidx.core.content.FileProvider
import com.blankj.utilcode.util.UriUtils
import java.io.File

/**
 * @author  张磊  on  2022/01/14 at 22:33
 * Email: 913305160@qq.com
 */

fun file2Uri(filePath: String): Uri {
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
        val app = BaseUtil.app
        FileProvider.getUriForFile(app, app.packageName + ".fileprovider", file)
    } else {
        Uri.fromFile(file)
    }
}

fun uri2File(fileUri: Uri): File? {
    return UriUtils.uri2File(fileUri)
}

fun res2Uri(resPath: String): Uri? {
    return UriUtils.res2Uri(resPath)
}

fun uri2Bytes(fileUri: Uri): ByteArray? {
    return UriUtils.uri2Bytes(fileUri)
}