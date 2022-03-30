package com.hl.api

import android.util.Log
import com.hl.api.interceptor.RequestHeaderOrParamsInterceptor
import okhttp3.ConnectionSpec
import okhttp3.OkHttpClient
import okhttp3.TlsVersion
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.security.SecureRandom
import java.security.cert.X509Certificate
import java.util.concurrent.TimeUnit
import javax.net.ssl.*

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
        isPrintLog: Boolean = true,
        noinline publicHeaderOrParamsBlock: RequestHeaderOrParamsInterceptor.Builder.() -> Unit = {},
        noinline okHttpBuilderBlock: OkHttpClient.Builder.() -> Unit = {},
    ): T {
        return if (apiInterFace == null || apiInterFace !is T) {
            Retrofit.Builder()
                .baseUrl(baseUrl)
                .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .client(createClient(isPrintLog, publicHeaderOrParamsBlock, okHttpBuilderBlock))
                .build()
                .create(T::class.java).also {
                    apiInterFace = it
                }
        } else apiInterFace as T
    }


    fun createClient(
        isPrintLog: Boolean,
        publicHeaderOrParamsBlock: RequestHeaderOrParamsInterceptor.Builder.() -> Unit = {},
        okHttpBuilderBlock: OkHttpClient.Builder.() -> Unit = {}
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .connectTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .sslSocketFactory(getSSLSocketFactory(), createX509TrustManager())
            .hostnameVerifier(getHostnameVerifier())
            // .connectionSpecs(getConnectionSpecs())
            //错误重连
            .retryOnConnectionFailure(true)
            .apply {
                okHttpBuilderBlock()

                // 公共请求头、请求参数拦截器添加在日志拦截器之前, okhttp 是按添加顺序进行拦截的
                val headerBuilder: RequestHeaderOrParamsInterceptor.Builder = RequestHeaderOrParamsInterceptor.Builder()
                    .addHeaderParam("Content-Type", "application/json;charset=UTF-8")
                addInterceptor(headerBuilder.apply(publicHeaderOrParamsBlock).build())

                if (isPrintLog) {
                    //添加retrofit log拦截器, 打印日志
                    val loggingInterceptor = HttpLoggingInterceptor(object : HttpLoggingInterceptor.Logger {
                        override fun log(message: String) {
                            Log.d("Retrofit", "HttpLogging: $message")
                        }
                    }).apply {
                        this.level = HttpLoggingInterceptor.Level.BODY
                    }

                    /**
                     * 网络拦截器, 位于CallServerInterceptor之前，属于倒数第二个拦截器,处在发送数据的前一刻，以及收到数据的第一刻。
                     * 这么敏感的位置，决定了通过这个拦截器可以看到更多的信息
                     *      请求之前: OkHttp处理之后的请求报文数据，比如增加了各种header之后的数据。
                     *      请求之后: OkHttp处理之前的响应报文数据，比如解压缩之前的数据。
                     */
                    addNetworkInterceptor(loggingInterceptor)
                }
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
            val sslContext = SSLContext.getInstance("SSL")
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
        return HostnameVerifier { s, sslSession -> //未真正校检服务器端证书域名
            true
        }
    }

    private fun getConnectionSpecs(): List<ConnectionSpec> {
        val spec: ConnectionSpec = ConnectionSpec.Builder(ConnectionSpec.COMPATIBLE_TLS)
            .tlsVersions(TlsVersion.TLS_1_2, TlsVersion.TLS_1_1, TlsVersion.TLS_1_0)
            .allEnabledCipherSuites()
            .build() //解决在Android5.0版本以下https无法访问
        val spec1: ConnectionSpec = ConnectionSpec.Builder(ConnectionSpec.CLEARTEXT).build() //兼容http接口

        return listOf(spec, spec1)
    }
}