package com.hl.utils

import com.blankj.utilcode.util.AppUtils

/**
 * @author  张磊  on  2023/02/20 at 11:02
 * Email: 913305160@qq.com
 */

/**
 * 获取应用名称
 *
 * @param packageName 指定应用包名
 */
fun getAppName(packageName: String? = null) =
	packageName?.run { AppUtils.getAppName(this) } ?: AppUtils.getAppName()

/**
 * 获取应用图标
 *
 * @param packageName 指定应用包名
 */
fun getAppIconId(packageName: String? = null) =
	packageName?.run { AppUtils.getAppIconId(this) } ?: AppUtils.getAppIconId()

/**
 * 获取应用版本 code
 *
 * @param packageName 指定应用包名
 */
fun getAppVersionCode(packageName: String? = null) =
	packageName?.run { AppUtils.getAppVersionCode(this) } ?: AppUtils.getAppVersionCode()

/**
 * 获取应用版本名称
 *
 * @param packageName 指定应用包名*
 */
fun getAppVersionName(packageName: String? = null) =
	packageName?.run { AppUtils.getAppVersionName(this) } ?: AppUtils.getAppVersionName()

/**
 * 获取应用的 MD5 签名
 *
 * @param packageName 指定应用包名*
 */
fun getAppSignaturesMD5(packageName: String? = null) =
	packageName?.run { AppUtils.getAppSignaturesMD5(this) } ?: AppUtils.getAppSignaturesMD5()

/**
 * 获取应用的 SHA1 签名
 *
 * @param packageName 指定应用包名*
 */
fun getAppSignaturesSHA1(packageName: String? = null) =
	packageName?.run { AppUtils.getAppSignaturesSHA1(this) } ?: AppUtils.getAppSignaturesSHA1()

/**
 * 获取应用的 SHA256 签名
 *
 * @param packageName 指定应用包名*
 */
fun getAppSignaturesSHA256(packageName: String? = null) =
	packageName?.run { AppUtils.getAppSignaturesSHA256(this) } ?: AppUtils.getAppSignaturesSHA256()