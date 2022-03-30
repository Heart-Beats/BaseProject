package com.hl.arch

import android.content.Context
import android.widget.Toast

/**
 * @author  张磊  on  2021/12/26 at 13:27
 * Email: 913305160@qq.com
 */
internal object ToastUtils {

    @JvmStatic
    fun showShort(context: Context, message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }
}