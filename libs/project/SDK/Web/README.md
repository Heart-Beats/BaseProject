# Web — WebView 模块

## 模块概述

`web` 是对 Android WebView 的深度封装，基于 JsBridge 提供完整的 H5 与原生通信方案。实现了 `IStandSdk` 标准接口，预置了设备信息、导航、文件预览、图片选择、扫码、分享等 18+ 个标准 JS 桥接方法。采用 SDK 可插拔设计，业务方可注入自定义 H5 功能。

**模块坐标**: `com.hl.web`
**发布 GroupId**: `io.github.heart-beats.baseproject`

## 目录结构

```
Web/
├── build.gradle.kts
└── src/main/java/com/hl/web/
    ├── WebViewFragment.kt              # WebView Fragment 基类（核心入口）
    ├── WebViewFragmentArgs.kt          # 页面参数
    ├── H5Constants.kt                  # H5 常量
    ├── sdk/                            # JS Bridge SDK 层
    │   ├── ISdk.kt                     # SDK 方法模板
    │   ├── IStandSdk.kt                # 标准 H5 桥接方法定义
    │   ├── IStandSdkImpl.kt            # 标准方法的默认实现
    │   ├── ISdkRegister.kt             # 桥接注册接口
    │   ├── ISdkHandlerProxy.kt         # 桥接处理器代理（日志+错误处理）
    │   └── ISdkImplProvider.kt         # SDK 实现提供者（扩展点）
    ├── client/
    │   ├── MyBridgeWebViewClient.kt    # 自定义 WebViewClient
    │   └── MyWebChromeClient.kt        # 自定义 ChromeClient（文件选择、地理位置）
    ├── widgets/
    │   └── ProgressBridgeWebView.kt    # 带进度条的 BridgeWebView
    ├── pool/
    │   └── WebViewPoolManager.kt       # WebView 对象池（预热+复用）
    ├── helpers/
    │   ├── JsBridgeHelper.kt           # JS Bridge 注册
    │   ├── H5DataHelper.kt             # H5 数据缓存
    │   └── _CallBackFunction.kt        # 回调函数扩展
    ├── receiver/
    │   ├── CallBackFunctionDataStore.kt
    │   └── CallBackFunctionHandlerReceiver.kt
    ├── providers/
    │   └── WebFileProvider.kt          # ContentProvider（预加载 WebView 池）
    ├── bean/                           # H5 桥接数据模型（20+ 文件）
    └── utils/
        ├── _ActivityExt.kt
        ├── ClipboardHelper.kt          # 剪贴板工具
        ├── ReflectHelper.kt            # 反射工具
        ├── _Proxy.kt                   # 代理工具
        ├── _ThreadUtil.kt
        └── _UriUtil.kt
```

## 依赖关系

### 外部依赖
| 依赖 | 用途 |
|------|------|
| `com.github.lzyzsd:jsbridge` | Android JsBridge |
| `com.tencent.tbs:tbssdk` (X5) | 腾讯 X5 WebView 内核 |

### 内部依赖
| 模块 | 关系 |
|------|------|
| `base-ui` | 依赖 —— Activity/Fragment 基类 |
| `umeng` | 依赖 —— 分享功能 |
| `picture-selector` | 依赖 —— 图片预览/选择 |
| `download` | 依赖 —— 文件下载 |
| `qrcode` | 依赖 —— 二维码扫描 |
| `preview-file` | 依赖 —— 文件预览 |
| `permission` | 依赖 —— 权限请求 |
| `popup` | 依赖 —— 弹窗组件 |

## 对外接口

### WebViewFragment

```kotlin
open class WebViewFragment : ViewBindingBaseFragment<HlWebFragmentWebViewBinding>() {
    companion object {
        fun startNewPage(currentFragment: Fragment, url: String, title: String? = null, isNeedTitle: Boolean = false)
        fun startNewPage(context: Context, url: String, title: String? = null, isNeedTitle: Boolean = false)
    }
    fun reload()
    fun loadUrl(url: String)
    
    // 可重写的钩子方法
    protected open fun getWebViewClient(): MyBridgeWebViewClient
    protected open fun getWebChromeClient(args: WebViewFragmentArgs): MyWebChromeClient
    open fun WebSettings.initWebSetting()
    open fun onNativeRequestLoading(view: WebView?, uri: Uri): Boolean
}
```

### 导航扩展

```kotlin
fun Fragment.navigateToWeb(url: String, title: String? = null, isNeedTitle: Boolean = false)
fun Context.navigateToWeb(url: String, title: String? = null, isNeedTitle: Boolean = false)
fun View.navigateToWeb(url: String, title: String? = null, isNeedTitle: Boolean = false)
```

