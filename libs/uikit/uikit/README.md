# uikit — UIKit 核心模块

## 模块概述

`uikit` 是项目 UI 组件库的核心模块，提供 100+ 个自定义控件和工具类，涵盖对话框、表单、图片、选择器、刷新、评分、搜索栏、流程布局等完整 UI 组件体系。

## Maven 坐标

| 属性 | 值 |
|------|-----|
| **GroupId** | `io.github.heart-beats.baseproject` |
| **ArtifactId** | `uikit` |
| **当前版本** | `0.0.4-SNAPSHOT` |

```groovy
implementation 'io.github.heart-beats.baseproject:uikit:0.0.4-SNAPSHOT'
```

## 目录结构

```
uikit/
├── build.gradle
└── src/main/java/com/hl/uikit/
    ├── BasicDialogFragment.kt         # 基础对话框
    ├── CornerLayout.kt                # 圆角布局
    ├── FocusableTouchLayout.kt        # 可获取焦点的触摸布局
    ├── ProgressWebView.kt             # 带进度的 WebView
    ├── UIKitToolbar.kt                # 自定义工具栏
    ├── UIKitNumberStepView.kt         # 步进器
    ├── UIKitDividerView.kt            # 分割线
    ├── UIKitCollapsingToolbarLayout.kt # 折叠工具栏
    ├── actionsheet/                   # ActionSheet 底部选项
    ├── adapter/                       # 菜单适配器
    ├── button/                        # UIKitCommonButton, UIKitOptionRadio
    ├── dialog/                        # Alert/Bottom/Loading/Input/Progress/SlideX 对话框
    ├── edittext/                      # 带删除按钮的 EditText
    ├── filter/                        # 筛选对话框、FlowFilter
    ├── flowlayout/                    # FlowLayout、TagFlowLayout、TagAdapter
    ├── form/                          # 表单组件体系
    ├── image/                         # 图片组件
    ├── maxheight/                     # MaxHeightFrameLayout, MaxHeightNestedScrollView
    ├── pickerview/                    # 时间/选项/区间选择器
    ├── progressbar/                   # 圆形进度条
    ├── ratingbar/                     # 评分条
    ├── recyclerview/                  # RecyclerView 装饰器
    ├── refresh/                       # SmartRefreshLayout 封装
    ├── search/                        # 搜索栏组件
    ├── sidebar/                       # 字母索引侧边栏
    ├── text/                          # 可选文本、文本域
    ├── utils/                         # 图片/权限/屏幕/文本/View 工具
    └── wave/                          # 波浪加载动画
```

## 依赖关系

### 外部依赖
| 依赖 | 用途 |
|------|------|
| `com.scwang.smart:refresh-layout-kernel` | 刷新布局 |
| `com.airbnb.android:lottie` | Lottie 动画 |
| `com.github.bumptech.glide:glide` | 图片加载 |
| `io.github.lucksiege:pictureselector` | 图片选择 |
| `com.github.li-xiaojun:XPopup` | 弹窗 |
| `com.github.chrisbanes:PhotoView` | 图片查看 |
| `com.contrarywind:Android-PickerView` | 选择器 |

### 内部依赖
| 模块 | 关系 |
|------|------|
| `uikit-res` | 依赖 —— 颜色和尺寸资源 |
| `uikit-toast` | 依赖 —— Toast 提示 |
| `view-binding` | 依赖 —— ViewBinding 工具 |

## 构建与测试

```bash
./gradlew :uikit:assemble
./gradlew :uikit:publishToMavenLocal
```

---

# 🎯 组件详细文档

## 1. 按钮组件

### 1.1 UIKitCommonButton

可配置样式的通用按钮，支持 8 种预设样式和自定义样式。

#### XML 属性

| 属性 | 类型 | 说明 | 默认值 |
|------|------|------|--------|
| `uikit_buttonStyle` | enum | 按钮样式 | `solid_color1_radius4` |
| `uikit_custom_drawable` | reference | 自定义背景，仅 `custom` 样式生效 | - |

**buttonStyle 枚举值：**

| 值 | 说明 |
|----|------|
| `solid_color1_radius4` | 实心颜色1，圆角4dp，最小高度50dp |
| `solid_color1_radius4_minHigh44` | 实心颜色1，圆角4dp，最小高度44dp |
| `solid_color1_radius0` | 实心颜色1，无圆角，最小高度50dp |
| `solid_colorGray_radius0` | 实心灰色，无圆角，最小高度50dp |
| `stroke_radius4` | 描边，圆角4dp，最小高度50dp |
| `capsule` | 胶囊形，最小高度25dp，最小宽度72dp，字体12sp |
| `capsule_stroke` | 胶囊形描边，最小高度25dp，最小宽度72dp，字体12sp |
| `custom` | 自定义样式，需配合 `uikit_custom_drawable` |

#### 使用示例

```xml
<!-- 实心颜色1，圆角4dp -->
<com.hl.uikit.button.UIKitCommonButton
    android:layout_width="match_parent"
    android:layout_height="48dp"
    android:text="确认"
    app:uikit_buttonStyle="solid_color1_radius4" />

<!-- 胶囊形按钮 -->
<com.hl.uikit.button.UIKitCommonButton
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    android:text="小按钮"
    app:uikit_buttonStyle="capsule" />

<!-- 描边按钮 -->
<com.hl.uikit.button.UIKitCommonButton
    android:layout_width="match_parent"
    android:layout_height="48dp"
    android:text="取消"
    app:uikit_buttonStyle="stroke_radius4" />

<!-- 自定义背景 -->
<com.hl.uikit.button.UIKitCommonButton
    android:layout_width="match_parent"
    android:layout_height="48dp"
    android:text="自定义"
    app:uikit_buttonStyle="custom"
    app:uikit_custom_drawable="@drawable/custom_bg" />
```

### 1.2 UIKitOptionRadio

单选按钮组件，继承自 `AppCompatRadioButton`。

#### 使用示例

```xml
<com.hl.uikit.button.UIKitOptionRadio
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    android:text="选项1" />
```

---

## 2. 文本组件

### 2.1 UIKitTextArea

多行文本域组件。

#### XML 属性

| 属性 | 类型 | 说明 | 默认值 |
|------|------|------|--------|
| `uikit_textAreaHint` | string/reference | 提示文本 | - |
| `uikit_textAreaHintColor` | color/reference | 提示文本颜色 | `#ffc4c4c4` |
| `uikit_textAreaTextColor` | color/reference | 文本颜色 | `#333333` |
| `uikit_textAreaTextSize` | dimension/reference | 字体大小 | `14sp` |
| `uikit_textAreaMaxCount` | integer | 最大字数 | `200` |
| `uikit_textAreaMaxCountTextColor` | color/reference | 字数统计颜色 | `#ffc4c4c4` |
| `uikit_textAreaMaxCountTextSize` | dimension/reference | 字数统计字体大小 | `13sp` |

#### 使用示例

```xml
<com.hl.uikit.text.UIKitTextArea
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    app:uikit_textAreaHint="请输入描述"
    app:uikit_textAreaMaxLength="200"
    app:uikit_textAreaMaxCount="200"
    app:uikit_textAreaTextColor="#333333"
    app:uikit_textAreaTextSize="14sp" />
```

### 2.2 UIKitSelectTextView

可选中的文本控件。

#### 使用示例

```xml
<com.hl.uikit.text.UIKitSelectTextView
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:text="可选中的文本内容"
    app:uikit_selectText="true" />
```

---

## 3. 搜索栏组件

### 3.1 UIKitSearchBar

搜索栏组件，继承自 `SearchView`。

#### XML 属性

继承 `SearchView` 的所有属性，可通过 `UiKit.SearchBarStyle` 样式配置：

