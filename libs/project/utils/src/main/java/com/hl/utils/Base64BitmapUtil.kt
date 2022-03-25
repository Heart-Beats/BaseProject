package com.hl.utils

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.util.Base64
import android.util.Log
import android.widget.ImageView
import java.io.ByteArrayOutputStream
import java.io.IOException

object Base64BitmapUtil {

    private const val TAG = "Base64BitmapUtil"

    /**
     * bitmap转base64
     *
     * @param bitmap
     * @return
     */
    fun bitmap2bytes(bitmap: Bitmap): ByteArray {
        val outputStream = ByteArrayOutputStream(bitmap.byteCount)
        bitmap.compress(Bitmap.CompressFormat.JPEG, 60, outputStream)
        return outputStream.toByteArray()
    }

    fun bytesToBitmap(bytes: ByteArray): Bitmap {
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
    }

    //把bitmap转换成String
    fun bitmapToString(bm: Bitmap): String {
        val baos = ByteArrayOutputStream()

        //1.5M的压缩后在100Kb以内，测试得值,压缩后的大小=94486字节,压缩后的大小=74473字节
        //这里的JPEG 如果换成PNG，那么压缩的就有600kB这样
        bm.compress(Bitmap.CompressFormat.JPEG, 40, baos)
        val b = baos.toByteArray()
        Log.d(TAG, "压缩后的大小= " + b.size)
        return Base64.encodeToString(b, Base64.DEFAULT)
    }

    /**
     * bitmap转为base64*
     * @param bitmap
     * @return
     */
    fun bitmapToBase64(bitmap: Bitmap?): String? {
        var result: String? = null
        var baos: ByteArrayOutputStream? = null
        try {
            if (bitmap != null) {
                baos = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
                baos.flush()
                baos.close()
                val bitmapBytes = baos.toByteArray()
                result = Base64.encodeToString(bitmapBytes, Base64.DEFAULT)
            }
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            try {
                if (baos != null) {
                    baos.flush()
                    baos.close()
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        return result
    }

    /**
     * base64转为bitmap
     *
     * @param base64Data
     * @return
     */
    fun base64ToBitmap(base64Data: String?): Bitmap {
        val bytes = Base64.decode(base64Data, Base64.DEFAULT)
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
    }

    private fun getBitmapOfImageView(imageView: ImageView): Bitmap {
        return (imageView.drawable as BitmapDrawable).bitmap
    }

    fun imageView2Base64(imageView: ImageView): String? {
        val bitmap = getBitmapOfImageView(imageView)
        return bitmapToBase64(bitmap)
    }
}