package com.hl.api.interceptor

import okhttp3.HttpUrl
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import java.io.IOException

/**
 * @author  张磊  on  2023/01/13 at 10:17
 * Email: 913305160@qq.com
 *
 * 添加多域名的拦截器
 *
 * 需要接口上添加 @Headers("Domain-Name: {domainName}") 注解
 */
class MultiBaseUrlInterceptor(val onDomainGetBaseUrl: (domainName: String) -> String) : Interceptor {

	private val DOMAIN_NAME = "Domain-Name"

	/**
	 * Domain 与 baseUrl 的映射集合
	 */
	private val domainCacheMap = hashMapOf<String, String>()

	@Throws(IOException::class)
	override fun intercept(chain: Interceptor.Chain): Response {
		//获取原始的originalRequest
		val originalRequest: Request = chain.request()

		//获取老的url
		val oldUrl: HttpUrl = originalRequest.url

		//获取originalRequest的创建者builder
		val builder: Request.Builder = originalRequest.newBuilder()

		//获取 Domain-Name 对应的 Header
		val domainName = originalRequest.headers(DOMAIN_NAME).firstOrNull()

		val request = domainName?.run {
			//如果有这个 header，先将配置的 header删除，因此该 header 仅用作 app 和 okhttp 之间使用
			builder.removeHeader(DOMAIN_NAME)

			if (!domainCacheMap.contains(this)) {
				domainCacheMap[this] = onDomainGetBaseUrl(this).trim()
			}

			val newHttpUrl = domainCacheMap[this]?.toHttpUrlOrNull()?.run {
				// 根据新的 baseUrl 替换全局的 baseUrl 部分
				oldUrl.newBuilder()
					.scheme(this.scheme) //http协议如：http或者https
					.host(this.host) //主机地址
					.port(this.port) //端口
					.build()

			} ?: oldUrl

			//获取处理后的新newRequest
			builder.url(newHttpUrl).build()
		} ?: originalRequest

		return chain.proceed(request)
	}
}