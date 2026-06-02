# Umeng — 友盟服务模块

## 模块概述

`umeng` 集成了友盟 SDK，包括应用统计、推送服务、三方社交登录（QQ/微信/微博）和社交分享功能。通过 `UMInitUtil`、`UMAuthUtil`、`UMShareUtil` 三个工具类提供简化调用。

**模块坐标**: `com.hl.umeng`
**发布 GroupId**: `io.github.heart-beats.baseproject`

## 目录结构

```
Umeng/
├── build.gradle
└── src/main/java/com/hl/umeng/
    ├── sdk/
    │   ├── UMInitUtil.kt              # 初始化工具
    │   ├── UMAuthUtil.kt              # 三方登录授权
    │   ├── UMShareUtil.kt             # 三方分享
    │   ├── MyUMShareListener.kt       # 分享监听基类
    │   ├── MyUMAuthListener.kt        # 授权监听基类
    │   └── SharePlatformParam.kt      # 分享参数
    ├── wxapi/
    │   └── WXEntryActivity.kt         # 微信回调 Activity
    └── providers/
        └── UmengFileProvider.kt       # 友盟 FileProvider
```

## 依赖关系

### 外部依赖
| 依赖 | 用途 |
|------|------|
| `com.umeng.umsdk:*` | 友盟基础 SDK |
| `com.umeng.umsdk:share-wechat/qq/sina` | 分享 SDK |
| `com.umeng.umsdk:push-*` | 推送 SDK |
| `com.umeng.umsdk:apm` | 性能监测 |

### 内部依赖
无内部模块依赖。

## 对外接口

### UMInitUtil

```kotlin
object UMInitUtil {
    // 预初始化（在 Application.onCreate 中尽早调用）
    fun preInitUM(context: Context, umInitBuilderBlock: UMInitBuilder.() -> Unit)

    // 正式初始化（异步）
    fun initUM(context: Context, pushRegisterCallback: UPushRegisterCallback,
               pushAgentBlock: PushAgent.() -> Unit = {})
}

class UMInitBuilder {
    var isLogEnabled = false
    var appKey = ""
    var secretKey = ""
    var channel = "Umeng"
}
```

### UMAuthUtil

```kotlin
object UMAuthUtil {
    // 发起三方登录授权
    @JvmStatic fun toShareAppAuthGetInfo(activity: Activity, action: SHARE_MEDIA, listener: MyUMAuthListener)

    // 删除授权信息
    @JvmStatic fun deleteOauth(activity: Activity, action: SHARE_MEDIA, listener: MyUMAuthListener)

    // 检查是否安装目标应用
    fun isInstall(activity: Activity, action: SHARE_MEDIA): Boolean
}
```

### UMShareUtil

```kotlin
object UMShareUtil {
    // 分享 Web 链接到指定平台
    fun shareUMWebWithPlatform(activity: Activity, sharePlatformParam: SharePlatformParam,
                               shareListener: MyUMShareListener = ...)

    // 通用分享（自定义内容）
    fun shareWithPlatform(activity: Activity, platform: SHARE_MEDIA,
                          shareListener: MyUMShareListener = ...,
                          shareContentAction: ShareAction.() -> Unit)

    // 弹出分享面板
    fun shareUMWeb2PlatformsWithBoard(activity: Activity, sharePlatformParam: SharePlatformParam,
                                      shareListener: MyUMShareListener = ...)

    fun share2PlatformsWithBoard(activity: Activity, vararg sharePlatforms: SHARE_MEDIA,
                                 shareListener: MyUMShareListener = ...,
                                 shareContentAction: ShareAction.() -> Unit)
}
```

### SharePlatformParam

```kotlin
data class SharePlatformParam(
    var title: String = "",              // 分享标题
    var description: String = "",        // 分享描述
    var link: String = "",               // 分享链接
    var coverUrl: String = "",           // 分享封面图
    var platform: SHARE_MEDIA = SHARE_MEDIA.WEIXIN,
    var sharePlatforms: List<SHARE_MEDIA> = listOf(SHARE_MEDIA.WEIXIN)
)
```

