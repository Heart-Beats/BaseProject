package com.youma.api.interceptor

import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
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
        dynamicHeaderOrParams?.onDynamic(builder)

        val request = chain.request()
        val requestBuilder = request.newBuilder()

        // process header params inject
        val headerBuilder = request.headers.newBuilder()
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
        if (POST == request.method && canInjectIntoBody(request)) {
            val formBodyBuilder = FormBody.Builder()
            if (paramsMap.isNotEmpty()) {
                for ((key, value) in paramsMap) {
                    formBodyBuilder.add(key, value)
                }
            }
            val formBody: RequestBody = formBodyBuilder.build()
            var postBodyString = bodyToString(request.body)
            postBodyString += (if (postBodyString.isNotEmpty()) "&" else "") + bodyToString(formBody)
            requestBuilder.post(
                postBodyString
                    .toRequestBody("application/x-www-form-urlencoded;charset=UTF-8".toMediaTypeOrNull())
            )
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
        if (POST != request.method) {
            return false
        }
        val body = request.body
        return if (body == null) {
            false
        } else {
            val mediaType = body.contentType()
            if (mediaType == null) {
                false
            } else {
                "x-www-form-urlencoded" == mediaType.subtype
            }
        }
    }

    /**
     * 将参数注入 url
     */
    private fun injectParamsIntoUrl(request: Request, requestBuilder: Request.Builder, paramsMap: Map<String, String>) {
        val httpUrlBuilder = request.url.newBuilder()
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

        fun addParam(key: String, value: String): Builder {
            interceptor.paramsMap[key] = value
            return this
        }

        fun addParamsMap(paramsMap: Map<String, String>): Builder {
            interceptor.paramsMap.putAll(paramsMap)
            return this
        }

        fun addHeaderParam(key: String, value: String): Builder {
            interceptor.headerParamsMap[key] = value
            return this
        }

        fun addHeaderParamsMap(headerParamsMap: Map<String, String>): Builder {
            interceptor.headerParamsMap.putAll(headerParamsMap)
            return this
        }

        fun addHeaderLine(headerLine: String): Builder {
            val index = headerLine.indexOf(":")
            require(index != -1) { "Unexpected header: $headerLine" }
            interceptor.headerLinesList.add(headerLine)
            return this
        }

        fun addHeaderLinesList(headerLinesList: List<String>): Builder {
            for (headerLine in headerLinesList) {
                val index = headerLine.indexOf(":")
                require(index != -1) { "Unexpected header: $headerLine" }
                interceptor.headerLinesList.add(headerLine)
            }
            return this
        }

        fun addQueryParam(key: String, value: String): Builder {
            interceptor.queryParamsMap[key] = value
            return this
        }

        fun addQueryParamsMap(queryParamsMap: Map<String, String>): Builder {
            interceptor.queryParamsMap.putAll(queryParamsMap)
            return this
        }

        fun addDynamicHeaderOrParams(impl: IDynamicHeaderOrParams): Builder {
            interceptor.dynamicHeaderOrParams = impl
            return this
        }

        fun build(): RequestHeaderOrParamsInterceptor {
            interceptor.builder = this
            return interceptor
        }

    }

    interface IDynamicHeaderOrParams {
        /**
         * 动态添加公共请求头或请求参数
         *
         * @param headerInterceptorBuilder 添加动态参数的 builder
         */
        fun onDynamic(headerInterceptorBuilder: Builder)
    }
}
