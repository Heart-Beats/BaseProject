package com.hl.utils

import android.Manifest
import android.content.ContentResolver
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.fragment.app.FragmentActivity
import com.blankj.utilcode.util.UriUtils
import com.hl.permission.reqPermissions
import com.hl.utils.mimetype.MimeType
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import java.io.File
import java.util.*


/**
 * @author  张磊  on  2022/04/09 at 17:32
 * Email: 913305160@qq.com
 */
interface ScanResultCallBack {
    /**
     * 扫描插入图库成功
     *
     * @param scanFile 插入到图库的文件
     */
    fun onScanSuccess(scanFile: File)

    /**
     * 扫描插入图库失败
     *
     * @param  errorMsg 失败原因
     */
    fun onScanFail(errorMsg: String)

    /**
     * 扫描插入图库中的相关提示信息
     *
     * @param  msg 提示信息
     */
    fun onScanInfo(msg: String) {}
}

object ScanFileActionUtil {

    private var scanResultCallBack: ScanResultCallBack? = null

    /**
     *  部分机型保存图片至私有目录会失败， 因此这里将相应文件复制到公共目录再去扫描
     */
    @JvmStatic
    fun scanMedia2Public(
        context: Context,
        vararg mediaFilePathS: String,
        scanResultCallBack: ScanResultCallBack? = null
    ) {
        this.scanResultCallBack = scanResultCallBack

        if (context is FragmentActivity) {
            context.reqPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE, deniedAction = {
                scanResultCallBack?.onScanFail("拒绝权限，插入到图库失败")
            }) {

                mediaFilePathS.forEach {
                    val (srcFile, copyOutputFile, copyFile) = copyMediaFile2PublicDirectory(it)
                    if (copyFile == null) {
                        scanResultCallBack?.onScanFail("拷贝${srcFile.absolutePath} 到 ${copyOutputFile.absolutePath} 失败")
                    } else {
                        scanResultCallBack?.onScanInfo("拷贝${srcFile.absolutePath} 到 ${copyOutputFile.absolutePath} 成功")
                        scanMediaToGallery(context, copyFile.absolutePath)
                    }
                }
            }
        } else {
            scanResultCallBack?.onScanFail("context 非 FragmentActivity 类型")
        }
    }

    /**
     * 拷贝媒体文件到公共目录下
     */
    private fun copyMediaFile2PublicDirectory(mediaFilePath: String): Triple<File, File, File?> {
        val mimeTypeName = MimeType.getByExtension(File(mediaFilePath).extension)?.mMimeTypeName ?: ""

        val publicDirectory = when {
            mimeTypeName.matches(Regex("image/.*")) -> Environment.DIRECTORY_PICTURES
            mimeTypeName.matches(Regex("video/.*")) -> Environment.DIRECTORY_MOVIES
            else -> Environment.DIRECTORY_DOWNLOADS
        }

        val srcFile = File(mediaFilePath)
        val externalStoragePublicDirectory = Environment.getExternalStoragePublicDirectory(publicDirectory)

        var copyOutputFile: File
        var copyFile: File?
        if (srcFile.startsWith(externalStoragePublicDirectory)) {
            // 当前媒体文件在公共目录下无需拷贝操作
            copyOutputFile = srcFile
            copyFile = srcFile
        } else {
            copyOutputFile = File(externalStoragePublicDirectory, srcFile.name)
            copyFile = FileUtil.copyFile(mediaFilePath, copyOutputFile.absolutePath)
        }

        return Triple(srcFile, copyOutputFile, copyFile)
    }

    /**
     *   使用 MediaScannerConnection 把文件插入到系统图库
     *   @param  filePaths           文件路径数组, 不可为应用私有目录路径，否则会添加图库失败
     */
    private fun scanMediaToGallery(context: Context, vararg filePaths: String) {
        MediaScannerConnection.scanFile(context, filePaths, null) { path, uri ->
            MainScope().launch {
                if (uri != null) {
                    scanResultCallBack?.onScanSuccess(File(path))
                } else {
                    scanResultCallBack?.onScanFail("扫描插入图库失败：文件路径 == $path")
                }
            }
        }
    }


    /**
     * 扫描媒体文件到相册
     *
     * 注： vivo 手机和魅族手机部分机型只支持在文件管理中查看视频, 用微信保存视频试了一下同样不能在相册中将视频显示出来
     *
     *
     * @param context     context
     * @param mediaFilePath  文件路径
     * @param createTime 创建时间 <=0时为当前时间 ms
     * @param width      宽度
     * @param height     高度
     * @param duration   视频长度 ms, 视频时可以选择传入
     */
    fun scanMedia(
        context: Context,
        mediaFilePath: String,
        createTime: Long = System.currentTimeMillis(),
        width: Int = 0,
        height: Int = 0,
        duration: Long = 0,
        scanResultCallBack: ScanResultCallBack? = null
    ) {
        this.scanResultCallBack = scanResultCallBack

        if (!checkFile(mediaFilePath)) {
            scanResultCallBack?.onScanFail("$mediaFilePath 不存在")
            return
        }

        if (isSystemDcim(mediaFilePath)) {
            notifyScanDcimByPath(context, mediaFilePath)
        } else {
            scanFile2MediaStore(context, mediaFilePath, createTime, width, height, duration)
        }
    }


    ///////////////////////////////////////////////////////////////////////////
    // 扫描系统相册核心方法
    ///////////////////////////////////////////////////////////////////////////
    /**
     * 针对系统文夹只需要扫描,不用插入内容提供者,不然会重复
     *
     * 通知刷新文件到相册
     *
     * @param context  上下文
     * @param filePath 文件路径
     */
    private fun notifyScanDcimByPath(context: Context, filePath: String) {
        if (!checkFile(filePath)) {
            scanResultCallBack?.onScanFail("$filePath 不存在")
            return
        }
        notifyScanDcimByUri(context, UriUtils.file2Uri(File(filePath)))
    }


    /**
     *  通知刷新文件到相册
     */
    private fun notifyScanDcimByUri(context: Context, fileUri: Uri) {
        val intent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, fileUri).apply {
            this.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        scanResultCallBack?.onScanInfo("开始通知图库刷新 $fileUri")
        context.sendBroadcast(intent)
        scanResultCallBack?.onScanSuccess(UriUtils.uri2File(fileUri))
    }


    // 检测文件存在
    private fun checkFile(filePath: String): Boolean {
        return File(filePath).exists()
    }

    // 是不是系统相册
    private fun isSystemDcim(path: String): Boolean {
        return path.lowercase(Locale.getDefault()).contains("dcim")
                || path.lowercase(Locale.getDefault()).contains("camera")
    }


    ///////////////////////////////////////////////////////////////////////////
    // 非系统相册像MediaContent中插入数据，核心方法
    ///////////////////////////////////////////////////////////////////////////

    /**
     * 扫描非系统文件夹下的资源文件到媒体库
     *
     * 注： vivo 手机和魅族手机部分机型只支持在文件管理中查看视频, 用微信保存视频试了一下同样不能在相册中将视频显示出来
     *
     *
     * @param context     context
     * @param mediaFilePath  文件路径
     * @param createTime 创建时间 <=0时为当前时间 ms
     * @param width      宽度
     * @param height     高度
     * @param duration   视频长度 ms, 视频时可以选择传入
     */
    private fun scanFile2MediaStore(
        context: Context,
        mediaFilePath: String,
        createTime: Long = System.currentTimeMillis(),
        width: Int = 0,
        height: Int = 0,
        duration: Long = 0
    ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val values = initCommonContentValues(File(mediaFilePath), createTime)
            val mimeTypeName = values.getAsString(MediaStore.MediaColumns.MIME_TYPE)

            val externalContentUri = when {
                mimeTypeName?.matches(Regex("image/.*")) == true -> {
                    insertImageValue(values, createTime, width, height)
                    // 图片
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                }
                mimeTypeName?.matches(Regex("video/.*")) == true -> {
                    insertVideoValue(values, createTime, width, height, duration)
                    // 视频
                    MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                }
                else -> MediaStore.Files.getContentUri("external")
            }

            scanResultCallBack?.onScanInfo("开始向 ($externalContentUri) 中插入 ($values)")

            val contentResolver = context.applicationContext.contentResolver
            val uri = contentResolver.insert(externalContentUri, values)

            if (uri == null) {
                scanResultCallBack?.onScanInfo("插入${mediaFilePath}到图库失败, 开始复制文件到公共目录再插入...")

                scanMedia2Public(context, mediaFilePath)
            } else {
                notifyOverstepQ(context, contentResolver, uri, mediaFilePath)
            }
        } else {
            val mediaUri = UriUtils.file2Uri(File(mediaFilePath))
            notifyScanDcimByUri(context, mediaUri)
        }
    }

    private fun notifyOverstepQ(
        context: Context,
        contentResolver: ContentResolver,
        uri: Uri,
        mediaFilePath: String
    ) {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.Q) {
            // 拷贝到指定 uri, 如果没有这步操作，android11以上无法在相册显示
            try {
                contentResolver.openOutputStream(uri)?.also {
                    FileUtil.copyFile(mediaFilePath, it)
                } ?: scanResultCallBack?.onScanInfo("notifyOverstepQ  调用 openOutputStream($uri) 返回 null")
            } catch (e: Exception) {
                scanResultCallBack?.onScanInfo("拷贝${mediaFilePath}到${uri} 异常.")
            }
        }

        notifyScanDcimByUri(context, uri)
    }

    /**
     * 插入时初始化公共字段
     *
     * @param mediaFile 文件
     * @param createTime   创建时间  ms
     * @return ContentValues
     */
    private fun initCommonContentValues(mediaFile: File, createTime: Long): ContentValues {
        val mMimeTypeName = MimeType.getByExtension(mediaFile.extension)?.mMimeTypeName ?: ""

        val values = ContentValues()
        val timeMillis: Long = getTimeWrap(createTime)
        values.put(MediaStore.MediaColumns.TITLE, mediaFile.name)
        values.put(MediaStore.MediaColumns.DISPLAY_NAME, mediaFile.name)
        values.put(MediaStore.MediaColumns.DATE_MODIFIED, timeMillis)
        values.put(MediaStore.MediaColumns.DATE_ADDED, timeMillis)
        values.put(MediaStore.MediaColumns.DATA, mediaFile.absolutePath)
        values.put(MediaStore.MediaColumns.SIZE, mediaFile.length())

        values.put(MediaStore.MediaColumns.MIME_TYPE, mMimeTypeName)
        return values
    }


    /**
     * 向 MediaStore.Images 插入指定的值
     *
     * @param values   ContentValues
     * @param createTime 创建时间 <=0时为当前时间 ms
     * @param width      宽度
     * @param height     高度
     */
    private fun insertImageValue(values: ContentValues, createTime: Long, width: Int, height: Int) {
        values.put(MediaStore.Images.ImageColumns.DATE_TAKEN, createTime)
        values.put(MediaStore.Images.ImageColumns.ORIENTATION, 0)
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN) {
            if (width > 0) values.put(MediaStore.Images.ImageColumns.WIDTH, 0)
            if (height > 0) values.put(MediaStore.Images.ImageColumns.HEIGHT, 0)
        }
    }


    /**
     * 向 MediaStore.Video 插入指定的值
     *
     * @param values   ContentValues
     * @param createTime 创建时间 <=0时为当前时间 ms
     * @param width      宽度
     * @param height     高度
     * @param duration   视频长度 ms
     */
    private fun insertVideoValue(values: ContentValues, createTime: Long, width: Int, height: Int, duration: Long) {
        values.put(MediaStore.Video.VideoColumns.DATE_TAKEN, createTime)
        if (duration > 0) {
            values.put(MediaStore.Video.VideoColumns.DURATION, duration)
        }
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN) {
            if (width > 0) values.put(MediaStore.Video.VideoColumns.WIDTH, width)
            if (height > 0) values.put(MediaStore.Video.VideoColumns.HEIGHT, height)
        }
    }

    /**
     * 获得转化后的时间
     */
    private fun getTimeWrap(time: Long): Long {
        return if (time <= 0) {
            System.currentTimeMillis()
        } else time
    }
}