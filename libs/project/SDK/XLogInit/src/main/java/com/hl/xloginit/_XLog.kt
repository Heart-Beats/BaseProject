package com.hl.xloginit

import com.elvishew.xlog.XLog
import com.elvishew.xlog.XLog.tag

/**
 * @author  张磊  on  2023/10/19 at 18:01
 * Email: 913305160@qq.com
 */

@JvmOverloads
fun xlogV(tag: String, msg: String, tr: Throwable? = null) {
	XLog.tag(tag).v(msg)
}

fun xlogV(tag: String, format: String, vararg args: Any) {
	XLog.tag(tag).v(format, args)
}

@JvmOverloads
fun xlogW(tag: String, msg: String, tr: Throwable? = null) {
	XLog.tag(tag).w(msg)
}

fun xlogW(tag: String, format: String, vararg args: Any) {
	XLog.tag(tag).w(format, args)
}

@JvmOverloads
fun xlogD(tag: String, msg: String, tr: Throwable? = null) {
	XLog.tag(tag).d(msg)
}

fun xlogD(tag: String, format: String, vararg args: Any) {
	XLog.tag(tag).d(format, args)
}

@JvmOverloads
fun xlogI(tag: String, msg: String, tr: Throwable? = null) {
	XLog.tag(tag).i(msg)
}

fun xlogI(tag: String, format: String, vararg args: Any) {
	XLog.tag(tag).i(format, args)
}

@JvmOverloads
fun xlogE(tag: String, msg: String, tr: Throwable? = null) {
	XLog.tag(tag).e(msg, tr)
}

fun xlogE(tag: String, format: String, vararg args: Any) {
	XLog.tag(tag).e(format, args)
}