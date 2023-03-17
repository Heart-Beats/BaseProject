# 项目基础框架

[TOC]



### 1. **基础框架组成**

<img src="https://raw.githubusercontent.com/Heart-Beats/Note-Pictures/main/images/image-20230303175646369.png" alt="image-20230303175646369" style="zoom:80%;" />

如图所示，主要包括以下几个模块：

- `base-api`：网络请求模块
- `base-app-res`：应用公共资源模块
- `base-arch`：基础架构模块
- `navigation`：Jetpack  Navigation 使用优化模块
- `SDK`：三方  SDK 接入模块 ， 其中目前集成以下 SDK
  <img src="https://raw.githubusercontent.com/Heart-Beats/Note-Pictures/main/images/image-20230314111313188.png" alt="image-20230314111313188" style="zoom: 80%;" />
  - `LocalAAR`：一些  SDK  所必需依赖的 AAR 包
  - `Pay` ：支付模块，目前支持微信与支付宝
  - `shadow`:  Shadow 插件化宿主接入模块
  - `TencentCloud`： 腾讯云对象存储模块
  - `Umeng`：友盟分享、推送、三方应用授权以及应用性能监测模块
  - `UniMP`：Uni 小程序接入模块，可使原生应用拥有小程序能力
- `utils`：工具类模块

其中相关模块之间的依赖关系如下：