| 属性 | 说明 | 默认值 |
|------|------|--------|
| `layout` | 布局文件 | `@layout/uikit_search_bar_layout` |
| `queryBackground` | 查询背景 | `null` |
| `searchIcon` | 搜索图标 | `@drawable/uikit_icon_search` |
| `searchHintIcon` | 搜索提示图标 | `@drawable/uikit_icon_search` |
| `defaultQueryHint` | 默认提示文本 | `搜索关键词` |
| `closeIcon` | 关闭图标 | `@drawable/uikit_icon_search_close` |
| `iconifiedByDefault` | 是否默认展开 | `false` |

#### 使用示例

```xml
<com.hl.uikit.search.UIKitSearchBar
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    app:uikit_searchHint="搜索" />
```

---

## 4. 流式布局组件

### 4.1 FlowLayout

流式布局容器，子元素自动换行。

#### 使用示例

```xml
<com.hl.uikit.flowlayout.FlowLayout
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    app:flowLayoutSpacing="8dp">

    <TextView
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="标签1" />

    <TextView
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="标签2" />
</com.hl.uikit.flowlayout.FlowLayout>
```

### 4.2 TagFlowLayout

标签流式布局，支持单选/多选。

#### XML 属性

| 属性 | 类型 | 说明 | 默认值 |
|------|------|------|--------|
| `uikit_max_select` | integer | 最大选择数量 | - |
| `uikit_tag_gravity` | enum | 标签对齐方式 | `left` |

**tag_gravity 枚举值：**

| 值 | 说明 |
|----|------|
| `left` | 左对齐 |
| `center` | 居中 |
| `right` | 右对齐 |

#### 使用示例

```xml
<com.hl.uikit.flowlayout.TagFlowLayout
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    app:uikit_max_select="3"
    app:uikit_tag_gravity="left">
    <!-- 标签项动态添加 -->
</com.hl.uikit.flowlayout.TagFlowLayout>
```

```kotlin
val tagFlowLayout = findViewById<TagFlowLayout>(R.id.tagFlowLayout)
val adapter = TagAdapter(tagList)
tagFlowLayout.setAdapter(adapter)
tagFlowLayout.setMaxSelect(3) // 最多选择3个
```

### 4.3 TagView

标签视图。

#### 使用示例

```xml
<com.hl.uikit.flowlayout.TagView
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    android:text="标签" />
```

```kotlin
val flowLayout = findViewById<FlowLayout>(R.id.flowLayout)
flowLayout.addView(TagView(context).apply {
    text = "标签1"
})
```

---

## 5. 评分组件

### 5.1 UIKitRatingBar

评分组件，支持整星/半星评分。

#### XML 属性

| 属性 | 类型 | 说明 | 默认值 |
|------|------|------|--------|
| `uikit_starImageSize` | dimension/reference | 星星尺寸 | - |
| `uikit_starPadding` | dimension/reference | 星星间距 | - |
| `uikit_starCount` | integer | 星星总数 | `5` |
| `uikit_starEmpty` | reference | 空星资源 | - |
| `uikit_starFill` | reference | 满星资源 | - |
| `uikit_starHalf` | reference | 半星资源 | - |
| `uikit_clickable` | boolean | 是否可点击 | `true` |
| `uikit_starStep` | float | 当前进度 | - |
| `uikit_stepSize` | enum | 进度方式 | `Half` |

**stepSize 枚举值：**

| 值 | 说明 |
|----|------|
| `Half` | 半星评分 |
| `Full` | 整星评分 |

#### 使用示例

```xml
<com.hl.uikit.ratingbar.UIKitRatingBar
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    app:uikit_starCount="5"
    app:uikit_starImageSize="24dp"
    app:uikit_starPadding="4dp"
    app:uikit_starStep="4.5"
    app:uikit_clickable="true"
    app:uikit_stepSize="Half" />
```

```kotlin
val ratingBar = findViewById<UIKitRatingBar>(R.id.ratingBar)
ratingBar.ratingValue = 4.5f
```

---

## 6. 进度条组件

### 6.1 UIKitCircleProgressBar

圆形进度条。

#### XML 属性

| 属性 | 类型 | 说明 | 默认值 |
|------|------|------|--------|
| `uikit_cpb_background` | color/reference | 背景颜色 | - |
| `uikit_cpb_background_width` | dimension/reference | 背景宽度 | - |
| `uikit_cpb_color` | color/reference | 进度颜色 | - |
| `uikit_cpb_width` | dimension/reference | 进度宽度 | - |
| `uikit_cpb_max_progress` | float | 最大进度 | `100` |
| `uikit_cpb_progress` | float | 当前进度 | `0` |

#### 使用示例

```xml
<com.hl.uikit.progressbar.UIKitCircleProgressBar
    android:layout_width="60dp"
    android:layout_height="60dp"
    app:uikit_cpb_color="@color/uikit_color_1"
    app:uikit_cpb_width="4dp"
    app:uikit_cpb_max_progress="100"
    app:uikit_cpb_progress="50" />
```

---

## 7. 加载动画组件

### 7.1 UIKitWaveLoadingView

波浪加载动画。

#### XML 属性

| 属性 | 类型 | 说明 | 默认值 |
|------|------|------|--------|
| `uikit_wl_shape` | enum | 边框形状 | `circle` |
| `uikit_wl_textLocation` | enum | 文字位置 | `flow` |
| `uikit_wl_shapeCorner` | dimension/reference | 边框圆角（square/rect 时生效） | `0dp` |
| `uikit_wl_waveColor` | color/reference | 波浪颜色 | - |
| `uikit_wl_waveBackgroundColor` | color/reference | 波浪背景颜色 | - |
| `uikit_wl_waveAmplitude` | float | 波峰（0~0.9f） | `0.2f` |
| `uikit_wl_waveVelocity` | float | 水平移动速度（0~1f） | `0.5f` |
| `uikit_wl_borderColor` | color/reference | 边框颜色 | - |
| `uikit_wl_borderWidth` | dimension/reference | 边框宽度 | `0dp` |
| `uikit_wl_process` | integer | 波浪占比（0~100） | `50` |
| `uikit_wl_text` | string/reference | 加载文字 | - |
| `uikit_wl_textColor` | color/reference | 文字颜色 | - |
| `uikit_wl_textSize` | dimension/reference | 文字大小 | - |
| `uikit_wl_textStrokeWidth` | dimension/reference | 文字边框宽度 | `0dp` |
| `uikit_wl_textStrokeColor` | color/reference | 文字边框颜色 | - |
| `uikit_wl_textBold` | boolean | 文字是否粗体 | `false` |
| `uikit_wl_textWave` | boolean | 文字是否跟随波浪浮动 | `false` |

**shape 枚举值：**

| 值 | 说明 |
|----|------|
| `circle` | 圆形 |
| `square` | 正方形 |
| `rect` | 矩形 |
| `none` | 无形状约束 |

**textLocation 枚举值：**

| 值 | 说明 |
|----|------|
| `flow` | 漂浮在波浪上面 |
| `center` | 居中 |
| `top` | 顶部 |
| `bottom` | 底部 |

#### 使用示例

```xml
<com.hl.uikit.wave.UIKitWaveLoadingView
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    app:uikit_wl_shape="circle"
    app:uikit_wl_waveColor="@color/uikit_color_1"
    app:uikit_wl_waveBackgroundColor="@color/uikit_color_7"
    app:uikit_wl_waveAmplitude="0.2"
    app:uikit_wl_waveVelocity="0.5"
    app:uikit_wl_borderColor="@color/uikit_color_1"
    app:uikit_wl_borderWidth="2dp"
    app:uikit_wl_process="50"
    app:uikit_wl_text="加载中"
    app:uikit_wl_textColor="@android:color/white"
    app:uikit_wl_textSize="14sp"
    app:uikit_wl_textLocation="flow"
    app:uikit_wl_textBold="true"
    app:uikit_wl_textWave="true" />
```

---

