# UIKit - Android UI 组件库

## 🎨 项目概述

UIKit 是一个功能丰富的 Android UI 组件库，提供了一套完整的 UI 解决方案，包括对话框、表单、图片、选择器、刷新、评分、搜索栏、流程布局等。旨在帮助开发者快速构建美观、高效的 Android 应用界面。

## 📦 模块结构

```
uikit/
├── uikit/                           # 主 UI 库模块（70+ 自定义控件）
├── uikit-res/                       # UI 资源模块（颜色、尺寸）
├── uikit-toast/                     # Toast 组件模块
├── demo/                            # 演示应用
└── build.gradle                     # 模块根配置
```

## 🎯 子模块文档

| 模块 | 说明 | 文档链接 |
|------|------|---------|
| **uikit** | 核心 UI 组件库，包含 70+ 个自定义控件和工具类 | [uikit/README.md](uikit/README.md) |
| **uikit-res** | 公共资源模块，提供统一的颜色方案和尺寸规范 | [uikit-res/README.md](uikit-res/README.md) |
| **uikit-toast** | 自定义 Toast 组件，支持图标、位置和时长控制 | [uikit-toast/README.md](uikit-toast/README.md) |

## 📦 Maven 坐标

所有模块均发布至 **Maven Central**，统一 GroupId 为 `io.github.heart-beats.baseproject`。

| 模块 | ArtifactId | Gradle 依赖 |
|------|-----------|------------|
| uikit | `uikit` | `io.github.heart-beats.baseproject:uikit:0.0.4-SNAPSHOT` |
| uikit-res | `uikit-res` | `io.github.heart-beats.baseproject:uikit-res:0.0.4-SNAPSHOT` |
| uikit-toast | `uikit-toast` | `io.github.heart-beats.baseproject:uikit-toast:0.0.4-SNAPSHOT` |

## 🚀 快速开始

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
    // 核心 UI 组件库
    implementation 'io.github.heart-beats.baseproject:uikit:0.0.4-SNAPSHOT'

    // 如需单独使用 Toast 模块
    // implementation 'io.github.heart-beats.baseproject:uikit-toast:0.0.4-SNAPSHOT'
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

#### 使用 Toast

```kotlin
// 基础 Toast
toast("操作成功")

// 成功提示
toastSuccess("保存成功")

// 失败提示
toastFailure("网络连接失败")
```

## 🎨 主题和样式

### 颜色资源

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
| `uikit_fontcolor_1` | `#FF000000` | 主字体颜色（纯黑） |
| `uikit_fontcolor_2` | `#BF000000` | 次要字体颜色（75% 黑） |

### 尺寸资源

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

## 🎯 核心组件概览

### 按钮组件
- **UIKitCommonButton** - 可配置样式的通用按钮（8种预设样式）
- **UIKitOptionRadio** - 单选按钮组件

### 表单组件体系
- **UIKitFormGroup** - 表单分组容器
- **UIKitFormItemInput** - 输入框表单项
- **UIKitFormItemLabel** - 标签显示表单项
- **UIKitFormItemText** - 文本显示表单项
- **UIKitFormItemImage** - 图片选择表单项
- **UIKitFormItemToggleButton** - 开关表单项
- **UIKitFormHeaderActionText** - 带操作的表单组标题
- **UIKitFormNumberStepView** - 数字步进器表单项
- **UIKitFormTextVerifyCode** - 验证码输入表单项

### 文本组件
- **UIKitSelectTextView** - 可选中的文本控件
- **UIKitTextArea** - 多行文本域

### 流式布局
- **FlowLayout** - 流式布局容器
- **TagFlowLayout** - 标签流式布局
- **TagAdapter** - 标签适配器
- **FilterTagAdapter** - 筛选标签适配器
- **TagView** - 标签视图

### 搜索栏
- **UIKitSearchBar** - 搜索栏组件

### 评分组件
- **UIKitRatingBar** - 评分条（支持整星/半星）

### 进度条
- **UIKitCircleProgressBar** - 圆形进度条

### 刷新组件
- **UIKitCommonRefreshHeader** - 通用刷新头
- **UIKitCommonRefreshFooter** - 通用刷新尾
- **UIKitLottieRefreshHeaderFooter** - Lottie 动画刷新头/尾

### 加载动画
- **UIKitWaveLoadingView** - 波浪加载动画

