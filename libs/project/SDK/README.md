# SDK 集成模块文档

## 模块概述

SDK 模块集成了各种第三方服务和功能组件，为应用提供完整的生态系统支持。所有 SDK 都经过封装，提供统一的接口。

## 模块列表

```
SDK/
├── ActivityResult/              # ActivityResult API 封装
├── Banner/                      # 轮播图组件（ViewPager2 + Banner）
├── BitmapUtil/                  # 位图工具（Base64互转、View截图）
├── Camera/                      # 相机功能（JCameraView 封装）
├── DateUtil/                    # 日期工具（格式化、农历、时间计算）
├── Download/                    # 文件下载（EasyHttp 封装）
├── ImageLoad/                   # 图片加载（Glide 封装）
├── JsonUtil/                    # JSON 处理（Gson 封装）
├── MMKVSharedPreferences/       # 高性能存储（MMKV + SharedPreferences 兼容）
├── MimeType/                    # 文件类型识别（MIME 类型枚举）
├── Navigation/                  # Navigation 组件优化（add/hide 策略）
├── Pay/                         # 支付服务（微信 + 支付宝）
├── Permission/                  # 权限请求（PermissionX 封装）
├── PictureSelector/             # 图片选择（LuckPicture 封装）
├── Popup/                       # 弹窗组件（XPopup 封装）
├── PreviewFile/                 # 文件预览（腾讯 X5 内核）
├── QRCode/                      # 二维码生成/扫描（ZXingLite）
├── Share/                       # 系统分享（文本、文件）
├── SmsUtil/                     # 短信发送
├── TencentCloud/                # 腾讯云对象存储（COS）
├── Umeng/                       # 友盟服务（统计、推送、三方登录、分享）
├── UniMP/                       # 小程序支持（DCloud UniMPSDK）
├── update/                      # 应用更新（APK 下载安装）
├── VideoPlayer/                 # 视频播放（GSYVideoPlayer）
├── ViewBinding/                 # ViewBinding 工具（泛型自动推导）
├── Web/                         # WebView 模块（JsBridge 通信）
├── XLogInit/                    # 日志初始化（XLog 封装）
├── LocalAAR/                    # 本地 AAR 依赖（UniMP 相关）
└── Shadow/                      # 插件化框架（腾讯 Shadow）
    ├── shadow-lib/              # 常量定义
    ├── shadow-init/             # 核心初始化与插件管理器
    ├── plugin-aidl/             # AIDL 接口定义
    └── plugin-manager/          # 插件管理器 APK 构建
```

## 核心模块

### Pay — 支付模块

集成微信支付和支付宝支付，通过 `PaymentHelper` 提供统一支付接口。

**主要类**：`PaymentHelper`、`PayResultCallBack`、`WxPayResponse`

```kotlin
// 微信支付
PaymentHelper.startWeChatPay(activity, configAppId, wxPayResponse, payResultCallBack)

// 支付宝支付
PaymentHelper.startAliPay(activity, configAppId, aliPayOrderInfo, payResultCallBack)
```

> 详细文档见 [Pay/README.md](Pay/README.md)

### Umeng — 友盟服务模块

集成友盟 SDK，包括应用统计、推送服务、三方社交登录（QQ/微信/微博）和社交分享功能。

**主要类**：`UMInitUtil`、`UMAuthUtil`、`UMShareUtil`、`SharePlatformParam`

```kotlin
// 预初始化
UMInitUtil.preInitUM(context) {
    isLogEnabled = BuildConfig.DEBUG
    appKey = "your_umeng_app_key"
    secretKey = "your_umeng_secret"
    channel = "official"
}

// 正式初始化
UMInitUtil.initUM(context, pushRegisterCallback)

// 三方登录授权
UMAuthUtil.toShareAppAuthGetInfo(activity, SHARE_MEDIA.WEIXIN, authListener)

// 分享
UMShareUtil.shareUMWebWithPlatform(activity, sharePlatformParam, shareListener)
```