## 8. 侧边栏组件

### 8.1 UIKitSideBarView

字母索引侧边栏（仿联系人列表）。

#### XML 属性

| 属性 | 类型 | 说明 | 默认值 |
|------|------|------|--------|
| `uikit_sideTextColor` | color/reference | 文字颜色 | - |
| `uikit_sidePressedTextColor` | color/reference | 按下时文字颜色 | - |
| `uikit_sidePressedTextBgColor` | color/reference | 按下时背景颜色 | - |
| `uikit_sideItemHeight` | dimension/reference | 每项高度 | - |
| `uikit_sideItemSpacing` | dimension/reference | 项间距 | - |
| `uikit_sideTextSize` | dimension/reference | 文字大小 | - |
| `uikit_sideLetters` | string/reference | 字母列表 | - |

#### 使用示例

```xml
<com.hl.uikit.sidebar.UIKitSideBarView
    android:layout_width="24dp"
    android:layout_height="match_parent"
    app:uikit_sideTextColor="@color/uikit_color_4"
    app:uikit_sidePressedTextColor="@android:color/white"
    app:uikit_sidePressedTextBgColor="@color/uikit_color_1"
    app:uikit_sideItemHeight="20dp"
    app:uikit_sideTextSize="10sp" />
```

---

## 9. 选择器组件

### 9.1 OptionsPickerDialogFragment

选项滚动选择器弹窗。

#### 属性

| 属性 | 类型 | 说明 |
|------|------|------|
| `options` | List<String> | 选项列表 |
| `isCenterHorizontal` | Boolean | 是否水平居中 |
| `onOptionSelected` | (Int, String) -> Unit | 选择回调 |

#### 使用示例

```kotlin
OptionsPickerDialogFragment().showPop {
    isCenterHorizontal = true
    options = listOf("选项1", "选项2", "选项3")
    onOptionSelected = { position, option ->
        // 处理选择结果
    }
}
```

### 9.2 TimePickerDialogFragment

时间选择器弹窗。

#### 属性

| 属性 | 类型 | 说明 |
|------|------|------|
| `isCenterHorizontal` | Boolean | 是否水平居中 |
| `onTimeSelected` | (Int, Int, Int, Int, Int) -> Unit | 选择回调（年, 月, 日, 时, 分） |

#### 使用示例

```kotlin
TimePickerDialogFragment().showPop {
    isCenterHorizontal = true
    onTimeSelected = { year, month, day, hour, minute ->
        // 处理时间选择结果
    }
}
```

### 9.3 RangeTimePickerDialogFragment

时间区间选择器弹窗。

#### 属性

| 属性 | 类型 | 说明 |
|------|------|------|
| `isCenterHorizontal` | Boolean | 是否水平居中 |
| `onRangeTimeSelected` | (Long, Long) -> Unit | 选择回调（开始时间戳, 结束时间戳） |

#### 使用示例

```kotlin
RangeTimePickerDialogFragment().showPop {
    isCenterHorizontal = true
    onRangeTimeSelected = { startTime, endTime ->
        // 处理时间区间选择结果
    }
}
```

### 9.4 UIKitOptionsExtPickerView

选项选择器视图。

#### XML 属性

| 属性 | 类型 | 说明 | 默认值 |
|------|------|------|--------|
| `android:textSize` | dimension/reference | 字体大小 | - |
| `android:textColor` | color/reference | 字体颜色 | - |
| `uikit_wheelCurved` | boolean/reference | 是否曲面效果 | `true` |
| `uikit_showWheelDivider` | boolean/reference | 是否显示分割线 | `true` |
| `uikit_wheelDividerColor` | color/reference | 分割线颜色 | - |
| `uikit_wheelDividerHeight` | dimension/reference | 分割线高度 | `1dp` |
| `uikit_wheelLineSpacing` | dimension/reference | 行间距 | `18dp` |
| `uikit_wheelVisibleItems` | integer/reference | 可见项数量 | `7` |

---

## 10. 对话框组件

### 10.1 AlertDialogFragment

警告对话框。

#### 使用示例

```kotlin
AlertDialogFragment().showPop {
    title = "提示"
    content = "确定要删除吗？"
    onConfirm {
        // 确认操作
    }
}
```

### 10.2 BottomDialogFragment

底部弹出对话框。

#### 使用示例

```kotlin
BottomDialogFragment().showPop {
    title = "选择操作"
    onItemClick = { position ->
        // 处理点击
    }
}
```

### 10.3 InputDialogFragment

输入对话框。

#### 使用示例

```kotlin
InputDialogFragment().showPop {
    title = "请输入"
    onInputConfirmed = { inputText ->
        // 处理输入
    }
}
```

### 10.4 LoadingDialogFragment

加载对话框。

#### 使用示例

```kotlin
LoadingDialogFragment().showPop {
    loadingText = "加载中..."
}
```

### 10.5 ProgressBarDialog

进度条对话框。

#### 使用示例

```kotlin
ProgressBarDialog().showPop {
    title = "上传中"
    progress = 50 // 0-100
}
```

### 10.6 SlideXDialogFragment

侧滑关闭对话框。

#### 使用示例

```kotlin
SlideXDialogFragment().showPop {
    onDismiss {
        // 对话框关闭
    }
}
```

### 10.7 AdvertDialogFragment

广告对话框。

#### 使用示例

```kotlin
AdvertDialogFragment().showPop {
    imageUrl = "https://example.com/image.jpg"
    onImageClick {
        // 广告点击
    }
}
```

### 10.8 BasicDialogFragment

基础对话框。

#### 使用示例

```kotlin
BasicDialogFragment().showPop {
    title = "提示"
    content = "内容"
}
```

### 10.9 LongPressMenuDialog

长按菜单对话框。

#### 使用示例

```kotlin
LongPressMenuDialog().showPop {
    items = listOf("复制", "删除", "分享")
    onItemClick = { position ->
        // 处理点击
    }
}
```

---

## 11. ActionSheet 组件

### 11.1 ActionSheetDialogFragment

底部选项列表。

#### 使用示例

```kotlin
ActionSheetDialogFragment().showPop {
    title = "选择操作"
    items = listOf("拍照", "从相册选择")
    onItemClick = { position, item ->
        // 处理选择
    }
}
```

### 11.2 Alert1SheetDialogFragment

警告样式选项列表（样式1）。

#### 使用示例

```kotlin
Alert1SheetDialogFragment().showPop {
    title = "提示"
    message = "确定要执行此操作吗？"
    onConfirm {
        // 确认操作
    }
}
```

### 11.3 Alert2SheetDialogFragment

警告样式选项列表（样式2）。

#### 使用示例

```kotlin
Alert2SheetDialogFragment().showPop {
    title = "提示"
    message = "确定要执行此操作吗？"
    confirmText = "确定"
    cancelText = "取消"
    onConfirm {
        // 确认操作
    }
}
```

### 11.4 ArrayListSheetDialogFragment

数组列表选项弹窗。

#### 使用示例

```kotlin
ArrayListSheetDialogFragment().showPop {
    title = "选择"
    items = listOf("选项1", "选项2", "选项3")
    onItemClick = { position ->
        // 处理选择
    }
}
```

---

## 12. 筛选组件

### 12.1 FilterDialogFragment

筛选条件弹窗。

#### 属性

| 属性 | 类型 | 说明 |
|------|------|------|
| `filterOptions` | List<FilterOptions> | 筛选选项列表 |
| `onFilterConfirmed` | (List<FilterOption>) -> Unit | 筛选确认回调 |

#### 使用示例

```kotlin
FilterDialogFragment().showPop {
    filterOptions = filterOptionsList
    onFilterConfirmed = { selectedOptions ->
        // 处理筛选结果
    }
}
```

### 12.2 FlowFilterDialogFragment

流式筛选弹窗。

#### 使用示例

