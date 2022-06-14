package com.hl.arch.web.bean

import com.hl.umeng.sdk.SharePlatformParam


data class H5DeviceInfo(
	val deviceInfo: AndroidDeviceInfo,
)

data class AndroidDeviceInfo(
	/**
	 * 状态栏高度
	 */
	val statusBarHeight: Int,

	/**
	 * 系统类型： 0:Android  1: IOS
	 */
	val systemPlatform: Int = 0,

	/**
	 * 是否处于 Wifi 状态: 0-非 wifi 1-wifi
	 */
	val isWifi: Int,
)

data class H5SaveDataEntity(

	/**
	 * 存储数据的 key
	 */
	var key: String? = null,

	/**
	 * 存储的数据
	 */
	var value: String? = null,
)

data class H5GetDataEntity(

	/**
	 * 存储数据的 key
	 */
	var key: String? = null,

	)

data class H5NavigateToParam(
	/**
	 * H5页面的标题
	 */
	var title: String = "",

	/**
	 * H5页面地址
	 */
	var url: String = "",

	/**
	 *是否显示标题 0显示 1不显示
	 */
	var hiddenNavBar: Int = 1
) {
	fun isNeedTitle() = hiddenNavBar == 0
}

data class H5NavigateBackParam(
	/**
	 * 1刷新 0不刷新
	 */
	var isRefresh: Int,

	/**
	 *  返回的页数
	 */
	var step: Int
) {

	fun isNeedRefresh() = isRefresh == 1
}

data class StatusBarLightModeParam(

	/**
	 * 状态栏是否为明亮模式： 0-黑暗模式：白色字体，状态栏透明背景  1-明亮模式： 黑色字体，状态栏背景白色
	 */
	var isLightMode: Int = 1
) {
	fun isLightMode() = isLightMode == 1
}

data class StatusBarColorParam(

	/**
	 *  状态栏颜色
	 */
	var color: String? = null
)

data class GetNetworkConnectTypeReturn(

	/**
	 * 返回值：WIFI、 MOBILE、UNKNOWN、NO
	 */
	var networkType: String? = null,
)


data class Share2PlatformParam(
	/**
	 * 标题
	 */
	var title: String = "",

	/**
	 * 描述
	 */
	var description: String = "",

	/**
	 * 链接
	 */
	var link: String = "",

	/**
	 * 封面图
	 */
	var coverUrl: String = "",

	/**
	 * 类型
	 */
	var type: String = "",
) {

	fun convert2SharePlatformParam(): SharePlatformParam {
		return SharePlatformParam().also {
			it.title = this.title
			it.description = this.description
			it.link = this.link
			it.coverUrl = this.coverUrl
		}
	}
}