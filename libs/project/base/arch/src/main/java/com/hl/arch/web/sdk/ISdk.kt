package com.hl.arch.web.sdk

/**
 * @author  张磊  on  2022/06/13 at 15:21
 * Email: 913305160@qq.com
 */

/**
 * 继承此接口可自定义 H5 可以调用的方法
 *
 * 具体使用方式见 ： @see [IStandSdk] 与 @see [IStandSdkImpl]
 */
interface ISdk {

	/**
	 * 方法定义模版
	 *
	 * @param handlerName    : 注册的方法名
	 */
	fun sdkFunTemplate(handlerName: String = Thread.currentThread().stackTrace[1].methodName) {
	}
}