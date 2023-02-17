package com.hl.baseproject.repository.network.bean

import androidx.annotation.Keep


/**
 * @author  张磊  on  2023/02/17 at 18:10
 * Email: 913305160@qq.com
 */
@Keep
data class BannerData(
	var desc: String? = null,
	var id: Int? = null,
	var imagePath: String? = null,
	var isVisible: Int? = null,
	var order: Int? = null,
	var type: Int? = null,

	/**
	 * 标题
	 */
	var title: String? = null,

	/**
	 * 跳转链接
	 */
	var url: String? = null
)