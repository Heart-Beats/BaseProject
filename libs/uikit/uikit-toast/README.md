# uikit-toast — Toast 组件模块

## 模块概述

`uikit-toast` 是 UIKit 组件库的自定义 Toast 组件模块，提供统一的 Toast 提示框，支持图标（成功/失败）、位置和时长控制、多种快捷扩展函数。解决原生 Toast 样式不统一、功能单一的问题。

**模块坐标**: `com.hl.uikit`
**发布 GroupId**: `io.github.heart-beats.baseproject`

## 目录结构

```
uikit-toast/
├── build.gradle.kts
└── src/main/
    ├── AndroidManifest.xml
    ├── java/com/hl/uikit/
    │   ├── ToastUtils.kt          # Toast 核心类（含 ToastBuilder 构建器）
    │   ├── GravityFlag.kt         # 位置注解
    │   ├── _Toast.kt              # Context/Fragment 扩展函数
    │   └── _ToastUtils.kt         # 快捷扩展函数
    └── res/
        ├── drawable/
        │   ├── uikit_shape_toast_bg.xml        # 无图标 Toast 背景
        │   └── uikit_shape_toast_icon_bg.xml   # 带图标 Toast 背景
        ├── drawable-xxhdpi/
        │   ├── uikit_ic_toast_fail.png         # 失败图标
        │   └── uikit_ic_toast_success.png      # 成功图标
        └── layout/
            ├── uikit_layout_toast.xml               # 纯文本 Toast 布局
            └── uikit_layout_toast_with_icon.xml     # 带图标 Toast 布局
```

## 依赖关系

| 外部依赖 | `androidx.appcompat:appcompat` |
|----------|-------------------------------|

## 架构设计

### 核心组件

**ToastUtils** — Toast 管理单例
- 负责 Toast 的初始化、显示和取消
- 使用 Application 上下文，避免内存泄漏
- 自动处理 Toast 的去重（取消前一个再显示下一个）

**ToastBuilder** — Toast 构建器
- 使用 DSL 风格的构建器模式
- 支持自定义布局、图标、文字、位置、时长

**GravityFlag** — 位置注解
- 类型安全的位置约束
- 支持 TOP、BOTTOM、START、END、CENTER 等位置

### 扩展函数

**_Toast.kt** — Context/Fragment 扩展
- `toast()` — 基础 Toast 显示
- `toastSuccess()` — 成功提示（带成功图标）
- `toastFailure()` — 失败提示（带失败图标）

**_ToastUtils.kt** — 快捷扩展
- `showShortToast()` — 短时提示
- `showLongToast()` — 长时提示
- `showShortError()` — 短时错误提示
- `showLongError()` — 长时错误提示

## 对外接口

### ToastUtils API

```kotlin
object ToastUtils {
    // 显示 Toast（带 Context 初始化）
    fun show(context: Context, build: ToastBuilder.() -> Unit)
}
```

### ToastBuilder API

```kotlin
class ToastBuilder {
    // 布局文件（必须包含 tag 为 "toast_text" 的 TextView）
    @LayoutRes var layout: Int = R.layout.uikit_layout_toast

    // 图标资源（可选）
    var iconRes: Int? = null

    // 显示文字
    var text: CharSequence? = null

    // 位置（使用 @GravityFlag 注解）
    @GravityFlag var gravity: Int = Gravity.CENTER

    // 时长（Toast.LENGTH_SHORT 或 Toast.LENGTH_LONG）
    var duration: Int = Toast.LENGTH_SHORT
}
```

### Context 扩展函数

```kotlin
// 基础 Toast
fun Context.toast(text: CharSequence, duration: Int = LENGTH_SHORT, gravity: Int = CENTER)
fun Context.toast(resId: Int, duration: Int = LENGTH_SHORT, gravity: Int = CENTER)

// 成功提示
fun Context.toastSuccess(text: CharSequence, duration: Int = LENGTH_SHORT, gravity: Int = CENTER)
fun Context.toastSuccess(textRes: Int, duration: Int = LENGTH_SHORT, gravity: Int = CENTER)

// 失败提示
fun Context.toastFailure(text: CharSequence, duration: Int = LENGTH_SHORT, gravity: Int = CENTER)
fun Context.toastFailure(textRes: Int, duration: Int = LENGTH_SHORT, gravity: Int = CENTER)

// 快捷提示
fun Context.showShortToast(message: String, gravity: Int = CENTER)
fun Context.showLongToast(message: String, gravity: Int = CENTER)
fun Context.showShortError(message: String, gravity: Int = CENTER)
fun Context.showLongError(message: String, gravity: Int = CENTER)
```