> 详细文档见 [Umeng/README.md](Umeng/README.md)

### TencentCloud — 腾讯云对象存储

集成腾讯云 COS SDK，提供文件上传和下载的统一接口。

**主要类**：`TencentCosUtil`、`TransferListener`、`CredentialProvider`

```kotlin
TencentCosUtil.init(context, secretId, secretKey, regionName)
TencentCosUtil.uploadFile(bucketName, cosPath, srcPath, transferListener = listener)
TencentCosUtil.downloadFile(context, bucketName, cosPath, fileName, saveDir, transferListener = listener)
```

> 详细文档见 [TencentCloud/README.md](TencentCloud/README.md)

### UniMP — 小程序模块

集成 DCloud Uni 小程序 SDK，提供小程序初始化、WGT 包安装、打开/关闭、生命周期管理和原生通信等能力。

**主要类**：`UniMPHelper`

```kotlin
UniMPHelper.initUniMP(context) { /* DCSDKInitConfig 配置 */ }
UniMPHelper.openUniMPFromWgt(context, appid, wgtPathConfig, openArguments)
UniMPHelper.isExistsApp(appid)
UniMPHelper.setUniMPOnCloseCallBack(closeCallBack)
UniMPHelper.setOnUniMPEventCallBack(eventCallBack)
```

> 详细文档见 [UniMP/README.md](UniMP/README.md)

### Shadow — 插件化框架模块

基于腾讯 Shadow 框架，提供 Android 应用插件化宿主接入的完整解决方案。

**主要类**：`Shadow`、`ShadowConstants`

```kotlin
Shadow.initShadowLog(LogLevel.DEBUG) { level, message, t -> }
Shadow.getMyPluginManager(context)          // 单 Loader
Shadow.getMultiPluginManager(context, name) // 多 Loader
Shadow.initDynamicPluginManager(apkPath)    // 动态 APK
Shadow.getDynamicPluginManager()
```

> 详细文档见 [Shadow/README.md](Shadow/README.md)

### Web — WebView 模块

对 Android WebView 的深度封装，基于 JsBridge 提供完整的 H5 与原生通信方案。预置了 18+ 个标准 JS 桥接方法。

**主要类**：`WebViewFragment`、`IStandSdk`、`ISdkImplProvider`

```kotlin
// 打开 WebView 页面
navigateToWeb("https://www.example.com", title = "示例页面")
WebViewFragment.startNewPage(this, url, title, isNeedTitle = true)

// 注册自定义 H5 桥接方法
JsBridgeHelper.setISdkImplProvider(object : ISdkImplProvider() {
    override fun provideProjectSdkImpl(fragment, bridgeWebView): ISdk {
        return object : ISdk { /* 自定义桥接方法 */ }
    }
})
```

> 详细文档见 [Web/README.md](Web/README.md)

### Navigation — 导航组件优化模块

对 Android Jetpack Navigation 组件的优化改造，使用 `add()`/`hide()` 替代原生 `replace()` 策略，保留 Fragment 状态。

**主要类**：`MyNavHostFragment`、`MyFragmentNavigator`、`NavAnimations`

```kotlin
navHostFragment.setCommonNavAnimations {
    enterAnim = R.anim.slide_in_right
    exitAnim = R.anim.slide_out_left
    popEnterAnim = R.anim.slide_in_left
    popExitAnim = R.anim.slide_out_right
}
```

> 详细文档见 [Navigation/README.md](Navigation/README.md)

## 基础功能模块

### XLogInit — 日志初始化

基于 XLog 提供应用的日志系统初始化和配置能力。

**主要类**：`XLogInitUtil`、`XLogInitConfig`

```kotlin
XLogInitUtil.init {
    tagName = "MyApp"
    isPrintLog = BuildConfig.DEBUG
    filePrinter = XLogUtil.getFilePrinter(logFolderPath, DateFileNameGenerator())
}
xlogD("Main", "页面加载完成")
```

