package com.hl.bitmaputil

import android.Manifest
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Environment
import android.util.Log
import android.view.View
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.lifecycleScope
import com.blankj.utilcode.util.ImageUtils
import com.hl.permission.reqPermissions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL


/**
 * @author  张磊  on  2022/04/09 at 15:57
 * Email: 913305160@qq.com
 */

fun View.toBitmap(): Bitmap? {
    return ImageUtils.view2Bitmap(this)
}

object BitmapUtil {

    private const val TAG = "BitmapUtil"

    suspend fun getBitmapFromUrl(imageUrl: String): Bitmap? {
        return withContext(Dispatchers.IO) {
            var bitmap: Bitmap?
            var `is`: InputStream? = null
            try {
                val myFileUrl = URL(imageUrl)
                val conn: HttpURLConnection = myFileUrl.openConnection() as HttpURLConnection
                conn.doInput = true
                conn.connect()
                `is` = conn.inputStream
                bitmap = BitmapFactory.decodeStream(`is`)
                Log.d(TAG, "image download finished. ---- $imageUrl")
            } catch (e: Exception) {
                Log.d(TAG, "get bitmap fail !", e)
                bitmap = null
            } finally {
                `is`?.close()
            }
            bitmap
        }
    }

    /**
     * 保存 bitmap 图片文件
     *
     * @param bitmap            bitmap
     * @param saveName          保存图片名称
     * @param isSave2AppDir     是否保存在 App 私有目录下
     *@param  failAction        失败回调
     *@param  successAction     成功回调
     *
     */
    fun saveBitmap(
        context: Context,
        bitmap: Bitmap,
        saveName: String,
        isSave2AppDir: Boolean = false,
        failAction: (errorMsg: String) -> Unit = {},
        successAction: () -> Unit = {}
    ) {

        val saveFile = if (isSave2AppDir) {
            File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), saveName)
        } else {
            File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), saveName)
        }

        saveBitmap(context, bitmap, saveFile, isSave2AppDir, failAction, successAction)
    }

    /**
     * 保存 bitmap 图片文件
     *
     * @param bitmap            bitmap
     * @param saveFile          保存的图片文件
     * @param isSave2AppDir     是否保存在 App 私有目录下， false: saveFile 若在私有目录下会被拷贝到公共目录
     *@param  failAction        失败回调
     *@param  successAction     成功回调
     *
     */
    fun saveBitmap(
        context: Context,
        bitmap: Bitmap,
        saveFile: File,
        isSave2AppDir: Boolean = false,
        failAction: (errorMsg: String) -> Unit = {},
        successAction: () -> Unit = {}
    ) {
        if (context is FragmentActivity) {
            context.reqPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE, deniedAction = {
                failAction("拒绝权限，插入到图库失败")
            }) {
                context.lifecycleScope.launch {
                    val isSave = withContext(Dispatchers.IO) {
                        ImageUtils.save(bitmap, saveFile, Bitmap.CompressFormat.PNG)
                    }

                    if (isSave) {
                        val scanResultCallBack = object : ScanResultCallBack {
                            override fun onScanSuccess(scanFile: File) {
                                successAction()
                            }

                            override fun onScanFail(errorMsg: String) {
                                failAction(errorMsg)
                            }

                            override fun onScanInfo(msg: String) {
                                Log.d(TAG, "onScanInfo: $msg")
                            }
                        }

                        if (isSave2AppDir) {
                            ScanFileActionUtil.scanMedia(
                                context,
                                saveFile.absolutePath,
                                scanResultCallBack = scanResultCallBack
                            )
                        } else {
                            ScanFileActionUtil.scanMedia2Public(
                                context,
                                saveFile.absolutePath,
                                scanResultCallBack = scanResultCallBack
                            )
                        }
                    } else {
                        failAction("保存图片失败")
                    }
                }
            }
        } else {
            failAction("context 非 FragmentActivity 类型")
        }
    }
}