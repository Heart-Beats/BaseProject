# utils — 工具类模块

## 模块概述

`utils` 是项目的综合工具类模块，提供设备信息、网络状态、时间处理、文件操作、崩溃捕获、反射代理、灰度模式、软键盘管理等超过 70 个工具函数和扩展方法。

**模块坐标**: `com.hl.utils`
**发布 GroupId**: `io.github.heart-beats.baseproject`

## 目录结构

```
utils/
├── build.gradle
└── src/main/java/com/hl/utils/
    ├── BaseUtil.kt                       # 全局 Application 持有
    ├── UtilsInitializer.kt               # AndroidX Startup 初始化器
    ├── MyCrashHandler.kt                 # 全局崩溃处理器
    ├── DeviceInfoUtil.kt                 # 设备信息获取
    ├── BuildVersionUtil.kt               # 构建版本工具
    ├── NetworkUtil.kt                    # 网络状态检测
    ├── MyNetworkCallback.kt              # 网络变化监听
    ├── GrayUtil.kt                       # 全局灰度模式
    ├── TimeUtil.kt                       # 时间计算与计时
    ├── FileUtil.kt                       # 文件工具
    ├── AppInstallUtil.kt                 # 应用安装工具
    ├── AppSizeUtil.kt                    # 应用大小计算
    ├── UrlParamUtil.kt                   # URL 参数处理
    ├── ResourceUtil.kt                   # 资源工具
    ├── PaletteUtil.kt                    # Palette 取色工具
    ├── OrientationSensorHelper.kt        # 方向传感器辅助
    ├── Base64Util.kt                     # Base64 编解码
    ├── DESUtil.kt                        # DES 加解密
    ├── _RSAUtil.kt                       # RSA 加解密
    ├── ClipboardHelper.kt                # 剪贴板工具
    ├── NotificationUtils.kt              # 通知工具
    ├── ReflectHelper.kt                  # 反射辅助
    ├── _Proxy.kt                         # 动态代理（含 ProxyHandler、MethodHook）
    ├── PackageInstallerUtil.kt           # APK 安装
    ├── aspectj/                          # AOP 切面编程
    │   ├── Aspect.kt                     # 切面定义
    │   └── _PointCuts.kt                # 切点定义
    ├── keyboard/                         # 软键盘相关
    │   ├── SoftKeyboardUtil.kt           # 软键盘工具
    │   ├── SoftKeyboardHeightProvider.kt # 键盘高度监听
    │   └── AndroidBug5497Workaround.kt   # Android 5497 Bug 适配
    ├── location/                         # 定位相关
    │   ├── GpsUtil.kt                    # GPS 状态检测
    │   ├── LocationUtil.kt              # 位置工具
    │   └── RequestOpenGPSPop.kt         # GPS 开启提示弹窗
    ├── media/                            # 媒体相关
    │   ├── AudioHelper.kt               # 音频播放辅助
    │   └── MediaPlayerHelper.kt         # 媒体播放辅助
    ├── span/                             # 文本 Span 相关
    │   ├── AlignMiddleImageSpan.kt      # 图文对齐 Span
    │   ├── CustomTypefaceSpan.kt        # 自定义字体 Span
    │   ├── LinkMovementMethodEx.kt      # 链接点击处理
    │   ├── MiddleIMarginImageSpan.kt    # 带边距图片 Span
    │   ├── SpanExt.kt                   # Span 扩展函数
    │   └── dsl/                         # Span DSL 构建器
    │       ├── DslSpanBuilder.kt
    │       ├── DslSpannableStringBuilder.kt
    │       └── ...
    ├── views/                            # View 扩展工具
    │   ├── TextViewLinesUtil.kt         # TextView 行数工具
    │   ├── _BottomNavigationView.kt     # BottomNavigation 扩展
    │   ├── _EditTextUtil.kt             # EditText 扩展
    │   ├── _ImageViewUtil.kt            # ImageView 扩展
    │   ├── _NestedScrollView.kt         # NestedScrollView 扩展
    │   ├── _RecyclerView.kt             # RecyclerView 扩展
    │   ├── _TextView.kt                 # TextView 扩展
    │   └── _View.kt                     # View 扩展
    ├── _ActionBar.kt                     # ActionBar 扩展
    ├── _AssetsUtil.kt                    # Assets 读取扩展
    ├── _Boolean.kt                       # Boolean 扩展
    ├── _BroadcastReceiver.kt            # 广播注册扩展
    ├── _ClassLoader.kt                   # ClassLoader 扩展
    ├── _ClickUtil.kt                     # 点击工具扩展
    ├── _CollectionUtil.kt               # 集合工具扩展
    ├── _Color.kt                         # 颜色工具扩展
    ├── _Context.kt                       # Context 扩展
    ├── _ConvertUtil.kt                   # 类型转换扩展
    ├── _CursorUtil.kt                    # Cursor 扩展
    ├── _DensityUtils.kt                  # 密度转换扩展
    ├── _LaunchHomeUtil.kt               # 回到桌面扩展
    ├── _LifecycleOwner.kt               # LifecycleOwner 扩展
    ├── _LiveDataUtils.kt                # LiveData 扩展（setSafeValue、onceFirstObserve 等）
    ├── _MoneyUtil.kt                     # 金额格式化扩展
    ├── _NavigationBarUtil.kt            # 导航栏扩展
    ├── _PackageUtil.kt                   # 包信息扩展
    ├── _ProcessUtils.kt                  # 进程工具扩展
    ├── _RegexUtil.kt                     # 正则工具扩展
    ├── _ResolveInfo.kt                   # ResolveInfo 扩展
    ├── _String.kt                        # 字符串扩展
    ├── _ThreadUtil.kt                    # 线程工具扩展
    └── _UriUtil.kt                       # URI 工具扩展
```

