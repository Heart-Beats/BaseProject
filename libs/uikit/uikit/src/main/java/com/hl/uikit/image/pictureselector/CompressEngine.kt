package com.hl.uikit.image.pictureselector

import android.content.Context
import android.net.Uri
import android.os.Environment
import android.text.TextUtils
import android.util.Log
import com.luck.picture.lib.config.PictureMimeType
import com.luck.picture.lib.engine.CompressFileEngine
import com.luck.picture.lib.interfaces.OnCallbackListener
import com.luck.picture.lib.interfaces.OnKeyValueResultCallbackListener
import com.luck.picture.lib.utils.DateUtils
import top.zibin.luban.Luban
import top.zibin.luban.OnCompressListener
import top.zibin.luban.OnNewCompressListener
import java.io.File

/**
 * @author  张磊  on  2022/03/18 at 17:02
 * Email: 913305160@qq.com
 */
internal class CompressEngine(val needDeleteOrigin: Boolean = false) : CompressFileEngine {

    private val TAG = this.javaClass.simpleName

    override fun onStartCompress(
        context: Context,
        source: ArrayList<Uri>,
        call: OnKeyValueResultCallbackListener?
    ) {
        Luban.with(context).load(source).apply {
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
            .setCompressListener(object : OnNewCompressListener {
                override fun onStart() {
                    Log.i(TAG, "onStart: 开始压缩")
                }

                override fun onSuccess(source: String?, compressFile: File?) {
                    call?.onCallback(source, compressFile?.absolutePath)

                    if (needDeleteOrigin) {
                        File(source ?: return).run {
                            if (this.exists()) {
                                // 压缩完成后如果需要删除原始图片
                                this.delete()
                            }
                        }
                    }
                }

                override fun onError(source: String?, e: Throwable?) {
                    call?.onCallback(source, null)
                }
            })
            .launch()
    }


    fun startCompress(context: Context, originList: List<String>, listener: OnCallbackListener<List<String>>) {
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