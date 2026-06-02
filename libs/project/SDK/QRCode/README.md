# QRCode — 二维码模块

## 模块概述

`qrcode` 集成了二维码/条形码的生成、解析和扫描功能，基于 ZXingLite 封装。

**模块坐标**: `com.hl.qrcode`
**发布 GroupId**: `io.github.heart-beats.baseproject`

## 依赖关系

| 外部 | `com.king.zxing:zxing-lite` |
| 内部 | `activity-result` |

## 对外接口

```kotlin
object QRCodeUtil {
    fun createQRCode(content: String, heightPx: Int, logo: Bitmap? = null,
        ratio: Float = 1f, codeColor: Int = Color.BLACK): Bitmap?
    fun createBarCode(content: String, format: BarcodeFormat, widthPx: Int, heightPx: Int,
        isShowText: Boolean = false, textSize: Float = 30f, codeColor: Int = Color.BLACK): Bitmap?
    fun parseCode(bitmap: Bitmap): String?       // 解析二维码/条形码
    fun parseCode(bitmapPath: String): String?
    fun parseQRCode(bitmap: Bitmap): String?
}

class QRScanUtil(caller: ActivityResultCaller) {
    fun launchDefault(options: ActivityOptionsCompat? = null,
        scanCancelAction: () -> Unit, scanResultAction: (String?) -> Unit)
}
```

## 构建与测试

```bash
./gradlew :qrcode:assemble
```

## 使用示例

```kotlin
// 生成二维码
val qrBitmap = QRCodeUtil.createQRCode("https://example.com", 400)

// 扫描二维码
val scanner = QRScanUtil(this)
scanner.launchDefault(
    scanCancelAction = { toast("扫描取消") },
    scanResultAction = { result -> handleScanResult(result) }
)
```
