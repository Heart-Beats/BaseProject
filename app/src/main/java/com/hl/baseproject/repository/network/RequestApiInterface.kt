package com.hl.baseproject.repository.network

import com.hl.baseproject.repository.network.bean.BannerData
import com.hl.baseproject.repository.network.bean.WanAndroidPublicResp
import retrofit2.http.GET

/**
 * @author  张磊  on  2023/02/16 at 18:07
 * Email: 913305160@qq.com
 */
interface RequestApiInterface {

	/**
	 * 获取顶部 banner 数据
	 */
	@GET("banner/json")
	suspend fun getTopBannerList(): WanAndroidPublicResp<List<BannerData>?>
}