```kotlin
FlowFilterDialogFragment().showPop {
    filterTags = tagList
    onFilterConfirmed = { selectedTags ->
        // 处理筛选结果
    }
}
```

---

## 13. 图片组件

### 13.1 UIKitRoundImageView

圆角/圆形 ImageView。

#### XML 属性

| 属性 | 类型 | 说明 | 默认值 |
|------|------|------|--------|
| `uikit_is_circle` | boolean | 是否圆形 | `false` |
| `uikit_is_cover_src` | boolean | 是否覆盖源图 | `false` |
| `uikit_corner_radius` | dimension/reference | 统一圆角半径 | `0dp` |
| `uikit_corner_top_left_radius` | dimension/reference | 左上圆角半径 | `0dp` |
| `uikit_corner_top_right_radius` | dimension/reference | 右上圆角半径 | `0dp` |
| `uikit_corner_bottom_left_radius` | dimension/reference | 左下圆角半径 | `0dp` |
| `uikit_corner_bottom_right_radius` | dimension/reference | 右下圆角半径 | `0dp` |
| `uikit_border_width` | dimension/reference | 边框宽度 | `0dp` |
| `uikit_border_color` | color/reference | 边框颜色 | - |
| `uikit_inner_border_width` | dimension/reference | 内边框宽度 | `0dp` |
| `uikit_inner_border_color` | color/reference | 内边框颜色 | - |
| `uikit_mask_color` | color/reference | 遮罩颜色 | - |

#### 使用示例

```xml
<!-- 圆形图片 -->
<com.hl.uikit.image.UIKitRoundImageView
    android:layout_width="100dp"
    android:layout_height="100dp"
    android:src="@drawable/avatar"
    app:uikit_is_circle="true"
    app:uikit_border_width="2dp"
    app:uikit_border_color="@color/uikit_color_1" />

<!-- 圆角图片 -->
<com.hl.uikit.image.UIKitRoundImageView
    android:layout_width="match_parent"
    android:layout_height="200dp"
    android:src="@drawable/image"
    app:uikit_corner_radius="12dp" />
```

### 13.2 UIKitGifImageView

GIF 图片视图。

#### XML 属性

| 属性 | 类型 | 说明 | 默认值 |
|------|------|------|--------|
| `uikit_gif_src` | reference | GIF 文件引用 | - |
| `uikit_auto_play` | boolean | 是否加载完自动播放 | `true` |
| `uikit_play_count` | integer | 播放次数（-1 为永远播放） | `-1` |
| `uikit_end_last_frame` | boolean | 播放完成后是否停留在最后一帧 | `false` |

#### 使用示例

```xml
<com.hl.uikit.image.UIKitGifImageView
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    app:uikit_gif_src="@drawable/animation"
    app:uikit_auto_play="true"
    app:uikit_play_count="-1"
    app:uikit_end_last_frame="false" />
```

### 13.3 UIKitProgressImageView

进度图片视图。

#### XML 属性

| 属性 | 类型 | 说明 | 默认值 |
|------|------|------|--------|
| `uikit_progress` | integer | 进度值（0-100） | `0` |
| `uikit_progressAreaColor` | color/reference | 进度区域颜色 | - |

#### 使用示例

```xml
<com.hl.uikit.image.UIKitProgressImageView
    android:layout_width="100dp"
    android:layout_height="100dp"
    app:uikit_progress="50"
    app:uikit_progressAreaColor="#80000000" />
```

### 13.4 UIKitUploadPicImageGridLayout

上传图片网格布局。

#### XML 属性

| 属性 | 类型 | 说明 | 默认值 |
|------|------|------|--------|
| `uikit_maxColumn` | integer | 最大列数 | `3` |
| `uikit_maxSelectPictureCount` | integer | 最大选择图片数 | `9` |
| `uikit_itemSpace` | dimension/reference | 项间距 | `10dp` |
| `uikit_addItemRes` | reference | 添加按钮资源 | `@drawable/uikit_icon_pick_image` |
| `uikit_isCanDelete` | boolean | 是否可删除 | `true` |
| `uikit_isCanAdd` | boolean | 是否可添加 | `true` |
| `uikit_imageRoundRadius` | dimension/reference | 图片圆角半径 | `8dp` |

#### 使用示例

```xml
<com.hl.uikit.image.UIKitUploadPicImageGridLayout
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    app:uikit_maxColumn="3"
    app:uikit_maxSelectPictureCount="9"
    app:uikit_itemSpace="10dp"
    app:uikit_isCanDelete="true"
    app:uikit_isCanAdd="true"
    app:uikit_imageRoundRadius="8dp" />
```

### 13.5 PickImageUtil

图片选择工具类。

#### 使用示例

```kotlin
// 拍照
PickImageUtil.takePhoto(activity) { uri ->
    // 处理拍照结果
}

// 从相册选择
PickImageUtil.pickImage(activity) { uri ->
    // 处理选择结果
}

// 多图选择
PickImageUtil.pickImages(activity, maxCount) { uris ->
    // 处理选择结果
}
```

### 13.6 TakePhotoLifecycleObserver

拍照生命周期观察者。

#### 使用示例

```kotlin
val observer = TakePhotoLifecycleObserver(activity)
lifecycle.addObserver(observer)
```

### 13.7 GlideEngine

Glide 图片加载引擎。

#### 使用示例

```kotlin
val engine = GlideEngine.create()
PickImageUtil.setEngine(engine)
```

### 13.8 CompressEngine

图片压缩引擎。

#### 使用示例

```kotlin
val engine = CompressEngine.create()
PickImageUtil.setCompressEngine(engine)
```

---

## 14. 布局组件

### 14.1 UIKitMaxHeightFrameLayout

最大高度 FrameLayout。

#### XML 属性

| 属性 | 类型 | 说明 | 默认值 |
|------|------|------|--------|
| `uikit_maxScreenHeightPercent` | fraction/dimension | 最大高度占屏幕百分比 | - |

#### 使用示例

```xml
<com.hl.uikit.maxheight.UIKitMaxHeightFrameLayout
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    app:uikit_maxScreenHeightPercent="50%">
    <!-- 内容 -->
</com.hl.uikit.maxheight.UIKitMaxHeightFrameLayout>
```

### 14.2 UIKitMaxHeightNestedScrollView

最大高度 NestedScrollView。

#### XML 属性

同 `UIKitMaxHeightFrameLayout`。

#### 使用示例

```xml
<com.hl.uikit.maxheight.UIKitMaxHeightNestedScrollView
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    app:uikit_maxScreenHeightPercent="60%">
    <!-- 内容 -->
</com.hl.uikit.maxheight.UIKitMaxHeightNestedScrollView>
```

### 14.3 CornerLayout

圆角布局容器。

#### 子 View XML 属性

| 属性 | 类型 | 说明 |
|------|------|------|
| `uikit_corner` | flags | 圆角位置 |

**corner flags 值：**

| 值 | 说明 |
|----|------|
| `leftTop` | 左上角 |
| `rightTop` | 右上角 |
| `leftBottom` | 左下角 |
| `rightBottom` | 右下角 |

#### 使用示例

```xml
<com.hl.uikit.CornerLayout
    android:layout_width="match_parent"
    android:layout_height="wrap_content">

    <View
        android:layout_width="match_parent"
        android:layout_height="50dp"
        app:uikit_corner="leftTop|rightTop" />
</com.hl.uikit.CornerLayout>
```

### 14.4 FocusableTouchLayout

可获取焦点的触摸布局。

#### 使用示例

```xml
<com.hl.uikit.FocusableTouchLayout
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:focusableInTouchMode="true">
    <!-- 内容 -->
</com.hl.uikit.FocusableTouchLayout>
```

### 14.5 UIKitCollapsingToolbarLayout

折叠工具栏布局。

#### 使用示例

