package com.hl.share.utils

/**
 * @author  张磊  on  2022/12/14 at 23:56
 * Email: 913305160@qq.com
 *
 */


/**
 * 将 List 转为 ArrayList
 */
internal fun <T> List<T>.toArrayList(): ArrayList<T> {
	return ArrayList(this)
}

/**
 * 是否仅含有一个元素
 */
internal fun <T> List<T>.isSingle(): Boolean {
	return this.singleOrNull() != null
}