## 依赖关系

### 外部依赖
| 依赖 | 用途 |
|------|------|
| `androidx.lifecycle:lifecycle-extensions` | 生命周期感知 |
| `androidx.lifecycle:lifecycle-livedata-ktx` | LiveData KTX |
| `androidx.lifecycle:lifecycle-viewmodel-ktx` | ViewModel KTX |
| `androidx.palette:palette-ktx` | Palette 取色 |
| `com.google.android.material:material` | Material 组件 |
| `com.blankj:utilcodex` | 第三方工具库 |

### 内部依赖
| 模块 | 关系 |
|------|------|
| `base-app-res` | 依赖 —— 公共资源 |
| `activity-result` | 依赖 —— Activity 结果处理 |
| `xlog-init` | 依赖 —— XLog 日志 |
| `permission` | 依赖 —— 权限请求 |
| `popup` | 依赖 —— 弹窗组件 |
| `image-load` | 依赖 —— 图片加载 |

## 对外接口

### BaseUtil（全局 Application）

```kotlin
object BaseUtil {
    lateinit var app: Application
    fun init(app: Application)
}
```

### DeviceInfoUtil

```kotlin
object DeviceInfoUtil {
    fun getDeviceWidth(context: Context): Int
    fun getDeviceHeight(context: Context): Int
    @RequiresApi(Build.VERSION_CODES.O)
    fun getIMEIDeviceId(context: Context): String
    fun getDeviceManufacturer(): String       // e.g. "Xiaomi"
    fun getDeviceProduct(): String
    fun getDeviceBrand(): String
    fun getDeviceModel(): String              // e.g. "MI 11"
    fun getDeviceBoard(): String
    fun getDeviceName(): String
    fun getDeviceFingerPrint(): String
    fun getDeviceHardware(): String
    fun getDeviceDisplay(): String
    fun getDeviceId(): String
    fun getDeviceUser(): String
    fun getDeviceSDK(): Int
    fun getDeviceAndroidVersion(): String
    fun getDeviceDefaultLanguage(): String
    fun getDeviceSupportLanguage(): List<String>
    fun getEMUI(): String
    @RequiresApi(Build.VERSION_CODES.O)
    fun getDeviceAllInfo(context: Context): String
}

// 设备品牌枚举
enum class DeviceInfo(val manufacturer: String, val brandName: String) {
    HUA_WEI("huawei", "华为"),
    VIVO("vivo", "VIVO"),
    OPPO("oppo", "OPPO"),
    XIAO_MI("xiaomi", "小米"),
    SAMSUNG("samsung", "三星")
}
```

### NetworkUtil

```kotlin
object NetworkUtil {
    @RequiresPermission(Manifest.permission.ACCESS_NETWORK_STATE)
    fun isConnected(context: Context): Boolean
    fun isWifiConnected(context: Context): Boolean
    fun isMobileData(context: Context): Boolean
    fun getNetworkInfo(context: Context): Any?
}
```

### TimeUtil

