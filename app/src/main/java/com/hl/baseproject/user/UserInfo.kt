package com.hl.baseproject.user

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * @author  张磊  on  2022/01/05 at 11:55
 * Email: 913305160@qq.com
 */
@Parcelize
data class UserInfo(

	/**
	 * 用户ID
	 */
	@JvmField
	var userUuid: String? = null,

	/**
	 * 用户名称
	 */
	@JvmField
	var userName: String? = null,

	/**
	 * 手机号码
	 */
	@JvmField
	var userMobile: String? = null,

	/**
	 * 企业uuid
	 */
	var enterpriseUuid: String? = null,

	/**
	 * 商户uuid
	 */
	var merchantUuid: String? = null,

	/**
	 * 商户logo
	 */
	var merchantLogo: String? = null,

	/**
	 * 商户名称
	 */
	var merchantName: String? = null,

	/**
	 * 门店uuid
	 */
	var storeUuid: String? = null,

	/**
	 * 门店名称
	 */
	var storeName: String? = null,

	/**
	 * 门店图片
	 */
	var storePhoto: String? = null,


	/********************** 非接口字段，仅便于处理应用   ******************/

	@JvmField
	var token: String? = null,

	) : Parcelable