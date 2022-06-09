package com.hl.utils

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import androidx.collection.arraySetOf
import com.blankj.utilcode.util.UriUtils
import java.io.File
import java.io.OutputStream
import java.util.*


/**
 * @author  张磊  on  2022/04/09 at 17:32
 * Email: 913305160@qq.com
 */
object ScanFileActionUtil {

    private const val TAG = "ScanFileActionUtil"

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
        duration: Long = 0
    ) {
        if (!checkFile(mediaFilePath)) return

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
        if (!checkFile(filePath)) return
        notifyScanDcimByUri(context, UriUtils.file2Uri(File(filePath)))
    }


    /**
     *  通知刷新文件到相册
     */
    private fun notifyScanDcimByUri(context: Context, fileUri: Uri) {
        val intent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, fileUri).apply {
            this.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        context.sendBroadcast(intent)
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
        val mediaUri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
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

            val contentResolver = context.applicationContext.contentResolver
            val uri = contentResolver.insert(externalContentUri, values)

            if (uri == null) {
                Log.e(TAG, "插入${mediaFilePath}到图库失败.")
                return
            }

            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.Q) {
                // 拷贝到指定 uri, 如果没有这步操作，android11不会在相册显示
                try {
                    val out: OutputStream = contentResolver.openOutputStream(uri) ?: return
                    FileUtil.copyFile(mediaFilePath, out)
                } catch (e: Exception) {
                    Log.e(TAG, "拷贝${mediaFilePath}到图库失败.")
                }
            }

            uri
        } else {
            UriUtils.file2Uri(File(mediaFilePath))
        }

        notifyScanDcimByUri(context, mediaUri)
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


enum class MimeType(val mMimeTypeName: String, private val mExtensions: Set<String>) {


    JPEG("image/jpeg", arraySetOf("jpg", "jpeg")),

    PNG("image/png", arraySetOf("png")),

    GIF("image/gif", arraySetOf("gif")),

    BMP("image/x-ms-bmp", arraySetOf("bmp")),

    WEBP("image/webp", arraySetOf("webp")),

    // ============== videos ==============
    MPEG("video/mpeg", arraySetOf("mpeg", "mpg")),

    MP4("video/mp4", arraySetOf("mp4", "m4v")),

    QUICKTIME("video/quicktime", arraySetOf("mov")),

    THREE_GPP("video/3gpp", arraySetOf("3gp", "3gpp")),

    THREE_GPP2("video/3gpp2", arraySetOf("3g2", "3gpp2")),

    MKV("video/x-matroska", arraySetOf("mkv")),

    WEBM("video/webm", arraySetOf("webm")),

    TS("video/mp2ts", arraySetOf("ts")),

    AVI("video/avi", arraySetOf("avi"));

    override fun toString(): String {
        return mMimeTypeName
    }

    companion object {

        fun ofAll(): Set<MimeType> {
            return EnumSet.allOf(MimeType::class.java)
        }

        fun of(type: MimeType?, vararg rest: MimeType?): Set<MimeType> {
            return EnumSet.of(type, *rest)
        }

        fun ofImage(): Set<MimeType> {
            return EnumSet.of(JPEG, PNG, GIF, BMP, WEBP)
        }

        fun ofImage(onlyGif: Boolean): Set<MimeType> {
            return EnumSet.of(GIF)
        }

        fun ofGif(): Set<MimeType> {
            return ofImage(true)
        }

        fun ofVideo(): Set<MimeType> {
            return EnumSet.of(MPEG, MP4, QUICKTIME, THREE_GPP, THREE_GPP2, MKV, WEBM, TS, AVI)
        }

        fun isImage(mimeType: String?): Boolean {
            return mimeType?.startsWith("image") ?: false
        }

        fun isVideo(mimeType: String?): Boolean {
            return mimeType?.startsWith("video") ?: false
        }

        fun isGif(mimeType: String?): Boolean {
            return if (mimeType == null) false else mimeType == GIF.toString()
        }


        /**
         * 通过文件扩展名获取 MimeType
         */
        fun getByExtension(extension: String): MimeType? {
            for (value in values()) {
                if (value.mExtensions.contains(extension)) {
                    return value
                }
            }

            return null
        }
    }
}