> 详细文档见 [XLogInit/README.md](XLogInit/README.md)

### MMKVSharedPreferences — 高性能存储

基于腾讯 MMKV 提供高性能键值存储，完全兼容 SharedPreferences API。

**主要函数**：`sharedPreferences()`、`putObject()`、`getObject()`

```kotlin
val sp = sharedPreferences("user", isUseMMKV = true)
sp.edit().putString("token", "xxx").apply()
sp.edit().putObject("user", User("张三", 25)).apply()
val user = sp.getObject<User>("user")
```

> 详细文档见 [MMKVSharedPreferences/README.md](MMKVSharedPreferences/README.md)

### ImageLoad — 图片加载

基于 Glide 封装，提供简化的图片加载 API。

**主要类**：`GlideUtil`

```kotlin
GlideUtil.load(context, url, imageView)
GlideUtil.loadHead(context, avatarUrl, imageView, isCircle = true)
GlideUtil.load(context, url, imageView, placeholderResId = R.drawable.placeholder, roundPx = 8)
GlideUtil.loadUrl2Bitmap(context, url) { bitmap -> }
```

> 详细文档见 [ImageLoad/README.md](ImageLoad/README.md)

### JsonUtil — JSON 处理

基于 Gson 提供简化的 JSON 序列化/反序列化工具。

**主要类**：`GsonUtil`

```kotlin
val json = user.toJson()
val user = json.fromJson<User>()
val isValid = """{"key":"value"}""".isJson()
```

> 详细文档见 [JsonUtil/README.md](JsonUtil/README.md)

### Permission — 权限请求

基于 PermissionX 封装，提供 Fragment/Activity 的权限请求扩展函数。

```kotlin
reqPermissions(Manifest.permission.CAMERA) { /* 已授权 */ }

reqPermissions(
    Manifest.permission.CAMERA,
    needExplainRequestReason = { deniedList, scope ->
        scope.showRequestReasonDialog(deniedList, "需要相机权限", "允许", "拒绝")
    },
    deniedAction = { deniedList -> toast("权限被拒绝: $deniedList") }
) { toast("所有权限已授权") }
```

> 详细文档见 [Permission/README.md](Permission/README.md)

### ViewBinding — ViewBinding 工具

提供 ViewBinding 的便捷创建与获取工具，支持泛型自动推导绑定类型。

```kotlin
// Activity
private val binding by binding<ActivityMyBinding>()

// Fragment
private val binding by inflate<FragmentMyBinding>()

// ViewHolder
class VH(parent: ViewGroup) : BindingViewHolder<ItemUserBinding>(parent)
```

> 详细文档见 [ViewBinding/README.md](ViewBinding/README.md)

### ActivityResult — Activity 结果处理

封装 Android Activity Result API，提供简化的 `startActivityForResult` 替代方案。

```kotlin
val helper = ActivityResultHelper(this)
helper.launchActivity(DetailActivity::class.java, callback = object : OnActivityResult {
    override fun onResultOk(data: Intent?) { }
})
```

> 详细文档见 [ActivityResult/README.md](ActivityResult/README.md)

## 工具与功能模块

### Download — 文件下载

基于 EasyHttp 封装文件下载功能，支持进度监听、生命周期绑定和 MD5 缓存跳过。

```kotlin
DownloadFileUtil.startDownLoad(viewLifecycleOwner, fileUrl,
    fileName = "report.pdf", isSave2AppDir = true,
    listener = object : OnDownloadListener {
        override fun onDownloadProgressChange(file: File?, progress: Int) { }
        override fun onDownloadSuccess(file: File?) { }
        override fun onDownloadFail(file: File?, e: Exception?) { }
    })
DownloadFileUtil.stopDownload(fileUrl)
DownloadFileUtil.stopAllDownload()
```

> 详细文档见 [Download/README.md](Download/README.md)

### Banner — 轮播图

基于 ViewPager2 + Banner 库封装轮播图组件。

