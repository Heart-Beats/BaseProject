package com.hl.umeng.sdk

import com.umeng.socialize.bean.SHARE_MEDIA

/**
 * @author  张磊  on  2022/06/14 at 22:05
 * Email: 913305160@qq.com
 */

data class SharePlatformParam(
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
	 * 分享平台
	 */
	var platform: SHARE_MEDIA = SHARE_MEDIA.WEIXIN,

	/**
	 * 分享平台集合， 供分享面板使用
	 */
	var sharePlatforms: List<SHARE_MEDIA> = listOf(SHARE_MEDIA.WEIXIN)
)