package com.hl.utils.qrcode

import android.graphics.Bitmap
import android.graphics.Color
import androidx.annotation.FloatRange
import com.king.zxing.util.CodeUtils

/**
 * @author  张磊  on  2022/01/23 at 6:08
 * Email: 913305160@qq.com
 */
object QRCreateUtil {

    /**
     * 生成二维码
     * @param content 二维码的内容
     * @param heightPx 二维码的高
     * @param logo 二维码中间的logo
     * @param ratio logo所占比例 因为二维码的最大容错率为30%，所以建议ratio的范围小于0.3
     * @param codeColor 二维码的颜色
     * @return
     */
    fun createQRCode(
        content: String,
        heightPx: Int,
        logo: Bitmap? = null,
        @FloatRange(from = 0.0, to = 1.0) ratio: Float = 0.2F,
        codeColor: Int = Color.BLACK
    ): Bitmap? {
        return CodeUtils.createQRCode(content, heightPx, logo, ratio, codeColor)
    }

}