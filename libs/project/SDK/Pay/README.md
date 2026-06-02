# Pay — 支付模块

## 模块概述

`pay` 集成了微信支付和支付宝支付 SDK，通过 `PaymentHelper` 提供统一支付接口，封装了订单签名、支付发起、结果回调等完整流程。

**模块坐标**: `com.hl.pay`
**发布 GroupId**: `io.github.heart-beats.baseproject`

## 目录结构

```
Pay/
├── build.gradle.kts
└── src/main/java/com/hl/pay/
    ├── PaymentHelper.kt                # 支付统一入口
    ├── PayResultCallBack.kt            # 支付结果回调接口
    ├── alipay/
    │   ├── AlipayConfig.kt             # 支付宝配置
    │   ├── AliPayThread.kt             # 支付宝支付线程
    │   └── util/
    │       ├── OrderInfoUtil2_0.kt     # 订单信息构建与签名
    │       └── SignUtils.kt            # RSA 签名工具
    └── weixin/
        ├── WxPayResponse.kt            # 微信支付订单信息
        └── wxapi/
            └── WXPayEntryActivity.kt   # 微信支付回调 Activity
```

## 依赖关系

### 外部依赖
| 依赖 | 用途 |
|------|------|
| `com.tencent.mm.opensdk:wechat-sdk-android` | 微信支付 SDK |
| `com.alipay.sdk:alipay-sdk` (local AAR) | 支付宝支付 SDK |

### 内部依赖
| 模块 | 关系 |
|------|------|
| `uikit-toast` | 依赖 —— Toast 提示 |

## 对外接口

### PaymentHelper

```kotlin
object PaymentHelper {
    lateinit var WX_APP_ID: String

    // 微信支付
    fun startWeChatPay(
        activity: Activity,
        configAppId: String,        // 微信 AppId
        wxPayResponse: WxPayResponse, // 订单信息
        payResultCallBack: PayResultCallBack
    )

    // 支付宝支付
    fun startAliPay(
        activity: Activity,
        configAppId: String,        // 支付宝 AppId
        aliPayOrderInfo: String,    // 签名后的订单信息
        payResultCallBack: PayResultCallBack
    )
}
```

### PayResultCallBack

```kotlin
interface PayResultCallBack {
    fun onResult(state: PayState, payResult: String)
}

enum class PayState {
    SUCCESS,    // 支付成功
    FAILED,     // 支付失败
    CANCEL      // 支付取消
}
```

### WxPayResponse

```kotlin
data class WxPayResponse(
    var appid: String?,      // 应用 ID
    var noncestr: String?,   // 随机字符串
    var `package`: String?,  // 包名（固定值 "Sign=WXPay"）
    var partnerid: String?,  // 商户号
    var prepayid: String?,   // 预支付会话 ID
    var sign: String?,       // 签名
    var timestamp: String?   // 时间戳
)
```

## 构建与测试

```bash
# 构建模块
./gradlew :pay:assemble

# 发布到本地 Maven
./gradlew :pay:publishToMavenLocal
```

## 配置指南

### AndroidManifest 配置（微信支付回调）

```xml
<!-- 微信支付回调 Activity -->
<activity-alias
    android:name=".wxapi.WXPayEntryActivity"
    android:exported="true"
    android:label="@string/app_name"
    android:launchMode="singleTop"
    android:targetActivity="com.hl.pay.weixin.wxapi.WXPayEntryActivity"
    android:taskAffinity="${applicationId}"
    android:theme="@android:style/Theme.Translucent.NoTitleBar" />
```

## 使用示例

### 微信支付

```kotlin
// 通常支付参数由服务端返回，客户端发起支付
PaymentHelper.startWeChatPay(
    activity = this,
    configAppId = "wx_app_id_here",
    wxPayResponse = WxPayResponse(
        appid = "wx1234567890",
        partnerid = "1234567890",
        prepayid = "wx20230101000000xxxx",
        noncestr = "random_string",
        timestamp = "1672531200",
        `package` = "Sign=WXPay",
        sign = "generated_sign"
    ),
    payResultCallBack = object : PayResultCallBack {
        override fun onResult(state: PayState, payResult: String) {
            when (state) {
                PayState.SUCCESS -> {
                    toast("支付成功")
                    navigateToResultPage()
                }
                PayState.FAILED -> {
                    toast("支付失败: $payResult")
                }
                PayState.CANCEL -> {
                    toast("已取消支付")
                }
            }
        }
    }
)
```

### 支付宝支付

```kotlin
// orderInfo 由服务端签名后下发
val orderInfo = "app_id=xxx&biz_content=xxx&sign=xxx..." // 服务端返回的签名串

PaymentHelper.startAliPay(
    activity = this,
    configAppId = "alipay_app_id_here",
    aliPayOrderInfo = orderInfo,
    payResultCallBack = object : PayResultCallBack {
        override fun onResult(state: PayState, payResult: String) {
            when (state) {
                PayState.SUCCESS -> handlePaySuccess()
                else -> handlePayFail(state.name, payResult)
            }
        }
    }
)
```