### Fragment 扩展函数

```kotlin
// 基础 Toast
fun Fragment.toast(text: CharSequence, duration: Int = LENGTH_SHORT, gravity: Int = CENTER)
fun Fragment.toast(resId: Int, duration: Int = LENGTH_SHORT, gravity: Int = CENTER)

// 成功提示
fun Fragment.toastSuccess(text: CharSequence, duration: Int = LENGTH_SHORT, gravity: Int = CENTER)
fun Fragment.toastSuccess(textRes: Int, duration: Int = LENGTH_SHORT, gravity: Int = CENTER)

// 失败提示
fun Fragment.toastFailure(text: CharSequence, duration: Int = LENGTH_SHORT, gravity: Int = CENTER)
fun Fragment.toastFailure(textRes: Int, duration: Int = LENGTH_SHORT, gravity: Int = CENTER)

// 快捷提示
fun Fragment.showShortToast(message: String)
fun Fragment.showLongToast(message: String)
fun Fragment.showShortError(message: String)
fun Fragment.showLongError(message: String)
```

## 使用示例

### 基础使用

```kotlin
// 纯文本提示
toast("操作成功")

// 使用字符串资源
toast(R.string.success_message)

// 成功提示（带成功图标）
toastSuccess("保存成功")

// 失败提示（带失败图标）
toastFailure("网络连接失败")
```

### 快捷方式

```kotlin
// 短时提示
showShortToast("这是一个短提示")

// 长时提示
showLongToast("这是一个长提示")

// 错误提示
showShortError("操作失败")
showLongError("网络连接失败，请稍后重试")
```

### 自定义位置和时长

```kotlin
// 顶部提示
toast("顶部提示", gravity = Gravity.TOP)

// 底部长时提示
toast("底部提示", duration = Toast.LENGTH_LONG, gravity = Gravity.BOTTOM)

// 自定义位置和时长
toast("自定义提示",
    duration = Toast.LENGTH_LONG,
    gravity = Gravity.TOP or Gravity.CENTER_HORIZONTAL
)
```

### 在 Fragment 中使用

```kotlin
class MyFragment : Fragment() {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 基础使用
        toast("Fragment 中的提示")

        // 成功提示
        toastSuccess("保存成功")

        // 失败提示
        toastFailure("保存失败")

        // 快捷方式
        showShortToast("短提示")
        showLongToast("长提示")
    }
}
```

### 高级用法（使用 ToastBuilder）

```kotlin
ToastUtils.show(context) {
    layout = R.layout.custom_toast_layout
    text = "自定义 Toast"
    gravity = Gravity.TOP
    duration = Toast.LENGTH_LONG
}
```

## 布局要求

如果使用自定义布局，需要满足以下要求：

1. **必须包含以下 tag 的控件**：
   - `toast_text` — 显示文字的 TextView
   - `toast_icon` — 显示图标的 ImageView（可选）

2. **示例布局结构**：

```xml
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    android:background="@drawable/uikit_shape_toast_bg"
    android:gravity="center"
    android:orientation="vertical">

    <TextView
        android:tag="toast_text"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_margin="16dp"
        android:textColor="@android:color/white"
        android:textSize="14sp" />
</LinearLayout>
```

## 样式定制

### 预设样式

- **无图标样式** (`uikit_shape_toast_bg.xml`)
  - 圆角: 4dp
  - 背景色: `#BF000000`（黑色 75% 透明度）

- **带图标样式** (`uikit_shape_toast_icon_bg.xml`)
  - 圆角: 8dp
  - 背景色: `#BF000000`（黑色 75% 透明度）

### 预设图标

- `uikit_ic_toast_success` — 成功图标（勾选）
- `uikit_ic_toast_fail` — 失败图标（叉号）

## 构建与测试

```bash
# 构建模块
./gradlew :uikit-toast:assemble

# 发布到本地 Maven
./gradlew :uikit-toast:publishToMavenLocal

# 运行单元测试
./gradlew :uikit-toast:test

# 运行仪器测试
./gradlew :uikit-toast:connectedAndroidTest
```

## 注意事项

1. **自动初始化**：无需手动调用 `init()`，首次使用时会自动初始化
2. **去重机制**：连续调用 `show()` 会自动取消前一个 Toast，避免重叠显示
3. **内存安全**：使用 Application 上下文，避免持有 Activity/Fragment 引用
4. **线程安全**：可以在任意线程调用，Toast 会在主线程显示
5. **样式统一**：建议统一使用预设样式，确保应用内提示风格一致

## 版本历史

- **v1.0.0** — 初始版本，支持基础 Toast、成功/失败提示、位置和时长控制
