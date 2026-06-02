# Banner — 轮播图模块

## 模块概述

`banner` 基于 ViewPager2 + Banner 库封装轮播图组件，支持自定义指示器、自动轮播、图片加载。

**模块坐标**: `com.hl.banner`
**发布 GroupId**: `io.github.heart-beats.baseproject`

## 依赖关系

| 外部 | `io.github.youth5201314:banner`, `androidx.viewpager2:viewpager2` |
| 内部 | `image-load` |

## 对外接口

```kotlin
data class AdsDetail(
    var adsTypeAlias: String?, var adsTitle: String?, var adsContent: String?,
    var adsId: String?, var adsImgUrl: String?, var adsFlowUrl: String?,
    var localImageRes: Int?
)

// Fragment 中快速初始化
fun <AdsDetail, AdAdapter> Banner<AdsDetail, AdAdapter>.initAdvertBanner(
    fragment: Fragment, @LayoutRes adLayoutId: Int,
    indicator: Indicator?, onItemClick: OnItemClickListener<AdsDetail>?,
    onPageChange: OnPageChangeListener?
)
```

## 构建与测试

```bash
./gradlew :banner:assemble
```

## 使用示例

```kotlin
val bannerList = listOf(
    AdsDetail(adsImgUrl = "https://example.com/banner1.jpg"),
    AdsDetail(adsImgUrl = "https://example.com/banner2.jpg")
)
binding.banner.initAdvertBanner(this, R.layout.item_banner,
    indicator = AdsIndicator(context),
    onItemClick = { item, pos -> navigateTo(item.adsFlowUrl) },
    onPageChange = null)
```