```xml
<com.hl.uikit.UIKitCollapsingToolbarLayout
    android:layout_width="match_parent"
    android:layout_height="wrap_content">
    <!-- 内容 -->
</com.hl.uikit.UIKitCollapsingToolbarLayout>
```

---

## 15. 工具栏组件

### 15.1 UIKitToolbar

自定义顶部导航栏。

#### XML 属性

| 属性 | 类型 | 说明 | 默认值 |
|------|------|------|--------|
| `uikit_backgroundColor` | color/reference | 背景颜色 | `white` |
| `uikit_title` | string/reference | 标题文本 | - |
| `uikit_titleColor` | color/reference | 标题颜色 | `@color/uikit_color_3` |
| `uikit_titleIsBold` | boolean | 标题是否粗体 | `false` |
| `uikit_titleMargin` | dimension/reference | 标题边距 | `0dp` |
| `uikit_titleSize` | dimension/reference | 标题字体大小（仅居中有效） | `@dimen/uikit_font_size_6` |
| `uikit_titleGravity` | enum | 标题位置 | `center` |
| `uikit_titleEllipsize` | enum | 标题超长样式 | `middle` |
| `uikit_subtitle` | string/reference | 副标题文本 | - |
| `uikit_subtitleColor` | color/reference | 副标题颜色 | `@color/uikit_color_3` |
| `uikit_subtitleIsBold` | boolean | 副标题是否粗体 | `false` |
| `uikit_subtitleSize` | dimension/reference | 副标题字体大小（仅居中有效） | `@dimen/uikit_font_size_9` |
| `uikit_rightPaddingEnd` | dimension/reference | 右侧内边距 | `15dp` |
| `uikit_rightSpacing` | dimension/reference | 右侧元素间距 | `5dp` |
| `uikit_rightText` | string/reference | 右侧文本 | - |
| `uikit_rightTextSize` | dimension/reference | 右侧文本大小 | `@dimen/uikit_font_size_8` |
| `uikit_rightTextColor` | color/reference | 右侧文本颜色 | `@color/uikit_color_1` |
| `uikit_rightImage` | reference | 右侧图片 | - |
| `uikit_rightImageColor` | color/reference | 右侧图片颜色 | - |

**titleGravity 枚举值：**

| 值 | 说明 |
|----|------|
| `center` | 居中 |
| `start` | 靠左 |

**titleEllipsize 枚举值：**

| 值 | 说明 |
|----|------|
| `start` | 开头省略 |
| `middle` | 中间省略 |
| `end` | 末尾省略 |
| `marquee` | 跑马灯 |

#### 使用示例

```xml
<com.hl.uikit.UIKitToolbar
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    app:uikit_title="标题"
    app:uikit_titleGravity="center"
    app:uikit_rightText="完成"
    app:uikit_rightTextColor="@color/uikit_color_1" />
```

```kotlin
val toolbar = findViewById<UIKitToolbar>(R.id.toolbar)
toolbar.title = "标题"
toolbar.setBackClickListener {
    finish()
}
```

---

## 16. 分割线组件

### 16.1 UIKitDividerView

分割线组件。

#### XML 属性

| 属性 | 类型 | 说明 | 默认值 |
|------|------|------|--------|
| `uikit_dividerColor` | color/reference | 分割线颜色 | - |
| `uikit_dividerThickness` | dimension/reference | 分割线厚度 | `1dp` |
| `uikit_dividerLineType` | enum | 线条类型 | `solid_path` |
| `uikit_dashWidth` | dimension/reference | 虚线宽度（dash_path 时生效） | - |
| `uikit_dashSpaceWidth` | dimension/reference | 虚线间距（dash_path 时生效） | - |
| `uikit_dividerOrientation` | enum | 方向 | `horizontal` |

**dividerLineType 枚举值：**

| 值 | 说明 |
|----|------|
| `solid_path` | 实线 |
| `dash_path` | 虚线 |

**dividerOrientation 枚举值：**

| 值 | 说明 |
|----|------|
| `horizontal` | 水平 |
| `vertical` | 垂直 |

#### 使用示例

```xml
<!-- 实线分割线 -->
<com.hl.uikit.UIKitDividerView
    android:layout_width="match_parent"
    android:layout_height="1dp"
    app:uikit_dividerColor="@color/uikit_color_5"
    app:uikit_dividerThickness="1dp"
    app:uikit_dividerLineType="solid_path"
    app:uikit_dividerOrientation="horizontal" />

<!-- 虚线分割线 -->
<com.hl.uikit.UIKitDividerView
    android:layout_width="match_parent"
    android:layout_height="1dp"
    app:uikit_dividerColor="@color/uikit_color_5"
    app:uikit_dividerLineType="dash_path"
    app:uikit_dashWidth="4dp"
    app:uikit_dashSpaceWidth="2dp" />
```

---

## 17. 步进器组件

### 17.1 UIKitNumberStepView

步进器组件。

#### XML 属性

| 属性 | 类型 | 说明 | 默认值 |
|------|------|------|--------|
| `android:text` | string/reference | 文本 | - |
| `android:textColor` | color/reference | 文本颜色 | - |
| `android:textSize` | dimension/reference | 文本大小 | - |
| `uikit_operatorVisibility` | enum | 操作按钮可见性 | `visible` |
| `uikit_operatorPadding` | dimension/reference | 操作按钮内边距 | - |
| `uikit_hasUnit` | boolean | 是否有单位 | `false` |
| `uikit_unitValue` | string/reference | 单位值 | - |

**operatorVisibility 枚举值：**

| 值 | 说明 |
|----|------|
| `visible` | 可见 |
| `invisible` | 不可见（占位） |
| `gone` | 隐藏 |

#### 使用示例

```xml
<com.hl.uikit.UIKitNumberStepView
    android:layout_width="120dp"
    android:layout_height="36dp"
    app:uikit_stepMinValue="1"
    app:uikit_stepMaxValue="99"
    app:uikit_hasUnit="true"
    app:uikit_unitValue="个" />
```

---

## 18. 输入框组件

### 18.1 UIKitShapeEditTextWithDelete

带删除按钮的输入框。

#### XML 属性

| 属性 | 类型 | 说明 | 默认值 |
|------|------|------|--------|
| `uikit_deleteIcon` | reference | 删除图标 | `@drawable/uikit_icon_input_delete` |
| `uikit_deleteIcon_marginEnd` | dimension/reference | 删除图标右边距 | `13.33dp` |

#### 使用示例

```xml
<com.hl.uikit.edittext.UIKitShapeEditTextWithDelete
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:hint="请输入内容"
    app:uikit_deleteIcon="@drawable/uikit_icon_input_delete"
    app:uikit_deleteIcon_marginEnd="13.33dp" />
```

---

## 19. WebView 组件

### 19.1 ProgressWebView

带进度条的 WebView。

#### 使用示例

```xml
<com.hl.uikit.ProgressWebView
    android:layout_width="match_parent"
    android:layout_height="match_parent" />
```

```kotlin
val webView = findViewById<ProgressWebView>(R.id.webView)
webView.loadUrl("https://example.com")
```

---

## 20. RecyclerView 装饰器

### 20.1 RecyclerViewDividerDecoration

分割线装饰器。

#### 使用示例

```kotlin
recyclerView.addItemDecoration(
    RecyclerViewDividerDecoration(context)
)
```

### 20.2 GridSpaceItemDecoration

网格间距装饰器。

#### 使用示例

```kotlin
recyclerView.addItemDecoration(
    GridSpaceItemDecoration(spanCount, spacing)
)
```

### 20.3 GridSpacingItemDecoration

网格间距装饰器（支持边缘）。

#### 使用示例

```kotlin
recyclerView.addItemDecoration(
    GridSpacingItemDecoration(spanCount, spacing, includeEdge)
)
```

### 20.4 MarginStartDecoration

起始边距装饰器。

#### 使用示例

