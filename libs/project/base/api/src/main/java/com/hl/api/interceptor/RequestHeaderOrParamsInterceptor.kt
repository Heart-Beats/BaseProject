package com.hl.api.interceptor

import okhttp3.*
import okio.Buffer
import java.io.IOException
import java.util.*


/**
 * @author  张磊  on  2021/11/02 at 18:39
 * Email: 913305160@qq.com
 */
class RequestHeaderOrParamsInterceptor private constructor() : Interceptor {

    private companion object {
        private const val POST = "POST"
    }

    private val queryParamsMap: MutableMap<String, String> = HashMap()
    private val paramsMap: MutableMap<String, String> = HashMap()
    private val headerParamsMap: MutableMap<String, String> = HashMap()
    private val headerLinesList: MutableList<String> = ArrayList()

    private lateinit var builder: Builder
    private var dynamicHeaderOrParams: IDynamicHeaderOrParams? = null

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        dynamicHeaderOrParams?.invoke(builder)

        val request = chain.request()
        val requestBuilder = request.newBuilder()

        // process header params inject
        val headerBuilder = request.headers().newBuilder()
        if (headerParamsMap.isNotEmpty()) {
            for ((key, value) in headerParamsMap) {
                headerBuilder.add(key, value)
            }
        }
        if (headerLinesList.size > 0) {
            for (line in headerLinesList) {
                headerBuilder.add(line)
            }
        }
        requestBuilder.headers(headerBuilder.build())
        // process header params end


        // process queryParams inject whatever it's GET or POST
        if (queryParamsMap.isNotEmpty()) {
            injectParamsIntoUrl(request, requestBuilder, queryParamsMap)
        }
        // process queryParams end


        // process post body inject
        if (POST == request.method() && canInjectIntoBody(request)) {
            val formBodyBuilder = FormBody.Builder()
            if (paramsMap.isNotEmpty()) {
                for ((key, value) in paramsMap) {
                    formBodyBuilder.add(key, value)
                }
            }
            val formBody: RequestBody = formBodyBuilder.build()
            var postBodyString = bodyToString(request.body())
            postBodyString += (if (postBodyString.isNotEmpty()) "&" else "") + bodyToString(formBody)
            val mediaType = MediaType.parse("application/x-www-form-urlencoded;charset=UTF-8")
            requestBuilder.post(RequestBody.create(mediaType, postBodyString))
        } else {
            // can't inject into body, then inject into url
            injectParamsIntoUrl(request, requestBuilder, paramsMap)
        }


        // 对请求添加头部或参数，处理好返回原始的响应
        return chain.proceed(requestBuilder.build())
    }

    /**
     * 确认是否是 post 请求
     *
     * @param request 发出的请求
     * @return true 需要注入公共参数
     */
    private fun canInjectIntoBody(request: Request?): Boolean {
        if (request == null) {
            return false
        }
        if (POST != request.method()) {
            return false
        }
        val body = request.body()
        return if (body == null) {
            false
        } else {
            val mediaType = body.contentType()
            if (mediaType == null) {
                false
            } else {
                "x-www-form-urlencoded" == mediaType.subtype()
            }
        }
    }

    /**
     * 将参数注入 url
     */
    private fun injectParamsIntoUrl(request: Request, requestBuilder: Request.Builder, paramsMap: Map<String, String>) {
        val httpUrlBuilder = request.url().newBuilder()
        if (paramsMap.isNotEmpty()) {
            for ((key, value) in paramsMap) {
                httpUrlBuilder.addQueryParameter(key, value)
            }
        }
        requestBuilder.url(httpUrlBuilder.build())
    }

    private fun bodyToString(request: RequestBody?): String {
        return try {
            val buffer = Buffer()
            if (request != null) {
                request.writeTo(buffer)
            } else {
                return ""
            }
            buffer.readUtf8()
        } catch (e: IOException) {
            "did not work"
        }
    }

    class Builder {
        private var interceptor: RequestHeaderOrParamsInterceptor = RequestHeaderOrParamsInterceptor()

        /**
         * 添加公共请求参数， POST 添加到请求体中，  GET 拼接到 url 中
         */
        fun addParam(key: String, value: String): Builder {
            interceptor.paramsMap[key] = value
            return this
        }

        /**
         * 添加公共请求参数， POST 添加到请求体中，  GET 拼接到 url 中
         */
        fun addParamsMap(paramsMap: Map<String, String>): Builder {
            interceptor.paramsMap.putAll(paramsMap)
            return this
        }

        /**
         * 添加公共请求头， 以 key-value 形式
         */
        fun addHeaderParam(key: String, value: String): Builder {
            interceptor.headerParamsMap[key] = value
            return this
        }

        /**
         * 添加公共请求头，  以 map 形式
         */
        fun addHeaderParamsMap(headerParamsMap: Map<String, String>): Builder {
            interceptor.headerParamsMap.putAll(headerParamsMap)
            return this
        }

        /**
         * 添加公共请求头，  直接以字符串形式
         */
        fun addHeaderLine(headerLine: String): Builder {
            val index = headerLine.indexOf(":")
            require(index != -1) { "Unexpected header: $headerLine" }
            interceptor.headerLinesList.add(headerLine)
            return this
        }

        /**
         * 添加公共请求头，  以字符串列表形式
         */
        fun addHeaderLinesList(headerLinesList: List<String>): Builder {
            for (headerLine in headerLinesList) {
                val index = headerLine.indexOf(":")
                require(index != -1) { "Unexpected header: $headerLine" }
                interceptor.headerLinesList.add(headerLine)
            }
            return this
        }

        /**
         * 添加公共查询参数，以 key-value 形式，无论 POST、GET 都拼接到 url 中
         */
        fun addQueryParam(key: String, value: String): Builder {
            interceptor.queryParamsMap[key] = value
            return this
        }

        /**
         * 添加公共查询参数，以 map 形式，无论 POST、GET 都拼接到 url 中
         */
        fun addQueryParamsMap(queryParamsMap: Map<String, String>): Builder {
            interceptor.queryParamsMap.putAll(queryParamsMap)
            return this
        }

        /**
         * 添加动态请求参数，可以回调调用其他设置请求头或参数的方法
         *
         * 但与直接调用其他方法的不同点之处在于：
         *      其他方法调用设置后的请求头或参数在每次拦截处理请求时，值和第一次设置有关，不会进行动态改变
         *      在本回调中调用其他设置请求头或参数方法后，每次拦截处理请求时都会调用触发，因此可以得到动态的请求头或者参数，并且会将之前设置的进行覆盖
         */
        fun addDynamicHeaderOrParams(dynamicHeaderOrParams: IDynamicHeaderOrParams): Builder {
            interceptor.dynamicHeaderOrParams = dynamicHeaderOrParams
            return this
        }

        fun build(): RequestHeaderOrParamsInterceptor {
            interceptor.builder = this
            return interceptor
        }

    }
}

/**
 * 动态添加公共请求头或请求参数
 *
 * @param headerInterceptorBuilder 添加动态参数的 builder
 */
typealias IDynamicHeaderOrParams = (headerInterceptorBuilder: RequestHeaderOrParamsInterceptor.Builder) -> Unit
