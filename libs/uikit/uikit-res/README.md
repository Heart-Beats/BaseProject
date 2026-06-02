# uikit-res — UIKit 资源模块

## 模块概述

`uikit-res` 是 UIKit 组件库的公共资源模块，统一提供 UIKit 组件的颜色方案和尺寸规范。所有资源均以 `uikit_` 为前缀，方便识别和管理。

**模块坐标**: `com.hl.uikitres`
**发布 GroupId**: `io.github.heart-beats.baseproject`

## 目录结构

```
uikit-res/
├── build.gradle
└── src/main/
    ├── AndroidManifest.xml
    └── res/
        ├── values/
        │   ├── colors.xml         # UIKit 颜色资源（10种主题色 + 2种字体色）
        │   └── dimens.xml         # UIKit 尺寸资源（10种字体大小）
```

## 资源清单

### 颜色资源 (colors.xml)

UIKit 提供 10 种主题色和 2 种字体色，覆盖常见的 UI 场景：

#### 主题色

| 资源名称 | 色值 | 用途说明 |
|---------|------|---------|
| `uikit_color_1` | `#FF5E60C7` | 主色调（紫色） |
| `uikit_color_2` | `#FFF36F46` | 强调色（橙红色） |
| `uikit_color_3` | `#FF333333` | 深色背景 |
| `uikit_color_4` | `#FF818181` | 次要文字 |
| `uikit_color_5` | `#FFC4C4C4` | 辅助文字 |
| `uikit_color_6` | `#26F36F46` | 半透明强调色（20% 透明度） |
| `uikit_color_7` | `#FFF4F4F4` | 浅色背景 |
| `uikit_color_8` | `#FFF4F7FB` | 页面背景 |
| `uikit_color_9` | `#FFFF3B30` | 错误/警告色 |
| `uikit_color_10` | `#FF4F51A9` | 深紫色 |

#### 字体色

| 资源名称 | 色值 | 用途说明 |
|---------|------|---------|
| `uikit_fontcolor_1` | `#FF000000` | 主要文字（100% 不透明度） |
| `uikit_fontcolor_2` | `#BF000000` | 次要文字（75% 不透明度） |

### 尺寸资源 (dimens.xml)

UIKit 提供 10 级字体大小，从 40sp 到 12sp，满足不同场景需求：

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

## 依赖关系

无外部和内部依赖，是最底层资源模块，可被其他所有 UIKit 组件模块依赖。

## 构建与测试

```bash
# 构建模块
./gradlew :uikit-res:assemble

# 发布到本地 Maven 仓库（用于本地开发调试）
./gradlew :uikit-res:publishToMavenLocal
```

## 使用示例

### 在 XML 布局中使用

```xml
<!-- 使用颜色资源 -->
<TextView
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    android:textColor="@color/uikit_color_1"
    android:textSize="@dimen/uikit_font_size_7"
    android:text="示例文本" />

<!-- 使用尺寸资源 -->
<Button
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    android:textSize="@dimen/uikit_font_size_5"
    android:background="@color/uikit_color_2" />
```

### 在 Kotlin/Java 代码中使用

```kotlin
// 在 Activity 或 Fragment 中
textView.setTextColor(ContextCompat.getColor(this, R.color.uikit_color_1))
textView.textSize = resources.getDimension(R.dimen.uikit_font_size_7)

// 使用 Kotlin 扩展属性（如果支持）
textView.textColor = R.color.uikit_color_1
```

## 设计规范

### 颜色使用原则

1. **主题色优先**: 优先使用 `uikit_color_1`（主色调）和 `uikit_color_2`（强调色）
2. **层级分明**: 使用不同深浅的颜色建立视觉层级
3. **一致性**: 相同场景使用相同颜色，保持界面一致性
4. **可访问性**: 确保文字与背景色有足够的对比度

### 字体大小使用原则

1. **层级清晰**: 使用不同大小的字体建立信息层级
2. **可读性**: 正文内容不小于 14sp，确保可读性
3. **一致性**: 相同层级的内容使用相同大小的字体
4. **适配性**: 使用 sp 单位以支持用户字体大小设置

## 注意事项

- 所有资源名称都以 `uikit_` 为前缀，避免与其他模块资源冲突
- 该模块不包含任何代码逻辑，仅提供静态资源
- 资源值基于设计稿定义，修改时需同步更新设计稿
- 新增资源时请遵循现有的命名规范
