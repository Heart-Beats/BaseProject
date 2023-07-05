package com.hl.qrcode

import android.graphics.Bitmap
import android.graphics.Color
import androidx.annotation.FloatRange
import androidx.annotation.Px
import com.google.zxing.BarcodeFormat
import com.king.zxing.util.CodeUtils

/**
 * @author  张磊  on  2022/01/23 at 6:08
 * Email: 913305160@qq.com
 */
object QRCodeUtil {

    /**
     * 生成二维码
     * @param content 二维码的内容
     * @param heightPx 二维码的高
     * @param logo 二维码中间的logo
     * @param ratio logo 所占比例 因为二维码的最大容错率为 30%，所以建议 ratio 的范围小于0.3
     * @param codeColor 二维码的颜色
     * @return
     */
    fun createQRCode(
        content: String,
        @Px heightPx: Int,
        logo: Bitmap? = null,
        @FloatRange(from = 0.0, to = 1.0) ratio: Float = 0.2F,
        codeColor: Int = Color.BLACK
    ): Bitmap? {
        return CodeUtils.createQRCode(content, heightPx, logo, ratio, codeColor)
    }


    /**
     * 创建条形码
     *
     * @param [content] 内容
     * @param [format] 格式
     * @param [widthPx] 像素宽度
     * @param [heightPx] 像素高度
     * @param [isShowText] 显示文本
     * @param [textSize] 文字大小
     * @param [codeColor] 颜色
     *
     * @return [Bitmap?]
     */
    fun createBarCode(
        content: String,
        format: BarcodeFormat = BarcodeFormat.CODE_128,
        @Px widthPx: Int = 800,
        @Px heightPx: Int = 200,
        isShowText: Boolean = false,
        @Px textSize: Int = 40,
        codeColor: Int = Color.BLACK
    ): Bitmap? {
        return CodeUtils.createBarCode(content, format, widthPx, heightPx, null, isShowText, textSize, codeColor)
    }


    /**
     * 解析条形码/二维码
     * @param [bitmap] 位图
     */
    fun parseCode(bitmap: Bitmap): String? {
        return CodeUtils.parseCode(bitmap)
    }

    /**
     * 解析条形码/二维码
     * @param [bitmapPath] 图片路径
     */
    fun parseCode(bitmapPath: String): String? {
        return CodeUtils.parseCode(bitmapPath)
    }


    /**
     * 解析二维码
     * @param [bitmap] 位图
     */
    fun parseQRCode(bitmap: Bitmap): String? {
        return CodeUtils.parseQRCode(bitmap)
    }

    /**
     * 解析二维码
     * @param [bitmapPath] 图片路径
     */
    fun parseQRCode(bitmapPath: String): String? {
        return CodeUtils.parseQRCode(bitmapPath)
    }
}