```kotlin
recyclerView.addItemDecoration(
    MarginStartDecoration(startMargin)
)
```

### 20.5 RecyclerViewPaddingDecoration

内边距装饰器。

#### 使用示例

```kotlin
recyclerView.addItemDecoration(
    RecyclerViewPaddingDecoration(padding)
)
```

### 20.6 MaxHeightLayoutManager

最大高度 LayoutManager。

#### 使用示例

```kotlin
recyclerView.layoutManager = MaxHeightLayoutManager(context, maxHeight)
```

---

## 21. 刷新组件

### 21.1 UIKitCommonRefreshHeader

通用刷新头。

#### 使用示例

```kotlin
binding.smartRefreshLayout.apply {
    setRefreshHeader(UIKitCommonRefreshHeader(context))
    setOnRefreshListener { /* 刷新逻辑 */ }
}
```

### 21.2 UIKitCommonRefreshFooter

通用刷新尾。

#### 使用示例

```kotlin
binding.smartRefreshLayout.apply {
    setRefreshFooter(UIKitCommonRefreshFooter(context))
    setOnLoadMoreListener { /* 加载更多逻辑 */ }
}
```

### 21.3 UIKitLottieRefreshHeaderFooter

Lottie 动画刷新头/尾。

#### XML 属性

| 属性 | 类型 | 说明 | 默认值 |
|------|------|------|--------|
| `uikit_pull_lottie_animation_res` | reference | 下拉动画资源 | - |
| `uikit_pull_lottie_animation_url` | string/reference | 下拉动画URL | - |
| `uikit_refresh_lottie_animation_res` | reference | 刷新动画资源 | - |
| `uikit_refresh_lottie_animation_url` | string/reference | 刷新动画URL | - |
| `uikit_spinner_style` | enum | 旋转样式 | `translate` |
| `uikit_primary_background` | color/reference | 背景颜色 | - |

**spinner_style 枚举值：**

| 值 | 说明 |
|----|------|
| `translate` | 平移 |
| `scale` | 缩放 |
| `fixed_behind` | 固定在后 |
| `fixed_front` | 固定在前 |
| `match_layout` | 匹配布局 |

#### 使用示例

```kotlin
binding.smartRefreshLayout.apply {
    setRefreshHeader(UIKitLottieRefreshHeaderFooter(context))
    setRefreshFooter(UIKitLottieRefreshHeaderFooter(context))
}
```

---

## 22. 适配器组件

### 22.1 MenuItemAdapter

菜单列表适配器。

#### 使用示例

```kotlin
val adapter = MenuItemAdapter(menuList)
recyclerView.adapter = adapter
```

### 22.2 FilterTagAdapter

筛选标签适配器。

#### 使用示例

```kotlin
val adapter = FilterTagAdapter(tagList)
flowFilterView.setAdapter(adapter)
```

---

## 23. 工具类

### 23.1 图片加载扩展 (_ImageView.kt)

```kotlin
// 使用 Glide 加载图片
imageView.loadImage(url)
imageView.loadImage(url, placeholder)
imageView.loadCircleImage(url)
imageView.loadRoundImage(url, radius)
```

### 23.2 权限工具 (_PermissionXUtil.kt)

```kotlin
// 请求权限
context.requestPermissions(permissions) { granted, denied ->
    // 处理权限结果
}
```

### 23.3 屏幕工具 (_ScreenUtil.kt)

```kotlin
val screenWidth = context.screenWidth
val screenHeight = context.screenHeight
val statusBarHeight = context.statusBarHeight
val dpValue = 10.dp
val spValue = 14.sp
```

### 23.4 文本工具 (_TextView.kt)

```kotlin
val textWidth = textView.measureTextWidth()
val textHeight = textView.measureTextHeight()
```

### 23.5 View 工具 (_View.kt)

```kotlin
view.visible()
view.invisible()
view.gone()
view.isVisible = true
```

---

## 24. 表单组件体系

### 24.1 UIKitFormGroup

表单分组容器。

#### XML 属性

| 属性 | 类型 | 说明 | 默认值 |
|------|------|------|--------|
| `uikit_formDividerEnable` | boolean | 是否启用分割线 | `true` |
| `uikit_formDividerColor` | color/reference | 分割线颜色 | - |
| `uikit_formDividerHeight` | dimension/reference | 分割线高度 | `1dp` |
| `uikit_formDividerPaddingStart` | dimension/reference | 分割线起始内边距 | `15dp` |
| `uikit_formDividerPaddingEnd` | dimension/reference | 分割线结束内边距 | `0dp` |
| `uikit_formDividerGravity` | enum | 分割线位置 | `bottom` |
| `uikit_formDividerNeedLast` | boolean | 最后一项是否显示分割线 | `false` |

**formDividerGravity 枚举值：**

| 值 | 说明 |
|----|------|
| `top` | 顶部 |
| `bottom` | 底部 |

#### 子 View XML 属性

子 View 可单独配置分割线：

| 属性 | 类型 | 说明 |
|------|------|------|
| `uikit_formDividerEnable` | boolean | 是否启用分割线 |
| `uikit_formDividerColor` | color/reference | 分割线颜色 |
| `uikit_formDividerHeight` | dimension/reference | 分割线高度 |
| `uikit_formDividerPaddingStart` | dimension/reference | 分割线起始内边距 |
| `uikit_formDividerPaddingEnd` | dimension/reference | 分割线结束内边距 |

#### 使用示例

```xml
<com.hl.uikit.form.UIKitFormGroup
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    app:uikit_formDividerEnable="true"
    app:uikit_formDividerColor="@color/uikit_color_5"
    app:uikit_formDividerHeight="1dp"
    app:uikit_formDividerGravity="bottom"
    app:uikit_formDividerNeedLast="false">

    <com.hl.uikit.form.UIKitFormItemLabel
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        app:uikit_formItemTitle="姓名" />

    <com.hl.uikit.form.UIKitFormItemLabel
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        app:uikit_formItemTitle="手机号" />
</com.hl.uikit.form.UIKitFormGroup>
```

### 24.2 UIKitFormItemView

表单项基类。

#### XML 属性

| 属性 | 类型 | 说明 | 默认值 |
|------|------|------|--------|
| `uikit_formItemTitle` | string/reference | 标题 | - |
| `uikit_formItemTitleColor` | color/reference | 标题颜色 | - |
| `uikit_formItemTitleSize` | dimension/reference | 标题大小 | - |
| `uikit_formItemTitleBold` | boolean | 标题是否粗体 | - |
| `uikit_formItemTitleMarginEnd` | dimension/reference | 标题右边距 | - |
| `uikit_formItemLeftIcon` | reference | 左侧图标 | - |
| `uikit_formItemLeftIconMarginEnd` | dimension/reference | 左侧图标右边距 | - |
| `uikit_formItemHint` | string/reference | 提示文本 | - |
| `uikit_formItemHintColor` | color/reference | 提示文本颜色 | - |

### 24.3 UIKitFormItemInput

输入框表单项。

#### XML 属性

| 属性 | 类型 | 说明 | 默认值 |
|------|------|------|--------|
| `uikit_formTextDeletable` | boolean | 是否显示删除按钮 | `true` |

继承 `UIKitFormItemView` 和 `UIKitFormItemText` 的所有属性。

#### 使用示例

```xml
<com.hl.uikit.form.UIKitFormItemInput
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    app:uikit_formItemTitle="姓名"
    app:uikit_formItemHint="请输入姓名"
    app:uikit_formTextDeletable="true" />
```

### 24.4 UIKitFormItemLabel

标签显示表单项。

#### XML 属性

