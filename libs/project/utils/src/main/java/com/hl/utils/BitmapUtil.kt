package com.hl.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Environment
import android.util.Log
import com.blankj.utilcode.util.ImageUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL


/**
 * @author  张磊  on  2022/04/09 at 15:57
 * Email: 913305160@qq.com
 */
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
                Log.e(TAG, "get bitmap fail !", e)
                bitmap = null
            } finally {
                `is`?.close()
            }
            bitmap
        }
    }

    suspend fun saveBitmap(
        context: Context,
        bitmap: Bitmap,
        saveName: String,
        failAction: () -> Unit = {},
        successAction: () -> Unit = {}
    ) {
        val saveFile = File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)?.absolutePath, saveName)
        val isSave = withContext(Dispatchers.IO) {
            ImageUtils.save(bitmap, saveFile, Bitmap.CompressFormat.PNG)
        }

        if (isSave) {
            ScanFileActionUtil.scanMedia(context, saveFile)
            successAction()
        } else {
            failAction()
        }
    }
}