![流程图.drawio](https://raw.githubusercontent.com/Heart-Beats/Note-Pictures/main/images/%E6%B5%81%E7%A8%8B%E5%9B%BE.drawio.png)

---



### 2. 各个模块介绍

介绍完基本组成之后，现在就来看一下各个模块的具体相关接入使用流程。


#### 2.1  `base-api`

基于 Retrofit 进行封装的接口请求模块，主要对外公开接口方法：`RetrofitManager.buildRetrofit(baseUrl, isPrintLog, publicHeaderOrParamsBlock, okHttpBuilderBlock)`

具体使用方式见  Demo 中的 [Repository.kt](https://github.com/Heart-Beats/BaseProject/blob/c30c6a69e05548a794d96670d298c3a9a9c1a38a/app/src/main/java/com/hl/baseproject/repository/Repository.kt#LL22C3-L22C3) 类，其中 `publicHeaderOrParamsBlock`  参数用来实现公共请求头的定制化，`okHttpBuilderBlock`  参数用来实现 `OkHttpClient`  的定制化。

在进行请求时，接口请求类会被单例缓存起来优化性能，只要  `buildRetrofit`  方法中的泛型类不改变，就不会多次创建接口对象。


除了上述外，该模块还内置实现了相关拦截器：

- `RequestHeaderOrParamsInterceptor`

  通过此拦截器可以实现添加公共请求头或者参数，并且提供了动态进行改变的方法
- `MultiBaseUrlInterceptor`
  可以借助此拦截器实现 `Retorfit` 的多域名请求，具体使用如下：

  ```kotlin
  ① 首先需要在请求接口上声明 Domain-Name 请求头信息
  @Headers("Domain-Name: wanandroid")
  suspend fun getTopBannerList(): WanAndroidPublicResp<List<BannerData>?>
  
  ② 然后实例化 MultiBaseUrlInterceptor，在回调中返回 Domain-Name 对应的域名，并添加到 OKHttp 的拦截器中即可
  val multiBaseUrlInterceptor = MultiBaseUrlInterceptor {
      if (it == "wanandroid") "https://www.wanandroid.com/" else ""
  }
  addInterceptor(multiBaseUrlInterceptor)
  ```



#### 2.2 `base-app-res`

应用公共资源模块，包括动画、Shape、图片、字体、颜色以及大小，没啥好说的


#### 2.3 `utils`

常用的相关 APP 工具类，这里也不作过多介绍，使用到相关功能时自取即可


#### 2.4 `SDK`

##### 2.4.1 `Pay`

本模块内置集成微信支付与支付宝支付 SDK，`PaymentHelper`  为对外使用类，该类中 `startWeChatPay()`  方法为进行微信支付，`startAliPay()`  方法为进行支付宝支付，

 支付完成后会有相应的结果回调通知：

```kotlin
interface PayResultCallBack {
    fun onResult(state: PayState, payResult: String)
}

enum class PayState {
    SUCCESS, FAILED, CANCEL
}
```

可在此回调中进行相应的结果处理，`payResult` 为相应平台 SDK 返回的支付结果的 Json 字符串。

除了上述以外，对于微信支付来说，还需要添加一个回调 Activity，这里只需要按如下方式声明即可：

```xml-dtd
<!-- 微信支付 -->
<activity-alias
    android:name=".wxapi.WXPayEntryActivity"
    android:exported="true"
    android:label="@string/app_name"
    android:launchMode="singleTop"
    android:targetActivity="com.hl.pay.weixin.wxapi.WXPayEntryActivity"
    android:taskAffinity="${applicationId}"
    android:theme="@android:style/Theme.Translucent.NoTitleBar" />
```

更多详细说明见官方文档：

- [微信支付](https://pay.weixin.qq.com/wiki/doc/apiv3/open/pay/chapter2_5_2.shtml)
- [支付包支付](https://opendocs.alipay.com/open/204/105296)



##### 2.4.2 `shadow`

该模块集成了 Shadow 插件化宿主接入的相关  SDK，可通过该模块借助 Shadow 快速实现原生应用插件化宿主的的接入工作。

Shadow 的相关介绍及应用的插件化改造这里不作介绍，可见 [**Shadow 框架的使用**](https://github.com/Heart-Beats/StudyNotes/blob/master/Android/%E4%B8%89%E6%96%B9%E5%BA%93/Shadow%20%E6%A1%86%E6%9E%B6%E7%9A%84%E4%BD%BF%E7%94%A8.md)





##### 2.4.3 `TencentCloud`

本模块目前集成了腾讯云的对象存储功能，通过提供的工具类可快速实现上传和下载文件对象。

`TencentCosUtil` 为对外提供直接调用的接口，使用时首先需要调用 `TencentCosUtil.init(...)` 对 COS SDK 进行初始化，

然后根据具体的场景调用 `TencentCosUtil.uploadFile(...)` 和 `TencentCosUtil.downloadFile(...)` 就可以实现文件的上传与下载，通过实现 `TransferListener` 该接口即可获取上传或下载过程中的相关状态，该接口定义如下：

```kotlin
interface TransferListener {

   /**
    * 传输进度
    */
   fun onTransferProgress(progress: Int) {}

   /**
    * 传输成功
    *
    * @param accessUrl  访问地址
    */
   fun onTransferSuccess(accessUrl: String) {}

   /**
    * 传输失败
    *
    * @param message 失败原因
    */
   fun onTransferFail(message: String?) {}

   /**
    * 传输状态
    *
    * @param transferState  传输状态
    */
   fun onTransState(transferState: TransferState) {}
}
```

相关的操作方法参数皆有注释，这里不作详细介绍，更多说明见官方文档：[**腾讯云对象存储快速入门**](https://cloud.tencent.com/document/product/436/12159)



##### 2.4.4 `Umeng`

本模块自动集成了友盟分享、推送、三方应用授权以及应用性能监测功能，同时有以下工具类帮助简化开发：

- `UMInitUtil`

  用来辅助预初始化友盟以及初始化友盟，简化相关流程，相关详细介绍见官方文档：[**友盟推送自动集成文档**](https://developer.umeng.com/docs/67966/detail/206987)
  

- `UMAuthUtil`
  通过该工具类可借助友盟实现三方社交 APP 登录授权，如：QQ、微信、新浪微博等， 相关详细介绍见官方文档：[**友盟三方登录集成文档**](https://developer.umeng.com/docs/143070/detail/171113)
  但需要注意的是，这些三方社交 APP 开发者平台认证的  key 需要自己去申请，同时还需要在调用相关授权之前先设置给友盟，代码如下：

  ```kotlin
  PlatformConfig.setWeixin(wxAppKey, wxSecretKey)     // 微信
  PlatformConfig.setQQZone(qqAppKey, qqSecretKey)     // QQ
  ```

  对于微信来说，还需要添加一个回调 Activity，具体为在当前项目包名目录下创建 wxapi 文件夹，新建一个名为`WXEntryActivity` 的 Activity 继承`WXCallbackActivity`， 无需任何操作如下即可：

  ```kotlin
  class WXEntryActivity : WXEntryActivity()
  ```

  同时还需要在 `AndroidManifest` 文件中声明如下：

  ```xml
  <!-- 微信登录、分享 -->
  <activity
      android:name=".wxapi.WXEntryActivity"
      android:configChanges="keyboardHidden|orientation|screenSize"
      android:exported="true"
      android:theme="@android:style/Theme.Translucent.NoTitleBar" />
  ```

  

- `UMShareUtil`
  通过该工具类可借助友盟实现分享数据到三方社交 APP，如：QQ、微信、新浪微博等， 相关详细介绍见官方文档：[**友盟三方分享集成文档**](https://developer.umeng.com/docs/128606/detail/193879#h2-u6743u9650u6DFBu52A08)
  但需要注意的是，这些三方社交 APP 开发者平台相关的  key 需要自己去申请，同时还需要在调用相关分享之前先设置给友盟，代码如下：

  ```kotlin
  //  微信设置
  PlatformConfig.setWeixin(wxAppKey, wxSecretKey)
  PlatformConfig.setWXFileProvider("${BuildConfig.APPLICATION_ID}.fileprovider")
  
  // QQ设置
  PlatformConfig.setQQZone(qqAppKey, qqSecretKey)
  PlatformConfig.setQQFileProvider("${BuildConfig.APPLICATION_ID}.fileprovider")
  ```

  这里与登录认证授权里设置的一个不同点就是多了个  `FileProvider` 的设置，因为分享会涉及到应用之间的数据共享，所以需要提供 Provider。

  上述完成之后，微信分享同样需要添加回调 Activity，添加方式和 `AndroidManifest` 声明都与登录认证一致，不再介绍。

  



##### 2.4.5 `UniMP`

本模块集成了 Uni 小程序 接入所必须的环境，同时也对 `DCUniMPSDK`  提供的相关 API 进行封装，便于使用，相关详细介绍见官方文档：[**Uni 小程序集成教程**](https://nativesupport.dcloud.net.cn/UniMPDocs/UseSdk/android.html)

`UniMPHelper`  为对外使用类，要使用 Uni 小程序，首先需要初始化 `DCUniMPSDK` ，调用 `UniMPHelper.initUniMP(...)` 方法即可，需要注意接入小程序后会使应用存在多个进程，因此==需要在主进程中执行该方法进行初始化==。

其他方法可见相关注释来在具体场景中进行使用，这里也不作介绍。
