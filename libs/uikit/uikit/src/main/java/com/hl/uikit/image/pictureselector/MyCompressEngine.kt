package com.hl.uikit.image.pictureselector

import android.content.Context
import android.net.Uri
import android.os.Environment
import android.text.TextUtils
import android.util.Log
import com.luck.picture.lib.config.PictureMimeType
import com.luck.picture.lib.engine.CompressEngine
import com.luck.picture.lib.entity.LocalMedia
import com.luck.picture.lib.interfaces.OnCallbackListener
import com.luck.picture.lib.utils.DateUtils
import com.luck.picture.lib.utils.SdkVersionUtils
import top.zibin.luban.Luban
import top.zibin.luban.OnCompressListener
import java.io.File

/**
 * @author  张磊  on  2022/03/18 at 17:02
 * Email: 913305160@qq.com
 */
class MyCompressEngine(val needDeleteOrigin: Boolean = false) : CompressEngine {

    private val TAG = "MyCompressEngine"

    override fun onStartCompress(
        context: Context,
        list: ArrayList<LocalMedia>,
        listener: OnCallbackListener<ArrayList<LocalMedia>>
    ) {

        // 1、构造可用的压缩数据源
        val compress: MutableList<Uri> = ArrayList()
        for (i in 0 until list.size) {
            val media = list[i]
            val availablePath = media.availablePath
            val uri: Uri =
                if (PictureMimeType.isContent(availablePath) || PictureMimeType.isHasHttp(availablePath)) Uri.parse(
                    availablePath
                ) else Uri.fromFile(File(availablePath))
            compress.add(uri)
        }
        if (compress.size == 0) {
            listener.onCall(list)
            return
        }

        // 2、调用Luban压缩
        Luban.with(context)
            .load(compress)
            .apply {
                // 忽略不压缩图片的大小
                this.ignoreBy(100)

                context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)?.absolutePath?.let {
                    // 设置压缩后文件存储位置
                    this.setTargetDir(it)
                }
            }
            .filter { path ->
                PictureMimeType.isUrlHasImage(path) && !PictureMimeType.isHasHttp(path)
            }
            .setRenameListener { filePath ->
                val indexOf = filePath.lastIndexOf(".")
                val postfix = if (indexOf != -1) filePath.substring(indexOf) else ".jpg"
                DateUtils.getCreateFileName("CMP_").toString() + postfix
            }
            .setCompressListener(object : OnCompressListener {
                override fun onStart() {
                    Log.i(TAG, "onStart: 开始压缩")
                }

                override fun onSuccess(index: Int, compressFile: File) {
                    Log.d(TAG, "onSuccess: 压缩图片成功 --------->  index==$index,  compressFile==$compressFile")

                    // 压缩完构造LocalMedia对象
                    val media = list[index]
                    if (compressFile.exists() && !TextUtils.isEmpty(compressFile.absolutePath)) {
                        media.isCompressed = true
                        media.compressPath = compressFile.absolutePath
                        media.sandboxPath = if (SdkVersionUtils.isQ()) media.compressPath else null

                        if (needDeleteOrigin) {
                            File(media.realPath).run {
                                if (this.exists()) {
                                    // 压缩完成后如果需要删除原始图片
                                    this.delete()
                                }
                            }
                        }
                    }
                    // 因为是多图压缩，所以判断压缩到最后一张时返回结果
                    if (index == list.size - 1) {
                        listener.onCall(list)
                    }
                }

                override fun onError(index: Int, e: Throwable?) {
                    Log.e(TAG, "onSuccess: 压缩图片失败 --------->  index==$index", e)

                    // 压缩失败
                    if (index != -1) {
                        val media = list[index]
                        media.isCompressed = false
                        media.compressPath = null
                        media.sandboxPath = null
                        if (index == list.size - 1) {
                            listener.onCall(list)
                        }
                    }
                }
            }).launch()
    }

    fun startCompress(
        context: Context,
        originList: List<String>,
        listener: OnCallbackListener<List<String>>
    ) {
        Luban.with(context)
            .load(originList)
            .apply {
                // 忽略不压缩图片的大小
                this.ignoreBy(100)

                context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)?.absolutePath?.let {
                    // 设置压缩后文件存储位置
                    this.setTargetDir(it)
                }
            }
            .filter { path ->
                PictureMimeType.isUrlHasImage(path) && !PictureMimeType.isHasHttp(path)
            }
            .setRenameListener { filePath ->
                val indexOf = filePath.lastIndexOf(".")
                val postfix = if (indexOf != -1) filePath.substring(indexOf) else ".jpg"
                DateUtils.getCreateFileName("CMP_").toString() + postfix
            }
            .setCompressListener(object : OnCompressListener {

                private val compressList = mutableListOf<String>()

                override fun onStart() {
                    Log.i(TAG, "onStart: 开始压缩")
                }

                override fun onSuccess(index: Int, compressFile: File) {
                    Log.d(TAG, "onSuccess: 压缩图片成功 --------->  index==$index,  compressFile==$compressFile")

                    // 压缩完构造LocalMedia对象
                    val originFilePath = originList[index]
                    if (compressFile.exists() && !TextUtils.isEmpty(compressFile.absolutePath)) {
                        if (needDeleteOrigin) {
                            File(originFilePath).run {
                                if (this.exists()) {
                                    // 压缩完成后如果需要删除原始图片
                                    this.delete()
                                }
                            }
                        }

                        compressList.add(compressFile.absolutePath)
                    }
                    // 因为是多图压缩，所以判断压缩到最后一张时返回结果
                    if (index == originList.size - 1) {
                        listener.onCall(compressList)
                    }
                }

                override fun onError(index: Int, e: Throwable?) {
                    Log.e(TAG, "onSuccess: 压缩图片失败 --------->  index==$index", e)

                    // 压缩失败
                    if (index != -1) {
                        if (index == originList.size - 1) {
                            listener.onCall(originList)
                        }
                    }
                }
            }).launch()
    }
}