| 属性 | 类型 | 说明 | 默认值 |
|------|------|------|--------|
| `uikit_formLabel` | string/reference | 标签内容 | - |
| `uikit_formLabelSize` | dimension/reference | 标签字体大小 | `@dimen/uikit_font_size_8` |
| `uikit_formLabelColor` | color/reference | 标签颜色 | `@color/uikit_color_3` |
| `uikit_formLabelBold` | boolean | 是否粗体 | `false` |
| `uikit_formLabelMarginEnd` | dimension/reference | 标签右边距 | `40dp` |
| `uikit_formTagText` | string/reference | 标签文本 | - |
| `uikit_formTagTextColor` | color/reference | 标签文本颜色 | - |
| `uikit_formLeftIcon` | reference | 左侧图标 | - |
| `uikit_formLeftIconMarginEnd` | dimension/reference | 左侧图标右边距 | `8dp` |
| `uikit_formChildLabel` | string/reference | 子标签（以","分隔） | - |
| `uikit_formChildLabelSize` | dimension/reference | 子标签字体大小 | `@dimen/uikit_font_size_10` |
| `uikit_formChildLabelColor` | color/reference | 子标签颜色 | `@color/uikit_color_4` |
| `uikit_formChildLabelMarginTop` | dimension/reference | 子标签顶部边距 | `0dp` |
| `uikit_formRightLayoutOrientation` | enum | 右侧布局方向 | `horizontal` |
| `uikit_formRightLayoutChildMargin` | dimension/reference | 右侧布局子元素间距 | `4dp` |
| `uikit_formChildWeight` | enum | 子布局权重 | `right_weight` |
| `uikit_formLeftLayoutGravity` | flags | 左侧布局重力 | - |
| `uikit_formRightLayoutGravity` | flags | 右侧布局重力 | - |
| `uikit_formCanSelectable` | boolean | 是否可选择 | `false` |

**formRightLayoutOrientation 枚举值：**

| 值 | 说明 |
|----|------|
| `horizontal` | 水平 |
| `vertical` | 垂直 |

**formChildWeight 枚举值：**

| 值 | 说明 |
|----|------|
| `left_weight` | 左侧占比大 |
| `right_weight` | 右侧占比大 |

**formLeftLayoutGravity / formRightLayoutGravity flags 值：**

| 值 | 说明 |
|----|------|
| `top` | 顶部 |
| `bottom` | 底部 |
| `start` | 起始 |
| `end` | 结束 |
| `center_vertical` | 垂直居中 |
| `center_horizontal` | 水平居中 |
| `center` | 居中 |

#### 使用示例

```xml
<com.hl.uikit.form.UIKitFormItemLabel
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    app:uikit_formItemTitle="昵称"
    app:uikit_formLabel="张三"
    app:uikit_formLabelSize="16sp"
    app:uikit_formLabelColor="@color/uikit_color_3"
    app:uikit_formLabelBold="false"
    app:uikit_formLabelMarginEnd="40dp" />
```

### 24.5 UIKitFormItemText

文本显示表单项。

#### XML 属性

| 属性 | 类型 | 说明 | 默认值 |
|------|------|------|--------|
| `uikit_formText` | string/reference | 文本内容 | - |
| `uikit_formTextGravity` | flags | 文本对齐方式 | `end` |
| `uikit_formTextHint` | string/reference | 提示文本 | - |
| `uikit_formTextHintColor` | color/reference | 提示文本颜色 | - |
| `uikit_formTextBold` | boolean | 是否粗体 | `false` |
| `uikit_formTextMaxLines` | integer | 最大行数 | - |
| `uikit_formTextMaxLength` | integer | 最大长度 | `20` |
| `uikit_formTextCustom` | boolean | 是否自定义样式 | `false` |
| `uikit_formTextSize` | dimension/reference | 字体大小 | `@dimen/uikit_font_size_8` |
| `uikit_formTextColor` | color/reference | 文本颜色 | `@color/uikit_color_4` |
| `uikit_formRightIcon` | reference | 右侧图标 | - |

继承 `UIKitFormItemLabel` 的所有属性。

#### 使用示例

```xml
<com.hl.uikit.form.UIKitFormItemText
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    app:uikit_formItemTitle="手机号"
    app:uikit_formText="13800138000"
    app:uikit_formTextColor="@color/uikit_color_3"
    app:uikit_formTextSize="16sp" />
```

### 24.6 UIKitFormItemImage

图片选择表单项。

#### XML 属性

| 属性 | 类型 | 说明 | 默认值 |
|------|------|------|--------|
| `uikit_formImage` | reference | 图片资源 | - |
| `uikit_formImageText` | string/reference | 图片说明文字 | - |
| `uikit_formImageTextColor` | color/reference | 说明文字颜色 | - |
| `uikit_formImageTextSize` | dimension/reference | 说明文字大小 | - |
| `uikit_formImageMarginVertical` | dimension/reference | 图片垂直边距 | - |

继承 `UIKitFormItemView` 的所有属性。

#### 使用示例

```xml
<com.hl.uikit.form.UIKitFormItemImage
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    app:uikit_formItemTitle="头像"
    app:uikit_formImage="@drawable/avatar"
    app:uikit_formImageText="点击上传"
    app:uikit_formImageTextColor="@color/uikit_color_4" />
```

### 24.7 UIKitFormItemToggleButton

开关表单项。

#### XML 属性

| 属性 | 类型 | 说明 | 默认值 |
|------|------|------|--------|
| `uikit_formToggleButtonRes` | reference | 开关按钮资源 | `@drawable/uikit_selector_toggle_button_bg` |
| `uikit_formToggleButtonWidth` | dimension/reference | 开关宽度 | - |
| `uikit_formToggleButtonHeight` | dimension/reference | 开关高度 | - |
| `uikit_formToggleCheck` | boolean | 是否选中 | `false` |

继承 `UIKitFormItemLabel` 的所有属性。

#### 使用示例

```xml
<com.hl.uikit.form.UIKitFormItemToggleButton
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    app:uikit_formItemTitle="开启通知"
    app:uikit_formToggleCheck="true"
    app:uikit_formToggleButtonRes="@drawable/custom_toggle" />
```

### 24.8 UIKitFormHeaderActionText

带操作的表单组标题。

#### XML 属性

| 属性 | 类型 | 说明 | 默认值 |
|------|------|------|--------|
| `uikit_formActionText` | string/reference | 操作文本 | - |
| `uikit_formActionTextBold` | boolean | 是否粗体 | `true` |
| `uikit_formActionTextSize` | dimension/reference | 字体大小 | `@dimen/uikit_font_size_8` |
| `uikit_formActionTextColor` | color/reference | 文本颜色 | `@color/uikit_color_1` |

继承 `UIKitFormItemLabel` 的所有属性。

#### 使用示例

```xml
<com.hl.uikit.form.UIKitFormHeaderActionText
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    app:uikit_formItemTitle="教育经历"
    app:uikit_formActionText="添加"
    app:uikit_formActionTextColor="@color/uikit_color_1" />
```

### 24.9 UIKitFormNumberStepView

数字步进器表单项。

#### XML 属性

| 属性 | 类型 | 说明 | 默认值 |
|------|------|------|--------|
| `uikit_formStepLabel` | string/reference | 标签文本 | - |
| `uikit_formStepLabelSize` | dimension/reference | 标签字体大小 | - |
| `uikit_formStepLabelColor` | color/reference | 标签颜色 | - |
| `uikit_formStepLabelBold` | boolean | 标签是否粗体 | `false` |
| `uikit_formStepText` | string/reference | 当前值文本 | - |
| `uikit_formStepTextBold` | boolean | 数值是否粗体 | `false` |
| `uikit_formStepTextMaxLines` | integer | 数值最大行数 | - |
| `uikit_formStepTextSize` | dimension/reference | 数值字体大小 | - |
| `uikit_formStepTextColor` | color/reference | 数值颜色 | - |
| `uikit_formStepHasUnit` | boolean | 是否有单位 | `false` |
| `uikit_formStepUnitValue` | string/reference | 单位值 | - |
| `uikit_formStepInputAble` | boolean | 是否可输入 | `false` |
| `uikit_formStepIsInterval` | boolean | 是否区间模式 | `false` |