### IStandSdk（标准 H5 桥接方法，18+ 个）

```kotlin
interface IStandSdk : ISdk {
    fun getDeviceInfo(...)      // 获取设备信息
    fun navigateBack(...)       // 返回上一页
    fun navigateTo(...)         // 跳转新页面
    fun setH5Data(...)          // 存储 H5 数据
    fun getH5Data(...)          // 读取 H5 数据
    fun clearH5Data(...)        // 清除 H5 数据
    fun redirectTo(...)         // 重定向
    fun reLaunch(...)           // 重新启动
    fun setStatusBarLightMode(...)  // 设置状态栏
    fun setStatusBarColor(...)      // 设置状态栏颜色
    fun getNetworkConnectType(...)  // 获取网络类型
    fun setWebView(...)         // 设置 WebView 属性
    fun getLocation(...)        // 获取位置
    fun previewImage(...)       // 预览图片
    fun savePhotoToAlbum(...)   // 保存图片到相册
    fun callPhone(...)          // 拨打电话
    fun downloadFile(...)       // 下载文件
    fun previewFile(...)        // 预览文件
    fun scanQRCode(...)         // 扫描二维码
    fun share2Platform(...)     // 分享到社交平台
}
```

### 扩展标准 SDK（ISdkImplProvider）

```kotlin
abstract class ISdkImplProvider {
    open fun provideUserAgent(): String = "JsBridge"
    open fun provideStandSdkImpl(webViewFragment: Fragment, bridgeWebView: BridgeWebView): ISdk
    abstract fun provideProjectSdkImpl(webViewFragment: Fragment, bridgeWebView: BridgeWebView): ISdk
}
```

## 构建与测试

```bash
# 构建模块
./gradlew :web:assemble

# 发布到本地 Maven
./gradlew :web:publishToMavenLocal
```

## 使用示例

### 打开 WebView 页面

```kotlin
// 在 Fragment 中
navigateToWeb("https://www.example.com", title = "示例页面")
WebViewFragment.startNewPage(this, "https://www.example.com", "示例页面", isNeedTitle = true)

// 在 Activity 中
navigateToWeb("https://www.example.com")
```

### 注册自定义 H5 桥接方法

```kotlin
class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        
        // 设置自定义 SDK 实现提供者
        JsBridgeHelper.setISdkImplProvider(object : ISdkImplProvider() {
            override fun provideProjectSdkImpl(
                webViewFragment: Fragment,
                bridgeWebView: BridgeWebView
            ): ISdk {
                return object : ISdk {
                    fun getUserInfo(handlerName: String = sdkFunTemplate()) {
                        val data = """
                            {"name": "张三", "userId": "123"}
                        """.trimIndent()
                        bridgeWebView.callHandler(handlerName, data) { response ->
                            Log.d("JSBridge", "getUserInfo 回调: $response")
                        }
                    }
                    
                    fun setTitle(handlerName: String = sdkFunTemplate()) {
                        // 由框架自动注入 data 参数解析
                    }
                }
            }
        })
    }
}
```

### H5 端调用原生方法

```javascript
// H5 端通过 JsBridge 调用原生
window.WebViewJavascriptBridge.callHandler(
    'getDeviceInfo',         // 方法名（对应 IStandSdk 方法名）
    {},                      // 参数
    function(response) {     // 回调
        console.log(JSON.parse(response));
    }
);

// 或使用 H5 SDK 自定义方法
window.WebViewJavascriptBridge.callHandler(
    'getUserInfo',
    {},
    function(response) {
        var user = JSON.parse(response);
        console.log(user.userId);
    }
);
```

### H5 数据缓存

```kotlin
// 原生端存储
H5DataHelper.putData("token", "xxx-token-value")
H5DataHelper.putData("userId", "123")

// H5 端读取（通过 getH5Data 桥接方法）
window.WebViewJavascriptBridge.callHandler('getH5Data', 
    { key: 'token' },
    function(response) {
        var res = JSON.parse(response);
        if (res.status === 'success') {
            console.log('token:', res.data);
        }
    }
);
```

### WebView 池预热

```kotlin
// WebFileProvider 已在 Manifest 中声明，会自动在 Application 启动时预热
// 手动预热
WebViewPoolManager.prepare(context)

// 获取预热的 WebView
val webView = WebViewPoolManager.obtain(context)

// 回收 WebView
WebViewPoolManager.recycle(webView)
```
