package com.hl.utils.banner

import com.google.gson.annotations.SerializedName

/**
 * @author  张磊  on  2022/09/02 at 18:01
 * Email: 913305160@qq.com
 */
data class AdsDetail(
	/**
	 * 广告类型名称
	 */
	@SerializedName("adsTypeAlias")
	var adsTypeAlias: String? = null,

	/**
	 * 广告标题
	 */
	var adsTitle: String? = null,

	/**
	 * 广告内容
	 */
	var adsContent: String? = null,
	@SerializedName("adsFlowId")

	/**
	 * 广告id
	 */
	var adsId: String? = null,
	@SerializedName("adsPicUrl")

	/**
	 * 广告图片url
	 */
	var adsImgUrl: String? = null,
	@SerializedName("creTime")

	/**
	 * 创建时间：格式为：yyyy-MM-dd HH:mm:ss
	 */
	var createTime: String? = null,
	@SerializedName("webUrl")

	/**
	 * 广告HTML的url
	 */
	var adsFlowUrl: String? = null,

	/**
	 * 广告时长
	 */
	var adsDuration: String? = null,

	/**
	 * 消息类型 01-首页轮播 02-自定义 03-欢迎界面
	 */
	var adsType: String? = null,

	/**
	 * 采用本地广告图片，不可与 adsImgUrl 同时使用
	 */
	var localImageRes: Int? = null
)