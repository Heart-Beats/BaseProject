package com.hl.utils

import java.util.regex.Pattern

/**
 * @author  张磊  on  2021/11/05 at 15:02
 * Email: 913305160@qq.com
 */

fun String.isUrl(): Boolean {

    val regex =
        "^(http(s)?:\\/\\/)[a-zA-Z0-9][-a-zA-Z0-9]{0,62}(\\.[a-zA-Z0-9][-a-zA-Z0-9]{0,62})+(:[0-9]{1,5})?[-a-zA-Z0-9()@:%_\\\\\\+\\.~#?&//=]*\$"
    return Pattern.compile(regex).matcher(this).matches()
    // return this.contains("http") || this.contains("https")
}