# PictureSelector — 图片选择模块

## 模块概述

`picture-selector` 基于 LuckPicture 封装拍照和相册选择功能，集成 Glide 图片引擎和微信风格压缩。

**模块坐标**: `com.hl.pictureselector`
**发布 GroupId**: `io.github.heart-beats.baseproject`

## 依赖关系

| 外部 | `io.github.lucksiege:pictureselector`, `io.github.lucksiege:compress`, `io.github.lucksiege:ucrop` |
| 内部 | `image-load`（GlideEngine 实现） |

## 对外接口

```kotlin
object PickImageUtil {
    fun startTakePhoto(ctx: Context, option: PictureSelectionCameraModel.() -> Unit,
                       onSelectCancel: (() -> Unit)?, onSelectResult: (List<String>) -> Unit)
    fun startPictureSelect(ctx: Context, option: PictureSelectionModel.() -> Unit,
                           onSelectCancel: (() -> Unit)?, onSelectResult: (List<String>) -> Unit)
}
```

## 构建与测试

```bash
./gradlew :picture-selector:assemble
```

## 使用示例

```kotlin
// 拍照
PickImageUtil.startTakePhoto(this,
    option = { isEnableCrop = true; cropImageWideHigh = 300 to 300 },
    onSelectCancel = {},
    onSelectResult = { paths -> /* paths[0] 为拍照结果 */ }
)

// 相册多选
PickImageUtil.startPictureSelect(this,
    option = { maxSelectNum = 9; isEnableCrop = false },
    onSelectCancel = {},
    onSelectResult = { paths -> /* 选中的图片路径列表 */ }
)
```