## 构建与测试

```bash
# 构建模块
./gradlew :umeng:assemble

# 发布到本地 Maven
./gradlew :umeng:publishToMavenLocal
```

## 配置指南

### AndroidManifest 配置

```xml
<!-- 微信回调 Activity（登录、分享共用） -->
<activity
    android:name=".wxapi.WXEntryActivity"
    android:configChanges="keyboardHidden|orientation|screenSize"
    android:exported="true"
    android:theme="@android:style/Theme.Translucent.NoTitleBar" />

<!-- FileProvider（分享必需） -->
<provider
    android:name="com.hl.umeng.providers.UmengFileProvider"
    android:authorities="${applicationId}.fileprovider"
    android:exported="false"
    android:grantUriPermissions="true">
    <meta-data
        android:name="android.support.FILE_PROVIDER_PATHS"
        android:resource="@xml/hl_res_public_file_paths" />
</provider>
```

### WXEntryActivity 创建（必做）

在项目包名目录下创建 `wxapi/WXEntryActivity.kt`：

```kotlin
package your.package.name.wxapi

import com.hl.umeng.wxapi.WXEntryActivity

class WXEntryActivity : WXEntryActivity()
```

## 使用示例

### 初始化

```kotlin
class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        
        // 1. 预初始化（尽早调用）
        UMInitUtil.preInitUM(this) {
            isLogEnabled = BuildConfig.DEBUG
            appKey = "your_umeng_app_key"
            secretKey = "your_umeng_secret"
            channel = "official"
        }
        
        // 2. 正式初始化
        UMInitUtil.initUM(this, object : UPushRegisterCallback {
            override fun onSuccess(registerId: String) {
                XLog.d("友盟推送注册成功: $registerId")
            }
            override fun onFailure(errCode: String, errDesc: String) {
                XLog.e("友盟推送注册失败: $errCode $errDesc")
            }
        })
        
        // 3. 配置三方平台 Key
        PlatformConfig.setWeixin("wx_app_key", "wx_secret")
        PlatformConfig.setQQZone("qq_app_key", "qq_secret")
        PlatformConfig.setWXFileProvider("${BuildConfig.APPLICATION_ID}.fileprovider")
        PlatformConfig.setQQFileProvider("${BuildConfig.APPLICATION_ID}.fileprovider")
    }
}
```

### 三方登录

```kotlin
UMAuthUtil.toShareAppAuthGetInfo(activity, SHARE_MEDIA.WEIXIN, object : MyUMAuthListener() {
    override fun onSuccess(platform: SHARE_MEDIA, data: UMAuthInfo) {
        val uid = data.uid
        val name = data.name
        val avatar = data.iconurl
        // 使用授权信息进行服务端登录
        userLoginWithAuth(uid, name, avatar)
    }
    
    override fun onError(platform: SHARE_MEDIA, action: Int, error: String) {
        toast("登录失败: $error")
    }
    
    override fun onCancel(platform: SHARE_MEDIA, action: Int) {
        toast("登录已取消")
    }
})
```

### 分享到微信

```kotlin
UMShareUtil.shareUMWebWithPlatform(activity, SharePlatformParam(
    title = "分享标题",
    description = "分享描述内容",
    link = "https://www.example.com",
    coverUrl = "https://www.example.com/cover.png",
    platform = SHARE_MEDIA.WEIXIN
), object : MyUMShareListener() {
    override fun onResult(platform: SHARE_MEDIA) {
        toast("分享成功")
    }
    override fun onError(platform: SHARE_MEDIA, error: Throwable) {
        toast("分享失败: ${error.message}")
    }
    override fun onCancel(platform: SHARE_MEDIA) {
        toast("分享已取消")
    }
})
```
