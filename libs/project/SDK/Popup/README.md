# Popup — 弹窗组件模块

## 模块概述

`popup` 基于 XPopup 封装，提供便捷的弹窗创建和图片预览功能。

**模块坐标**: `com.hl.popup`
**发布 GroupId**: `io.github.heart-beats.baseproject`

## 依赖关系

| 外部 | `com.github.li-xiaojun:XPopup` |

## 对外接口

```kotlin
// 创建/显示弹窗
inline fun <reified T : BasePopupView> T.showPop(popOptions: XPopup.Builder.() -> Unit)
inline fun <reified T : BasePopupView> T.createPop(popOptions: XPopup.Builder.() -> Unit): T

// 图片预览
fun Context.showImage(iv: ImageView?, url: String)
fun Context.showImages(iv: ImageView?, index: Int, urls: List<String>)
```

## 构建与测试

```bash
./gradlew :popup:assemble
```

## 使用示例

```kotlin
// 创建弹窗
BottomDialogFragment().showPop {
    hasStatusBarShadow = true
    dismissOnTouchOutside = true
}
// 图片预览
context.showImages(imageView, 0, listOf("url1", "url2"))
```
