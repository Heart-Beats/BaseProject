# SmsUtil — 短信发送模块

## 模块概述

`sms-util` 封装了 Android 短信发送功能，支持后台发送、多号码群发、生命周期感知的广播接收。

**模块坐标**: `com.hl.smsutil`
**发布 GroupId**: `io.github.heart-beats.baseproject`

## 对外接口

```kotlin
class SmsHelper(private val activity: FragmentActivity) {
    fun sendMessageInBackground(phoneNumber: String, message: String,
        callback: SendSmsResultCallBack)
    fun sendMessageInBackground(phoneNumbers: List<String>?, message: String,
        callback: SendSmsResultCallBack)
    fun sendMessage(message: String, phoneNumber: String? = null,
        callback: SendSmsResultCallBack)
}
```

## 构建与测试

```bash
./gradlew :sms-util:assemble
```

## 使用示例

```kotlin
val smsHelper = SmsHelper(this)
smsHelper.sendMessageInBackground("13800138000", "您的验证码：123456",
    object : SendSmsResultCallBack {
        override fun onSendSuccess() { toast("发送成功") }
        override fun onSendFailed(errorCode: Int) { toast("发送失败: $errorCode") }
    })
```
