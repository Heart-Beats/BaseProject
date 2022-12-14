package com.hl.utils.share

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import com.blankj.utilcode.util.UriUtils
import com.hl.uikit.toast
import com.hl.utils.mimetype.MimeType
import java.io.File

/**
 * @Description: 打开文件工具类
 */
object OpenFileUtil {

    /**
     * 分享文件通过其他应用打开, 使用 Android intent 解析器
     */
    fun openFileShare(context: Context, path: String) {
        val shareFile = File(path)

        // 获取分享文件的类型
        val mimeType = MimeType.getByExtension(shareFile.extension)?.mMimeTypeName ?: "*/*"

        val shareIntent = Intent().apply {
            this.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
            this.action = Intent.ACTION_SEND
            this.putExtra(Intent.EXTRA_STREAM, UriUtils.file2Uri(shareFile))
            this.type = mimeType
        }

        context.startActivity(shareIntent)
    }

    /**
     * 根据路径打开文件
     * @param context 上下文
     * @param path 文件路径
     */
    fun openFileByPath(context: Context, path: String) {
        try {
            val shareFile = File(path)
            // 获取分享文件的类型
            val mimeType = MimeType.getByExtension(shareFile.extension)?.mMimeTypeName ?: "*/*"

            val intent = Intent().apply {
                //设置intent的Action属性
                this.action = Intent.ACTION_VIEW
                this.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                this.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                this.addCategory(Intent.CATEGORY_DEFAULT)

                //设置intent的data和Type属性
                this.setDataAndType(UriUtils.file2Uri(shareFile), mimeType)
            }

            //跳转
            if (context.packageManager.resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY) != null) {
                context.startActivity(intent)
            } else {
                context.toast("没有找到对应的程序")
            }
        } catch (e: Exception) { //当系统没有携带文件打开软件，提示
            context.toast("无法打开该格式文件")
            e.printStackTrace()
        }
    }
}