```kotlin
object TimeUtil {
    fun calculateMills2HM(mills: Long): Pair<Int, Int>            // 毫秒 → 时:分
    fun calculateMills2HMS(mills: Long): Triple<Int, Int, Int>    // 毫秒 → 时:分:秒
    fun calculateCountTime2String(countTime: Long): String        // 秒数 → 格式化字符串
    fun calculateTotalMills(inputHours: Int, inputMinutes: Int): Long
    fun startTiming(lifecycleOwner: LifecycleOwner, startTime: Long = 0,
        interval: Int = 1000, action: (Long, String) -> Unit): Job
}
```

### GrayUtil（全局灰度模式）

```kotlin
object GrayUtil {
    fun apply2View(view: View, isGray: Boolean)
    fun apply2Fragment(fragment: Fragment, isGray: Boolean)
    fun apply2Activity(activity: Activity, isGray: Boolean)
    fun apply2Application(application: Application, isGray: Boolean)
}

// 扩展函数快捷方式
fun View.apply2Gray()
fun View.apply2CancelGray()
fun Fragment.apply2Gray()
fun Fragment.apply2CancelGray()
fun Activity.apply2Gray()
fun Activity.apply2CancelGray()
fun Application.apply2Gray()
fun Application.apply2CancelGray()
```

### 动态代理

```kotlin
class ProxyHandler<T : Any>(methodHook: MethodHook<T>? = null) : InvocationHandler {
    fun bind(target: T): T
}

open class MethodHook<T : Any> {
    open fun beforeHookedMethod(target: T, proxy: T, method: Method, args: Array<Any>)
    open fun onHookedMethod(target: T, proxy: T, method: Method, args: Array<Any>): Any?
    open fun afterHookedMethod(target: T, proxy: T, method: Method, args: Array<Any>)
}
```

### PackageInstallerUtil

```kotlin
object PackageInstallerUtil {
    fun installApp(context: Context, apkFilePath: String, packageName: String, callback: InstallCallback?)
    interface InstallCallback {
        fun onInstallResult(success: Boolean, message: String)
    }
}
```

### MyCrashHandler

```kotlin
object MyCrashHandler : UncaughtExceptionHandler {
    fun setOnPrintException(onPrintExceptionHandler: (stacktrace: String) -> Unit)
}
```

### LiveData 扩展

```kotlin
fun <T> LiveData<T>.onceFirstObserve(owner: LifecycleOwner, onChanged: (T) -> Unit)
fun <T> LiveData<T>.onceLastObserve(owner: LifecycleOwner, onChanged: (T) -> Unit)
fun <T> MutableLiveData<T>.setSafeValue(obj: T?)
```

## 构建与测试

```bash
# 构建模块
./gradlew :utils:assemble

# 发布到本地 Maven
./gradlew :utils:publishToMavenLocal
```

## 使用示例

### 设备信息

```kotlin
val info = buildString {
    append("品牌: ${DeviceInfoUtil.getDeviceBrand()}\n")
    append("型号: ${DeviceInfoUtil.getDeviceModel()}\n")
    append("系统: Android ${DeviceInfoUtil.getDeviceAndroidVersion()}\n")
    append("SDK: ${DeviceInfoUtil.getDeviceSDK()}\n")
}
```

### 网络状态检测

```kotlin
if (!NetworkUtil.isConnected(context)) {
    toast("网络不可用，请检查网络连接")
    return
}
```

### LiveData 安全赋值和单次监听

```kotlin
val liveData = MutableLiveData<String>()

// 安全赋值（确保在主线程）
liveData.setSafeValue("new value")

// 仅首次监听
liveData.onceFirstObserve(viewLifecycleOwner) { value ->
    XLog.d("首次收到数据: $value")
}
```

### 全局灰度模式（特殊节日）

```kotlin
// Application 级别启用灰度
application.apply2Gray()

// Activity 级别取消灰度
activity.apply2CancelGray()
```

### 崩溃日志捕获

```kotlin
MyCrashHandler.setOnPrintException { stacktrace ->
    XLog.e("Crash", stacktrace)
    // 可在此处上传崩溃日志
}
```

### 软键盘管理

```kotlin
// 监听软键盘高度变化
SoftKeyboardHeightProvider(view).addKeyboardListener { height ->
    XLog.d("键盘高度: $height")
}
```

### 动态代理

```kotlin
val proxy = ProxyHandler(object : MethodHook<MyInterface>() {
    override fun beforeHookedMethod(target: MyInterface, proxy: MyInterface, method: Method, args: Array<Any>) {
        XLog.d("调用前: ${method.name}")
    }
    override fun afterHookedMethod(target: MyInterface, proxy: MyInterface, method: Method, args: Array<Any>) {
        XLog.d("调用后: ${method.name}")
    }
})
val proxiedObj = proxy.bind(originalObj)
```