### 侧边栏
- **UIKitSideBarView** - 字母索引侧边栏

### 选择器
- **TimePickerDialogFragment** - 时间选择器弹窗
- **OptionsPickerDialogFragment** - 选项滚动选择器弹窗
- **RangeTimePickerDialogFragment** - 时间区间选择器弹窗

### 对话框
- **AlertDialogFragment** - 警告对话框
- **BottomDialogFragment** - 底部弹出对话框
- **InputDialogFragment** - 输入对话框
- **LoadingDialogFragment** - 加载对话框
- **ProgressBarDialog** - 进度条对话框
- **SlideXDialogFragment** - 侧滑关闭对话框
- **AdvertDialogFragment** - 广告对话框
- **BasicDialogFragment** - 基础对话框
- **LongPressMenuDialog** - 长按菜单对话框

### ActionSheet
- **ActionSheetDialogFragment** - 底部选项列表
- **Alert1SheetDialogFragment** - 警告样式选项列表（样式1）
- **Alert2SheetDialogFragment** - 警告样式选项列表（样式2）
- **ArrayListSheetDialogFragment** - 列表数据选项弹窗

### 筛选组件
- **FilterDialogFragment** - 筛选条件弹窗
- **FlowFilterDialogFragment** - 流式筛选弹窗

### 图片组件
- **PickImageUtil** - 图片选择工具类
- **UIKitRoundImageView** - 圆角/圆形 ImageView
- **UIKitGifImageView** - GIF 图片视图
- **UIKitProgressImageView** - 进度图片视图
- **UIKitUploadPicImageGridLayout** - 上传图片网格布局

### 布局组件
- **UIKitMaxHeightFrameLayout** - 最大高度 FrameLayout
- **UIKitMaxHeightNestedScrollView** - 最大高度 NestedScrollView
- **CornerLayout** - 圆角布局容器
- **FocusableTouchLayout** - 可获取焦点的触摸布局
- **UIKitCollapsingToolbarLayout** - 折叠工具栏布局

### 其他组件
- **UIKitToolbar** - 自定义顶部导航栏
- **UIKitNumberStepView** - 步进器组件
- **ProgressWebView** - 带进度条的 WebView
- **UIKitDividerView** - 分割线组件
- **UIKitShapeEditTextWithDelete** - 带删除按钮的输入框

### RecyclerView 装饰器
- **RecyclerViewDividerDecoration** - 分割线装饰器
- **GridSpaceItemDecoration** - 网格间距装饰器
- **GridSpacingItemDecoration** - 网格间距装饰器（支持边缘）
- **MarginStartDecoration** - 起始边距装饰器
- **RecyclerViewPaddingDecoration** - 内边距装饰器

### 工具类扩展
- **图片加载扩展** - loadImage、loadCircleImage 等
- **权限工具** - requestPermissions
- **屏幕工具** - screenWidth、screenHeight、statusBarHeight
- **文本工具** - measureTextWidth、measureTextHeight
- **View 工具** - visible、invisible、gone

## 🔧 构建与测试

```bash
# 构建所有模块
./gradlew :uikit:assemble :uikit-res:assemble :uikit-toast:assemble

# 发布到本地 Maven 仓库
./gradlew :uikit:publishToMavenLocal :uikit-res:publishToMavenLocal :uikit-toast:publishToMavenLocal

# 运行演示应用
./gradlew :demo:installDebug
```

## 📱 演示应用

`demo` 模块包含了所有组件的演示示例，可以安装到设备上查看效果。

## 📄 相关文档

- [uikit 核心模块文档](uikit/README.md) - 详细的组件属性和使用示例
- [uikit-res 资源模块文档](uikit-res/README.md) - 颜色和尺寸资源说明
- [uikit-toast 组件文档](uikit-toast/README.md) - Toast 组件使用说明

## 📝 注意事项

1. 所有组件均支持 XML 属性配置和 Kotlin/Java 代码调用
2. 表单组件支持继承关系，子组件会继承父组件的属性
3. 对话框组件使用 `showPop {}` DSL 风格调用
4. 刷新组件基于 SmartRefreshLayout，需添加相应依赖
5. 图片选择组件基于 PictureSelector，需添加相应依赖
6. 所有资源名称都以 `uikit_` 为前缀，避免与其他模块资源冲突