```kotlin
binding.banner.initAdvertBanner(
    localFragment = this,
    adLayoutId = R.layout.item_banner,
    bannerIndicator = AdsIndicator(context),
    onBannerClickListener = { item, pos -> navigateTo(item.adsFlowUrl) }
)
```

> 详细文档见 [Banner/README.md](Banner/README.md)

### BitmapUtil — 位图工具

提供 Bitmap 与 Base64 互转、View 截图、从 URL 获取 Bitmap 等功能。

```kotlin
val bitmap = BitmapUtil.getBitmapFromUrl("https://example.com/img.jpg")
val base64 = Base64BitmapUtil.bitmapToBase64(bitmap)
val screenshot = someView.toBitmap()
```

> 详细文档见 [BitmapUtil/README.md](BitmapUtil/README.md)

### Camera — 相机功能

基于 JCameraView 封装拍照和录像功能，支持仅拍照、仅录像和两者兼备三种模式。

```kotlin
MyCaptureActivity.start(this, CaptureFeature.ONLY_CAPTURE, REQUEST_CODE_CAMERA)

// onActivityResult 中获取结果
val filePath = data?.getStringExtra(MyCaptureActivity.CAPTURE_FILE_PATH)
```

> 详细文档见 [Camera/README.md](Camera/README.md)

### DateUtil — 日期工具

提供日期格式化、农历转换、时间计算、Calendar 操作等。

```kotlin
now.toFormatString()                    // "2024-01-15 14:30:00"
now.differHours(targetDate)             // 3.5
Calendar.getInstance().toLunarString()  // "腊月初五"
LunarUtil.getConstellation(10, 24)      // "天蝎座"
```

> 详细文档见 [DateUtil/README.md](DateUtil/README.md)

### MimeType — 文件类型识别

提供文件扩展名与 MIME 类型的双向映射。

```kotlin
val type = MimeType.getByExtension(".apk")  // MimeType.APK
MimeType.JPEG.isImage()                     // true
```

> 详细文档见 [MimeType/README.md](MimeType/README.md)

### PictureSelector — 图片选择

基于 LuckPicture 封装拍照和相册选择功能。

```kotlin
PickImageUtil.startTakePhoto(this,
    option = { isEnableCrop = true; cropImageWideHigh = 300 to 300 },
    onSelectCancel = {},
    onSelectResult = { paths -> })

PickImageUtil.startPictureSelect(this,
    option = { maxSelectNum = 9 },
    onSelectCancel = {},
    onSelectResult = { paths -> })
```

> 详细文档见 [PictureSelector/README.md](PictureSelector/README.md)

### Popup — 弹窗组件

基于 XPopup 封装，提供便捷的弹窗创建和图片预览功能。

```kotlin
BottomDialogFragment().showPop {
    hasStatusBarShadow = true
    dismissOnTouchOutside = true
}
context.showImages(imageView, 0, listOf("url1", "url2"))
```

> 详细文档见 [Popup/README.md](Popup/README.md)

### PreviewFile — 文件预览

基于腾讯 X5 WebView 内核，提供常见办公文档、图片、视频等文件的在线预览。

```kotlin
X5Helper.initX5(context, isPrintLog = BuildConfig.DEBUG)
PreviewFileActivity.start(context, fileName = "report.docx", url = "https://example.com/report.docx")
```

> 详细文档见 [PreviewFile/README.md](PreviewFile/README.md)

### QRCode — 二维码

集成二维码/条形码的生成、解析和扫描功能。

```kotlin
val qrBitmap = QRCodeUtil.createQRCode("https://example.com", 400)
val scanner = QRScanUtil(this)
scanner.launchDefault(
    scanCancelAction = { },
    scanResultAction = { result -> })
```

> 详细文档见 [QRCode/README.md](QRCode/README.md)

### Share — 分享

提供系统分享功能的封装。

```kotlin
ShareUtil.shareText(this, "分享一段文字")
ShareUtil.shareFile(this, "/sdcard/download/report.pdf")
OpenFileUtil.openFileByPath(this, "/sdcard/download/image.jpg")
```

