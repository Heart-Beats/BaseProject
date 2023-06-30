package com.hl.utils.share

import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import com.blankj.utilcode.util.UriUtils
import com.hl.mimetype.MimeType
import com.hl.uikit.toast
import com.hl.utils.isSingle
import com.hl.utils.toArrayList
import java.io.File

/**
 * @author  张磊  on  2022/12/14 at 23:02
 * Email: 913305160@qq.com
 *
 * 使用 Android ShareSheet 来进行分享应用数据
 */
object ShareUtil {

    /**
     * 分享文本
     */
    fun shareText(context: Context, text: String) {
        val sendIntent = Intent().apply {
            this.action = Intent.ACTION_SEND
            this.putExtra(Intent.EXTRA_TEXT, text)
            this.type = "text/plain"
        }

        val shareIntent = Intent.createChooser(sendIntent, "")
        context.startActivity(shareIntent)
    }

    /**
     * 分享 文本 + 文件, 如文档、媒体文件等
     */
    fun shareFileWithText(context: Context, text: String, vararg filePaths: String) {
        if (filePaths.isEmpty()) {
            context.toast("分享的文件不可为空")
            return
        }

        val shareIntent = getShareIntent(text, filePaths = filePaths)
        context.startActivity(Intent.createChooser(shareIntent, ""))
    }

    /**
     * 分享文件, 如文档、媒体文件等
     */
    fun shareFile(context: Context, vararg filePaths: String) {
        if (filePaths.isEmpty()) {
            context.toast("分享的文件不可为空")
        }

        val shareIntent = getShareIntent(filePaths = filePaths)
        context.startActivity(Intent.createChooser(shareIntent, ""))
    }

    private fun getShareIntent(text: String? = null, vararg filePaths: String): Intent {
        val shareFiles = filePaths.map { File(it) }
        val shareAction = if (shareFiles.isSingle()) Intent.ACTION_SEND else Intent.ACTION_SEND_MULTIPLE

        val shareIntent: Intent = Intent().apply {
            this.action = shareAction
            this.setShareData(shareFiles)
            this.setShareType(shareFiles)

            text?.let {
                this.putExtra(Intent.EXTRA_TEXT, it)
            }
        }

        return shareIntent
    }

    /**
     * 设置分享的数据
     */
    private fun Intent.setShareData(shareFiles: List<File>) {
        val fileUris = shareFiles.map { UriUtils.file2Uri(it) }.toArrayList()
        val isSingle = fileUris.isSingle()
        if (isSingle) {
            this.putExtra(Intent.EXTRA_STREAM, fileUris.single())
        } else {
            this.putParcelableArrayListExtra(Intent.EXTRA_STREAM, fileUris)
        }
    }

    /**
     * 设置分享的数据类型
     */
    private fun Intent.setShareType(shareFiles: List<File>) {
        val mimeTypes = shareFiles.map {
            //获取分享文件的类型, 默认使用 "image/jpeg", 通常用于分享图片，但也可以用于分享任何类型的二进制内容
            MimeType.getByExtension(it.extension).run {
                if (this == MimeType.UNKNOWN) MimeType.JPEG else this
            }
        }

        when {
            mimeTypes.all { it.isText() } -> this.type = "text/*"
            mimeTypes.all { it.isImage() } -> this.type = "image/*"
            mimeTypes.all { it.isVideo() } -> this.type = "video/*"
            mimeTypes.all { it.isAudio() } -> this.type = "audio/*"
            else -> {
                if (mimeTypes.isSingle()) {
                    this.type = mimeTypes.single().mMimeTypeName
                } else {
                    // 这时接收方应注册支持的文件扩展名
                    this.type = "*/*"
                }
            }
        }
    }

    /**
     * 从 Android 10 开始，Android ShareSheet 可以显示分享的文本的预览
     *
     *  如需预览文本，可以设置相应的标题 [和/或] 缩略图
     */
    @RequiresApi(Build.VERSION_CODES.Q)
    private fun Intent.setShareContent(title: String, imageFile: File) {
        this.putExtra(Intent.EXTRA_TITLE, title)  // 设置描述内容
        this.data = UriUtils.file2Uri(imageFile)  // 设置缩略图
        this.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
    }
}