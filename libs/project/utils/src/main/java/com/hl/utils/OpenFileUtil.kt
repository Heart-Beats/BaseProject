package com.hl.utils

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.StrictMode
import android.os.StrictMode.VmPolicy
import android.widget.Toast
import com.blankj.utilcode.util.UriUtils
import java.io.File

/**
 * @Description: 打开文件工具类
 * @author: ZhangYW
 * @time: 2019/1/10 10:52
 */
object OpenFileUtil {

    private val MATCH_ARRAY = arrayOf(
        arrayOf(".3gp", "video/3gpp"),
        arrayOf(".apk", "application/vnd.android.package-archive"),
        arrayOf(".asf", "video/x-ms-asf"),
        arrayOf(".avi", "video/x-msvideo"),
        arrayOf(".bin", "application/octet-stream"),
        arrayOf(".bmp", "image/bmp"),
        arrayOf(".c", "text/plain"),
        arrayOf(".class", "application/octet-stream"),
        arrayOf(".conf", "text/plain"),
        arrayOf(".cpp", "text/plain"),
        arrayOf(".doc", "application/msword"),
        arrayOf(".docx", "application/msword"),
        arrayOf(".xls", "application/msword"),
        arrayOf(".xlsx", "application/msword"),
        arrayOf(".exe", "application/octet-stream"),
        arrayOf(".gif", "image/gif"),
        arrayOf(".gtar", "application/x-gtar"),
        arrayOf(".gz", "application/x-gzip"),
        arrayOf(".h", "text/plain"),
        arrayOf(".htm", "text/html"),
        arrayOf(".html", "text/html"),
        arrayOf(".jar", "application/java-archive"),
        arrayOf(".java", "text/plain"),
        arrayOf(".jpeg", "image/jpeg"),
        arrayOf(".jpg", "image/jpeg"),
        arrayOf(".js", "application/x-javascript"),
        arrayOf(".log", "text/plain"),
        arrayOf(".m3u", "audio/x-mpegurl"),
        arrayOf(".m4a", "audio/mp4a-latm"),
        arrayOf(".m4b", "audio/mp4a-latm"),
        arrayOf(".m4p", "audio/mp4a-latm"),
        arrayOf(".m4u", "video/vnd.mpegurl"),
        arrayOf(".m4v", "video/x-m4v"),
        arrayOf(".mov", "video/quicktime"),
        arrayOf(".mp2", "audio/x-mpeg"),
        arrayOf(".mp3", "audio/x-mpeg"),
        arrayOf(".mp4", "video/mp4"),
        arrayOf(".mpc", "application/vnd.mpohun.certificate"),
        arrayOf(".mpe", "video/mpeg"),
        arrayOf(".mpeg", "video/mpeg"),
        arrayOf(".mpg", "video/mpeg"),
        arrayOf(".mpg4", "video/mp4"),
        arrayOf(".mpga", "audio/mpeg"),
        arrayOf(".msg", "application/vnd.ms-outlook"),
        arrayOf(".ogg", "audio/ogg"),
        arrayOf(".pdf", "application/pdf"),
        arrayOf(".png", "image/png"),
        arrayOf(".pps", "application/vnd.ms-powerpoint"),
        arrayOf(".ppt", "application/vnd.ms-powerpoint"),
        arrayOf(".prop", "text/plain"),
        arrayOf(".rar", "application/x-rar-compressed"),
        arrayOf(".rc", "text/plain"),
        arrayOf(".rmvb", "audio/x-pn-realaudio"),
        arrayOf(".rtf", "application/rtf"),
        arrayOf(".sh", "text/plain"),
        arrayOf(".tar", "application/x-tar"),
        arrayOf(".tgz", "application/x-compressed"),
        arrayOf(".txt", "text/plain"),
        arrayOf(".wav", "audio/x-wav"),
        arrayOf(".wma", "audio/x-ms-wma"),
        arrayOf(".wmv", "audio/x-ms-wmv"),
        arrayOf(".wps", "application/vnd.ms-works"),
        arrayOf(".xml", "text/plain"),
        arrayOf(".z", "application/x-compress"),
        arrayOf(".zip", "application/zip"),
        arrayOf("", "*/*")
    )

    fun openFileShare(context: Context, path: String) {
        //文件的类型
        var type = ""
        for (i in MATCH_ARRAY.indices) {
            //判断文件的格式
            if (path.contains(MATCH_ARRAY[i][0])) {
                type = MATCH_ARRAY[i][1]
                break
            }
        }
        val shareIntent = Intent()
        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
        shareIntent.action = Intent.ACTION_SEND
        val uri = UriUtils.file2Uri(File(path))
        shareIntent.putExtra(Intent.EXTRA_STREAM, uri)
        shareIntent.type = type
        context.startActivity(shareIntent)
    }

    /**
     * 根据路径打开文件
     * @param context 上下文
     * @param path 文件路径
     */
    fun openFileByPath(context: Context?, path: String?) {
        if (context == null || path == null) {
            return
        }
        val intent = Intent()
        //设置intent的Action属性
        intent.action = Intent.ACTION_VIEW
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        intent.addCategory(Intent.CATEGORY_DEFAULT)

        //文件的类型
        var type = ""
        for (i in MATCH_ARRAY.indices) {
            //判断文件的格式
            if (path.contains(MATCH_ARRAY[i][0])) {
                type = MATCH_ARRAY[i][1]
                break
            }
        }
        try {
            val fileURI = UriUtils.file2Uri(File(path))
            //设置intent的data和Type属性
            intent.setDataAndType(fileURI, type)
            //跳转
            if (context.packageManager.resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY) != null) {
                context.startActivity(intent)
            } else {
                Toast.makeText(context, "没有找到对应的程序", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) { //当系统没有携带文件打开软件，提示
            Toast.makeText(context, "无法打开该格式文件", Toast.LENGTH_SHORT).show()
            e.printStackTrace()
        }
    }
}