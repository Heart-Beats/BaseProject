package com.hl.arch.web.sdk

/**
 * @author  张磊  on  2022/02/19 at 23:40
 * Email: 913305160@qq.com
 */
interface IStandSdk : ISdk {

	/**
	 * 获取设备的相关信息
	 */
	fun getDeviceInfo(handlerName: String = Thread.currentThread().stackTrace[1].methodName)

	/**
	 * 返回页面
	 */
	fun navigateBack(handlerName: String = Thread.currentThread().stackTrace[1].methodName)

	/**
	 * 打开新 H5 页面
	 */
	fun navigateTo(handlerName: String = Thread.currentThread().stackTrace[1].methodName)

	/**
	 * 向本地保存数据
	 */
	fun setH5Data(handlerName: String = Thread.currentThread().stackTrace[1].methodName)

	/**
	 * 从本地获取数据
	 */
	fun getH5Data(handlerName: String = Thread.currentThread().stackTrace[1].methodName)

	/**
	 * 清除所有 H5 本地数据
	 */
	fun clearH5Data(handlerName: String = Thread.currentThread().stackTrace[1].methodName)


	/**
	 * 关闭当前 H5 页面，跳转到指定页面
	 */
	fun redirectTo(handlerName: String = Thread.currentThread().stackTrace[1].methodName)

	/**
	 * 关闭所有 H5 页面，打开指定页面
	 */
	fun reLaunch(handlerName: String = Thread.currentThread().stackTrace[1].methodName)


	/**
	 *  设置状态栏是否为明亮模式
	 */
	fun setStatusBarLightMode(handlerName: String = Thread.currentThread().stackTrace[1].methodName)

	/**
	 *  设置状态栏颜色
	 */
	fun setStatusBarColor(handlerName: String = Thread.currentThread().stackTrace[1].methodName)

	/**
	 * 获取网络连接类型
	 */
	fun getNetworkConnectType(handlerName: String = Thread.currentThread().stackTrace[1].methodName)

	/**
	 * 获取位置信息
	 */
	fun getLocation(handlerName: String = Thread.currentThread().stackTrace[1].methodName)

	/**
	 *  图片预览
	 */
	fun previewImage(handlerName: String = Thread.currentThread().stackTrace[1].methodName)

	/**
	 *  保存图片
	 */
	fun savePhotoToAlbum(handlerName: String = Thread.currentThread().stackTrace[1].methodName)

	/**
	 *  打电话
	 */
	fun callPhone(handlerName: String = Thread.currentThread().stackTrace[1].methodName)

	/**
	 *  保存/下载文件
	 */
	fun downLoadFile(handlerName: String = Thread.currentThread().stackTrace[1].methodName)

	/**
	 *  扫描二维码
	 */
	fun scanQRCode(handlerName: String = Thread.currentThread().stackTrace[1].methodName)


	/**
	 *  分享链接/图片 到三方平台
	 */
	fun share2Platform(handlerName: String = Thread.currentThread().stackTrace[1].methodName)
}