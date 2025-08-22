package com.hl.baseproject.repository.network

import com.hl.api.PublicResp
import com.hl.baseproject.repository.network.bean.BannerData
import com.hl.baseproject.repository.network.bean.CheckPhoneBean
import com.hl.baseproject.repository.network.bean.CheckPhoneParam
import com.hl.baseproject.repository.network.bean.DecryptBean
import com.hl.baseproject.repository.network.bean.DecryptParams
import com.hl.baseproject.repository.network.bean.EncryptBean
import com.hl.baseproject.repository.network.bean.EncryptParams
import com.hl.baseproject.repository.network.bean.HomeArticleList
import com.hl.baseproject.repository.network.bean.WanAndroidPublicResp
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST
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

	/**
	 * 校验手机号
	 */
	@POST("http://127.0.0.1:8763/ctq/pivot/api/check/phone")
	suspend fun checkPhone(@Body param: CheckPhoneParam): PublicResp<CheckPhoneBean>

	/**
	 * 请求量子加密
	 */
	@Headers("X-Phone-Hash: FFE624BEEBD1385EB4D1C674902CECC6673422DC114FC9BCADE96914685ECDAC")
	@POST("http://127.0.0.1:8763/ctq/pivot/api/encrypt")
	suspend fun doQuantumEncrypt(@Body param: EncryptParams): PublicResp<EncryptBean>

	/**
	 * 请求量子加密
	 */
	@Headers("X-Phone-Hash: FFE624BEEBD1385EB4D1C674902CECC6673422DC114FC9BCADE96914685ECDAC")
	@POST("http://127.0.0.1:8763/ctq/pivot/api/decrypt")
	suspend fun doQuantumDecrypt(@Body param: DecryptParams): PublicResp<DecryptBean>
}