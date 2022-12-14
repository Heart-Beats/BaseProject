package com.hl.utils

/**
 * @author  张磊  on  2022/12/14 at 23:56
 * Email: 913305160@qq.com
 *
 *  集合相关工具类
 */

fun <T> List<T>.toArrayList(): ArrayList<T> {
    return ArrayList(this)
}

/**
 * 是否仅含有一个元素
 */
fun <T> List<T>.isSingle(): Boolean {
    return this.singleOrNull() != null
}