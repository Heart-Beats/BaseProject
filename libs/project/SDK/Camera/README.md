# Camera — 相机模块

## 模块概述

`camera` 基于 JCameraView 封装拍照和录像功能，提供 `MyCaptureActivity` 快捷拍照/录屏入口，支持仅拍照、仅录像和两者兼备三种模式。

**模块坐标**: `com.hl.camera`
**发布 GroupId**: `io.github.heart-beats.baseproject`

## 依赖关系

| 外部 | 相机相关库、视频录制库 |
| 内部 | `base-ui`, `permission`, `uikit-toast` |

## 对外接口

```kotlin
enum class CaptureFeature { ONLY_CAPTURE, ONLY_RECORD, BOTH }

class MyCaptureActivity : FragmentActivity() {
    companion object {
        const val CAPTURE_FILE_PATH = "CAPTURE_FILE_PATH"
        const val CAPTURE_FEATURES = "CAPTURE_FEATURES"
        @JvmStatic fun start(activity: Activity, captureFeature: CaptureFeature, reqCode: Int)
    }
}
```

## 构建与测试

```bash
./gradlew :camera:assemble
```

## 使用示例

```kotlin
MyCaptureActivity.start(this, CaptureFeature.ONLY_CAPTURE, REQUEST_CODE_CAMERA)

override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    if (requestCode == REQUEST_CODE_CAMERA && resultCode == RESULT_OK) {
        val filePath = data?.getStringExtra(MyCaptureActivity.CAPTURE_FILE_PATH)
        // 处理拍照结果
    }
}
```
