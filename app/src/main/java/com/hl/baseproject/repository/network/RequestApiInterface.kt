package com.hl.baseproject.repository.network

import com.hl.api.PublicResp
import com.hl.baseproject.repository.network.bean.BannerData
import com.hl.baseproject.repository.network.bean.HomeArticleList
import com.hl.baseproject.repository.network.bean.WanAndroidPublicResp
import retrofit2.http.GET
import retrofit2.http.Path

/**
 * @author  张磊  on  2023/02/16 at 18:07
 * Email: 913305160@qq.com
 */
interface RequestApiInterface {

	/**
	 * 获取本地共享 token
	 */
	@GET("http://127.0.0.1:8042/api/gettoken")
	suspend fun getToken(): PublicResp<String>

	/**
	 * 获取顶部 banner 数据
	 */
	@GET("banner/json")
	suspend fun getTopBannerList(): WanAndroidPublicResp<List<BannerData>?>

	@GET("article/list/{pageIndex}/json")
	suspend fun getHomeArticleList(@Path("pageIndex") pageIndex: Int = 0): WanAndroidPublicResp<HomeArticleList?>
}