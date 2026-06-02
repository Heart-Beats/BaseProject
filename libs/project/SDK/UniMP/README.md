# UniMP — 小程序模块

## 模块概述

`uni-mp` 集成了 DCloud Uni 小程序 SDK（DCUniMPSDK），通过 `UniMPHelper` 提供小程序初始化、WGT 包安装、打开/关闭、生命周期管理和原生通信等完整能力。

**模块坐标**: `com.hl.unimp`
**发布 GroupId**: `io.github.heart-beats.baseproject`

## 目录结构

```
UniMP/
├── build.gradle.kts
└── src/main/java/com/hl/unimp/
    └── UniMPHelper.kt                # Uni 小程序操作入口
```

## 依赖关系

### 外部依赖
| 依赖 | 用途 |
|------|------|
| `com.hl.uniapp:uniMPSDK-V2-release` (local AAR) | Uni 小程序核心 SDK |
| `com.hl.uniapp:uniapp-v8-release` (local AAR) | V8 引擎 |
| `com.hl.uniapp:base-oaid-sdk` (local AAR) | OAID SDK |

### 内部依赖（AAR 模块提供）

```
LocalAAR/
├── base-oaid-sdk-aar/              (:uniapp-base-oaid-sdk)
├── breakpad-build-release-aar/     (:uniapp-breakpad-build-release)
├── uniapp-v8-release-aar/          (:uniapp-v8-release)
└── uniMPSDK-V2-release-aar/        (:uniMPSDK-V2-release)
```

## 对外接口

### UniMPHelper

```kotlin
object UniMPHelper {
    // 判断是否为 UniMP 进程
    fun isUniMPProcess(context: Context): Boolean

    // 初始化 SDK（需在主进程中执行）
    fun initUniMP(context: Context, uniSDKInitConfigBuilder: DCSDKInitConfig.Builder.() -> Unit)

    // 安装并打开 WGT 小程序
    fun openUniMPFromWgt(
        context: Context,
        appid: String,
        uniMPReleaseConfigurationBlock: UniMPReleaseConfiguration.() -> Unit,
        openArgumentsBlock: UniMPOpenConfiguration.() -> Unit = {}
    )

    // 打开已存在的小程序（不执行安装）
    fun openUniMPCheckExistsApp(
        context: Context,
        appid: String,
        openArgumentsBlock: UniMPOpenConfiguration.() -> Unit = {}
    ): Boolean

    // 检查小程序是否已存在
    fun isExistsApp(appid: String): Boolean

    // 获取小程序基础路径
    fun getUniMPAppBasePath(context: Context): String

    // 获取小程序版本信息
    fun getAppVersionInfo(appid: String): JSONObject?

    // 设置菜单按钮回调
    fun setDefaultMenuButtonClickCallBack(callBack: IMenuButtonClickCallBack)
    fun setCapsuleCloseButtonClickCallBack(callBack: IDCUniMPOnCapsuleCloseButtontCallBack)
    fun setCapsuleMenuButtonClickCallBack(callBack: IDCUniMPOnCapsuleMenuButtontCallBack)

    // 设置关闭回调
    fun setUniMPOnCloseCallBack(callBack: IUniMPOnCloseCallBack)

    // 设置事件回调（小程序 → 原生通信）
    fun setOnUniMPEventCallBack(callBack: IOnUniMPEventCallBack)
}
```

## 构建与测试

```bash
# 构建模块
./gradlew :uni-mp:assemble

# 发布到本地 Maven
./gradlew :uni-mp:publishToMavenLocal
```

## 配置指南

### AndroidManifest（关键进程声明）

```xml
<!-- UniMP 需要单独的进程 -->
<activity
    android:name="io.dcloud.feature.sdk.MainActivity"
    android:process=":unimp" />
```

## 使用示例

### 初始化（在 Application 主进程中）

```kotlin
if (isMainProcess()) {
    UniMPHelper.initUniMP(this) {
        // UniMP SDK 初始化配置
    }
}
```

### 安装并打开 WGT 小程序

```kotlin
UniMPHelper.openUniMPFromWgt(
    context = this,
    appid = "__UNI__11E9B3A",
    uniMPReleaseConfigurationBlock = {
        // 设置 WGT 包路径
        wgtPath = "path/to/miniapp.wgt"
    },
    openArgumentsBlock = {
        // 打开参数
        path = "/pages/index/index"   // 启动页面路径
        extraData = JSONObject().apply {
            put("userId", "123")
            put("token", "xxx")
        }
    }
)
```

### 检查并打开已安装的小程序

```kotlin
if (UniMPHelper.isExistsApp("__UNI__11E9B3A")) {
    UniMPHelper.openUniMPCheckExistsApp(this, "__UNI__11E9B3A") {
        path = "/pages/detail/detail"
        extraData = JSONObject().apply {
            put("itemId", "456")
        }
    }
} else {
    toast("小程序未安装，请先下载")
}
```

### 注册关闭回调

```kotlin
UniMPHelper.setUniMPOnCloseCallBack(object : IUniMPOnCloseCallBack {
    override fun onClose(appid: String) {
        XLog.d("小程序 $appid 被关闭")
    }
})
```

### 原生与小程序通信

```kotlin
// 原生监听小程序事件
UniMPHelper.setOnUniMPEventCallBack(object : IOnUniMPEventCallBack {
    override fun onUniMPEventCallBack(
        appid: String, event: String, data: Any,
        callBackFunction: CallBackFunction
    ) {
        when (event) {
            "getUserInfo" -> {
                // 返回用户信息给小程序
                callBackFunction.onSuccess(userInfo)
            }
            "shareContent" -> {
                // 处理分享请求
                handleMiniappShare(data.toString())
            }
        }
    }
})
```
