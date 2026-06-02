# TencentCloud — 腾讯云对象存储模块

## 模块概述

`tencent-cloud` 集成了腾讯云 COS（对象存储）SDK，通过 `TencentCosUtil` 提供文件上传和下载的统一接口，支持传输进度监听、断点续传和传输状态回调。

**模块坐标**: `com.hl.tencentcloud`
**发布 GroupId**: `io.github.heart-beats.baseproject`

## 目录结构

```
TencentCloud/
├── build.gradle.kts
└── src/main/java/com/hl/tencentcloud/
    └── cos/
        ├── TencentCosUtil.kt          # COS 操作入口
        ├── CredentialProvider.kt       # 永久密钥凭证提供者
        └── TransferListener.kt         # 传输监听接口
```

## 依赖关系

### 外部依赖
| 依赖 | 用途 |
|------|------|
| `com.qcloud.cos:cos-android` | 腾讯云 COS SDK |

### 内部依赖
无内部模块依赖。

## 对外接口

### TencentCosUtil

```kotlin
object TencentCosUtil {
    // 初始化（需在 Application 中调用）
    fun init(context: Context, secretId: String, secretKey: String, regionName: String)

    // 上传文件
    fun uploadFile(
        bucketName: String,
        cosPath: String,               // COS 上的目标路径
        srcPath: String,               // 本地文件路径
        transferConfig: TransferConfig = TransferConfig.Builder().build(),
        transferListener: TransferListener? = null
    )

    // 下载文件（多种重载）
    fun downloadFile(
        context: Context, bucketName: String, cosPath: String,
        fileName: String? = null, saveDir: String,
        transferConfig: TransferConfig = ..., transferListener: TransferListener? = null
    )
    fun downloadFile(
        context: Context, bucketName: String, cosPath: String,
        fileName: String? = null, isSave2AppDir: Boolean = true,
        transferConfig: TransferConfig = ..., transferListener: TransferListener? = null
    )
    fun downloadFile(
        context: Context, bucketName: String, cosPath: String,
        saveFilePath: String,
        transferConfig: TransferConfig = ..., transferListener: TransferListener? = null
    )
}
```

### TransferListener

```kotlin
interface TransferListener {
    fun onTransferProgress(progress: Int) {}           // 0-100 的整数进度
    fun onTransferSuccess(accessUrl: String) {}        // 上传成功后的访问地址
    fun onTransferFail(message: String?) {}            // 传输失败原因
    fun onTransState(transferState: TransferState) {}  // 传输状态变更
}
```

### CredentialProvider

```kotlin
object CredentialProvider {
    fun getForeverCredential(secretId: String, secretKey: String): ShortTimeCredentialProvider
}
```

## 构建与测试

```bash
# 构建模块
./gradlew :tencent-cloud:assemble

# 发布到本地 Maven
./gradlew :tencent-cloud:publishToMavenLocal
```

## 使用示例

### 初始化

```kotlin
TencentCosUtil.init(
    context = application,
    secretId = "your_secret_id",
    secretKey = "your_secret_key",
    regionName = "ap-guangzhou"
)
```

### 上传文件

```kotlin
TencentCosUtil.uploadFile(
    bucketName = "example-1250000000",
    cosPath = "/images/avatar.jpg",
    srcPath = "/sdcard/Pictures/avatar.jpg",
    transferListener = object : TransferListener {
        override fun onTransferProgress(progress: Int) {
            // 更新进度条: progress 为 0-100
            updateProgressBar(progress)
        }
        override fun onTransferSuccess(accessUrl: String) {
            XLog.d("上传成功，访问地址: $accessUrl")
            toast("上传成功")
        }
        override fun onTransferFail(message: String?) {
            XLog.e("上传失败: $message")
            toast("上传失败")
        }
    }
)
```

### 下载文件

```kotlin
// 保存到应用内部存储
TencentCosUtil.downloadFile(
    context = this,
    bucketName = "example-1250000000",
    cosPath = "/documents/report.pdf",
    fileName = "report.pdf",
    isSave2AppDir = true,
    transferListener = object : TransferListener {
        override fun onTransferProgress(progress: Int) {
            updateProgressBar(progress)
        }
        override fun onTransferSuccess(accessUrl: String) {
            // 打开下载完成的文件
            OpenFileUtil.openFileByPath(context, accessUrl)
        }
        override fun onTransferFail(message: String?) {
            toast("下载失败: $message")
        }
    }
)
```
