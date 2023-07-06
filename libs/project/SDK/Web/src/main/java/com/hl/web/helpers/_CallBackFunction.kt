package com.hl.web.helpers

import com.github.lzyzsd.jsbridge.CallBackFunction
import com.hl.web.bean.H5Return

/**
 * @author  张磊  on  2022/07/14 at 22:07
 * Email: 913305160@qq.com
 */

fun <T> CallBackFunction.onSuccess(data: T) {
	this.onCallBack(com.hl.web.bean.H5Return.success(data))
}

fun <T> CallBackFunction.onFail(data: T) {
	this.onCallBack(com.hl.web.bean.H5Return.fail(data))
}

fun CallBackFunction.onSuccess() {
	this.onSuccess("")
}

fun CallBackFunction.onFail() {
	this.onFail("")
}