继承 `UIKitFormItemView` 的所有属性。

#### 使用示例

```xml
<com.hl.uikit.form.UIKitFormNumberStepView
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    app:uikit_formItemTitle="数量"
    app:uikit_formStepHasUnit="true"
    app:uikit_formStepUnitValue="个"
    app:uikit_formStepInputAble="true" />
```

### 24.10 UIKitFormTextVerifyCode

验证码输入表单项。

#### XML 属性

| 属性 | 类型 | 说明 | 默认值 |
|------|------|------|--------|
| `uikit_formLabel` | string/reference | 标签文本 | - |
| `uikit_formLabelColor` | color/reference | 标签颜色 | - |
| `uikit_formLabelSize` | dimension/reference | 标签字体大小 | - |
| `uikit_formLabelMarginEnd` | dimension/reference | 标签右边距 | - |
| `uikit_formText` | string/reference | 文本内容 | - |
| `uikit_formTextGravity` | flags | 文本对齐方式 | `end` |
| `uikit_formTextHint` | string/reference | 提示文本 | - |
| `uikit_formTextHintColor` | color/reference | 提示文本颜色 | - |
| `uikit_formTextMaxLength` | integer | 最大长度 | `6` |
| `uikit_formLeftWarnText` | string/reference | 左侧警告文本 | - |
| `uikit_formRightWarnText` | string/reference | 右侧警告文本 | - |
| `uikit_formNeedSmsCode` | boolean | 是否需要短信验证码 | `false` |

继承 `UIKitFormItemView` 的所有属性。

#### 使用示例

```xml
<com.hl.uikit.form.UIKitFormTextVerifyCode
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    app:uikit_formItemTitle="验证码"
    app:uikit_formTextHint="请输入验证码"
    app:uikit_formNeedSmsCode="true"
    app:uikit_formTextMaxLength="6" />
```

---

## 25. 颜色资源

UIKit 使用统一的颜色方案，颜色资源定义在 `uikit-res` 模块中：

| 资源名称 | 色值 | 用途说明 |
|---------|------|---------|
| `uikit_color_1` | `#FF5E60C7` | 主色调（紫色） |
| `uikit_color_2` | `#FFF36F46` | 强调色（橙红色） |
| `uikit_color_3` | `#FF333333` | 深色背景/主文本色 |
| `uikit_color_4` | `#FF818181` | 次要文字 |
| `uikit_color_5` | `#FFC4C4C4` | 辅助文字 |
| `uikit_color_6` | `#26F36F46` | 半透明强调色 |
| `uikit_color_7` | `#FFF4F4F4` | 浅色背景 |
| `uikit_color_8` | `#FFF4F7FB` | 页面背景 |
| `uikit_color_9` | `#FFFF3B30` | 错误/警告色 |
| `uikit_color_10` | `#FF4F51A9` | 深紫色 |
| `uikit_fontcolor_1` | `#FF000000` | 主要文字（100%） |
| `uikit_fontcolor_2` | `#BF000000` | 次要文字（75%） |

---

## 26. 尺寸资源

| 资源名称 | 大小 | 适用场景 |
|---------|------|---------|
| `uikit_font_size_1` | 40sp | 大标题 |
| `uikit_font_size_2` | 34sp | 页面标题 |
| `uikit_font_size_3` | 30sp | 区块标题 |
| `uikit_font_size_4` | 28sp | 卡片标题 |
| `uikit_font_size_5` | 20sp | 按钮文字 |
| `uikit_font_size_6` | 18sp | 重要正文 |
| `uikit_font_size_7` | 16sp | 正文内容 |
| `uikit_font_size_8` | 14sp | 辅助文字 |
| `uikit_font_size_9` | 13sp | 提示文字 |
| `uikit_font_size_10` | 12sp | 小标签 |

---

## 27. 主题样式

UIKit 提供了多种预设样式，可在 `styles.xml` 中引用：

| 样式名称 | 说明 |
|---------|------|
| `UiKit.ToolbarStyle` | 默认工具栏样式 |
| `UiKit.ToolbarStylePrimary` | 主色调工具栏样式 |
| `UiKit.FormGroupStyle` | 表单分组样式 |
| `UiKit.FormItemStyle` | 表单项样式 |
| `UiKit.FormItemLabelStyle` | 表单标签样式 |
| `UiKit.FormItemTextStyle` | 表单文本样式 |
| `UiKit.FormItemInputStyle` | 表单输入样式 |
| `UiKit.FormItemToggleButtonStyle` | 表单开关样式 |
| `UiKit.CommonButtonStyle` | 通用按钮样式 |
| `UiKit.DividerViewStyle` | 分割线样式 |
| `UiKit.TextAreaStyle` | 文本域样式 |
| `UiKit.SearchBarStyle` | 搜索栏样式 |
| `UiKit.AlertDialog` | 警告对话框样式 |
| `UiKit.CommonDialogStyle` | 通用对话框样式 |
| `UiKit.ActionSheetDialogTheme` | ActionSheet 样式 |
| `UiKit.BottomDialog` | 底部对话框样式 |
| `UiKit.SlideXDialog` | 侧滑对话框样式 |
| `UiKit.LoadingDialog` | 加载对话框样式 |
| `UiKit.AdvertDialog` | 广告对话框样式 |
| `UiKit.LongPressMenuDialogStyle` | 长按菜单对话框样式 |

---

## 📝 快速开始

### 1. 添加依赖

```groovy
// settings.gradle
dependencyResolutionManagement {
    repositories {
        mavenCentral()
    }
}

// build.gradle
dependencies {
    implementation 'io.github.heart-beats.baseproject:uikit:0.0.4-SNAPSHOT'
}
```

### 2. 基础使用

#### 在 Activity 中使用

```kotlin
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 使用 UIKitToolbar
        val toolbar = findViewById<UIKitToolbar>(R.id.toolbar)
        toolbar.title = "标题"
        toolbar.setBackClickListener {
            finish()
        }
    }
}
```

#### 使用对话框

```kotlin
// 显示警告对话框
AlertDialogFragment().showPop {
    title = "提示"
    content = "确定要删除吗？"
    onConfirm {
        // 执行删除操作
    }
}

// 显示加载对话框
LoadingDialogFragment().showPop {
    loadingText = "加载中..."
}

// 显示选项列表
ActionSheetDialogFragment().showPop {
    title = "选择操作"
    items = listOf("拍照", "从相册选择")
    onItemClick = { position, item ->
        when (position) {
            0 -> takePhoto()
            1 -> pickImage()
        }
    }
}
```

#### 使用选择器

```kotlin
// 时间选择器
TimePickerDialogFragment().showPop {
    isCenterHorizontal = true
    onTimeSelected = { year, month, day, hour, minute ->
        // 处理时间选择结果
    }
}

// 选项选择器
OptionsPickerDialogFragment().showPop {
    isCenterHorizontal = true
    options = listOf("选项1", "选项2", "选项3")
    onOptionSelected = { position, option ->
        // 处理选项选择结果
    }
}
```

#### 使用流式布局

```kotlin
val tagFlowLayout = findViewById<TagFlowLayout>(R.id.tagFlowLayout)
val adapter = TagAdapter(tagList)
tagFlowLayout.setAdapter(adapter)
tagFlowLayout.setMaxSelect(3) // 最多选择3个
```

---

## 注意事项

1. 所有组件均支持 XML 属性配置和 Kotlin/Java 代码调用
2. 表单组件支持继承关系，子组件会继承父组件的属性
3. 对话框组件使用 `showPop {}` DSL 风格调用
4. 刷新组件基于 SmartRefreshLayout，需添加相应依赖
5. 图片选择组件基于 PictureSelector，需添加相应依赖