> 详细文档见 [Share/README.md](Share/README.md)

### SmsUtil — 短信发送

封装 Android 短信发送功能。

```kotlin
val smsHelper = SmsHelper(this)
smsHelper.sendMessageInBackground("13800138000", "您的验证码：123456") { isSuccess ->
    if (isSuccess) { /* 发送成功 */ } else { /* 发送失败 */ }
}
```

> 详细文档见 [SmsUtil/README.md](SmsUtil/README.md)

### Update — 应用更新

封装应用内下载 APK 并安装的功能。

```kotlin
UpdateHelper.downloadApk(
    context = this,
    apkUrl = "https://example.com/app-v2.0.apk",
    title = "发现新版本 v2.0",
    content = "修复了已知问题，优化性能",
    uiConfig = { /* UI 配置 */ },
    updateConfig = { isShowNotification = true },
    observer = object : DownloadApkObserver {
        override fun onDownload(progress: Int) { }
        override fun onFinish() { }
    }
)
```

> 详细文档见 [update/README.md](update/README.md)

### VideoPlayer — 视频播放

基于 GSYVideoPlayer 封装视频播放。

```kotlin
binding.videoPlayer.initPlayer(
    lifecycleOwner = viewLifecycleOwner,
    url = "https://example.com/video.mp4",
    videoName = "示例视频", needTitle = true) {
    setCacheWithPlay(true)
}
```

> 详细文档见 [VideoPlayer/README.md](VideoPlayer/README.md)

## 依赖配置

所有 SDK 模块均发布至 **Maven Central**，GroupId 为 `io.github.heart-beats.baseproject`，版本 `0.0.4-SNAPSHOT`。

```groovy
dependencies {
    // 按需集成
    implementation 'io.github.heart-beats.baseproject:pay:0.0.4-SNAPSHOT'
    implementation 'io.github.heart-beats.baseproject:umeng:0.0.4-SNAPSHOT'
    implementation 'io.github.heart-beats.baseproject:navigation:0.0.4-SNAPSHOT'
    // ... 更多模块见各子模块 README
}
```

## 相关文档

### 核心功能模块
- [Pay 支付模块](Pay/README.md)
- [Umeng 友盟服务](Umeng/README.md)
- [TencentCloud 腾讯云存储](TencentCloud/README.md)
- [UniMP 小程序](UniMP/README.md)
- [Shadow 插件化框架](Shadow/README.md)
- [Web WebView 模块](Web/README.md)
- [Navigation 导航组件](Navigation/README.md)

### 基础功能模块
- [XLogInit 日志初始化](XLogInit/README.md)
- [MMKVSharedPreferences 高性能存储](MMKVSharedPreferences/README.md)
- [ImageLoad 图片加载](ImageLoad/README.md)
- [JsonUtil JSON 处理](JsonUtil/README.md)
- [Permission 权限请求](Permission/README.md)
- [ViewBinding ViewBinding 工具](ViewBinding/README.md)
- [ActivityResult Activity 结果处理](ActivityResult/README.md)

### 工具与功能模块
- [Download 文件下载](Download/README.md)
- [Banner 轮播图](Banner/README.md)
- [BitmapUtil 位图工具](BitmapUtil/README.md)
- [Camera 相机功能](Camera/README.md)
- [DateUtil 日期工具](DateUtil/README.md)
- [MimeType 文件类型识别](MimeType/README.md)
- [PictureSelector 图片选择](PictureSelector/README.md)
- [Popup 弹窗组件](Popup/README.md)
- [QRCode 二维码](QRCode/README.md)
- [Share 分享](Share/README.md)
- [SmsUtil 短信发送](SmsUtil/README.md)
- [update 应用更新](update/README.md)
- [VideoPlayer 视频播放](VideoPlayer/README.md)
- [PreviewFile 文件预览](PreviewFile/README.md)
