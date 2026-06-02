# base-api — 网络请求模块

## 模块概述

`base-api` 是基于 Retrofit 进行二次封装的网络请求模块，提供统一的 HTTP 客户端创建、多域名支持、公共请求头/参数注入、网络监控、异常处理等完整能力。

**模块坐标**: `com.hl.api`
**发布 GroupId**: `io.github.heart-beats.baseproject`

## 目录结构

```
api/
├── build.gradle
└── src/main/java/com/hl/api/
    ├── RetrofitManager.kt                  # Retrofit 管理器（核心入口）
    ├── PublicResp.kt                       # 通用响应体封装
    ├── convert/
    │   ├── ConverterFormat.kt              # 转换格式（JSON/XML/Protobuf/String）
    │   └── SmartConverterFactory.kt        # 智能转换器工厂
    ├── interceptor/
    │   ├── HttpLoggingInterceptor.kt       # 日志拦截器
    │   ├── LogProxy.kt                     # 日志代理接口
    │   ├── MultiBaseUrlInterceptor.kt      # 多域名拦截器
    │   └── RequestHeaderOrParamsInterceptor.kt  # 公共请求头/参数拦截器
    ├── event/
    │   ├── ApiEvent.kt                     # API 事件（成功/失败）
    │   └── IApiEventProvider.kt            # API 事件提供者接口
    ├── error/
    │   └── ExceptionHandler.kt             # 统一异常处理器
    ├── launcher/
    │   ├── ApiLauncher.kt                  # API 启动器接口
    │   ├── ApiLauncherAction.kt            # API 启动动作
    │   └── ext/_Call.kt, _suspend.kt       # Call/suspend 扩展
    ├── monitor/
    │   ├── NetMonitor.kt                   # 网络监控 EventListener
    │   ├── NetMonitorCallback.kt           # 监控回调
    │   ├── MonitorResult.kt                # 监控结果数据类
    │   └── NetworkUtils.java               # 网络工具
    └── utils/
        └── _HttpUtil.kt                    # HTTP 工具扩展
```

## 依赖关系

### 外部依赖
| 依赖 | 用途 |
|------|------|
| `com.squareup.retrofit2:retrofit` | HTTP 客户端核心 |
| `com.squareup.retrofit2:converter-gson` | Gson 转换器 |
| `com.squareup.retrofit2:converter-jaxb` | JAXB 转换器 |
| `com.squareup.retrofit2:converter-protobuf` | Protobuf 转换器 |
| `com.squareup.retrofit2:converter-scalars` | Scalars 转换器 |
| `com.squareup.retrofit2:adapter-rxjava3` | RxJava3 适配器 |
| `com.squareup.okhttp3:okhttp` | HTTP 引擎 |
| `com.github.fengzhizi715:okhttp-logging-interceptor` | HTTP 日志（自定义版本） |
| `org.jetbrains.kotlinx:kotlinx-coroutines-android` | Kotlin 协程 |

### 内部依赖
无内部模块依赖，是最底层基础模块。

## 对外接口

### RetrofitManager

```kotlin
object RetrofitManager {
    // 核心方法：构建 Retrofit 接口实例
    inline fun <reified T> buildRetrofit(
        baseUrl: String,
        logProxy: LogProxy = ...,
        noinline publicHeaderOrParamsBlock: RequestHeaderOrParamsInterceptor.Builder.() -> Unit = {},
        noinline okHttpBuilderBlock: OkHttpClient.Builder.() -> Unit = {}
    ): T
}
```

### PublicResp

```kotlin
open class PublicResp<T> {
    open var code: String?
    open var msg: String?
    @SerializedName("data") open var respBody: T?
    fun code(): String
    fun message(): String
}
```

### MultiBaseUrlInterceptor

```kotlin
class MultiBaseUrlInterceptor(
    val onDomainGetBaseUrl: (domainName: String) -> String
) : Interceptor
```

### RequestHeaderOrParamsInterceptor

```kotlin
class RequestHeaderOrParamsInterceptor {
    class Builder {
        fun addParam(key: String, value: String): Builder
        fun addParamsMap(paramsMap: Map<String, String>): Builder
        fun addHeaderParam(key: String, value: String): Builder
        fun addHeaderParamsMap(headerParamsMap: Map<String, String>): Builder
        fun addHeaderLine(headerLine: String): Builder
        fun addQueryParam(key: String, value: String): Builder
        fun addQueryParamsMap(queryParamsMap: Map<String, String>): Builder
        fun addDynamicHeaderOrParams(dynamicBlock: IDynamicHeaderOrParams): Builder
        fun build(): RequestHeaderOrParamsInterceptor
    }
}
typealias IDynamicHeaderOrParams = (Builder, Request) -> Unit
```

