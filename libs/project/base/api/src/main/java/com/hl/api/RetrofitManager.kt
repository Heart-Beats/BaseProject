package com.hl.api

import android.util.Log
import com.hl.api.convert.SmartConverterFactory
import com.hl.api.interceptor.HttpLoggingInterceptor
import com.hl.api.interceptor.LogProxy
import com.hl.api.interceptor.RequestHeaderOrParamsInterceptor
import okhttp3.ConnectionSpec
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import java.security.SecureRandom
import java.security.cert.X509Certificate
import java.util.concurrent.TimeUnit
import javax.net.ssl.HostnameVerifier
import javax.net.ssl.SSLContext
import javax.net.ssl.SSLSocketFactory
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

/**
 * @author  张磊  on  2021/11/02 at 16:27
 * Email: 913305160@qq.com
 */
object RetrofitManager {

    var apiInterFace: Any? = null

    /**
     * 根据泛型参数创建相应的请求接口
     *
     *  baseUrl 整合的规则如下：
     *
     *     1. path 是绝对路径的形式：
     *          path = “/apath”， baseUrl = “http://host:port/a/b”  | “http://host:port/a/b/”
     *          Url = “http://host:port/apath”
     *
     *    2. path 是相对路径，baseUrl 是目录形式： ------------------------> 推荐使用形式，所拼即所得
     *          path = “apath”，baseUrl = “http://host:port/a/b/”
     *          Url = “http://host:port/a/b/apath”
     *
     *    3. path 是相对路径，baseUrl 是文件形式：
     *          path = “apath”，baseUrl = “http://host:port/a/b”
     *          Url = “http://host:port/a/apath”
     *
     *    4. path 是完整的 Url：                ------------------------> 可用来更改单个接口的请求地址
     *         path = “http://host:port/aa/apath“，baseUrl = “http://host:port/a/b”
     *         Url = “http://host:port/aa/apath”
     *
     */
    inline fun <reified T> buildRetrofit(
        baseUrl: String,
        logProxy: LogProxy = object : LogProxy {
            override fun d(tag: String, msg: String) {
                Log.d(tag, msg)
            }

            override fun e(tag: String, msg: String) {
                Log.e(tag, msg)
            }

            override fun i(tag: String, msg: String) {
                Log.i(tag, msg)
            }

            override fun w(tag: String, msg: String) {
                Log.w(tag, msg)
            }
        },
        noinline publicHeaderOrParamsBlock: RequestHeaderOrParamsInterceptor.Builder.() -> Unit = {},
        noinline okHttpBuilderBlock: OkHttpClient.Builder.() -> Unit = {},
    ): T {
        return if (apiInterFace == null || apiInterFace !is T) {
            Retrofit.Builder()
                .baseUrl(baseUrl)
                .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
                .addConverterFactory(SmartConverterFactory.create())
                .client(createClient(logProxy, publicHeaderOrParamsBlock, okHttpBuilderBlock))
                .build()
                .create<T>().also {
                    apiInterFace = it
                }
        } else apiInterFace as T
    }


    fun createClient(
        logProxy: LogProxy,
        publicHeaderOrParamsBlock: RequestHeaderOrParamsInterceptor.Builder.() -> Unit = {},
        okHttpBuilderBlock: OkHttpClient.Builder.() -> Unit = {}
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .connectTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .connectionSpecs(getConnectionSpecs())
            .sslSocketFactory(getSSLSocketFactory(), createX509TrustManager())
            .hostnameVerifier(getHostnameVerifier())
            //错误重连
            .retryOnConnectionFailure(true)
            .apply {
                okHttpBuilderBlock()

                // 公共请求头、请求参数拦截器添加在日志拦截器之前, okhttp 是按添加顺序进行拦截的
                val headerBuilder: RequestHeaderOrParamsInterceptor.Builder = RequestHeaderOrParamsInterceptor.Builder()
                    .addHeaderParam("Content-Type", "application/json;charset=UTF-8")
                addInterceptor(headerBuilder.apply(publicHeaderOrParamsBlock).build())

                //添加 retrofit log 拦截器, 打印日志
                val loggingInterceptor = HttpLoggingInterceptor
                    .build(logProxy, requestTag = "Retrofit-Request", responseTag = "Retrofit-Response")

                /**
                 * 网络拦截器, 位于 CallServerInterceptor 之前，属于倒数第二个拦截器,处在发送数据的前一刻，以及收到数据的第一刻。
                 * 这么敏感的位置，决定了通过这个拦截器可以看到更多的信息
                 *      请求之前: OkHttp 处理之后的请求报文数据，比如增加了各种 header 之后的数据。
                 *      请求之后: OkHttp 处理之前的响应报文数据，比如解压缩之前的数据。
                 */
                addNetworkInterceptor(loggingInterceptor)
            }.build()
    }

    //获取TrustManager
    private fun createX509TrustManager(): X509TrustManager {
        //不校检证书链
        return object : X509TrustManager {
            override fun checkClientTrusted(chain: Array<X509Certificate>, authType: String) {
                //不校检客户端证书
            }

            override fun checkServerTrusted(chain: Array<X509Certificate>, authType: String) {
                //不校检服务器证书
            }

            override fun getAcceptedIssuers(): Array<X509Certificate> {
                return arrayOf()
                //OKhttp3.0以前返回 null,3.0 以后返回 new X509Certificate[]{};
            }
        }
    }


    //通过这个类可以获得 SSLSocketFactory，这个东西就是用来管理证书和信任证书的
    private fun getSSLSocketFactory(): SSLSocketFactory {
        return try {
            val sslContext = SSLContext.getInstance("TLS")
            sslContext.init(null, getTrustManagerArray(), SecureRandom())
            sslContext.socketFactory
        } catch (e: Exception) {
            throw RuntimeException(e)
        }
    }

    //获取TrustManager
    private fun getTrustManagerArray(): Array<TrustManager> {
        //不校检证书链
        return arrayOf(createX509TrustManager())
    }

    //获取Hostname Verifier
    private fun getHostnameVerifier(): HostnameVerifier {
        return HostnameVerifier { _, _ -> //未真正校检服务器端证书域名
            true
        }
    }

    private fun getConnectionSpecs(): List<ConnectionSpec> {
        //解决在Android5.0版本以下https无法访问
        val spec: ConnectionSpec = ConnectionSpec.Builder(ConnectionSpec.COMPATIBLE_TLS)
            .allEnabledCipherSuites()
            .build()

        //兼容http接口
        val httpConnectionSpec = ConnectionSpec.CLEARTEXT

        return listOf(spec, httpConnectionSpec)
    }
}