# base-app-res — 公共资源模块

## 模块概述

`base-app-res` 是项目的公共资源模块，统一管理应用级别的基础资源文件，包括颜色、尺寸、动画、图标、字体等。所有资源均以 `hl_res_` 为前缀，避免与其他模块冲突。

**模块坐标**: `com.hl.res`
**发布 GroupId**: `io.github.heart-beats.baseproject`

## 目录结构

```
app-res/
├── build.gradle
└── src/main/
    ├── AndroidManifest.xml
    └── res/
        ├── anim/                              # 过渡动画（8个）
        │   ├── fade_in.xml                    # 淡入
        │   ├── fade_out.xml                   # 淡出
        │   ├── slide_in_bottom.xml            # 底部滑入
        │   ├── slide_in_left.xml              # 左侧滑入
        │   ├── slide_in_right.xml             # 右侧滑入
        │   ├── slide_out_bottom.xml           # 底部滑出
        │   ├── slide_out_left.xml             # 左侧滑出
        │   └── slide_out_right.xml            # 右侧滑出
        ├── drawable/                          # 图形资源（4个 Shape）
        │   ├── hl_res_shape_33fa5252_round4dp.xml
        │   ├── hl_res_shape_white_round18dp.xml
        │   ├── hl_res_shape_white_round8dp.xml
        │   └── hl_res_shape_white_top_round58px.xml
        ├── drawable-xxhdpi/                   # 位图资源（8个图标）
        │   ├── icon_close.png
        │   ├── icon_more_dot.png
        │   ├── icon_nodata.png
        │   ├── icon_scan_error.png
        │   ├── icon_switch_close.png
        │   ├── icon_switch_open.png
        │   ├── icon_title_back.png
        │   └── icon_unknown_file.png
        ├── font/                              # 字体资源
        │   └── hl_res_akrobat_bold.ttf
        ├── values/                            # 值资源
        │   ├── _colors.xml                    # 自定义颜色
        │   ├── colors.xml                     # 完整颜色体系
        │   ├── dimens.xml                     # dp 尺寸体系
        │   ├── dimens-px.xml                  # px 尺寸体系
        │   └── strings.xml                    # 字符串资源
        └── xml/
            └── hl_res_public_file_paths.xml   # FileProvider 路径配置
```

## 依赖关系

### 外部依赖
无外部依赖。

### 内部依赖
无内部依赖，是最底层基础设施模块。

## 对外接口

本模块为纯资源模块，不包含代码，仅通过 `R` 类对外暴露资源 ID。

### 资源前缀规范

所有资源名称均以 `hl_res_` 或 `hl_` 开头，例如：
- `R.color.hl_color_primary`
- `R.dimen.hl_spacing_medium`
- `R.anim.fade_in`
- `R.drawable.hl_res_round_bg`
- `R.string.hl_res_xxx`

## 构建与测试

```bash
# 构建模块
./gradlew :base-app-res:assemble

# 发布到本地 Maven
./gradlew :base-app-res:publishToMavenLocal
```

## 使用示例

```kotlin
// 在布局中使用资源
<TextView
    android:textColor="@color/blue"
    android:textSize="@dimen/sp_16"
    android:padding="@dimen/dp_16" />

// 在代码中使用
view.setBackgroundResource(R.drawable.hl_res_shape_white_round8dp)
context.getColor(R.color.green)
resources.getDimensionPixelSize(R.dimen.dp_16)
```