### ExceptionHandler

```kotlin
object ExceptionHandler {
    fun handleException(throwable: Throwable?): ResponseThrowable
    fun isRequestExceptionByCode(errorCode: Int): Boolean
    class ResponseThrowable(var code: Int, throwable: Throwable?) : Exception(throwable)
}
```

### ApiLauncher

```kotlin
interface ApiLauncher : IApiEventProvider {
    fun <BODY, Resp : PublicResp<BODY>> (suspend CoroutineScope.() -> Resp)
        .launchAction(action: ApiLauncherAction<BODY>.() -> Unit)
    fun <BODY, Resp : PublicResp<BODY>> (suspend CoroutineScope.() -> Resp)
        .launch(onFail: ..., onSuccess: ...)
}
```

### NetMonitor

```kotlin
class NetMonitor(
    context: Context,
    netMonitorCallback: NetMonitorCallback,
    openLog: Boolean = false
) : EventListener()
```

## 构建与测试

```bash
# 构建模块
./gradlew :base-api:assemble

# 发布到本地 Maven
./gradlew :base-api:publishToMavenLocal

# 运行 lint 检查
./gradlew :base-api:lint
```

## 使用示例

### 基础使用

```kotlin
// 1. 创建网络接口实例
val apiService = RetrofitManager.buildRetrofit<WanAndroidApi>(
    baseUrl = "https://www.wanandroid.com/",
    logProxy = object : LogProxy {
        override fun log(message: String) {
            XLog.d("HTTP", message)
        }
    }
)

// 2. 定义 API 接口
interface WanAndroidApi {
    @GET("banner/json")
    suspend fun getBannerList(): PublicResp<List<BannerData>>
}

// 3. 发起请求
viewModelScope.launch {
    val resp = apiService.getBannerList()
    if (resp.code() == "0") {
        // 处理成功，数据在 resp.respBody 中
    }
}
```

### 多域名支持

```kotlin
// 接口上加 Header 声明域名
interface MultiDomainApi {
    @Headers("Domain-Name: wanandroid")
    @GET("banner/json")
    suspend fun getBannerList(): PublicResp<List<BannerData>>
    
    @Headers("Domain-Name: github")
    @GET("users/{username}")
    suspend fun getUserInfo(@Path("username") username: String): PublicResp<User>
}

// 创建拦截器并注入
val multiBaseUrlInterceptor = MultiBaseUrlInterceptor { domainName ->
    when (domainName) {
        "wanandroid" -> "https://www.wanandroid.com/"
        "github" -> "https://api.github.com/"
        else -> ""
    }
}

val api = RetrofitManager.buildRetrofit<MultiDomainApi>(
    baseUrl = "https://www.wanandroid.com/",
    okHttpBuilderBlock = {
        addInterceptor(multiBaseUrlInterceptor)
    }
)
```

### 自定义公共请求头

```kotlin
val api = RetrofitManager.buildRetrofit<MyApi>(
    baseUrl = "https://api.example.com/",
    publicHeaderOrParamsBlock = {
        addHeaderParam("Authorization", "Bearer $token")
        addParam("platform", "android")
        addParam("version", BuildConfig.VERSION_NAME)
    },
    okHttpBuilderBlock = {
        connectTimeout(30, TimeUnit.SECONDS)
    }
)
```

### 网络请求监控

```kotlin
val netMonitor = NetMonitor(context, object : NetMonitorCallback {
    override fun onSuccess(call: Call, monitorResult: MonitorResult) {
        XLog.d("Network", "请求成功: ${monitorResult.url}, 耗时: ${monitorResult.callCoat}ms")
    }
    override fun onError(call: Call, monitorResult: MonitorResult, ioe: Exception) {
        XLog.e("Network", "请求失败: ${ioe.message}")
    }
})
```

### 异常处理

```kotlin
try {
    val data = api.getData()
} catch (e: Exception) {
    val responseThrowable = ExceptionHandler.handleException(e)
    when (responseThrowable.code) {
        ExceptionHandler.ERROR.NETWORK_ERROR -> showNetError()
        ExceptionHandler.ERROR.TIMEOUT_ERROR -> showTimeout()
        ExceptionHandler.ERROR.UNAUTHORIZED -> reLogin()
        else -> showError(responseThrowable.message)
    }
}
```
