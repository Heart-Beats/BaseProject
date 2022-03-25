package com.hl.utils

import java.net.URLDecoder

/**
 * @author  张磊  on  2021/12/09 at 14:28
 * Email: 913305160@qq.com
 */
object UrlParamUtil {

    /**
     * 解析 url 问号后的参数
     */
    fun getParameter(url: String): Map<String, Any> {
        val map: MutableMap<String, Any> = HashMap()
        try {
            val charset = "utf-8"
            val decodeUrl = URLDecoder.decode(url, charset)
            if (decodeUrl.indexOf('?') != -1) {
                val contents = decodeUrl.substring(decodeUrl.indexOf('?') + 1)
                val keyValues = contents.split("&").toTypedArray()
                for (i in keyValues.indices) {
                    val key = keyValues[i].substring(0, keyValues[i].indexOf("="))
                    val value = keyValues[i].substring(keyValues[i].indexOf("=") + 1)
                    map[key] = value
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return map
    }

    fun getOneParameter(url: String, keyWord: String): String {
        var retValue = ""
        try {
            val charset = "utf-8"
            val decodeUrl = URLDecoder.decode(url, charset)
            if (decodeUrl.indexOf('?') != -1) {
                val contents = decodeUrl.substring(decodeUrl.indexOf('?') + 1)
                val keyValues = contents.split("&").toTypedArray()
                for (i in keyValues.indices) {
                    val key = keyValues[i].substring(0, keyValues[i].indexOf("="))
                    val value = keyValues[i].substring(keyValues[i].indexOf("=") + 1)
                    if (key == keyWord) {
                        retValue = value
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return retValue
    }
}