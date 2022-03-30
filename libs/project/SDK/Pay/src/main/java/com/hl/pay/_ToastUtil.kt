package com.hl.pay

import android.content.Context
import android.widget.Toast

/**
 * @author  张磊  on  2022/02/23 at 15:13
 * Email: 913305160@qq.com
 */

internal fun Context.toastShort(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}