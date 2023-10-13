package com.hl.api.interceptor

/**
 * @author  张磊  on  2023/10/13 at 13:40
 * Email: 913305160@qq.com
 */
interface LogProxy {

	fun e(tag:String , msg:String)

	fun w(tag:String , msg:String)

	fun i(tag:String , msg:String)

	fun d(tag:String , msg:String)
}